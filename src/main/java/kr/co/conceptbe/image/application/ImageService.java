package kr.co.conceptbe.image.application;

import java.util.List;
import kr.co.conceptbe.auth.presentation.dto.AuthCredentials;
import kr.co.conceptbe.image.domain.IdeaValidator;
import kr.co.conceptbe.image.domain.Image;
import kr.co.conceptbe.image.domain.ImageChecker;
import kr.co.conceptbe.image.domain.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final IdeaValidator ideaValidator;
    private final ImageChecker imageChecker;
    private final S3Client s3Client;

    public void save(Long ideaId, AuthCredentials authCredentials, List<MultipartFile> files) {
        ideaValidator.validateIdea(ideaId, authCredentials.id());
        imageChecker.validateAdditionImagesSize(files.size());
        uploadImages(ideaId, files);
    }

    private void uploadImages(Long ideaId, List<MultipartFile> files) {
        files.stream()
            .map(s3Client::upload)
            .map(imageUrl -> new Image(ideaId, imageUrl))
            .forEach(imageRepository::save);
    }

    public void update(
        Long ideaId,
        AuthCredentials authCredentials,
        List<Long> imageIds,
        List<MultipartFile> additionFiles
    ) {
        ideaValidator.validateIdea(ideaId, authCredentials.id());
        List<Image> imagesToDeleted = getImagesToDeleted(ideaId, imageIds, additionFiles.size());
        imagesToDeleted.forEach(this::deleteImage);
        additionFiles.forEach(s3Client::upload);
    }

    private List<Image> getImagesToDeleted(
        Long ideaId,
        List<Long> imageIds,
        int additionFilesSize
    ) {
        List<Image> savedImages = imageRepository.findAllByIdeaId(ideaId);
        List<Long> imageIdsToDeleted = imageChecker.getImageIdsToDeleted(
            extractIds(savedImages),
            imageIds
        );
        imageChecker.validateTotalImageSize(
            savedImages.size(),
            imageIdsToDeleted.size(),
            additionFilesSize
        );
        return savedImages.stream()
            .filter(image -> imageIdsToDeleted.contains(image.getId()))
            .toList();
    }

    private List<Long> extractIds(List<Image> savedImages) {
        return savedImages.stream()
            .map(Image::getId)
            .toList();
    }

    private void deleteImage(Image image) {
        s3Client.delete(image.getImageUrl());
        imageRepository.delete(image);
    }

}

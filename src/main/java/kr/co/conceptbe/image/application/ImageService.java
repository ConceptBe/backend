package kr.co.conceptbe.image.application;

import java.util.List;

import kr.co.conceptbe.image.application.response.ImageResponse;
import kr.co.conceptbe.image.domain.Image;
import kr.co.conceptbe.image.domain.ImageChecker;
import kr.co.conceptbe.image.domain.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {

    @Value("${cloud-front-url}")
    private String cloudFrontUrl;
    private final ImageRepository imageRepository;
    private final ImageChecker imageChecker;
    private final S3Client s3Client;

    public void save(Long ideaId, List<MultipartFile> files) {
        imageChecker.validateImagesSize(files.size());
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
            List<Long> imageIds,
            List<MultipartFile> additionFiles
    ) {
        List<Image> imagesToDeleted = getImagesToDeleted(ideaId, imageIds, additionFiles.size());
        imagesToDeleted.forEach(this::deleteImage);
        additionFiles.stream()
                .map(s3Client::upload)
                .map(imageUrl -> new Image(ideaId, imageUrl))
                .forEach(imageRepository::save);
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

    public List<ImageResponse> getImageResponses(Long ideaId) {
        return imageRepository.findAllByIdeaId(ideaId)
                .stream()
                .map(image -> ImageResponse.of(image, cloudFrontUrl))
                .toList();
    }

}

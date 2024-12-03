package kr.co.conceptbe.idea.presentation;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import kr.co.conceptbe.auth.presentation.dto.AuthCredentials;
import kr.co.conceptbe.comment.dto.CommentParentResponse;
import kr.co.conceptbe.common.auth.Auth;
import kr.co.conceptbe.common.auth.OptionalAuth;
import kr.co.conceptbe.idea.application.IdeaService;
import kr.co.conceptbe.idea.application.request.FilteringRequest;
import kr.co.conceptbe.idea.application.request.IdeaRequest;
import kr.co.conceptbe.idea.application.request.IdeaUpdateRequest;
import kr.co.conceptbe.idea.application.response.BestIdeaResponse;
import kr.co.conceptbe.idea.application.response.FindIdeaWriteResponse;
import kr.co.conceptbe.idea.application.response.IdeaResponse;
import kr.co.conceptbe.idea.dto.IdeaDetailResponse;
import kr.co.conceptbe.idea.dto.IdeaHitResponse;
import kr.co.conceptbe.idea.presentation.doc.IdeaApi;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ideas")
@SecurityRequirement(name = AUTHORIZATION)
public class IdeaController implements IdeaApi {

    private final IdeaService ideaService;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> addIdea(
            @Parameter(hidden = true) @Auth AuthCredentials auth,
            @RequestPart IdeaRequest request,
            @RequestPart(required = false) List<MultipartFile> images
    ) {
        Long savedId = ideaService.save(
                auth,
                request,
                Optional.ofNullable(images)
                        .orElseGet(Collections::emptyList)
        );

        return ResponseEntity.created(URI.create("/ideas/" + savedId))
                .build();
    }

    @PutMapping(value = "/{id}", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE
    })
    public ResponseEntity<Void> modifyIdea(
            @Parameter(hidden = true) @Auth AuthCredentials auth,
            @RequestPart IdeaUpdateRequest request,
            @PathVariable Long id,
            @RequestPart(required = false) List<MultipartFile> images
    ) {
        ideaService.updateIdea(
                auth,
                id,
                request,
                Optional.ofNullable(images)
                        .orElseGet(Collections::emptyList)
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> removeIdea(
            @Parameter(hidden = true) @Auth AuthCredentials auth,
            @PathVariable Long id
    ) {
        ideaService.deleteIdea(auth, id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("writing")
    public ResponseEntity<FindIdeaWriteResponse> getIdeaWriteResponses() {
        FindIdeaWriteResponse response = ideaService.getFindIdeaWriteResponse();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<IdeaResponse>> findAll(
            @Parameter(hidden = true) @OptionalAuth AuthCredentials authCredentials,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) List<Long> branchIds,
            @RequestParam(required = false) List<Long> purposeIds,
            @RequestParam(required = false) String cooperationWay,
            @RequestParam(required = false) Long region,
            @RequestParam(required = false) List<Long> skillCategoryIds
    ) {
        Pageable pageable = PageRequest.of(page, size);
        FilteringRequest filteringRequest = new FilteringRequest(
                branchIds,
                purposeIds,
                cooperationWay,
                region,
                skillCategoryIds
        );
        List<IdeaResponse> responses = ideaService.findAll(authCredentials, filteringRequest,
                pageable);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/best")
    public ResponseEntity<List<BestIdeaResponse>> findBestIdeas(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) List<Long> branchIds,
            @RequestParam(required = false) List<Long> purposeIds,
            @RequestParam(required = false) String cooperationWay,
            @RequestParam(required = false) Long region,
            @RequestParam(required = false) List<Long> skillCategoryIds
    ) {
        Pageable pageable = PageRequest.of(page, size);
        FilteringRequest filteringRequest = new FilteringRequest(
                branchIds,
                purposeIds,
                cooperationWay,
                region,
                skillCategoryIds
        );
        List<BestIdeaResponse> responses = ideaService.findAllBestIdea(filteringRequest, pageable);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{ideaId}")
    public ResponseEntity<IdeaDetailResponse> getDetailIdeaResponse(
            @Parameter(hidden = true) @Auth AuthCredentials authCredentials,
            @PathVariable(name = "ideaId") Long ideaId
    ) {
        IdeaDetailResponse ideaDetailResponse = ideaService.getDetailIdeaResponse(
                authCredentials.id(), ideaId);
        return ResponseEntity.ok(ideaDetailResponse);
    }

    @GetMapping("/{ideaId}/comments")
    public ResponseEntity<List<CommentParentResponse>> getIdeaCommentResponse(
            @Parameter(hidden = true) @Auth AuthCredentials authCredentials,
            @PageableDefault(sort = "createdAt") Pageable pageable,
            @PathVariable(name = "ideaId") Long ideaId) {
        List<CommentParentResponse> commentParentResponses = ideaService.getIdeaCommentResponse(
                authCredentials.id(), ideaId, pageable);
        return ResponseEntity.ok(commentParentResponses);
    }

    @PostMapping("/likes/{ideaId}")
    public ResponseEntity<Void> likesIdea(
            @Parameter(hidden = true) @Auth AuthCredentials authCredentials,
            @PathVariable(name = "ideaId") Long ideaId
    ) {
        Long id = ideaService.likesIdea(authCredentials.id(), ideaId);
        return ResponseEntity.created(URI.create("/ideas/" + id))
                .build();
    }

    @DeleteMapping("/likes/{ideaId}")
    public ResponseEntity<Void> likesCancelIdea(
            @Parameter(hidden = true) @Auth AuthCredentials authCredentials,
            @PathVariable(name = "ideaId") Long ideaId
    ) {
        ideaService.likesCancelIdea(authCredentials.id(), ideaId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{ideaId}/hits")
    public ResponseEntity<List<IdeaHitResponse>> getIdeaHitsResponse(
            @Parameter(hidden = true) @Auth AuthCredentials authCredentials,
            @PathVariable(name = "ideaId") Long ideaId
    ) {
        List<IdeaHitResponse> ideaCommentResponse = ideaService.getIdeaHitsResponse(ideaId);
        return ResponseEntity.ok(ideaCommentResponse);
    }
}

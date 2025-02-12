package kr.co.conceptbe.member.controller.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import kr.co.conceptbe.auth.presentation.dto.AuthCredentials;
import kr.co.conceptbe.common.auth.Auth;
import kr.co.conceptbe.idea.application.response.IdeaResponse;
import kr.co.conceptbe.member.application.dto.GetMemberProfileResponse;
import kr.co.conceptbe.member.application.dto.MemberIdeaResponse;
import kr.co.conceptbe.member.application.dto.UpdateMemberProfileRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Member", description = "회원 API")
public interface MemberApi {

    @Operation(summary = "닉네임 중복 체크")
    ResponseEntity<Boolean> checkDuplicatedNickName(@PathVariable String nickname);

    @Operation(summary = "회원 프로필 조회")
    ResponseEntity<GetMemberProfileResponse> getMemberProfile(
        @Parameter(hidden = true) @Auth AuthCredentials authCredentials,
        @PathVariable Long id
    );

    @Operation(summary = "회원이 작성한 아이디어 조회")
    ResponseEntity<List<MemberIdeaResponse>> findMemberIdeas(
        @Parameter(hidden = true) @Auth AuthCredentials authCredentials,
        @PathVariable Long id,
        @RequestParam int page,
        @RequestParam int size
    );

    @Operation(summary = "회원이 북마크한 아이디어 조회")
    ResponseEntity<List<IdeaResponse>> findMemberBookmarks(
        @Parameter(hidden = true) @Auth AuthCredentials authCredentials,
        @RequestParam int page,
        @RequestParam int size
    );

    @Operation(summary = "회원 프로필 수정")
    ResponseEntity<Void> updateMemberProfile(
        @RequestBody @Valid UpdateMemberProfileRequest updateMemberProfileRequest,
        @Parameter(hidden = true) @Auth AuthCredentials authCredentials,
        @PathVariable Long id
    );

    @Operation(summary = "회원 프로필 이미지 삭제")
    ResponseEntity<Void> deleteMemberProfileImage(
        @Parameter(hidden = true) @Auth AuthCredentials authCredentials,
        @PathVariable Long id
    );

    @Operation(summary = "회원 탈퇴")
    ResponseEntity<Void> deleteMember(
        @Parameter(hidden = true) @Auth AuthCredentials authCredentials,
        @PathVariable Long id
    );
}

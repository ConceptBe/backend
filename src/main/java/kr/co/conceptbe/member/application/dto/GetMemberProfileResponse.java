package kr.co.conceptbe.member.application.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record GetMemberProfileResponse(
    @Schema(description = "프로필 이미지 URL", example = "https://conceptbe.png")
    String profileImageUrl,
    @Schema(description = "닉네임", example = "conceptbe")
    String nickname,
    @Schema(description = "이메일", example = "conceptbe@gmail.com")
    String email,
    @Schema(description = "프로필 수정 가능 여부", example = "true")
    boolean isMyProfile,
    @Schema(description = "대표스킬", example = "개발")
    String mainSkill,
    @Schema(description = "지역명", example = "서울")
    String livingPlace,
    @Schema(description = "직장명", example = "네이버")
    String workingPlace,
    @Schema(description = "자기소개", example = "안녕하세요 31살 백엔드 개발자입니다.")
    String introduction,
    @ArraySchema( arraySchema =  @Schema(description = "세부 스킬"))
    List<MemberProfileSkillResponse> skills,
    @ArraySchema( arraySchema =  @Schema(
        description = "가입목적",
        example ="[\"사이드프로젝트\", \"공모전\"]"))
    List<String> joinPurposes,
    @ArraySchema( arraySchema =  @Schema(
            description = "목적",
            example ="[\"쇼핑\", \"금융/앱테크\"]"))
    List<String> branches
) {
}

package kr.co.conceptbe.writing.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.conceptbe.auth.application.dto.PurposeResponse;
import kr.co.conceptbe.branch.domain.Branch;
import kr.co.conceptbe.idea.application.response.BranchCategoryResponse;
import kr.co.conceptbe.idea.application.response.RegionResponse;
import kr.co.conceptbe.idea.application.response.SkillCategoryResponse;
import kr.co.conceptbe.purpose.domain.Purpose;
import kr.co.conceptbe.region.domain.Region;
import kr.co.conceptbe.skill.domain.SkillCategory;

import java.util.List;

public record WritingResponses(
        @Schema(description = "팀원 모집 지역 목록")
        List<RegionResponse> regionsResponses,
        @Schema(description = "목적 목록")
        List<PurposeResponse> purposesResponses,
        @Schema(description = "분야 목록")
        List<BranchCategoryResponse> branchesResponses,
        @Schema(description = "팀원 모집 분야 목록")
        List<SkillCategoryResponse> skillCategoryResponses
) {

    public static WritingResponses of(
            List<Region> regions,
            List<Purpose> purposes,
            List<Branch> branches,
            List<SkillCategory> skillCategories
    ) {
        return new WritingResponses(
                regions.stream().map(RegionResponse::from).toList(),
                purposes.stream().map(PurposeResponse::from).toList(),
                branches.stream()
                        .filter(Branch::isParentBranch)
                        .map(branchCategory -> BranchCategoryResponse.of(branchCategory, branches))
                        .toList(),
                skillCategories.stream()
                        .filter(SkillCategory::isParentSkill)
                        .map(skillCategory -> SkillCategoryResponse.of(skillCategory, skillCategories))
                        .toList()
        );
    }

}

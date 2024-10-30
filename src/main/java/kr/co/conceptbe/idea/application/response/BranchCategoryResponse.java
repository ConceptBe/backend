package kr.co.conceptbe.idea.application.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import kr.co.conceptbe.branch.domain.Branch;

public record BranchCategoryResponse(
        @Schema(description = "분야 ID (카테고리)", example = "1")
        Long id,
        @Schema(description = "분야 이름 (카테고리)", example = "IT")
        String name,
        @Schema(description = "분야 이름(세부분야)")
        List<BranchResponse> branchResponses
) {
    public static BranchCategoryResponse of(
        Branch branch,
        List<Branch> branches
    ) {
       return new BranchCategoryResponse(
           branch.getId(),
           branch.getName(),
           branches.stream()
               .filter(childBranch -> !childBranch.isParentBranch())
               .filter(childBranch -> childBranch.isChildBranch(branch))
               .map(BranchResponse::from)
               .toList()
       );
    }
}

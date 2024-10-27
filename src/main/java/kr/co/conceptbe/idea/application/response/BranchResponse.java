package kr.co.conceptbe.idea.application.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.conceptbe.branch.domain.Branch;

public record BranchResponse(
        @Schema(description = "분야 ID", example = "1")
        Long id,
        @Schema(description = "분야 이름", example = "IT")
        String name
) {

    public static BranchResponse from(Branch branch) {
        return new BranchResponse(branch.getId(), branch.getName());
    }

    public static BranchResponse createNotificationBranch(Branch branch) {
        return new BranchResponse(
            branch.getId(),
            branch.getParentBranch().getName() + " - " + branch.getName()
        );
    }
}

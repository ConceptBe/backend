package kr.co.conceptbe.notification_setting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.List;
import kr.co.conceptbe.auth.application.dto.PurposeResponse;
import kr.co.conceptbe.branch.domain.Branch;
import kr.co.conceptbe.idea.application.response.BranchCategoryResponse;
import kr.co.conceptbe.idea.domain.vo.CooperationWay;
import kr.co.conceptbe.purpose.domain.Purpose;

public record NotificationSettingInfoResponse(
        @Schema(description = "목적 목록")
        List<PurposeResponse> purposes,
        @Schema(description = "협업 방식")
        List<CooperationWay> CooperationWays,
        @Schema(description = "분야 목록")
        List<BranchCategoryResponse> branches
) {

    public static NotificationSettingInfoResponse of(
            List<Purpose> purposes,
            CooperationWay[] cooperationWay,
            List<Branch> branches
    ){
        return new NotificationSettingInfoResponse(
                purposes.stream().map(PurposeResponse::from).toList(),
                Arrays.stream(cooperationWay).toList(),
                branches.stream()
                        .filter(Branch::isParentBranch)
                        .map(branchCategory -> BranchCategoryResponse.of(branchCategory, branches))
                        .toList()
        );
    }
}

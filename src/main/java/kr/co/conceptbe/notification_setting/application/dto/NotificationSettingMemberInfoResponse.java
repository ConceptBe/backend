package kr.co.conceptbe.notification_setting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import kr.co.conceptbe.auth.application.dto.PurposeResponse;
import kr.co.conceptbe.idea.application.response.BranchResponse;
import kr.co.conceptbe.idea.domain.vo.CooperationWay;
import kr.co.conceptbe.notification_setting.domain.IdeaNotificationSetting;

public record NotificationSettingMemberInfoResponse(
        @Schema(description = "목적 목록")
        List<PurposeResponse> purposes,
        @Schema(description = "분야 목록")
        List<BranchResponse> branches,
        @Schema(description = "협업 방식")
        CooperationWay CooperationWay

) {

    public static NotificationSettingMemberInfoResponse from(IdeaNotificationSetting ideaNotificationSetting) {
        return new NotificationSettingMemberInfoResponse(
                ideaNotificationSetting.getPurposes()
                    .getNotificationSettingPurposes()
                    .stream()
                    .map(purpose -> PurposeResponse.from(purpose.getPurpose()))
                    .toList(),
                ideaNotificationSetting.getBranches()
                        .getNotificationSettingBranches()
                        .stream()
                        .map(branch -> BranchResponse.createNotificationBranch(branch.getBranch()))
                        .toList(),
                ideaNotificationSetting.getCooperationWay()
        );
    }
}

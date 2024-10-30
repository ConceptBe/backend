package kr.co.conceptbe.notification_setting.application.dto;

import java.util.List;
import kr.co.conceptbe.idea.domain.vo.CooperationWay;

public record NotificationSettingRequest(
        List<Long> purposeIds,
        List<Long> branchIds,
        CooperationWay cooperationWay
) {
}

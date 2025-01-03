package kr.co.conceptbe.notification.app.dto;

import kr.co.conceptbe.notification.domain.IdeaNotification;

import java.util.List;

public record NotificationResponse(
        Long id,
        Long feedId,
        String title,
        String createAt,
        List<String> badges,
        boolean isAlreadyRead
) {

    public static NotificationResponse from(IdeaNotification notification) {
        List<String> ideaBranches = notification.getBranches();
        ideaBranches.addAll(notification.getPurposes());
        ideaBranches.add(notification.getCooperationWay());

        return new NotificationResponse(
                notification.getId(),
                notification.getIdeaId(),
                notification.getTitle(),
                notification.getCreatedAt().toString(),
                ideaBranches,
                notification.isAlreadyRead()
        );
    }

}

package kr.co.conceptbe.notification.presentation.swagger;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.conceptbe.auth.presentation.dto.AuthCredentials;
import kr.co.conceptbe.common.auth.Auth;
import kr.co.conceptbe.notification.app.dto.NotificationResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Notification", description = "알림 API")
public interface NotificationSwagger {

    ResponseEntity<List<NotificationResponse>> getNotifications(
            @Parameter(hidden = true) @Auth AuthCredentials auth,
            Long cursorId
    );

    ResponseEntity<Void> readNotification(
            @Parameter(hidden = true) AuthCredentials auth,
            Long notificationId
    );

}

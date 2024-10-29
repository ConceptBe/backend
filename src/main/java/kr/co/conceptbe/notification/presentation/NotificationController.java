package kr.co.conceptbe.notification.presentation;

import io.swagger.v3.oas.annotations.Parameter;
import kr.co.conceptbe.auth.presentation.dto.AuthCredentials;
import kr.co.conceptbe.common.auth.Auth;
import kr.co.conceptbe.notification.app.NotificationService;
import kr.co.conceptbe.notification.app.dto.NotificationResponse;
import kr.co.conceptbe.notification.presentation.swagger.NotificationSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationSwagger {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @Parameter(hidden = true) @Auth AuthCredentials auth,
            @RequestParam(required = false, defaultValue = "" + Long.MAX_VALUE) Long cursorId
    ) {
        Long limit = 10L;
        List<NotificationResponse> responses = notificationService.findAllNotifications(auth, cursorId, limit);

        return ResponseEntity.ok(responses);
    }

}

package kr.co.conceptbe.notification_setting.controller;

import io.swagger.v3.oas.annotations.Parameter;
import java.net.URI;
import kr.co.conceptbe.auth.presentation.dto.AuthCredentials;
import kr.co.conceptbe.common.auth.Auth;
import kr.co.conceptbe.notification_setting.application.NotificationSettingService;
import kr.co.conceptbe.notification_setting.application.dto.NotificationSettingInfoResponse;
import kr.co.conceptbe.notification_setting.application.dto.NotificationSettingRequest;
import kr.co.conceptbe.notification_setting.application.dto.NotificationSettingMemberInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification-setting")
public class NotificationSettingController {

    private final NotificationSettingService notificationSettingService;

    @GetMapping
    public ResponseEntity<NotificationSettingInfoResponse> getNotificationSettingInfo() {
        NotificationSettingInfoResponse response = notificationSettingService.getNotificationSettingInfo();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/memberInfo")
    public ResponseEntity<NotificationSettingMemberInfoResponse> getNotificationSettingMemberInfo(
            @Parameter(hidden = true) @Auth AuthCredentials auth
    ) {
        NotificationSettingMemberInfoResponse response = notificationSettingService.getNotificationSettingMemberInfo(auth);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<String> createNotificationSetting(
            @Parameter(hidden = true) @Auth AuthCredentials auth,
            @RequestBody NotificationSettingRequest notificationSettingRequest
    ) {
        Long savedId = notificationSettingService.createNotificationSetting(auth, notificationSettingRequest);

        return ResponseEntity.created(URI.create("/notification-setting/" + savedId))
                .build();
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<String> updateNotificationSetting(
            @Parameter(hidden = true) @Auth AuthCredentials auth,
            @PathVariable Long memberId,
            @RequestBody NotificationSettingRequest notificationSettingRequest
    ) {
        notificationSettingService.updateNotificationSetting(auth, memberId, notificationSettingRequest);

        return ResponseEntity.noContent().build();
    }
}

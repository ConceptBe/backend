package kr.co.conceptbe.notification_setting.domain.vo;

import jakarta.persistence.*;
import kr.co.conceptbe.notification_setting.domain.IdeaNotificationSetting;
import kr.co.conceptbe.purpose.domain.Purpose;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class NotificationSettingPurpose {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "notification_setting_id")
    private IdeaNotificationSetting notificationSetting;

    @ManyToOne
    @JoinColumn(name = "purpose_id")
    private Purpose purpose;

    private NotificationSettingPurpose(IdeaNotificationSetting notificationSetting, Purpose purpose) {
        this.notificationSetting = notificationSetting;
        this.purpose = purpose;
    }

    public static NotificationSettingPurpose of(IdeaNotificationSetting notificationSetting, Purpose purpose) {
        return new NotificationSettingPurpose(notificationSetting, purpose);
    }

}

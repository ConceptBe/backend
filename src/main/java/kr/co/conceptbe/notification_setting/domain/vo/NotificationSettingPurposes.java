package kr.co.conceptbe.notification_setting.domain.vo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.Set;
import kr.co.conceptbe.notification_setting.domain.IdeaNotificationSetting;
import kr.co.conceptbe.notification_setting.exception.InvalidPurposeException;
import kr.co.conceptbe.purpose.domain.Purpose;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Getter
@NoArgsConstructor(access = PROTECTED)
public class NotificationSettingPurposes {

    private static final int NOTIFICATION_SETTING_PURPOSES_SIZE_LOWER_BOUND_INCLUSIVE = 1;

    @OneToMany(
            mappedBy = "notificationSetting",
            orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE}
    )
    private List<NotificationSettingPurpose> notificationSettingPurposes;

    private NotificationSettingPurposes(List<NotificationSettingPurpose> notificationSettingPurposes) {
        this.notificationSettingPurposes = notificationSettingPurposes;
    }

    public static NotificationSettingPurposes of(
            IdeaNotificationSetting ideaNotificationSetting,
            Set<Purpose> purposes
    ) {
        validatePurpose(purposes);

        List<NotificationSettingPurpose> notificationSettingPurposes = purposes.stream()
                .map(purpose -> NotificationSettingPurpose.of(ideaNotificationSetting, purpose))
                .toList();

        return new NotificationSettingPurposes(notificationSettingPurposes);
    }

    public void update(
            IdeaNotificationSetting ideaNotificationSetting,
            Set<Purpose> purposes
    ) {
        validatePurpose(purposes);

        notificationSettingPurposes.clear();
        notificationSettingPurposes.addAll(
            purposes.stream()
                .map(purpose -> NotificationSettingPurpose.of(ideaNotificationSetting, purpose))
                .toList()
        );
    }

    private static void validatePurpose(Set<Purpose> purposes) {
        if (purposes.isEmpty()) {
            throw new InvalidPurposeException();
        }
    }
}

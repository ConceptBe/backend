package kr.co.conceptbe.notification_setting.domain.vo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.stream.Collectors;
import kr.co.conceptbe.branch.domain.Branch;
import kr.co.conceptbe.notification_setting.domain.IdeaNotificationSetting;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Getter
@NoArgsConstructor(access = PROTECTED)
public class NotificationSettingBranches {

    private static final int NOTIFICATION_SETTING_PURPOSES_SIZE_LOWER_BOUND_INCLUSIVE = 1;

    @OneToMany(
            mappedBy = "notificationSetting",
            orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE}
    )
    private List<NotificationSettingBranch> notificationSettingBranches;

    private NotificationSettingBranches(List<NotificationSettingBranch> notificationSettingBranches) {
        this.notificationSettingBranches = notificationSettingBranches;
    }

    public static NotificationSettingBranches of(
            IdeaNotificationSetting ideaNotificationSetting,
            List<Branch> branches
    ) {
        List<NotificationSettingBranch> notificationSettingBranches = branches.stream()
                .map(branch -> NotificationSettingBranch.of(ideaNotificationSetting, branch))
                .toList();

        return new NotificationSettingBranches(notificationSettingBranches);
    }

    public void update(IdeaNotificationSetting ideaNotificationSetting, List<Branch> branches) {
        notificationSettingBranches.clear();
        notificationSettingBranches.addAll(
            branches.stream()
                .map(branch -> NotificationSettingBranch.of(ideaNotificationSetting, branch))
                .toList()
        );
    }
}

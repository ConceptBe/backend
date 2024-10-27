package kr.co.conceptbe.notification_setting.domain.vo;

import jakarta.persistence.*;
import kr.co.conceptbe.branch.domain.Branch;
import kr.co.conceptbe.notification_setting.domain.IdeaNotificationSetting;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class NotificationSettingBranch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "notification_setting_id")
    private IdeaNotificationSetting notificationSetting;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    private NotificationSettingBranch(IdeaNotificationSetting notificationSetting, Branch branch) {
        this.notificationSetting = notificationSetting;
        this.branch = branch;
    }

    public static NotificationSettingBranch of(IdeaNotificationSetting notificationSetting, Branch branch) {
        return new NotificationSettingBranch(notificationSetting, branch);
    }

}

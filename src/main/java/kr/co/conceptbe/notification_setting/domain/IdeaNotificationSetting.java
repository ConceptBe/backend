package kr.co.conceptbe.notification_setting.domain;

import jakarta.persistence.*;
import java.util.List;
import kr.co.conceptbe.branch.domain.Branch;
import kr.co.conceptbe.common.entity.base.BaseTimeEntity;
import kr.co.conceptbe.idea.domain.vo.CooperationWay;
import kr.co.conceptbe.notification_setting.domain.vo.NotificationSettingBranches;
import kr.co.conceptbe.notification_setting.domain.vo.NotificationSettingPurposes;
import kr.co.conceptbe.purpose.domain.Purpose;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_notification_setting_member_id",
                columnNames = {"member_id"}
        )
})
public class IdeaNotificationSetting extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Embedded
    private NotificationSettingBranches branches;

    @Embedded
    private NotificationSettingPurposes purposes;

    @Enumerated(EnumType.STRING)
    private CooperationWay cooperationWay;

    public IdeaNotificationSetting(Long memberId, CooperationWay cooperationWay) {
        this.memberId = memberId;
        this.cooperationWay = cooperationWay;
    }

    public static IdeaNotificationSetting of(
            Long memberId,
            List<Branch> branches,
            List<Purpose> purposes,
            CooperationWay cooperationWay
    ) {
        IdeaNotificationSetting ideaNotificationSetting = new IdeaNotificationSetting(
                memberId,
                cooperationWay
        );
        ideaNotificationSetting.branches = NotificationSettingBranches.of(ideaNotificationSetting, branches);
        ideaNotificationSetting.purposes = NotificationSettingPurposes.of(ideaNotificationSetting, purposes);

        return ideaNotificationSetting;
    }

    public void update(
            List<Branch> branches,
            List<Purpose> purposes,
            CooperationWay cooperationWay
    ) {
        this.branches.update(this, branches);
        this.purposes.update(this, purposes);
        this.cooperationWay = cooperationWay;
    }

}

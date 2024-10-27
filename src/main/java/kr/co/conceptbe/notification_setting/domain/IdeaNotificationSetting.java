package kr.co.conceptbe.notification_setting.domain;

import jakarta.persistence.*;
import java.util.List;
import java.util.Set;
import kr.co.conceptbe.branch.domain.Branch;
import kr.co.conceptbe.branch.exception.InvalidBranchLengthException;
import kr.co.conceptbe.common.entity.base.BaseTimeEntity;
import kr.co.conceptbe.idea.domain.vo.CooperationWay;
import kr.co.conceptbe.notification_setting.domain.vo.NotificationSettingBranches;
import kr.co.conceptbe.notification_setting.domain.vo.NotificationSettingPurposes;
import kr.co.conceptbe.notification_setting.exception.InvalidBranchException;
import kr.co.conceptbe.notification_setting.exception.InvalidPurposeException;
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

    private IdeaNotificationSetting(Long memberId, CooperationWay cooperationWay) {
        this.memberId = memberId;
        this.cooperationWay = cooperationWay;
    }

    public static IdeaNotificationSetting of(
            Long memberId,
            Set<Branch> branches,
            Set<Purpose> purposes,
            CooperationWay cooperationWay
    ) {
        validatePurpose(purposes);
        validateBranch(branches);

        IdeaNotificationSetting ideaNotificationSetting = new IdeaNotificationSetting(
                memberId,
                cooperationWay
        );
        ideaNotificationSetting.branches = NotificationSettingBranches.of(ideaNotificationSetting, branches);
        ideaNotificationSetting.purposes = NotificationSettingPurposes.of(ideaNotificationSetting, purposes);

        return ideaNotificationSetting;
    }

    public void update(
            Set<Branch> branches,
            Set<Purpose> purposes,
            CooperationWay cooperationWay
    ) {
        validatePurpose(purposes);
        validateBranch(branches);

        this.branches.update(this, branches);
        this.purposes.update(this, purposes);
        this.cooperationWay = cooperationWay;
    }

    private static void validatePurpose(Set<Purpose> purposes) {
        if (purposes.isEmpty()) {
            throw new InvalidPurposeException();
        }
    }

    private static void validateBranch(Set<Branch> branches) {
        if (branches.isEmpty() || branches.size() > 10) {
            throw new InvalidBranchException();
        }
    }

}

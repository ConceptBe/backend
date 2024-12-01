package kr.co.conceptbe.notification.domain;

import kr.co.conceptbe.branch.domain.Branch;
import kr.co.conceptbe.idea.domain.Idea;
import kr.co.conceptbe.idea.domain.IdeaBranch;
import kr.co.conceptbe.idea.domain.IdeaPurpose;
import kr.co.conceptbe.notification_setting.domain.IdeaNotificationSetting;
import kr.co.conceptbe.notification_setting.domain.vo.NotificationSettingBranch;
import kr.co.conceptbe.notification_setting.domain.vo.NotificationSettingPurpose;
import kr.co.conceptbe.purpose.domain.Purpose;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class NotificationTrigger {

    public List<IdeaNotification> getNotificationByCreatedIdeaEvent(
            Idea idea,
            List<IdeaNotificationSetting> notificationSettings
    ) {
        return notificationSettings.stream()
                .filter(setting -> !Objects.equals(setting.getMemberId(), idea.getCreator().getId()))
                .filter(notificationSetting -> isSuitToMeIdea(idea, notificationSetting))
                .map(notificationSetting -> IdeaNotification.withIdea(idea))
                .toList();
    }

    private boolean isSuitToMeIdea(Idea idea, IdeaNotificationSetting notificationSetting) {
        return isSuitToMemberBranch(idea.getBranches(), notificationSetting)
                && isSuitToMemberPurpose(idea.getPurposes(), notificationSetting)
                && isSuitToMemberCooperationWay(idea.getCooperationWay(), notificationSetting);
    }

    private boolean isSuitToMemberBranch(
            List<IdeaBranch> branches,
            IdeaNotificationSetting ideaNotificationSetting
    ) {
        List<Branch> notificationBranches = ideaNotificationSetting.getBranches()
                .getNotificationSettingBranches()
                .stream()
                .map(NotificationSettingBranch::getBranch)
                .toList();
        List<Branch> ideaBranches = branches.stream()
                .map(IdeaBranch::getBranch)
                .toList();

        for (Branch notificationBranch : notificationBranches) {
            for (Branch ideaBranch : ideaBranches) {
                if (notificationBranch.isInclude(ideaBranch)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isSuitToMemberPurpose(
            List<IdeaPurpose> purposes,
            IdeaNotificationSetting ideaNotificationSetting
    ) {
        List<Purpose> notificationPurposes = ideaNotificationSetting.getPurposes()
                .getNotificationSettingPurposes()
                .stream()
                .map(NotificationSettingPurpose::getPurpose)
                .toList();

        List<Purpose> ideaPurposes = purposes.stream()
                .map(IdeaPurpose::getPurpose)
                .toList();

        for (Purpose notificationPurpose : notificationPurposes) {
            for (Purpose ideaPurpose : ideaPurposes) {
                if (notificationPurpose.isInclude(ideaPurpose)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isSuitToMemberCooperationWay(
            String cooperationWay,
            IdeaNotificationSetting ideaNotificationSetting
    ) {
        return ideaNotificationSetting.getCooperationWay()
                .isInclude(cooperationWay);
    }

}

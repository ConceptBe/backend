package kr.co.conceptbe.notification.domain;

import jakarta.persistence.*;
import kr.co.conceptbe.common.entity.base.BaseTimeEntity;
import kr.co.conceptbe.idea.domain.Idea;
import kr.co.conceptbe.idea.domain.IdeaBranch;
import kr.co.conceptbe.idea.domain.IdeaPurpose;
import kr.co.conceptbe.notification_setting.domain.IdeaNotificationSetting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
public class IdeaNotification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Long ideaId;

    @Column(nullable = false)
    private String branches;

    @Column(nullable = false)
    private String purposes;

    @Column(nullable = false)
    private String cooperationWay;

    public static IdeaNotification withIdea(Idea idea, IdeaNotificationSetting notificationSetting) {
        Long receiverId = notificationSetting.getMemberId();
        String title = idea.getTitle();
        List<String> branchBadges = idea.getBranchesAfterApplyDepth()
                .entrySet()
                .stream()
                .map(IdeaNotification::getBranchesBadgesEach)
                .flatMap(List::stream)
                .toList();
        List<IdeaPurpose> purposes = idea.getPurposes();
        String cooperationWay = idea.getCooperationWay();

        return new IdeaNotification(
                null,
                receiverId,
                title,
                idea.getId(),
                joinBadges(branchBadges, Function.identity()),
                joinBadges(purposes, purpose -> purpose.getPurpose().getName()),
                cooperationWay
        );
    }

    private static List<String> getBranchesBadgesEach(Map.Entry<IdeaBranch, List<IdeaBranch>> entry) {
        String parentBranchName = entry.getKey()
                .getBranch()
                .getName();
        List<IdeaBranch> childBranches = entry.getValue();

        return childBranches.stream()
                .map(ideaBranch -> ideaBranch.getBranch().getName())
                .map(branchName -> parentBranchName + "-" + branchName)
                .toList();
    }

    private static <T> String joinBadges(List<T> badges, Function<T, String> getter) {
        return badges.stream()
                .map(getter)
                .reduce((a, b) -> a + "," + b)
                .orElse("");
    }

    public List<String> getBranches() {
        return splitBadges(branches);
    }

    public List<String> getPurposes() {
        return splitBadges(purposes);
    }

    private List<String> splitBadges(String badges) {
        String[] badgesEach = badges.split(",");

        return new ArrayList<>(List.of(badgesEach));
    }

}

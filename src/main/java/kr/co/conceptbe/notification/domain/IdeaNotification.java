package kr.co.conceptbe.notification.domain;

import jakarta.persistence.*;
import java.util.Optional;
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

    @Column(nullable = false)
    private boolean isAlreadyRead;

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
                cooperationWay,
                false
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

    public void read(Long memberId, Function<Long, Optional<Idea>> getIdea) {
        if (!memberId.equals(this.memberId)) {
            throw new IllegalArgumentException("다른 계정의 알림을 읽을 수 없습니다.");
        }

        Optional<Idea> idea = getIdea.apply(this.ideaId);
        if (idea.isEmpty()) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }

        isAlreadyRead = true;
    }

}

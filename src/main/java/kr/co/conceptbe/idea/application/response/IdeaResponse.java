package kr.co.conceptbe.idea.application.response;

import java.time.LocalDateTime;
import java.util.List;
import kr.co.conceptbe.branch.domain.Branch;
import kr.co.conceptbe.teamrecruitment.domain.TeamRecruitment;
import kr.co.conceptbe.idea.domain.Idea;
import kr.co.conceptbe.idea.domain.IdeaBranch;
import kr.co.conceptbe.idea.domain.IdeaTeamRecruitment;

public record IdeaResponse(
        Long id,
        String title,
        String introduce,
        int hitsCount,
        int commentsCount,
        int likesCount,
        int bookmarksCount,
        boolean isBookmarked,
        LocalDateTime createdAt,
        MemberResponse memberResponse,
        List<String> branches,
        List<String> teamRecruitments
) {

    public static IdeaResponse ofMember(Idea idea, boolean isBookmarked) {
        return new IdeaResponse(
                idea.getId(),
                idea.getTitle(),
                idea.getIntroduce(),
                idea.getHitsCount(),
                idea.getCommentsCount(),
                idea.getLikesCount(),
                idea.getBookmarksCount(),
                isBookmarked,
                idea.getCreatedAt(),
                MemberResponse.from(idea.getCreator()),
                getBranches(idea.getBranches()),
                getTeamRecruitments(idea.getTeamRecruitments())
        );
    }

    private static List<String> getBranches(List<IdeaBranch> branches) {
        return branches.stream()
                .map(IdeaBranch::getBranch)
                .map(Branch::getName)
                .toList();
    }

    private static List<String> getTeamRecruitments(List<IdeaTeamRecruitment> teamRecruitments) {
        return teamRecruitments.stream()
                .map(IdeaTeamRecruitment::getTeamRecruitment)
                .map(TeamRecruitment::getName)
                .toList();
    }

    public static IdeaResponse ofGuest(Idea idea) {
        return new IdeaResponse(
                idea.getId(),
                idea.getTitle(),
                idea.getIntroduce(),
                idea.getHitsCount(),
                idea.getCommentsCount(),
                idea.getLikesCount(),
                idea.getBookmarksCount(),
                false,
                idea.getCreatedAt(),
                MemberResponse.from(idea.getCreator()),
                getBranches(idea.getBranches()),
                getTeamRecruitments(idea.getTeamRecruitments())
        );
    }

}
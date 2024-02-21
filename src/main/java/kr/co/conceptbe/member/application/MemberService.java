package kr.co.conceptbe.member.application;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kr.co.conceptbe.auth.presentation.dto.AuthCredentials;
import kr.co.conceptbe.bookmark.Bookmark;
import kr.co.conceptbe.bookmark.repository.BookmarkRepository;
import kr.co.conceptbe.idea.application.response.IdeaResponse;
import kr.co.conceptbe.idea.domain.Idea;
import kr.co.conceptbe.idea.domain.persistence.IdeaRepository;
import kr.co.conceptbe.member.application.dto.GetMemberProfileResponse;
import kr.co.conceptbe.member.application.dto.MemberIdeaResponse;
import kr.co.conceptbe.member.application.dto.MemberIdeaResponseOption;
import kr.co.conceptbe.member.domain.Member;
import kr.co.conceptbe.member.persistence.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final IdeaRepository ideaRepository;
    private final BookmarkRepository bookmarkRepository;

    public boolean validateDuplicatedNickName(String nickname) {
        return !memberRepository.existsByNickname(nickname);
    }

    public GetMemberProfileResponse getMemberProfileBy(AuthCredentials authCredentials, Long id) {
        Member member = memberRepository.getById(id);
        return new GetMemberProfileResponse(
            member.getProfileImageUrl(),
            member.getNickname(),
            Objects.equals(authCredentials.id(), id),
            member.getMainSkill().getName(),
            member.getWorkingPlace(),
            member.getWorkingPlace(),
            member.getIntroduce(),
            mapToMemberSkills(member),
            mapToMemberPurposes(member)
        );
    }

    private List<String> mapToMemberPurposes(Member member) {
        return member.getPurposes().stream()
            .map(purpose -> purpose.getPurpose().getName())
            .collect(Collectors.toList());
    }

    private List<String> mapToMemberSkills(Member member) {
        return member.getSkills().stream()
            .map(skill -> skill.getSkillCategory().getName())
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MemberIdeaResponse> findMemberIdeas(AuthCredentials authCredentials, Long id, Pageable pageable) {
        if (isMyMemberIdeas(authCredentials, id)) {
            return ideaRepository.findAllByCreatorIdOrderByCreatedAtDesc(authCredentials.id(), pageable)
                .stream()
                .map(idea -> MemberIdeaResponse.ofMember(idea, MemberIdeaResponseOption.IS_MINE))
                .toList();
        }

        return ideaRepository.findAllByCreatorIdOrderByCreatedAtDesc(id, pageable)
            .stream()
            .map(idea -> {
                if (findGuestBookmarkedIdeaIds(authCredentials).contains(idea.getId())) {
                    return MemberIdeaResponse.ofMember(idea, MemberIdeaResponseOption.IS_BOOKMARKED); 
                }
                return MemberIdeaResponse.ofMember(idea, MemberIdeaResponseOption.IS_NOT_BOOKMARKED);
            }).collect(Collectors.toList());
    }

    private boolean isMyMemberIdeas(AuthCredentials authCredentials, Long id) {
        return Objects.equals(id, authCredentials.id());
    }

    private Set<Long> findGuestBookmarkedIdeaIds(AuthCredentials authCredentials) {
        Member guest = memberRepository.getById(authCredentials.id());
        return guest.getBookmarks().stream()
            .map(bookmark -> bookmark.getIdea().getId())
            .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public List<IdeaResponse> findMemberBookMarks(AuthCredentials authCredentials, Pageable pageable) {
        List<Bookmark> bookmarks = bookmarkRepository.findAllByMemberIdOrderByIdeaCreatedAtDesc(authCredentials.id(), pageable);
        return bookmarks.stream()
            .map(bookmark -> IdeaResponse.ofMember(bookmark.getIdea(), true))
            .toList();
    }
}

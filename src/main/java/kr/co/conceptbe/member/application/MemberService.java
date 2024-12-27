package kr.co.conceptbe.member.application;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import kr.co.conceptbe.auth.presentation.dto.AuthCredentials;
import kr.co.conceptbe.bookmark.Bookmark;
import kr.co.conceptbe.bookmark.repository.BookmarkRepository;
import kr.co.conceptbe.comment.Comment;
import kr.co.conceptbe.comment.repository.CommentRepository;
import kr.co.conceptbe.idea.application.response.IdeaResponse;
import kr.co.conceptbe.idea.domain.persistence.HitRepository;
import kr.co.conceptbe.idea.domain.persistence.IdeaRepository;
import kr.co.conceptbe.member.application.dto.GetMemberProfileResponse;
import kr.co.conceptbe.member.application.dto.MemberIdeaResponse;
import kr.co.conceptbe.member.application.dto.MemberIdeaResponseOption;
import kr.co.conceptbe.member.application.dto.MemberProfileSkillResponse;
import kr.co.conceptbe.member.application.dto.UpdateMemberProfileRequest;
import kr.co.conceptbe.member.domain.Member;
import kr.co.conceptbe.member.domain.MemberPurpose;
import kr.co.conceptbe.member.domain.vo.Nickname;
import kr.co.conceptbe.member.exception.NotFoundMemberException;
import kr.co.conceptbe.member.exception.NotOwnerException;
import kr.co.conceptbe.member.persistence.MemberRepository;
import kr.co.conceptbe.purpose.domain.Purpose;
import kr.co.conceptbe.purpose.domain.persistence.PurposeRepository;
import kr.co.conceptbe.region.domain.presentation.RegionRepository;
import kr.co.conceptbe.skill.domain.SkillCategory;
import kr.co.conceptbe.skill.domain.SkillCategoryRepository;
import kr.co.conceptbe.skill.domain.SkillLevel;
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
    private final SkillCategoryRepository skillCategoryRepository;
    private final PurposeRepository purposeRepository;
    private final CommentRepository commentRepository;
    private final RegionRepository regionRepository;
    private final HitRepository hitRepository;

    @Transactional(readOnly = true)
    public boolean validateDuplicatedNickName(String nickname) {
        return !memberRepository.existsByNickname(Nickname.from(nickname));
    }

    @Transactional(readOnly = true)
    public GetMemberProfileResponse getMemberProfileBy(AuthCredentials authCredentials, Long id) {
        Member authMember = memberRepository.getById(authCredentials.id());
        Member member = memberRepository.findById(id).orElseThrow(() -> new NotFoundMemberException(id));
        return new GetMemberProfileResponse(
            member.getProfileImageUrl(),
            member.getNickname(),
            member.getEmail(),
            Objects.equals(authMember.getId(), id),
            member.getMainSkill().getName(),
            member.getLivingPlace().getName(),
            member.getWorkingPlace(),
            member.getIntroduce(),
            mapToMemberSkills(member),
            mapToMemberPurposes(member),
            mapToMemberBranches(member)
        );
    }

    private List<String> mapToMemberPurposes(Member member) {
        return member.getPurposes().stream()
            .map(purpose -> purpose.getPurpose().getName())
            .collect(Collectors.toList());
    }

    private List<MemberProfileSkillResponse> mapToMemberSkills(Member member) {
        return member.getSkills().getValues().stream()
            .map(skill -> new MemberProfileSkillResponse(
                skill.getSkillCategory().getId(),
                skill.getSkillCategory().getName(),
                skill.getSkillLevel().getName()
            ))
            .collect(Collectors.toList());
    }

    private List<String> mapToMemberBranches(Member member) {
        return member.getBranches().stream()
                .map(branch -> branch.getBranch().getName())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MemberIdeaResponse> findMemberIdeas(AuthCredentials authCredentials, Long id,
        Pageable pageable) {
        if (isMyMemberIdeas(authCredentials, id)) {
            return ideaRepository.findAllByCreatorIdOrderByCreatedAtDesc(authCredentials.id(),
                    pageable)
                .stream()
                .map(idea -> MemberIdeaResponse.ofMember(idea, MemberIdeaResponseOption.IS_MINE))
                .toList();
        }

        Set<Long> guestBookmarkedIdeaIds = findGuestBookmarkedIdeaIds(authCredentials);
        return ideaRepository.findAllByCreatorIdOrderByCreatedAtDesc(id, pageable).stream()
            .map(idea -> {
                if (guestBookmarkedIdeaIds.contains(idea.getId())) {
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
    public List<IdeaResponse> findMemberBookMarks(AuthCredentials authCredentials,
        Pageable pageable) {
        List<Bookmark> bookmarks = bookmarkRepository.findAllByMemberIdOrderByIdeaCreatedAtDesc(
            authCredentials.id(), pageable);
        return bookmarks.stream()
            .map(bookmark -> IdeaResponse.ofMember(bookmark.getIdea(), true))
            .toList();
    }

    public void updateMemberProfile(
        UpdateMemberProfileRequest updateMemberProfileRequest,
        AuthCredentials authCredentials,
        Long id
    ) {
        if (!Objects.equals(authCredentials.id(), id)) {
            throw new NotOwnerException(authCredentials.id());
        }

        Member member = memberRepository.getById(authCredentials.id());
        SkillCategory mainSkill = skillCategoryRepository.getById(
            updateMemberProfileRequest.mainSkillId());
        member.updateProfile(
            updateMemberProfileRequest.nickname(),
            mainSkill,
            updateMemberProfileRequest.profileImageUrl(),
            regionRepository.getById(updateMemberProfileRequest.livingPlaceId()),
            updateMemberProfileRequest.workingPlace(),
            updateMemberProfileRequest.introduction()
        );

        List<SkillCategory> skillCategories = mapToSkillCategories(updateMemberProfileRequest);
        List<SkillLevel> skillLevels = mapToSkillLevels(updateMemberProfileRequest);
        member.updateSkills(member, skillCategories, skillLevels);

        List<MemberPurpose> memberPurposes = mapToMemberPurposes(updateMemberProfileRequest,
            member);
        member.updateJoinPurposes(memberPurposes);
    }

    private List<SkillCategory> mapToSkillCategories(
        UpdateMemberProfileRequest updateMemberProfileRequest
    ) {
        return updateMemberProfileRequest.skills().stream()
            .map(skillRequest -> skillCategoryRepository.getById(skillRequest.skillId()))
            .toList();
    }

    private static List<SkillLevel> mapToSkillLevels(
        UpdateMemberProfileRequest updateMemberProfileRequest
    ) {
        return updateMemberProfileRequest.skills().stream()
            .map(skillRequest -> SkillLevel.from(skillRequest.level()))
            .toList();
    }

    private List<MemberPurpose> mapToMemberPurposes(
        UpdateMemberProfileRequest updateMemberProfileRequest, Member member) {
        return updateMemberProfileRequest.joinPurposes()
            .stream()
            .map(purposeId -> {
                Purpose purpose = purposeRepository.getById(purposeId);
                return new MemberPurpose(member, purpose);
            })
            .toList();
    }

    public void deleteMember(AuthCredentials authCredentials, Long id) {
        if (!Objects.equals(authCredentials.id(), id)) {
            throw new NotOwnerException(authCredentials.id());
        }

        Member member = memberRepository.getById(authCredentials.id());
        List<Comment> comments = commentRepository
            .findByCreator(member);

        for (Comment comment : comments) {
            comment.deleteWithMember();
        }
        hitRepository.deleteByMemberId(member.getId());
        memberRepository.delete(member);
    }

    public void deleteMemberProfileImage(AuthCredentials authCredentials, Long id) {
        if (!Objects.equals(authCredentials.id(), id)) {
            throw new NotOwnerException(authCredentials.id());
        }

        Member member = memberRepository.getById(authCredentials.id());
        member.deleteProfileImage();
    }
}

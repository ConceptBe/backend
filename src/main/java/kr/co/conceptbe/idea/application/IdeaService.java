package kr.co.conceptbe.idea.application;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import kr.co.conceptbe.bookmark.Bookmark;
import kr.co.conceptbe.comment.dto.CommentParentResponse;
import kr.co.conceptbe.common.entity.domain.persistence.BranchRepository;
import kr.co.conceptbe.common.entity.domain.persistence.PurposeRepository;
import kr.co.conceptbe.common.entity.domain.persistence.TeamRecruitmentRepository;
import kr.co.conceptbe.idea.IdeaLikeID;
import kr.co.conceptbe.idea.domain.Idea;
import kr.co.conceptbe.idea.domain.IdeaLike;
import kr.co.conceptbe.idea.domain.persistence.IdeaLikesRepository;
import kr.co.conceptbe.idea.domain.persistence.IdeaRepository;
import kr.co.conceptbe.idea.dto.IdeaDetailResponse;
import kr.co.conceptbe.idea.presentation.dto.response.BestIdeaResponse;
import kr.co.conceptbe.idea.presentation.dto.request.IdeaRequest;
import kr.co.conceptbe.idea.presentation.dto.response.IdeaResponse;
import kr.co.conceptbe.member.Member;
import kr.co.conceptbe.member.MemberRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class IdeaService {

    private final BranchRepository branchRepository;
    private final PurposeRepository purposeRepository;
    private final TeamRecruitmentRepository teamRecruitmentRepository;
    private final IdeaRepository ideaRepository;
    private final MemberRepository memberRepository;
    private final IdeaLikesRepository ideaLikesRepository;

    public Long save(Member member, IdeaRequest request) {
        Idea idea = Idea.of(
                request.title(),
                request.introduce(),
                request.cooperationWay(),
                request.recruitmentPlace(),
                member,
                branchRepository.findByIdIn(request.branchIds()),
                purposeRepository.findByIdIn(request.purposeIds()),
                teamRecruitmentRepository.findByIdIn(request.teamRecruitmentIds())
        );

        return ideaRepository.save(idea).getId();
    }

    @Transactional(readOnly = true)
    public List<BestIdeaResponse> findAllBestIdea() {
        return ideaRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Idea::getLikesCount).reversed())
                .map(BestIdeaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<IdeaResponse> findAll(Member member) {
        Set<Idea> ideasBookmarkedByMember = getIdeasBookmarkedByMember(member);

        return ideaRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(idea -> IdeaResponse.of(idea, ideasBookmarkedByMember.contains(idea)))
                .toList();
    }

    private Set<Idea> getIdeasBookmarkedByMember(Member member) {
        return member.getBookmarks()
                .stream()
                .map(Bookmark::getIdea)
                .collect(Collectors.toSet());
    }

    public IdeaDetailResponse getDetailIdeaResponse(Long ideaId) {
        Idea idea = ideaRepository.getById(ideaId);
        List<CommentParentResponse> commentParentResponses = idea.getComments()
            .stream()
            .map(CommentParentResponse::of)
            .toList();

        return IdeaDetailResponse.of(idea, commentParentResponses);
    }

    public Long likesIdea(Long ideaId, Long memberId) {
        Idea idea = ideaRepository.getById(ideaId);
        // TODO
        // Token 통해서 유저 id 가져올 시 수정될 예정
        Member member = memberRepository.getById(memberId);

        IdeaLikeID ideaLikeID = new IdeaLikeID(memberId, ideaId);
        Optional<IdeaLike> optionalIdeaLike = ideaLikesRepository.findById(ideaLikeID);
        if(optionalIdeaLike.isPresent()) {
            ideaLikesRepository.deleteById(ideaLikeID);
        } else {
            IdeaLike ideaLike = new IdeaLike(member, idea);
            ideaLikesRepository.save(ideaLike);
            idea.addIdeaLikes(ideaLike);
        }

        return idea.getId();
    }
}

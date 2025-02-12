package kr.co.conceptbe.idea.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import kr.co.conceptbe.branch.domain.Branch;
import kr.co.conceptbe.branch.domain.persistense.BranchRepository;
import kr.co.conceptbe.idea.application.request.FilteringRequest;
import kr.co.conceptbe.idea.domain.Idea;
import kr.co.conceptbe.idea.domain.IdeaLike;
import kr.co.conceptbe.idea.domain.persistence.IdeaLikesRepository;
import kr.co.conceptbe.idea.domain.persistence.IdeaRepository;
import kr.co.conceptbe.idea.fixture.IdeaFixture;
import kr.co.conceptbe.member.domain.Member;
import kr.co.conceptbe.member.fixture.MemberFixture;
import kr.co.conceptbe.member.persistence.MemberRepository;
import kr.co.conceptbe.purpose.domain.Purpose;
import kr.co.conceptbe.purpose.domain.persistence.PurposeRepository;
import kr.co.conceptbe.region.domain.Region;
import kr.co.conceptbe.region.domain.presentation.RegionRepository;
import kr.co.conceptbe.skill.domain.SkillCategory;
import kr.co.conceptbe.skill.domain.SkillCategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class IdeaRepositoryTest {

    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private SkillCategoryRepository skillCategoryRepository;
    @Autowired
    private PurposeRepository purposeRepository;
    @Autowired
    private IdeaRepository ideaRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private IdeaLikesRepository ideaLikesRepository;

    @Test
    void Query_DSL_을_적용한_Repository_Method_들이_정상적으로_동작하는지_확인한다() {
        // given
        Region region = regionRepository.save(Region.from("BUSAN"));
        Member member = memberRepository.save(MemberFixture.createMember(region));
        Branch branch = branchRepository.save(Branch.from("branch"));
        Purpose purpose = purposeRepository.save(Purpose.from("purpose"));
        SkillCategory skillCategory = skillCategoryRepository.save(new SkillCategory("skill"));
        Idea idea = IdeaFixture.createIdea(
            region, List.of(branch), List.of(purpose), List.of(skillCategory), member
        );
        ideaRepository.save(idea);
        PageRequest pageable = PageRequest.of(0, 1);
        FilteringRequest filteringRequest = new FilteringRequest(
            List.of(branch.getId()),
            null,
            null,
            null,
            null
        );

        // when
        List<Idea> resultOfLikesDesc = ideaRepository.findAllByOrderByCreatedAtDesc(
            filteringRequest, pageable);
        List<Idea> resultOfAllIdeas = ideaRepository.findAllByOrderByLikesDesc(filteringRequest,
            pageable);

        // then
        assertThat(resultOfLikesDesc).hasSize(1);
        assertThat(resultOfAllIdeas).hasSize(1);
    }

    @Test
    void 최근_게시글_조회에_페이지네이션을_적용한다() {
        // given
        Region region = regionRepository.save(Region.from("BUSAN"));
        Member member = memberRepository.save(MemberFixture.createMember(region));
        Branch branch = branchRepository.save(Branch.from("branch"));
        Purpose purpose = purposeRepository.save(Purpose.from("purpose"));
        SkillCategory skillCategory = skillCategoryRepository.save(new SkillCategory("skill"));
        Idea notInquiry = ideaRepository.save(IdeaFixture.createIdea(
            region, List.of(branch), List.of(purpose),
            List.of(skillCategory), member
        ));
        Idea result = ideaRepository.save(IdeaFixture.createIdea(
            region, List.of(branch), List.of(purpose),
            List.of(skillCategory), member
        ));

        // when
        List<Idea> queryResults = ideaRepository.findAllByOrderByCreatedAtDesc(
            new FilteringRequest(
                null, null, null, null, null
            ),
            PageRequest.of(0, 1)
        );

        // then
        assertThat(queryResults).hasSize(1)
            .usingRecursiveComparison()
            .isEqualTo(List.of(result));
    }

    @Test
    void 최근_게시글_조회에_필터링을_적용한다() {
        // given
        Region region = regionRepository.save(Region.from("BUSAN"));
        Member member = memberRepository.save(MemberFixture.createMember(region));
        Branch branch1 = branchRepository.save(Branch.from("branch1"));
        Branch branch2 = branchRepository.save(Branch.from("branch2"));
        Branch branch3 = branchRepository.save(Branch.from("branch3"));
        Purpose purpose = purposeRepository.save(Purpose.from("purpose"));
        SkillCategory skillCategory1 = skillCategoryRepository.save(new SkillCategory("skill1"));
        SkillCategory skillCategory2 = skillCategoryRepository.save(new SkillCategory("skill2"));
        SkillCategory skillCategory3 = skillCategoryRepository.save(new SkillCategory("skill3"));
        Idea result2 = ideaRepository.save(IdeaFixture.createIdea(
            region, List.of(branch1, branch2), List.of(purpose),
            List.of(skillCategory1, skillCategory3), member
        ));
        Idea result1 = ideaRepository.save(IdeaFixture.createIdea(
            region, List.of(branch1, branch3), List.of(purpose),
            List.of(skillCategory1, skillCategory2), member
        ));
        Idea notInquiry1 = ideaRepository.save(IdeaFixture.createIdea(
            region, List.of(branch2), List.of(purpose),
            List.of(skillCategory1, skillCategory2), member
        ));
        Idea notInquiry2 = ideaRepository.save(IdeaFixture.createIdea(
            region, List.of(branch1, branch3), List.of(purpose),
            List.of(skillCategory2), member
        ));

        // when
        List<Idea> queryResults = ideaRepository.findAllByOrderByCreatedAtDesc(
            new FilteringRequest(
                List.of(branch1.getId(), branch3.getId()),
                null, null, null,
                List.of(skillCategory1.getId(), skillCategory3.getId())
            ),
            PageRequest.of(0, 4)
        );

        // then
        assertThat(queryResults).hasSize(2)
            .usingRecursiveComparison()
            .isEqualTo(List.of(result1, result2));
    }

    @Test
    void 인기_게시글_조회에_페이지네이션을_적용한다() {
        // given
        Region region = regionRepository.save(Region.from("BUSAN"));
        Member member = memberRepository.save(MemberFixture.createMember(region));
        Branch branch = branchRepository.save(Branch.from("branch"));
        Purpose purpose = purposeRepository.save(Purpose.from("purpose"));
        SkillCategory skillCategory = skillCategoryRepository.save(new SkillCategory("skill"));
        Idea notInquiry = ideaRepository.save(IdeaFixture.createIdea(
            region, List.of(branch), List.of(purpose),
            List.of(skillCategory), member
        ));
        Idea result = ideaRepository.save(IdeaFixture.createIdea(
            region, List.of(branch), List.of(purpose),
            List.of(skillCategory), member
        ));
        ideaLikesRepository.save(IdeaLike.of(member, result));

        // when
        List<Idea> queryResults = ideaRepository.findAllByOrderByLikesDesc(
            new FilteringRequest(
                null, null, null, null, null
            ),
            PageRequest.of(0, 1)
        );

        // then
        assertThat(queryResults).hasSize(1)
            .usingRecursiveComparison()
            .isEqualTo(List.of(result));
    }

    @Test
    void 인기_게시글_조회에_필터링을_적용한다() {
        // given
        Region region = regionRepository.save(Region.from("BUSAN"));
        Member member = memberRepository.save(MemberFixture.createMember(region));
        Branch branch1 = branchRepository.save(Branch.from("branch1"));
        Branch branch2 = branchRepository.save(Branch.from("branch2"));
        Branch branch3 = branchRepository.save(Branch.from("branch3"));
        Purpose purpose = purposeRepository.save(Purpose.from("purpose"));
        SkillCategory skillCategory1 = skillCategoryRepository.save(new SkillCategory("skill1"));
        SkillCategory skillCategory2 = skillCategoryRepository.save(new SkillCategory("skill2"));
        SkillCategory skillCategory3 = skillCategoryRepository.save(new SkillCategory("skill3"));
        Idea result2 = ideaRepository.save(IdeaFixture.createIdea(
            region, List.of(branch1, branch2), List.of(purpose),
            List.of(skillCategory1, skillCategory3), member
        ));
        Idea result1 = ideaRepository.save(IdeaFixture.createIdea(
            region, List.of(branch1, branch3), List.of(purpose),
            List.of(skillCategory1, skillCategory2), member
        ));
        ideaLikesRepository.save(IdeaLike.of(member, result1));
        Idea notInquiry1 = ideaRepository.save(IdeaFixture.createIdea(
            region, List.of(branch2), List.of(purpose),
            List.of(skillCategory1, skillCategory2), member
        ));
        Idea notInquiry2 = ideaRepository.save(IdeaFixture.createIdea(
            region, List.of(branch1, branch3), List.of(purpose),
            List.of(skillCategory2), member
        ));

        // when
        List<Idea> queryResults = ideaRepository.findAllByOrderByLikesDesc(
            new FilteringRequest(
                List.of(branch1.getId(), branch3.getId()),
                null, null, null,
                List.of(skillCategory1.getId(), skillCategory3.getId())
            ),
            PageRequest.of(0, 4)
        );

        // then
        assertThat(queryResults).hasSize(2)
            .usingRecursiveComparison()
            .isEqualTo(List.of(result1, result2));
    }

    @Test
    void 인기_게시글_조회_시_최근_게시글이면서_좋아요_개수가_많은_것들을_가져온다() {
        // given
        Region region = regionRepository.save(Region.from("BUSAN"));
        Member member = memberRepository.save(MemberFixture.createMember(region));
        Branch branch1 = branchRepository.save(Branch.from("branch1"));
        Purpose purpose = purposeRepository.save(Purpose.from("purpose"));
        Idea result1 = ideaRepository.save(IdeaFixture.createIdea(
            region, List.of(branch1), List.of(purpose), List.of(), member
        ));
        Idea result4 = ideaRepository.save(IdeaFixture.createIdea(
            region, List.of(branch1), List.of(purpose), List.of(), member
        ));
        Idea result3 = ideaRepository.save(IdeaFixture.createIdea(
            region, List.of(branch1), List.of(purpose), List.of(), member
        ));
        Idea result2 = ideaRepository.save(IdeaFixture.createIdea(
            region, List.of(branch1), List.of(purpose), List.of(), member
        ));
        ideaLikesRepository.save(
            IdeaLike.createAssociatedWithIdeaAndMember(result1, member));

        // when
        List<Idea> queryResults = ideaRepository.findAllByOrderByLikesDesc(
            new FilteringRequest(
                List.of(branch1.getId()), List.of(purpose.getId()), null, null, null
            ),
            PageRequest.of(0, 5)
        );

        // then
        assertThat(queryResults).hasSize(4)
            .usingRecursiveComparison()
            .isEqualTo(List.of(result1, result2, result3, result4));
    }

    @Test
    void Filtering_조건으로_상관없음을_선택시에_상관없음_온라인_오프라인_모두_조회한다() {
        // given
        Region region = regionRepository.save(Region.from("BUSAN"));
        Member member = memberRepository.save(MemberFixture.createMember(region));
        Branch branch = branchRepository.save(Branch.from("branch"));
        Purpose purpose = purposeRepository.save(Purpose.from("purpose"));
        SkillCategory skillCategory = skillCategoryRepository.save(new SkillCategory("skill"));
        Idea result3 = ideaRepository.save(IdeaFixture.createIdeaByCooperationWay(
            "상관없음", region, List.of(branch), List.of(purpose),
            List.of(skillCategory), member
        ));
        Idea result2 = ideaRepository.save(IdeaFixture.createIdeaByCooperationWay(
            "오프라인", region, List.of(branch), List.of(purpose),
            List.of(skillCategory), member
        ));
        Idea result1 = ideaRepository.save(IdeaFixture.createIdeaByCooperationWay(
            "온라인", region, List.of(branch), List.of(purpose),
            List.of(skillCategory), member
        ));

        // when
        List<Idea> queryResults = ideaRepository.findAllByOrderByCreatedAtDesc(
            new FilteringRequest(
                null, null, "상관없음", null, null
            ),
            PageRequest.of(0, 3)
        );

        // then
        assertThat(queryResults).hasSize(3)
            .usingRecursiveComparison()
            .isEqualTo(List.of(result1, result2, result3));
    }

    @Test
    void Filtering_조건으로_협업방식을_선택하여_조회한다() {
        // given
        Region region = regionRepository.save(Region.from("BUSAN"));
        Member member = memberRepository.save(MemberFixture.createMember(region));
        Branch branch = branchRepository.save(Branch.from("branch"));
        Purpose purpose = purposeRepository.save(Purpose.from("purpose"));
        SkillCategory skillCategory = skillCategoryRepository.save(new SkillCategory("skill"));
        Idea result3 = ideaRepository.save(IdeaFixture.createIdeaByCooperationWay(
            "상관없음", region, List.of(branch), List.of(purpose),
            List.of(skillCategory), member
        ));
        Idea result2 = ideaRepository.save(IdeaFixture.createIdeaByCooperationWay(
            "오프라인", region, List.of(branch), List.of(purpose),
            List.of(skillCategory), member
        ));
        Idea result1 = ideaRepository.save(IdeaFixture.createIdeaByCooperationWay(
            "온라인", region, List.of(branch), List.of(purpose),
            List.of(skillCategory), member
        ));

        // when
        List<Idea> queryResults1 = ideaRepository.findAllByOrderByCreatedAtDesc(
            new FilteringRequest(
                null, null, "온라인", null, null
            ),
            PageRequest.of(0, 4)
        );
        List<Idea> queryResults2 = ideaRepository.findAllByOrderByCreatedAtDesc(
            new FilteringRequest(
                null, null, "오프라인", null, null
            ),
            PageRequest.of(0, 4)
        );

        // then
        assertAll(
            () -> assertThat(queryResults1).hasSize(2)
                .usingRecursiveComparison()
                .isEqualTo(List.of(result1, result3)),
            () -> assertThat(queryResults2).hasSize(2)
                .usingRecursiveComparison()
                .isEqualTo(List.of(result2, result3))
        );
    }

}

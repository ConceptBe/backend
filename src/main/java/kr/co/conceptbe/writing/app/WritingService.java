package kr.co.conceptbe.writing.app;

import kr.co.conceptbe.branch.domain.persistense.BranchRepository;
import kr.co.conceptbe.purpose.domain.persistence.PurposeRepository;
import kr.co.conceptbe.region.domain.presentation.RegionRepository;
import kr.co.conceptbe.skill.domain.SkillCategoryRepository;
import kr.co.conceptbe.writing.app.dto.WritingResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WritingService {

    private final SkillCategoryRepository skillCategoryRepository;
    private final PurposeRepository purposeRepository;
    private final BranchRepository branchRepository;
    private final RegionRepository regionRepository;

    public WritingResponses getWritings() {
        return WritingResponses.of(
                regionRepository.findAll(),
                purposeRepository.findAll(),
                branchRepository.findAll(),
                skillCategoryRepository.findAll()
        );
    }

}

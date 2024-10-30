package kr.co.conceptbe.branch.domain.persistense;

import java.util.List;
import kr.co.conceptbe.branch.domain.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    List<Branch> findByIdIn(List<Long> ids);

    List<Branch> findByParentBranchIsNull();

}

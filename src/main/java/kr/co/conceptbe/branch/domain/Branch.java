package kr.co.conceptbe.branch.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.*;

import java.util.Objects;

import kr.co.conceptbe.branch.exception.EmptyBranchNameException;
import kr.co.conceptbe.branch.exception.InvalidBranchLengthException;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Branch {

    private static final int BRANCH_LENGTH_LOWER_BOUND_INCLUSIVE = 1;
    private static final String CHARACTERS_INCLUDE_DOES_NOT_MATTER_OPTION_EXCEPT_COOPERATION_WAY = "전체";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_branch_id")
    private Branch parentBranch;

    @Column(nullable = false)
    private String name;

    private Branch(String name) {
        this.name = name;
    }

    public static Branch from(String name) {
        validateNull(name);
        validateLength(name);

        return new Branch(name);
    }

    private static void validateNull(String name) {
        if (Objects.nonNull(name)) {
            return;
        }

        throw new EmptyBranchNameException();
    }

    private static void validateLength(String name) {
        if (BRANCH_LENGTH_LOWER_BOUND_INCLUSIVE <= name.length()) {
            return;
        }

        throw new InvalidBranchLengthException();
    }

    public boolean isParentBranch() {
        return Objects.isNull(parentBranch) || id.equals(parentBranch.getId());
    }

    public boolean isChildBranch(Branch branch) {
        return !isParentBranch() && branch.id.equals(parentBranch.id);
    }

    public boolean isInclude(Branch branch) {
        if (branch.name.contains(CHARACTERS_INCLUDE_DOES_NOT_MATTER_OPTION_EXCEPT_COOPERATION_WAY)) {
            return true;
        }

        return name.equals(branch.getName());
    }

}

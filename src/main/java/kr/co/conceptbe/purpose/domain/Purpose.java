package kr.co.conceptbe.purpose.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

import kr.co.conceptbe.purpose.exception.EmptyPurposeNameException;
import kr.co.conceptbe.purpose.exception.InvalidPurposeLengthException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
public class Purpose {

    private static final String CHARACTERS_INCLUDE_DOES_NOT_MATTER_OPTION_EXCEPT_COOPERATION_WAY = "전체";
    private static final int PURPOSE_LENGTH_LOWER_BOUND_INCLUSIVE = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Purpose(String name) {
        this.name = name;
    }

    public static Purpose from(String name) {
        validateNull(name);
        validateLength(name);

        return new Purpose(name);
    }

    private static void validateNull(String purpose) {
        if (Objects.nonNull(purpose)) {
            return;
        }

        throw new EmptyPurposeNameException();
    }

    private static void validateLength(String purpose) {
        if (!purpose.isEmpty()) {
            return;
        }

        throw new InvalidPurposeLengthException();
    }

    public boolean isInclude(Purpose purpose) {
        if (purpose.name.contains(CHARACTERS_INCLUDE_DOES_NOT_MATTER_OPTION_EXCEPT_COOPERATION_WAY)) {
            return true;
        }

        return name.equals(purpose.getName());
    }

}

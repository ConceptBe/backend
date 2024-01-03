package kr.co.conceptbe.member;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;

import kr.co.conceptbe.auth.exception.TokenInvalidException;
import kr.co.conceptbe.bookmark.Bookmark;
import kr.co.conceptbe.common.entity.base.BaseTimeEntity;
import kr.co.conceptbe.idea.domain.Idea;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "member",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "oauth_id_unique",
            columnNames = {
                "oauth_server_id",
                "oauth_server_type"
            }
        ),
    }
)
public class Member extends BaseTimeEntity {

    //TODO 패키지 변경

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private OauthId oauthId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String profileImageUrl;

    @Column(nullable = false)
    private String email;

    @Column(nullable = true)
    private String introduce;

    @Column(nullable = true)
    private String workingPlace;

    @Column(nullable = true)
    private String livingPlace;

    @OneToMany(mappedBy = "creator")
    private final List<Idea> createdIdea = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private final List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private final List<MemberSkillCategory> skills = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private final List<MemberBranch> branches = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private final List<MemberPurpose> purposes = new ArrayList<>();

    public Member(
            OauthId oauthId,
            String nickname,
            String profileImageUrl,
            String email,
            String introduce,
            String workingPlace
    ) {
        this.oauthId = oauthId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.email = email;
        this.introduce = introduce;
        this.workingPlace = workingPlace;
    }

    public static void validateMember(Long tokenMemberId, Long validateId) {
        if(tokenMemberId.equals(validateId)) {
            return;
        }
        throw new TokenInvalidException();
    }
}

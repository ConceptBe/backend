package kr.co.conceptbe.comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import kr.co.conceptbe.common.entity.base.BaseTimeEntity;
import kr.co.conceptbe.idea.domain.Idea;
import kr.co.conceptbe.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment parentComment;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member creator;

    @ManyToOne
    @JoinColumn(name = "idea_id")
    private Idea idea;

    @OneToMany(mappedBy = "comment")
    private List<CommentLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> comments = new ArrayList<>();

    public Comment(String content, Comment parentComment, Member creator, Idea idea) {
        this.content = content;
        this.parentComment = parentComment;
        this.creator = creator;
        this.idea = idea;
        this.likes = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public int getLikesCount() { return likes.size(); }

    public int getCommentsCount() { return comments.size(); }
}

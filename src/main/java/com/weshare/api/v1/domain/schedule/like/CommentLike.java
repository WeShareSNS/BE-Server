package com.weshare.api.v1.domain.schedule.like;

import com.weshare.api.v1.domain.BaseTimeEntity;
import com.weshare.api.v1.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "comment_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLike extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_like_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liker_id")
    private User liker;
    @Column(name = "comment_id", nullable = false)
    private Long commentId;

    @Builder
    private CommentLike(User liker, Long commentId) {
        this.liker = liker;
        this.commentId = commentId;
    }
}

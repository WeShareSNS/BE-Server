package com.weshare.api.v1.domain.comment;

import com.weshare.api.v1.domain.BaseTimeEntity;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "schedule_comment")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Builder
    private Comment(String content, User user, Schedule schedule) {
        this.content = content;
        this.user = user;
        this.schedule = schedule;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}

package com.weshare.api.v1.domain.schedule.comment;

import com.weshare.api.v1.domain.BaseTimeEntity;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.ScheduleIdProvider;
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
public class Comment extends BaseTimeEntity implements ScheduleIdProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commenter_id", nullable = false)
    private User commenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Builder
    private Comment(String content, User commenter, Schedule schedule) {
        this.content = content;
        this.commenter = commenter;
        this.schedule = schedule;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public boolean isSameCommenter(User commenter) {
        return this.commenter.equals(commenter);
    }

    public boolean isSameScheduleId(Long scheduleId) {
        return schedule.getId() == scheduleId;
    }

    @Override
    public Long getScheduleId() {
        return schedule.getId();
    }
}

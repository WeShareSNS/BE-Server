package com.weshare.api.v1.domain.schedule.like;

import com.weshare.api.v1.domain.BaseTimeEntity;
import com.weshare.api.v1.domain.schedule.ScheduleIdProvider;
import com.weshare.api.v1.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "schedule_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleLike extends BaseTimeEntity implements ScheduleIdProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_like_id")
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Builder
    public ScheduleLike(User user, Long scheduleId) {
        this.user = user;
        this.scheduleId = scheduleId;
    }

    public boolean isSameLiker(User liker) {
        return user.equals(liker);
    }

    public boolean isSameScheduleId(Long scheduleId) {
        return this.scheduleId.equals(scheduleId);
    }

    @Override
    public Long getScheduleId() {
        return scheduleId;
    }
}

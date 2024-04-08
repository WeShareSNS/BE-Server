package com.weshare.api.v1.domain.schedule.like;

import com.weshare.api.v1.domain.BaseTimeEntity;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.ScheduleIdProvider;
import com.weshare.api.v1.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "schedule_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like extends BaseTimeEntity implements ScheduleIdProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_like_id")
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @Builder
    public Like(User user, Schedule schedule) {
        this.user = user;
        this.schedule = schedule;
    }

    public boolean isSameLiker(User liker) {
        return user.equals(liker);
    }

    public boolean isSameScheduleId(Long scheduleId) {
        return schedule.getId() == scheduleId;
    }

    @Override
    public Long getScheduleId() {
        return schedule.getId();
    }
}

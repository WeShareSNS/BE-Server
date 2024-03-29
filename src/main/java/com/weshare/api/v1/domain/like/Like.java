package com.weshare.api.v1.domain.like;

import com.weshare.api.v1.domain.BaseTimeEntity;
import com.weshare.api.v1.domain.schedule.Schedule;
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
public class Like extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_like_id")
    private Long id;
    @OneToOne
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
}

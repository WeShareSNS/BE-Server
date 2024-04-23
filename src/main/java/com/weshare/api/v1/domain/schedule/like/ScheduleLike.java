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
@Table(name = "schedule_like", indexes = {
        @Index(name = "idx_schedule_liker", columnList = "schedule_id, liker_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleLike extends BaseTimeEntity implements ScheduleIdProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_like_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liker_id")
    private User liker;
    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Builder
    public ScheduleLike(User liker, Long scheduleId) {
        this.liker = liker;
        this.scheduleId = scheduleId;
    }

    public boolean isSameLiker(Long likerId) {
        return liker.isSameId(likerId);
    }

    public boolean isSameScheduleId(Long scheduleId) {
        return this.scheduleId.equals(scheduleId);
    }

    @Override
    public Long getScheduleId() {
        return scheduleId;
    }
}

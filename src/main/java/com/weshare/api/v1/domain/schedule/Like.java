package com.weshare.api.v1.domain.schedule;

import com.weshare.api.v1.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "schedule_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_like_id")
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    @Getter
    private LikeState state;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;
    public Like(User user, LikeState state) {
        this.user = user;
        this.state = state;
    }
    public void updateLike() {
        state = LikeState.LIKE;
        schedule.updateLike(this);
    }

    public void deleteLike() {
        state = LikeState.UNLIKE;
        schedule.deleteLike(this);
    }
}

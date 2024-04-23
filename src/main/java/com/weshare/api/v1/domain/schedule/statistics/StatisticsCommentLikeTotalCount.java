package com.weshare.api.v1.domain.schedule.statistics;

import com.weshare.api.v1.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StatisticsCommentLikeTotalCount extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "comment_id", nullable = false)
    private Long commentId;
    @Column(columnDefinition = "bigint default 0", nullable = false)
    private long likeTotalCount;

    public StatisticsCommentLikeTotalCount(Long commentId, long likeTotalCount) {
        this.commentId = commentId;
        this.likeTotalCount = likeTotalCount;
    }

    public void incrementTotalCount() {
        likeTotalCount += 1;
    }

    public void decrementTotalCount() {
        if (likeTotalCount <= 0) {
            throw new IllegalStateException("총 카운트 수는 음수일 수 없습니다.");
        }
        likeTotalCount -= 1;
    }

    public void syncCommentTotalCount(long totalCount) {
        this.likeTotalCount = totalCount;
    }

}

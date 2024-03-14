package com.weshare.api.v1.repository.schedule;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SchedulePageDto {
    private Long scheduleId;
    @Setter
    private long expense;
    private String username; // 작성자 이름
    private long likesCount; // 좋아요 수
    private long commentsCount; // 댓글 개수

    public SchedulePageDto(Long scheduleId, String username, long likesCount, long commentsCount) {
        this.scheduleId = scheduleId;
        this.username = username;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
    }
}

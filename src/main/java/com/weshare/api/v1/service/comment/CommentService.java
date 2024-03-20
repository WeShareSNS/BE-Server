package com.weshare.api.v1.service.comment;

import com.weshare.api.v1.controller.comment.CreateCommentDto;
import com.weshare.api.v1.domain.comment.Comment;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.exception.ScheduleNotFoundException;
import com.weshare.api.v1.repository.comment.CommentRepository;
import com.weshare.api.v1.repository.schedule.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final ScheduleRepository scheduleRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CreateCommentResponse saveScheduleComment(CreateCommentDto createCommentDto) {
        Schedule findSchedule = scheduleRepository.findById(createCommentDto.scheduleId())
                .orElseThrow(ScheduleNotFoundException::new);

        final Comment comment = createComment(createCommentDto, findSchedule);
        Comment savedComment = commentRepository.save(comment);
        return getCreateCommentResponse(savedComment);
    }

    private static Comment createComment(CreateCommentDto createCommentDto, Schedule findSchedule) {
        return Comment.builder()
                .schedule(findSchedule)
                .user(createCommentDto.user())
                .content(createCommentDto.content())
                .build();
    }

    private CreateCommentResponse getCreateCommentResponse(Comment save) {
        log.info("user={}", save.getUser());

        return new CreateCommentResponse(
                save.getSchedule().getId(),
                save.getUser().getName(),
                save.getContent());
    }

}

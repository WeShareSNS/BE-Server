package com.weshare.api.v1.event.user;

import com.weshare.api.v1.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserEventListener {

    private final UserRepository userRepository;

    @Order // 기본값 우선순위 최하위
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void deletedUserEvent(UserDeletedEvent deletedEvent) {
        log.info("user event 진입");
        userRepository.deleteById(deletedEvent.userId());
    }

}

package com.weshare.api.v1.event.token;

import com.weshare.api.v1.event.user.UserDeletedEvent;
import com.weshare.api.v1.token.RefreshToken;
import com.weshare.api.v1.token.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenEventHandler {
    private final RefreshTokenRepository tokenRepository;

    @Order(2)
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void deletedEvent(UserDeletedEvent deletedEvent) {
        log.info("token event 진입");
        Optional<RefreshToken> findToken = tokenRepository.findByUserId(deletedEvent.userId());
        findToken.ifPresent(tokenRepository::delete);
    }

}

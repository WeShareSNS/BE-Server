package com.weshare.api.v1.token;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.weshare.api.v1.domain.user.QUser;
import com.weshare.api.v1.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.weshare.api.v1.domain.user.QUser.user;
import static com.weshare.api.v1.token.QRefreshToken.refreshToken;


@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Optional<RefreshToken> findTokenByUser(User findUser) {
        return Optional.ofNullable(
                jpaQueryFactory.selectFrom(refreshToken)
                        .innerJoin(refreshToken.user, user).fetchJoin()
                        .where(refreshToken.user.eq(findUser))
                        .fetchOne());
    }

    @Override
    public Optional<User> findUserByToken(String findToken) {
        return Optional.ofNullable(
                jpaQueryFactory.select(user)
                        .from(refreshToken)
                        .join(user)
                        .on(refreshToken.token.eq(findToken))
                        .fetchOne());
    }

    @Override
    public Optional<RefreshToken> findTokenByUserEmail(String userEmail) {
        return Optional.ofNullable(
                jpaQueryFactory.selectFrom(refreshToken)
                        .innerJoin(refreshToken.user, user).fetchJoin()
                        .where(refreshToken.user.email.eq(userEmail))
                        .fetchOne());
    }
}

package com.weShare.api.v1.token;

import com.weShare.api.v1.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  @Query(value = """
          select r from RefreshToken r inner join User u
          on r.user.id = u.id
          where r.user = :user
          """)
  Optional<RefreshToken> findTokenByUser(@Param("user") User user);

  // user 정보 사용안해서 fetch 안해도 괜찮
  @Query(value = """
          select u from RefreshToken r inner join User u
          on r.user.id = u.id
          where r.token = :refreshToken
          """)
  Optional<User> findUserByToken(@Param("refreshToken") String refreshToken);

  @Query(value = """
          select r from RefreshToken r join fetch r.user u
          where u.email = :userEmail 
          """)
  Optional<RefreshToken> findTokenByUserEmail(@Param("userEmail") String userEmail);

}

package com.weShare.api.v1.token;

import com.weShare.api.v1.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<Token, Long> {

  @Query(value = """
          select t from Token t inner join User u
          on t.user.id = u.id
          where t.user = :user
          """)
  Optional<Token> findTokenByUser(User user);

  // user 정보 사용안해서 fetch 안해도 괜찮
  @Query(value = """
          select u from Token t inner join User u
          on t.user.id = u.id
          where t.token = :token
          """)
  Optional<User> findUserByToken(String token);

  @Query(value = """
          select t from Token t inner join User u
          on t.user.id = u.id
          where t.user.email = :userEmail 
          """)
  Optional<Token> findTokenByUserEmail(String userEmail);
}

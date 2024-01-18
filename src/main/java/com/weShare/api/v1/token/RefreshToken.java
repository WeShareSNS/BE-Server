package com.weShare.api.v1.token;

import com.weShare.api.v1.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String token;

  @Enumerated(EnumType.STRING)
  private TokenType tokenType = TokenType.BEARER;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Builder
  private RefreshToken(String token, TokenType tokenType, User user) {
    this.token = token;
    this.tokenType = tokenType;
    this.user = user;
  }

  public void updateToken(String token) {
    this.token = token;
  }


}
package com.weshare.api.v1.token;

import com.weshare.api.v1.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

  // user에 값타입으로 들어가야 하는데 User와 생명주기가 다르다고 판단하고 Entity로 처리함
  @OneToOne(
          fetch = FetchType.LAZY, // EAGER로 하면 되는데 우선 LAZY
          cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
          orphanRemoval = true)
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

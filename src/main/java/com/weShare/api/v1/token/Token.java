package com.weShare.api.v1.token;

import com.weShare.api.v1.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(unique = true)
  public String token;

  @Enumerated(EnumType.STRING)
  public TokenType tokenType = TokenType.BEARER;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User user;

  public void updateToken(String token) {
    this.token = token;
  }
}

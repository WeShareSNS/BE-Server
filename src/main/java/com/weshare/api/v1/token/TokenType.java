package com.weshare.api.v1.token;

import lombok.Getter;

public enum TokenType {
  BEARER("Bearer ");

  @Getter
  private final String type;

  TokenType(String type) {
    this.type = type;
  }
}

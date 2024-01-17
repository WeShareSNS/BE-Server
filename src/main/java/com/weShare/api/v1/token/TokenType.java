package com.weShare.api.v1.token;

public enum TokenType {
  BEARER("Bearer ");

  private final String type;

  TokenType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}

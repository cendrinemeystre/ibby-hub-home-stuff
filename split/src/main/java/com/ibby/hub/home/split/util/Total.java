package com.ibby.hub.home.split.util;

public record Total(double person1, double person2) {
  public Total() {
    this(0, 0);
  }
}

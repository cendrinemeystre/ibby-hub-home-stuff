package com.ibby.hub.home.split;

import java.io.IOException;

public class Main {
  static void main(String[] args) throws IOException {
    if (args.length != 3) {
      IO.println("Usage: java -jar playground.jar <fileName> <person1> <person2>");
    }
    Split split = new Split(args[0], args[1], args[2]);
    split.readFile();
  }
}

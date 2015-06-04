package com.center.akka.typedActors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.concurrent.Future;
import akka.dispatch.Futures;
import akka.japi.Option;

public class SquarerImpl implements Squarer {
  public static final Logger log = LoggerFactory.getLogger(SquarerImpl.class);
  private String name;

  public SquarerImpl() {
    this.name = "default";
  }

  public SquarerImpl(String name) {
    this.name = name;
  }


  public void squareDontCare(int i) {
    try {
      for (int j = 0; j < 3; j++) {
        Thread.sleep(1000);
        log.info("squareDontCare is call " + j);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    int sq = i * i; // Nobody cares :(
  }

  public Future<Integer> square(int i) {
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return Futures.successful(Integer.valueOf(i * i));
  }

  public Option<Integer> squareNowPlease(int i) {
    return Option.some(i * i);
  }

  public int squareNow(int i) {
    return i * i;
  }
}

package com.center.akka.scheduler;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class TestActor extends UntypedActor {
  LoggingAdapter log = Logging.getLogger(getContext().system(), this);

  public TestActor() {}

  @Override
  public void onReceive(Object msg) throws Exception {
    log.info("msg : " + msg);
  }

}

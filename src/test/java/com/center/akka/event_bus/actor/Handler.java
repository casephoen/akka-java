package com.center.akka.event_bus.actor;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Handler extends UntypedActor {

  LoggingAdapter log = Logging.getLogger(getContext().system(), this);

  @Override
  public void onReceive(Object msg) throws Exception {
    log.info("Handled Event: " + msg);
  }
}

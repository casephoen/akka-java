package com.center.akka.event_bus.actor;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.center.akka.simple.command.Command;
import com.center.akka.simple.event.Event;

public class Emitter extends UntypedActor {

  LoggingAdapter log = Logging.getLogger(getContext().system(), this);

  @Override
  public void onReceive(Object msg) {

    if (msg instanceof Command) {

      log.info("Emitting Event: " + msg);

      String data = ((Command) msg).getData();

      // 发布Event类型的事件，会被Handler类型处理器处理
      getContext().system().eventStream().publish(new Event(data));
    }
  }


}

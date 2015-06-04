package com.center.akka.parent_child.actor;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ChildActor extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Override
    public void preStart() {
        log.info("Starting");
    }

    @Override
    public void onReceive(Object msg) {
        log.info("Received Event: " + msg);
    }


}
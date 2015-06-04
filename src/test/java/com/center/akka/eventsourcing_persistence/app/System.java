package com.center.akka.eventsourcing_persistence.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.center.akka.eventsourcing_persistence.event.EventHandler;
import com.center.akka.eventsourcing_persistence.persistence.BaseProcessor;
import com.center.akka.simple.command.Command;
import com.center.akka.simple.event.Event;

public class System {

  public static final Logger log = LoggerFactory.getLogger(System.class);

  public static void main(String... args) throws Exception {

    final ActorSystem actorSystem = ActorSystem.create("actor-server");

    final ActorRef handler = actorSystem.actorOf(Props.create(EventHandler.class));
    // 订阅
    actorSystem.eventStream().subscribe(handler, Event.class);

    Thread.sleep(5000);

    final ActorRef actorRef = actorSystem.actorOf(Props.create(BaseProcessor.class), "eventsourcing-processor");
    actorRef.tell(new Command("CMD 1"), null);
    actorRef.tell(new Command("CMD 2"), null);
    actorRef.tell(new Command("CMD 3"), null);
    actorRef.tell("snapshot", null);
    actorRef.tell(new Command("CMD 4"), null);
    actorRef.tell(new Command("CMD 5"), null);
    actorRef.tell("printstate", null);

    Thread.sleep(5000);

    log.debug("Actor System Shutdown Starting...");

    actorSystem.shutdown();
  }
}

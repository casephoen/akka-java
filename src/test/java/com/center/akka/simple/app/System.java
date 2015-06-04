package com.center.akka.simple.app;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.center.akka.simple.actor.SimpleActor;
import com.center.akka.simple.command.Command;

/**
 * tell:异步发送一个消息并立即返回; ask:异步发送一条消息并返回一个 Future代表一个可能的回应 所以总是使用tell更偏向性能,除非必须才用ask
 * 
 * @author lcq
 *
 */
public class System {

  public static final Logger log = LoggerFactory.getLogger(System.class);

  public static void main(String... args) throws Exception {

    final ActorSystem actorSystem = ActorSystem.create("actor-system");

    Thread.sleep(5000);

    final ActorRef actorRef = actorSystem.actorOf(Props.create(SimpleActor.class), "simple-actor");

    actorRef.tell(new Command("CMD 1"), null);
    actorRef.tell(new Command("CMD 2"), null);
    actorRef.tell(new Command("CMD 3"), null);
    actorRef.tell(new Command("CMD 4"), null);
    actorRef.tell(new Command("CMD 5"), null);

    Thread.sleep(5000);

    log.debug("Actor System Shutdown Starting...");

    actorSystem.shutdown();
  }
}

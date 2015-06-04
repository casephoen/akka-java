package com.center.akka.persistent_channel.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.persistence.Persistent;

import com.center.akka.persistent_channel.actor.BaseProcessor;
import com.center.akka.persistent_channel.actor.Receiver;
import com.center.akka.simple.command.Command;

public class System {

  public static final Logger log = LoggerFactory.getLogger(System.class);

  public static void main(String... args) throws Exception {

    final ActorSystem actorSystem = ActorSystem.create("channel-system");

    Thread.sleep(2000);

    final ActorRef receiver = actorSystem.actorOf(Props.create(Receiver.class));
    final ActorRef processor = actorSystem.actorOf(Props.create(BaseProcessor.class, receiver), "channel-processor");


    for (int i = 0; i < 10; i++) {
      processor.tell(Persistent.create(new Command("CMD " + i)), null);
    }

    Thread.sleep(2000);

    log.debug("Actor System Shutdown Starting...");

    actorSystem.shutdown();
  }
}

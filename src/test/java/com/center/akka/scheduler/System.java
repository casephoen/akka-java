package com.center.akka.scheduler;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * 定时器Scheduler
 * 
 * @author lcq
 *
 */
public class System {
  public static final Logger log = LoggerFactory.getLogger(System.class);

  public static void main(String[] args) throws InterruptedException {
    final ActorSystem actorSystem = ActorSystem.create("actor-system");

    final ActorRef testActor = actorSystem.actorOf(Props.create(TestActor.class));
    log.info("1111111111");

    // 在2秒后向testActor发送消息“haha”
    actorSystem.scheduler().scheduleOnce(Duration.create(2000, TimeUnit.MILLISECONDS), new Runnable() {
      public void run() {
        testActor.tell("haha", null);
      }
    }, actorSystem.dispatcher());

    log.info("2222222222");

    Thread.sleep(5000);

    log.debug("Actor System Shutdown Starting...");

    actorSystem.shutdown();

  }

}

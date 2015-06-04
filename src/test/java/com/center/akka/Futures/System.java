package com.center.akka.Futures;

import static akka.dispatch.Futures.future;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorSystem;

/**
 * 
 * Akka中的一个常见用例是在不需要使用 Actor的情况下并发地执行计算. 如果你发现你只是为了并行地执行一个计算而创建了一堆 Actor, 下面是一种更好（也更快）的方法
 * 
 * @author lcq
 *
 */
public class System {
  public static final Logger log = LoggerFactory.getLogger(System.class);

  public static void main(String[] args) throws Exception {
    final ActorSystem actorSystem = ActorSystem.create("actor-system");
    Future<String> f = future(new Callable<String>() {
      public String call() {
        return "Hello" + "World";
      }
    }, actorSystem.dispatcher());
    String result = (String) Await.result(f, Duration.create(1, SECONDS));
    log.info("result : " + result);

    Thread.sleep(2000);
    log.debug("Actor System Shutdown Starting...");
    actorSystem.shutdown();
  }

}

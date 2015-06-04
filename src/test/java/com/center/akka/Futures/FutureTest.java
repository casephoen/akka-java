package com.center.akka.Futures;

import static akka.dispatch.Futures.future;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.concurrent.Await;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorSystem;
import akka.dispatch.Mapper;

/**
 * 函数式 Future
 * 
 * @author lcq
 *
 */
public class FutureTest {
  public static final Logger log = LoggerFactory.getLogger(FutureTest.class);

  public static void main(String[] args) throws Exception {
    final ActorSystem actorSystem = ActorSystem.create("actor-system");
    Future<String> f1 = future(new Callable<String>() {
      public String call() {
        return "Hello" + "World";
      }
    }, actorSystem.dispatcher());
    final ExecutionContext ec = actorSystem.dispatcher();

    Future<Integer> f2 = f1.map(new Mapper<String, Integer>() {
      public Integer apply(String s) {
        return s.length();
      }
    }, ec);

    int result = Await.result(f2, Duration.create(1, SECONDS));
    log.info("result : " + result);

    Thread.sleep(2000);

    log.debug("Actor System Shutdown Starting...");

    actorSystem.shutdown();
  }

}

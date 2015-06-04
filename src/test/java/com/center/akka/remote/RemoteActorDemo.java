package com.center.akka.remote;

import java.util.Arrays;
import java.util.concurrent.Callable;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.Futures;
import akka.dispatch.OnSuccess;
import akka.japi.Function;
import akka.pattern.Patterns;
import akka.util.Timeout;

import com.typesafe.config.ConfigFactory;

public class RemoteActorDemo {

  public static void main(String args[]) {

    // 不使用默认的配置，而是选择加载选定的remote actor配置
    final ActorSystem system = ActorSystem.create("CalculatorWorkerSystem", ConfigFactory.load(("calculator")));

    // remote actor的ref
    final ActorRef calculatorActor = system.actorOf(Props.create(CalculatorActor.class), "CalculatorActor");

    System.out.println("Started CalculatorWorkerSystem");

    final Timeout timeout = new Timeout(Duration.create(5, "seconds"));
    Future<Object> addFuture = Patterns.ask(calculatorActor, new Op.Add(1, 2), timeout);
    Future<Object> subtractFuture = Patterns.ask(calculatorActor, new Op.Subtract(1, 2), timeout);
    Future<Object> multiplyFuture = Patterns.ask(calculatorActor, new Op.Multiply(1, 2), timeout);
    Future<Object> divideFuture = Patterns.ask(calculatorActor, new Op.Divide(1, 2), timeout);

    Iterable<Future<Object>> futureArray = Arrays.asList(addFuture, subtractFuture, multiplyFuture, divideFuture);
    Future<Iterable<Op.MathResult>> futureResult = Futures.traverse(futureArray, new Function<Future<Object>, Future<Op.MathResult>>() {
      public Future<Op.MathResult> apply(final Future<Object> param) throws Exception {
        return Futures.future(new Callable<Op.MathResult>() {
          public Op.MathResult call() throws Exception {
            return (Op.MathResult) Await.result(param, timeout.duration());
          }
        }, system.dispatcher());
      }
    }, system.dispatcher());

    futureResult.onSuccess(new OnSuccess<Iterable<Op.MathResult>>() {
      @Override
      public void onSuccess(Iterable<Op.MathResult> result) throws Throwable {
        for (Op.MathResult r : result) {
          if (r instanceof Op.AddResult) {
            System.out.println("add result=" + ((Op.AddResult) r).getResult());
          } else if (r instanceof Op.SubtractResult) {
            System.out.println("subtract result=" + ((Op.SubtractResult) r).getResult());
          } else if (r instanceof Op.MultiplicationResult) {
            System.out.println("multiply result=" + ((Op.MultiplicationResult) r).getResult());
          } else if (r instanceof Op.DivisionResult) {
            System.out.println("divide result=" + ((Op.DivisionResult) r).getResult());
          }
        }
      }
    }, system.dispatcher());
  }
}

package com.center.akka.typedActors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.dispatch.OnComplete;
import akka.japi.Option;

/**
 * 有类型 Actor 由两 “部分” 组成, 一个公开的接口和一个实现, 对普通actor来说，你拥有一个外部API (公开接口的实例) 来将方法调用异步地委托给 其实现的私有实例。
 * 
 * 有类型Actor相对于普通Actor的优势在于有类型Actor拥有静态的契约, 你不需要定义你自己的消息, 它的劣势在于对你能做什么和不能做什么进行了一些限制，i.e. 你不能使用
 * become/unbecome.就是不能动态改变actor接收到消息后的处理逻辑。
 * 
 * @author lcq
 *
 */
public class System {
  public static final Logger log = LoggerFactory.getLogger(System.class);

  public static void main(String... args) throws Exception {

    final ActorSystem actorSystem = ActorSystem.create("actor-system");

    Thread.sleep(2000);

    // 阻塞方式调用
    Squarer mySquarer = TypedActor.get(actorSystem).typedActorOf(new TypedProps<SquarerImpl>(Squarer.class, SquarerImpl.class));
    int sqNowValue = mySquarer.squareNow(5);
    log.info("sqNowValue" + sqNowValue);

    // 非阻塞方式调用
    Future<Integer> fu = mySquarer.square(6);
    // ExecutionContext类似于 java.util.concurrent.Executor
    final ExecutionContext ec = actorSystem.dispatcher();
    // Future的onComplete, onResult, 或 onTimeout 方法可以用来注册一个回调，以便在Future完成时得到通知。从而提供一种避免阻塞的方法。
    fu.onComplete(new OnComplete<Integer>() {
      @Override
      public void onComplete(Throwable failure, Integer result) throws Throwable {
        if (failure != null) {
          // We got a failure, handle it here
        } else {
          log.info("square result:" + result);
        }
      }
    }, ec);
    log.info("22222222");

    // 阻塞方式调用
    Option<Integer> op = mySquarer.squareNowPlease(7);
    log.info("squareNowPlease result : " + op.get());

    log.info("33333333");
    // 不关心的处理,方法会在另一个线程中异步地调用
    mySquarer.squareDontCare(2);
    log.info("44444444");
    Thread.sleep(2000);

    log.debug("Actor System Shutdown Starting...");

    actorSystem.shutdown();
  }
}

package com.center.akka.typedActors;


import scala.concurrent.Future;
import akka.japi.Option;

/**
 * tell:异步发送一个消息并立即返回; ask:异步发送一条消息并返回一个 Future代表一个可能的回应 所以总是使用tell更偏向性能,除非必须才用ask
 * 
 * @author lcq
 *
 */
public interface Squarer {
  void squareDontCare(int i); // 不关心的处理逻辑 fire-and-forget like ActorRef.tell

  Future<Integer> square(int i); // non-blocking send-request-reply like ActorRef.ask

  Option<Integer> squareNowPlease(int i);// blocking send-request-reply

  int squareNow(int i); // blocking send-request-reply

  // Any other type of value -->send-request-reply
}

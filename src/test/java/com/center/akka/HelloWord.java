package com.center.akka;

import scala.Option;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class HelloWord extends UntypedActor {
	public static void main(String[] args) throws Exception {

		ActorSystem system = ActorSystem.create("akka");
		ActorRef rcActor = system.actorOf(Props.create(HelloWord.class), "helloWorld");
		rcActor.tell("hi", ActorRef.noSender()); 
		Thread.sleep(6000);
		ActorSelection as = system.actorSelection("/user/helloWorld");
		as.tell("hello", ActorRef.noSender());

//		ActorSystem system = ActorSystem.create("demo1");
		ActorRef actor1 = system.actorOf(Props.create(Actor01.class));
		ActorRef actor2 = system.actorOf(Props.create(Actor02.class));
		actor2.tell("hello akka!!", actor1);
		system.shutdown();

	}

	@Override
	public void preStart() {
		System.out.println("===preStart");
	}

	@Override
	public void postRestart(Throwable reason) throws Exception {
		System.out.println("===postRestart");
		super.postRestart(reason);
	}

	@Override
	public void postStop() throws Exception {
		System.out.println("===postStop");
		super.postStop();
	}

	@Override
	public void preRestart(Throwable reason, Option<Object> message) throws Exception {
		System.out.println("===preRestart");
		super.preRestart(reason, message);
	}

	@Override
	public void onReceive(Object msg) {
		if (msg.toString().equals("hello")) {
			System.out.println("----" + msg);
			getContext().stop(getSelf());
		} else {
			// 导致崩溃，actor会自动重启
			System.out.println(3 / 0);
			// unhandled(msg);

		}
	}
}

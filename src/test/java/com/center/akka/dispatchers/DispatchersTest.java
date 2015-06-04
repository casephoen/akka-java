package com.center.akka.dispatchers;

import com.center.akka.simple.actor.SimpleActor;
import com.center.akka.simple.command.Command;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class DispatchersTest {
	public static void main(String[] args) throws InterruptedException {

		ActorSystem system = ActorSystem.create("MySystem");
		//为 Actor 指定派发器
		ActorRef myActor = system.actorOf(Props.create(SimpleActor.class).withDispatcher("my-dispatcher"), "myactor");
		myActor.tell(new Command("CMD 1"), ActorRef.noSender());
		Thread.sleep(2000);
		system.shutdown();
		
	}

}

package com.center.akka.dispatchers;

import com.center.akka.simple.actor.SimpleActor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;

public class PrioMailboxTest {
	public static void main(String[] args) throws InterruptedException {
		ActorSystem system = ActorSystem.create("MySystem");
		ActorRef myActor = system.actorOf(Props.create(SimpleActor.class).withDispatcher("prio-dispatcher"));
		myActor.tell("lowpriority", null);
		myActor.tell("lowpriority", null);
		myActor.tell("highpriority", null);
		myActor.tell("pigdog", null);
		myActor.tell("pigdog2", null);
		myActor.tell("pigdog3", null);
		myActor.tell("highpriority", null);
		myActor.tell(PoisonPill.getInstance(), null);

		Thread.sleep(2000);

		system.shutdown();
	}

}

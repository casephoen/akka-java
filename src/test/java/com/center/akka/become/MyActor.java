package com.center.akka.become;

import static com.center.akka.become.Messages.Think;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.japi.pf.FI.UnitApply;

public class MyActor {

	public static class Act  extends AbstractActor{
		public Act() {
			receive(ReceiveBuilder.matchEquals(Think, new UnitApply<Object>() {
				public void apply(Object i) throws Exception {
					System.out.println(String.format("%s starts to think","lcq"));
					//startThinking(Duration.create(5, SECONDS));
				}
			}).build());
		}
	}

	public static void main(String[] args) throws InterruptedException {
		ActorSystem system = ActorSystem.create();
		final ActorRef actorRef = system.actorOf(Props.create(Act.class), "Act-actor");
		actorRef.tell(Think, ActorRef.noSender());
		Thread.sleep(5000);
		system.shutdown();
	}

}

package com.center.akka;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Actor02 extends UntypedActor {

	public void onReceive(Object arg0) throws Exception {
		if(arg0 instanceof String)
			System.err.println("2-------------->"+arg0);
		getSender().tell("2222", ActorRef.noSender());
	}
	

}
package com.center.akka;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Actor01 extends UntypedActor {
	
	public void onReceive(Object arg0) throws Exception {
		if(arg0 instanceof String)
			System.err.println("1-------------->"+arg0);
		this.getSender().tell("22222", ActorRef.noSender());
	}
	
}
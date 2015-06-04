package com.center.akka.simple.actor;

import java.util.UUID;

import com.center.akka.simple.command.Command;
import com.center.akka.simple.event.Event;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class SimpleActor extends UntypedActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	public SimpleActor() {
		log.info("SimpleActor constructor");
	}

	@Override
	public void onReceive(Object msg) throws Exception {

		log.info("Received Command: " + msg);
		if (msg instanceof Command) {
			final String data = ((Command) msg).getData();
			final Event event = new Event(data, UUID.randomUUID().toString());
			// emmit an event somewhere...

		} else if (msg.equals("echo")) {
			log.info("ECHO!");
		}
	}
}
package com.center.akka.persistent_channel.actor;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.persistence.ConfirmablePersistent;

import com.center.akka.persistent_channel.command.ChannelReply;
import com.center.akka.simple.command.Command;

public class Receiver extends UntypedActor {

  LoggingAdapter log = Logging.getLogger(getContext().system(), this);

  @Override
  public void onReceive(Object msg) throws Exception {

    if (msg instanceof ConfirmablePersistent) {

      ConfirmablePersistent confirmablePersistent = (ConfirmablePersistent) msg;

      log.info("Incoming Paylod: " + confirmablePersistent.payload() + " #: " + confirmablePersistent.sequenceNr());

      getSender().tell(new ChannelReply((Command) confirmablePersistent.payload(), confirmablePersistent.sequenceNr()), null);
      confirmablePersistent.confirm();

    }
  }
}

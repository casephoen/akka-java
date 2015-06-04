package com.center.akka.persistent_channel.actor;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.persistence.Deliver;
import akka.persistence.Persistent;
import akka.persistence.PersistentChannel;
import akka.persistence.PersistentChannelSettings;

import com.center.akka.persistent_channel.command.ChannelReply;

public class BaseProcessor extends UntypedActor {

  LoggingAdapter log = Logging.getLogger(getContext().system(), this);

  private ActorRef receiver;
  private ActorRef channel;

  public BaseProcessor(ActorRef receiver) {
    this.receiver = receiver;
    this.channel =
        getContext().actorOf(
            PersistentChannel.props(PersistentChannelSettings.create().withRedeliverInterval(Duration.create(30, TimeUnit.SECONDS))
                .withPendingConfirmationsMax(10000) // max # of pending confirmations. suspend
                                                    // delivery until <
                .withPendingConfirmationsMin(2000) // min # of pending confirmation. suspend
                                                   // delivery until >
                .withReplyPersistent(true) // ack
                .withRedeliverMax(15)), "channel");
  }

  @Override
  public void onReceive(Object msg) {

    if (msg instanceof Persistent) {

      log.info("Send to Channel: " + ((Persistent) msg).payload());

      channel.tell(Deliver.create(((Persistent) msg).withPayload(((Persistent) msg).payload()), receiver.path()), getSelf());
    } else if (msg instanceof ChannelReply) {
      log.info(msg.toString());
    }
  }


}

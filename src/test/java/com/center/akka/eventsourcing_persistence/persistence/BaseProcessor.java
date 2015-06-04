package com.center.akka.eventsourcing_persistence.persistence;

import java.util.UUID;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import akka.persistence.Recover;
import akka.persistence.SnapshotOffer;
import akka.persistence.UntypedEventsourcedProcessor;

import com.center.akka.simple.command.Command;
import com.center.akka.simple.event.Event;

public class BaseProcessor extends UntypedEventsourcedProcessor {

  LoggingAdapter log = Logging.getLogger(getContext().system(), this);

  /**
   * The state of the processor
   */
  private ProcessorState processorState = new ProcessorState();



  @Override
  public void preStart() throws Exception {
    self().tell(Recover.create(0L), null);
  }

  /**
   * Called on restart. Loads from Snapshot first, and then replays Journal Events to update state.
   *
   * @param msg
   */
  public void onReceiveRecover(Object msg) {
    log.info("Received Recover: " + msg);
    if (msg instanceof Event) {
      System.out.println("onReceiveRecover -- msg instanceof Event");
      System.out.println("event --- " + ((Event) msg).getData());
      processorState.update((Event) msg);

    } else if (msg instanceof SnapshotOffer) {
      System.out.println("onReceiveRecover -- msg instanceof SnapshotOffer");
      processorState = (ProcessorState) ((SnapshotOffer) msg).snapshot();
    }
  }

  /**
   * Called on Command dispatch
   *
   * @param msg
   */
  public void onReceiveCommand(Object msg) {
    log.info("Received Command: " + msg);
    if (msg instanceof Command) {
      final String data = ((Command) msg).getData();

      // generate an event we will persist after being enriched with a uuid
      final Event event = new Event(data, UUID.randomUUID().toString());

      // persist event and THEN update the state of the processor
      persist(event, new Procedure<Event>() {
        public void apply(Event evt) throws Exception {

          processorState.update(evt);

          // broadcast event on eventstream 发布该事件
          getContext().system().eventStream().publish(evt);
        }
      });
    } else if (msg.equals("snapshot")) {
      saveSnapshot(processorState.copy());
    } else if (msg.equals("printstate")) {
      log.info(processorState.toString());
    }
  }
}

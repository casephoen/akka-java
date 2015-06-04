package com.center.akka.persistent_channel.command;

import com.center.akka.simple.command.Command;

public class ChannelReply {


  private Command command;
  private long sequenceNbr;

  public ChannelReply(Command command, long sequenceNbr) {
    this.command = command;
    this.sequenceNbr = sequenceNbr;
  }

  public Command getCommand() {
    return command;
  }

  public long getSequenceNbr() {
    return sequenceNbr;
  }

  @Override
  public String toString() {
    return "ChannelReply{" + "command=" + command + ", sequenceNbr=" + sequenceNbr + '}';
  }
}

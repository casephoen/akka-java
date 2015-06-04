package com.center.akka.demo1;

import org.apache.log4j.Logger;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class TaskMain {
	private static Logger logger  = Logger.getLogger(TaskMain.class);

	public static void main(String[] args) {
		ActorSystem rootSystem = ActorSystem.create("rootActor");
		ActorRef executeTaskActorRef = rootSystem.actorOf(Props.create(ExecuteTaskActor.class),"executeTask");
		logger.info("执行5个任务，每个任务并发执行，执行完成后返回执行成功的任务名称");
		for (int i = 0; i < 5; i++) {
			executeTaskActorRef.tell(new Task("我是任务" + i), null);
		}

	}
}

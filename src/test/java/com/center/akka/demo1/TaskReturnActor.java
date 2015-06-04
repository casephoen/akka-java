package com.center.akka.demo1;

import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.Maps;

import akka.actor.UntypedActor;

public class TaskReturnActor extends UntypedActor {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(TaskReturnActor.class);

	private Map<String, Object> results = Maps.newHashMap();
	private Integer taskFinishCount = 0;

	@Override
	public void onReceive(Object message) throws Exception {
		taskFinishCount++;
		if (message instanceof Result) {
			Result rs = (Result) message;
			logger.info("接收到被处理的任务 " + rs.getTaskName() + " 该任务执行情况  " + rs.getResult());
			if (rs.getResult().equals("success")) {
				results.put(rs.getTaskName(), rs.getResult());
			}
			if (results.size() == 5) {
				// 这里我只是简单的写一下 实际上我们可以传递参数 获取任务数目 为了回顾上面讲的知识 我们
				getSender().tell(new Result(), null); // 这里是我注定 告诉 已经处理完了 ok
														// 可以返回结果了 ，如果有些任务等不及
														// ，你使用
														// ExecuteTaskActorRef.tell(new
														// Result(),null)同样可以获得中间的结果
			}
		} else if (message instanceof String) {
			// 获得结果 我们就输出算了
			logger.info("执行任务成功的任务有 " + results.keySet().toString());
		} else
			unhandled(message);
		
		if(taskFinishCount >= 5){
			getContext().system().shutdown();
		}
		
	}
}

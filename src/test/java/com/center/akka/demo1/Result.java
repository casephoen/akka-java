package com.center.akka.demo1;

import java.io.Serializable;

public class Result implements Serializable {

	private static final long serialVersionUID = 1L;

	private String taskName;

	private String result;

	public Result() {
		super();
	}

	public Result(String taskName, String result) {
		super();
		this.taskName = taskName;
		this.result = result;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}

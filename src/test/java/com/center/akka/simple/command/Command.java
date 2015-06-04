package com.center.akka.simple.command;

import java.io.Serializable;

public class Command implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String data;

	public Command(String data) {
		this.data = data;
	}

	public String getData() {
		return this.data;
	}

	@Override
	public String toString() {
		return "Command{" + "data='" + data + '\'' + '}';
	}
}

package com.sds.tech.core;

import java.util.Arrays;

public class ThreadInfoSeries {
	private String nid;
	private String name;
	private ThreadInfo[] series;
	private String[] states;

	public ThreadInfoSeries() {
		super();

		series = new ThreadInfo[3];
		states = new String[3];
	}

	public ThreadInfoSeries(String nid, String name) {
		this();

		this.nid = nid;
		this.name = name;
	}

	public String getNid() {
		return nid;
	}

	public void setNid(String nid) {
		this.nid = nid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ThreadInfo getInfo(int index) {
		return this.series[index];
	}

	public void setInfo(int index, ThreadInfo info) {
		this.series[index] = info;
		setState(index, info.getState());
	}

	public String getState(int index) {
		return this.states[index];
	}

	public void setState(int index, String state) {
		this.states[index] = state;
	}

	@Override
	public String toString() {
		return "ThreadInfoSeries [name=" + name + ", series="
				+ Arrays.toString(series) + ", states="
				+ Arrays.toString(states) + "]";
	}
}

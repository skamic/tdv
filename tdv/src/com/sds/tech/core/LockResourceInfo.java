package com.sds.tech.core;

import java.util.ArrayList;
import java.util.List;

public class LockResourceInfo {
	private String id;
	private String path;
	private List<String> lockedThread;
	private List<String> waitingThread;

	public LockResourceInfo() {
		lockedThread = new ArrayList<String>();
		waitingThread = new ArrayList<String>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<String> getLockedThread() {
		return lockedThread;
	}

	public void addLockedThread(String lockedThread) {
		this.lockedThread.add(lockedThread);
	}

	public List<String> getWaitingThread() {
		return waitingThread;
	}

	public void setWaitingThread(String waitingThread) {
		this.waitingThread.add(waitingThread);
	}

	@Override
	public String toString() {
		return "LockResourceInfo [id=" + id + ", path=" + path
				+ ", lockedThread=" + lockedThread + ", waitingThread="
				+ waitingThread + "]";
	}

}

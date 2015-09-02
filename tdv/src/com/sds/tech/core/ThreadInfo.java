package com.sds.tech.core;

public class ThreadInfo {
	private String name;
	private int priority;
	private String tidHex;
	private long tidLong;
	private String nidHex;
	private long nidLong;
	private boolean isDaemon;
	private String statusDesc;
	private String state;
	private StringBuffer stackInfo;

	public ThreadInfo() {

	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * @return the tidHex
	 */
	public String getTidHex() {
		return tidHex;
	}

	/**
	 * @param tidHex
	 *            the tidHex to set
	 */
	public void setTid(String tidHex) {
		this.tidHex = tidHex;
		setTidLong(tidHex);
	}

	/**
	 * @return the tidLong
	 */
	public long getTidLong() {
		return tidLong;
	}

	/**
	 * @param tidHex
	 *            the tidLong to set
	 */
	private void setTidLong(String tidHex) {
		this.tidLong = Long.parseLong(tidHex, 16);
	}

	/**
	 * @return the nidHex
	 */
	public String getNidHex() {
		return nidHex;
	}

	/**
	 * @param nidHex
	 *            the nidHex to set
	 */
	public void setNid(String nidHex) {
		this.nidHex = nidHex;
		setNidLong(nidHex);
	}

	/**
	 * @return the nidLong
	 */
	public long getNidLong() {
		return nidLong;
	}

	/**
	 * @param nidLong
	 *            the nidLong to set
	 */
	private void setNidLong(String nidLong) {
		this.nidLong = Long.parseLong(nidLong, 16);
	}

	/**
	 * @return the isDaemon
	 */
	public boolean isDaemon() {
		return isDaemon;
	}

	/**
	 * @param isDaemon
	 *            the isDaemon to set
	 */
	public void setDaemon(boolean isDaemon) {
		this.isDaemon = isDaemon;
	}

	/**
	 * @return the statusDesc
	 */
	public String getStatusDesc() {
		return statusDesc;
	}

	/**
	 * @param statusDesc
	 *            the statusDesc to set
	 */
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the stackInfo
	 */
	public StringBuffer getStackInfo() {
		return stackInfo;
	}

	/**
	 * @param stackInfo
	 *            the stackInfo to set
	 */
	public void setStackInfo(StringBuffer stackInfo) {
		this.stackInfo = stackInfo;
	}

	@Override
	public String toString() {
		return "ThreadInfo [name=" + name + ", priority=" + priority
				+ ", tidHex=" + tidHex + ", nidHex=" + nidHex + ", isDaemon="
				+ isDaemon + "]";
	}

}

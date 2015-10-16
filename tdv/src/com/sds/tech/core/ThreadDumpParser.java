package com.sds.tech.core;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ThreadDumpParser {
	private static final String NEXTLINE_DELIMETER = "\n";
	private static final String SPACE_DELIMETER = " ";
	private static final String BLANK_DELIMETER = "";
	private static final String HEX_DELIMETER = "0x";
	private static final String THREAD_START_DELIMETER = "\"";
	private static final String STR_JAVA_LANG_THREAD_STATE = "java.lang.Thread.State: ";
	private static final String STR_DAEMON = "daemon";

	public static List<ThreadInfo> parse(String rawData) {
		List<ThreadInfo> threadList = new ArrayList<ThreadInfo>();
		String[] lines = rawData.split(NEXTLINE_DELIMETER);
		int lineCount = lines.length;

		for (int i = 0; i < lineCount; ++i) {
			String currentLine = lines[i];

			if (currentLine.startsWith(THREAD_START_DELIMETER)) {
				boolean isDaemon = (currentLine.indexOf(STR_DAEMON) >= 0);
				StringTokenizer st = new StringTokenizer(currentLine, "=");

				String threadName = st.nextToken().split(SPACE_DELIMETER)[0]
						.replaceAll(THREAD_START_DELIMETER, BLANK_DELIMETER);
				int priority = Integer.parseInt(st.nextToken().split(
						SPACE_DELIMETER)[0]);
				String tid = st.nextToken().split(SPACE_DELIMETER)[0]
						.replaceAll(HEX_DELIMETER, BLANK_DELIMETER);
				String tmp = st.nextToken();
				String nid = tmp.split(SPACE_DELIMETER)[0].replaceAll(
						HEX_DELIMETER, BLANK_DELIMETER);
				String statusDesc = tmp
						.substring(tmp.indexOf(SPACE_DELIMETER) + 1);

				ThreadInfo ti = new ThreadInfo();

				ti.setDaemon(isDaemon);
				ti.setName(threadName);
				ti.setPriority(priority);
				ti.setTid(tid);
				ti.setNid(nid);
				ti.setStatusDesc(statusDesc);

				currentLine = lines[++i];

				if (BLANK_DELIMETER.equals(currentLine)) {
					continue;
				}

				String state = currentLine.substring(
						currentLine.indexOf(STR_JAVA_LANG_THREAD_STATE)
								+ STR_JAVA_LANG_THREAD_STATE.length()).split(
						SPACE_DELIMETER)[0];

				ti.setState(state);

				StringBuffer stackInfo = new StringBuffer();

				while (!BLANK_DELIMETER.equals(currentLine = lines[++i])) {
					stackInfo.append(currentLine).append(NEXTLINE_DELIMETER);
				}

				ti.setStackInfo(stackInfo);

				threadList.add(ti);
			}
		}

		return threadList;
	}
}

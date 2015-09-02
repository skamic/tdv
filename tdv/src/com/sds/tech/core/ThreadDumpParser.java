package com.sds.tech.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ThreadDumpParser {
	private static final String NEXTLINE_DELIMETER = "\n";
	private static final String SPACE_DELIMETER = " ";
	private static final String BLANK_DELIMETER = "";
	private static final String HEX_DELIMETER = "0x";
	private static final String THREAD_START_DELIMETER = "\"";
	private static final String JAVA_LANG_THREAD_STATE = "java.lang.Thread.State: ";

	public static List<ThreadInfo> parse(InputStream inputStream)
			throws IOException {
		BufferedReader br = null;
		String buffer = null;
		List<ThreadInfo> threadList = new ArrayList<ThreadInfo>();

		br = new BufferedReader(new InputStreamReader(inputStream));

		try {
			while ((buffer = br.readLine()) != null) {
				if (buffer.startsWith(THREAD_START_DELIMETER)) {
					boolean isDaemon = (buffer.indexOf("daemon") >= 0);
					StringTokenizer st = new StringTokenizer(buffer, "=");

					String threadName = st.nextToken().split(SPACE_DELIMETER)[0]
							.replaceAll(THREAD_START_DELIMETER, BLANK_DELIMETER);
					int priority = Integer.parseInt(st.nextToken().split(
							SPACE_DELIMETER)[0]);
					String tid = st.nextToken().split(SPACE_DELIMETER)[0]
							.replaceAll(HEX_DELIMETER, BLANK_DELIMETER);
					String tmp = st.nextToken();
					String nid = tmp.split(SPACE_DELIMETER)[0].replaceAll(
							HEX_DELIMETER, BLANK_DELIMETER);
					String statusDesc = tmp.substring(tmp
							.indexOf(SPACE_DELIMETER) + 1);

					ThreadInfo ti = new ThreadInfo();

					ti.setDaemon(isDaemon);
					ti.setName(threadName);
					ti.setPriority(priority);
					ti.setTid(tid);
					ti.setNid(nid);
					ti.setStatusDesc(statusDesc);

					buffer = br.readLine();

					if (BLANK_DELIMETER.equals(buffer)) {
						continue;
					}

					String state = buffer.substring(
							buffer.indexOf(JAVA_LANG_THREAD_STATE)
									+ JAVA_LANG_THREAD_STATE.length()).split(
							SPACE_DELIMETER)[0];

					ti.setState(state);

					StringBuffer stackInfo = new StringBuffer();

					while (!BLANK_DELIMETER.equals(buffer = br.readLine())) {
						stackInfo.append(buffer).append(NEXTLINE_DELIMETER);
					}

					ti.setStackInfo(stackInfo);

					threadList.add(ti);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				br.close();
			}
		}

		return threadList;
	}
}

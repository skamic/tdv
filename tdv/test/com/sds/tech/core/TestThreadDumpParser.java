package com.sds.tech.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.sds.tech.core.ThreadDumpParser;
import com.sds.tech.core.ThreadInfo;
import com.sds.tech.core.ThreadInfoSeries;

public class TestThreadDumpParser {

	@Test
	public void parseTest() throws IOException {
		long startTime = System.currentTimeMillis();

		Map<String, ThreadInfoSeries> threadTable = new HashMap<String, ThreadInfoSeries>();

		for (int i = 0; i < 3; ++i) {
			File dump = new File("dump/ihome_jstack_20150828_1115.log");
			FileInputStream fis = new FileInputStream(dump);

			List<ThreadInfo> threadDump = ThreadDumpParser.parse(fis);

			for (ThreadInfo ti : threadDump) {
				ThreadInfoSeries tis = null;
				String nid = ti.getNidHex();
				String threadName = ti.getName();

				if (threadTable.containsKey(nid)) {
					tis = threadTable.get(nid);
				} else {
					tis = new ThreadInfoSeries(nid, threadName);
				}

				tis.setInfo(i, ti);

				threadTable.put(nid, tis);
			}
		}

		long elapsedTime = System.currentTimeMillis() - startTime;

		System.out.println(elapsedTime);
	}
}

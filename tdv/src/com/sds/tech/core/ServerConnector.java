package com.sds.tech.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jcraft.jsch.Session;
import com.sds.tech.util.ConnectionUtil;

public class ServerConnector {
	private String serverId;
	private String serverName;
	private String serverIP;
	private int serverPort;
	private String userId;
	private String userPw;
	private String osType;

	private Session session;
	private String identityPath;
	private Map<String, ThreadInfoSeries> threadTable;

	public ServerConnector() {

	}

	public ServerConnector(String serverName, String serverIP,
			String serverPort, String userId, String userPw) {
		this();

		this.serverName = serverName;
		this.serverIP = serverIP;
		this.serverPort = Integer.parseInt(serverPort);
		this.userId = userId;
		this.userPw = userPw;

		this.serverId = (new StringBuffer()).append(serverIP).append(":")
				.append(serverPort).toString();
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPw() {
		return userPw;
	}

	public void setUserPw(String userPw) {
		this.userPw = userPw;
	}

	public String getIdentityPath() {
		return identityPath;
	}

	public void setIdentityPath(String identityPath) {
		this.identityPath = identityPath;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	@Override
	public String toString() {
		return "ServerConnector [serverName=" + serverName + ", serverIP="
				+ serverIP + ", serverPort=" + serverPort + ", userId="
				+ userId + ", password=" + userPw + ", osType=" + osType + "]";
	}

	public void connect() {
		try {
			if (session == null) {
				session = ConnectionUtil.getSession(serverIP, serverPort,
						userId, userPw, identityPath);
			}

			if (!session.isConnected()) {
				session.connect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		session.disconnect();
	}

	public Map<String, String> getCurrentJavaProcess() {
		connect();

		String result = ConnectionUtil.execute(session, "ps -ef | grep java");
		String[] processArray = result.split("\n");
		Map<String, String> processMap = new HashMap<String, String>();

		for (String processInfo : processArray) {
			if (processInfo.startsWith(userId)) {
				Pattern pattern = Pattern.compile("[0-9]+");
				Matcher matcher = pattern.matcher(processInfo);

				while (matcher.find()) {
					String processId = matcher.group();

					processMap.put(processId, processInfo);

					break;
				}
			}
		}

		return processMap;
	}

	public String[][] getThreadDump(String processId) {
		StringBuffer command = new StringBuffer("jstack -l ");
		threadTable = new HashMap<String, ThreadInfoSeries>();
		String[] results = new String[3];

		command.append(processId);

		for (int i = 0; i < 3; ++i) {
			results[i] = ConnectionUtil.execute(session, command.toString());

			try {
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i < 3; ++i) {
			List<ThreadInfo> threadDump = ThreadDumpParser.parse(results[i]);

			for (ThreadInfo ti : threadDump) {
				ThreadInfoSeries tis = null;
				String nid = ti.getNidHex();

				if (threadTable.containsKey(nid)) {
					tis = threadTable.get(nid);
				} else {
					tis = new ThreadInfoSeries(nid, ti.getName());
				}

				tis.setInfo(i, ti);
				tis.setState(i, ti.getState());

				threadTable.put(nid, tis);
			}
		}

		String[][] dataValues = new String[threadTable.size()][5];
		int index = 0;
		StringBuffer buffer = new StringBuffer();

		for (String nid : threadTable.keySet()) {
			ThreadInfoSeries tis = threadTable.get(nid);

			dataValues[index][0] = nid;
			dataValues[index][1] = tis.getName();

			for (int i = 0; i < 3; ++i) {
				buffer.delete(0, buffer.length());
				buffer.append("State: ").append(tis.getState(i));
				buffer.append("\nStatus Description: ").append(
						tis.getInfo(i).getStatusDesc());
				dataValues[index][i + 2] = buffer.toString();
			}

			index++;
		}

		return dataValues;
	}

	public Map<String, ThreadInfoSeries> getThreadTable() {
		return threadTable;
	}
}

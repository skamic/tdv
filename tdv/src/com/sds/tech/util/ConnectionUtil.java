package com.sds.tech.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class ConnectionUtil.
 */
public class ConnectionUtil {

	private static Properties props;

	public static String getProperty(final String key) {
		return getProperty(key, null);
	}

	public static String getProperty(final String key, final String defaultValue) {
		String value = props.getProperty(key, defaultValue);

		try {
			Pattern pattern = Pattern.compile("\\$\\{(.*)\\}");
			Matcher matcher = pattern.matcher(value);

			while (matcher.find()) {
				String innerProperty = matcher.group();
				String innerPropertyValue = props.getProperty(innerProperty
						.substring(2, innerProperty.length() - 1));

				value = value.replace(innerProperty, innerPropertyValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return value;
	}

	public static void initProperties(String propertiesPath) {
		props = new Properties();

		try {
			if (propertiesPath == null || "".equals(propertiesPath)) {
				propertiesPath = "command.properties";
			}

			FileInputStream fis = new FileInputStream(propertiesPath);

			props.load(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the session.
	 *
	 * @param serverIP
	 *            the server ip
	 * @param serverPort
	 *            the server port
	 * @param userId
	 *            the user id
	 * @param userPw
	 *            the user pw
	 * @return the session
	 */
	public static Session getSession(final String serverIP,
			final int serverPort, final String userId, final String userPw) {
		return getSession(serverIP, serverPort, userId, userPw, null);
	}

	/**
	 * Gets the session.
	 *
	 * @param serverIP
	 *            the server ip
	 * @param serverPort
	 *            the server port
	 * @param userId
	 *            the user id
	 * @param userPw
	 *            the user pw
	 * @param identityPath
	 *            the identity path
	 * @return the session
	 */
	public static Session getSession(final String serverIP,
			final int serverPort, final String userId, final String userPw,
			final String identityPath) {
		JSch sch = new JSch();
		Session session = null;

		try {
			if (identityPath != null) {
				sch.addIdentity(identityPath);
			}

			session = sch.getSession(userId, serverIP, serverPort);
			session.setUserInfo(new UserInfo() {
				@Override
				public void showMessage(String arg0) {

				}

				@Override
				public boolean promptYesNo(String arg0) {
					return true;
				}

				@Override
				public boolean promptPassword(String arg0) {
					return true;
				}

				@Override
				public boolean promptPassphrase(String arg0) {
					return true;
				}

				@Override
				public String getPassword() {
					return userPw;
				}

				@Override
				public String getPassphrase() {
					return null;
				}
			});
		} catch (JSchException e) {
			e.printStackTrace();
		}

		return session;
	}

	/**
	 * Gets the channel.
	 *
	 * @param session
	 *            the session
	 * @param command
	 *            the command
	 * @return the channel
	 */
	public static Channel getChannel(Session session, final String command) {
		Channel channel = null;

		try {
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return channel;
	}

	/**
	 * Execute.
	 *
	 * @param session
	 *            the session
	 * @param command
	 *            the command
	 * @return the result of command execution
	 */
	public static String execute(Session session, final String command) {
		Channel channel = null;
		BufferedReader br = null;
		String buffer = null;
		StringBuffer result = new StringBuffer();

		try {
			channel = getChannel(session, command);

			br = new BufferedReader(new InputStreamReader(
					channel.getInputStream()));

			channel.connect();

			while ((buffer = br.readLine()) != null) {
				result.append(buffer).append("\n");
			}

			channel.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result.toString();
	}
}

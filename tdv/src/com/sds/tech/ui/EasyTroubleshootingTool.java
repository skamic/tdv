package com.sds.tech.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

import com.sds.tech.core.ServerConnector;
import com.sds.tech.core.ThreadInfo;
import com.sds.tech.core.ThreadInfoSeries;
import javax.swing.JTabbedPane;

// TODO: Auto-generated Javadoc
/**
 * The Class EasyTroubleshootingTool.
 */
public class EasyTroubleshootingTool extends JFrame {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3266643929384170771L;

	/** The column names. */
	private final String[] columnNames = new String[] { "NID", "Thread Name",
			"Thread Dump #1", "Thread Dump #2", "Thread Dump #3" };

	/** The server list button group. */
	private ButtonGroup serverListButtonGroup;

	/** The java process list button group. */
	private ButtonGroup javaProcessListButtonGroup;

	/** The server list panel. */
	private JPanel serverListPanel;

	/** The java process list panel. */
	private JPanel javaProcessListPanel;

	/** The thread dump panel. */
	private JPanel threadDumpPanel;

	/** The server list. */
	private List<ServerConnector> serverList = new ArrayList<ServerConnector>();

	/** The selected server index. */
	private int selectedServerIndex = -1;

	/** The selected process id. */
	private String selectedProcessId;

	/** The btn get thread dump. */
	private JButton btnGetThreadDump;

	/** The thread dump scroll pane. */
	private JScrollPane threadDumpScrollPane;

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				EasyTroubleshootingTool easyTroubleshootingTool = new EasyTroubleshootingTool();

				easyTroubleshootingTool.setExtendedState(6);
				easyTroubleshootingTool.setVisible(true);
			}
		});
	}

	/**
	 * Instantiates a new easy troubleshooting tool.
	 */
	public EasyTroubleshootingTool() {
		initUI();
	}

	/**
	 * 
	 */
	private void initUI() {
		setTitle("Easy Troubleshooting Tool (Thread Dump Visualizer)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(400, 600));

		createMenuBar();

		JSplitPane outerSplitPane = new JSplitPane();
		outerSplitPane.setContinuousLayout(true);
		outerSplitPane.setResizeWeight(0.2);

		JScrollPane leftScrollPane = new JScrollPane();
		leftScrollPane.setAutoscrolls(true);

		serverListPanel = new JPanel();
		serverListPanel.setLayout(new MigLayout("", "[]", "[][][]"));
		leftScrollPane.setViewportView(serverListPanel);

		JSplitPane innerSplitPane = new JSplitPane();
		innerSplitPane.setContinuousLayout(true);
		innerSplitPane.setResizeWeight(0.3);
		innerSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

		innerSplitPane.setLeftComponent(createTopRightScrollPane());
		innerSplitPane.setRightComponent(createBottomRightScrollPane());

		outerSplitPane.setLeftComponent(leftScrollPane);
		outerSplitPane.setRightComponent(innerSplitPane);

		getContentPane().add(outerSplitPane, BorderLayout.CENTER);
	}

	/**
	 * @return
	 */
	private JScrollPane createBottomRightScrollPane() {
		JScrollPane bottomRightScrollPane = new JScrollPane();
		bottomRightScrollPane.setPreferredSize(new Dimension(2, 100));

		threadDumpPanel = new JPanel();
		threadDumpPanel.setLayout(new MigLayout("", "[grow]", "[][grow]"));

		btnGetThreadDump = new JButton(
				"Get Thread Dump (Every 2 Seconds 3 Times)");
		btnGetThreadDump.setEnabled(false);
		btnGetThreadDump.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ServerConnector serverConnector = serverList
						.get(selectedServerIndex);

				drawThreadDumpTable(serverConnector
						.getThreadDump(selectedProcessId));
			}
		});
		threadDumpPanel.add(btnGetThreadDump, "cell 0 0");

		threadDumpScrollPane = new JScrollPane();
		threadDumpPanel.add(threadDumpScrollPane, "cell 0 1,grow");

		bottomRightScrollPane.setViewportView(threadDumpPanel);

		return bottomRightScrollPane;
	}

	/**
	 * Creates the top right scroll pane.
	 *
	 * @return the j scroll pane
	 */
	private JScrollPane createTopRightScrollPane() {
		JScrollPane topRightScrollPane = new JScrollPane();

		javaProcessListPanel = new JPanel();
		javaProcessListPanel.setFocusable(false);
		javaProcessListPanel.setLayout(new MigLayout("", "[]", "[][][][]"));

		JButton btnCurrentJavaProcess = new JButton("Current Java Process");
		btnCurrentJavaProcess.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedServerIndex < 0) {
					return;
				}

				ServerConnector selectedServer = serverList
						.get(selectedServerIndex);

				loadCurrentJavaProcess(selectedServer.getCurrentJavaProcess());

				selectedProcessId = null;
				btnGetThreadDump.setEnabled(false);
			}
		});
		javaProcessListPanel.add(btnCurrentJavaProcess, "cell 0 0");

		topRightScrollPane.setViewportView(javaProcessListPanel);

		return topRightScrollPane;
	}

	/**
	 * Creates the menu bar.
	 */
	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('f');
		menuBar.add(fileMenu);

		final JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter fileFilter = new FileNameExtensionFilter(
				"*.sl(Server List File)", new String[] { "sl" });
		fileChooser.setFileFilter(fileFilter);

		JMenuItem loadServerListMenuItem = new JMenuItem("Load Server List");
		loadServerListMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O, InputEvent.CTRL_MASK));
		loadServerListMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = fileChooser.showOpenDialog(null);

				if (result == 0) {
					File serverListFile = fileChooser.getSelectedFile();
					String fileName = serverListFile.getName();
					String extension = fileName.substring(
							fileName.lastIndexOf(".") + 1, fileName.length());
					if ("sl".equals(extension)) {
						loadServerList(serverListFile);
					}
				}
			}

		});
		fileMenu.add(loadServerListMenuItem);
	}

	/**
	 * Load server list.
	 *
	 * @param serverListFile
	 *            the server list file
	 */
	private void loadServerList(File serverListFile) {
		BufferedReader br = null;
		try {
			String buffer = null;
			int index = 0;

			removeAllServer();

			br = new BufferedReader(new FileReader(serverListFile));
			while ((buffer = br.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(buffer, ",");
				if (tokenizer.countTokens() >= 5) {
					ServerConnector serverConnector = new ServerConnector(
							tokenizer.nextToken().trim(), tokenizer.nextToken()
									.trim(), tokenizer.nextToken().trim(),
							tokenizer.nextToken().trim(), tokenizer.nextToken()
									.trim());

					if (tokenizer.hasMoreTokens()) {
						serverConnector.setIdentityPath(tokenizer.nextToken()
								.trim());
					}

					addServer(serverConnector, index++);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		serverListPanel.revalidate();
		serverListPanel.repaint();
	}

	/**
	 * Removes the all server.
	 */
	private void removeAllServer() {
		serverListPanel.removeAll();
		serverListButtonGroup = new ButtonGroup();

		serverList.clear();
		selectedServerIndex = -1;
	}

	/**
	 * Adds the server.
	 *
	 * @param serverConnector
	 *            the server connector
	 * @param index
	 *            the index
	 */
	private void addServer(ServerConnector serverConnector, int index) {
		JRadioButton rdbtnNewServer = new JRadioButton(
				serverConnector.getServerName());
		rdbtnNewServer.setName(String.valueOf(index));
		rdbtnNewServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JRadioButton rdbtnServer = (JRadioButton) e.getSource();

				selectedServerIndex = Integer.parseInt(rdbtnServer.getName());
			}
		});
		serverListPanel.add(rdbtnNewServer, "cell 0 " + index);
		serverListButtonGroup.add(rdbtnNewServer);

		serverList.add(serverConnector);
	}

	/**
	 * Load current java process.
	 *
	 * @param currentJavaProcess
	 *            the current java process
	 */
	private void loadCurrentJavaProcess(Map<String, String> currentJavaProcess) {
		int index = 1;

		removeAllProcess();

		for (String processId : currentJavaProcess.keySet()) {
			String processInfo = currentJavaProcess.get(processId);
			StringBuffer temp = new StringBuffer("<html>");
			int length = processInfo.length();

			for (int i = 0; i < length; i += 200) {
				if (i + 200 < length) {
					temp.append(processInfo.substring(i, i + 200));
				} else {
					temp.append(processInfo.substring(i));
				}

				temp.append("<br>");
			}
			temp.append("</html>");

			JRadioButton rdbtnProcess = new JRadioButton(temp.toString());
			rdbtnProcess.setName(processId);
			rdbtnProcess.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JRadioButton selectedProcess = (JRadioButton) e.getSource();

					selectedProcessId = selectedProcess.getName();
					btnGetThreadDump.setEnabled(true);
				}
			});
			javaProcessListPanel.add(rdbtnProcess, "cell 0 " + index++);
			javaProcessListButtonGroup.add(rdbtnProcess);
		}

		javaProcessListPanel.revalidate();
		javaProcessListPanel.repaint();
	}

	/**
	 * Removes the all process.
	 */
	private void removeAllProcess() {
		JButton btnCurrentJavaProcess = (JButton) javaProcessListPanel
				.getComponent(0);

		javaProcessListPanel.removeAll();
		javaProcessListPanel.add(btnCurrentJavaProcess);

		javaProcessListButtonGroup = new ButtonGroup();
	}

	/**
	 * Draw thread dump table.
	 *
	 * @param dataValues
	 *            the data values
	 */
	private void drawThreadDumpTable(final String[][] dataValues) {
//		threadDumpScrollPane.updateUI();

		final JTable threadDumpTable = new JTable(dataValues, columnNames);
		threadDumpTable.setDefaultRenderer(Object.class,
				new MultiLineCellRenderer());
		threadDumpTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() > 1) {
					int row = threadDumpTable.rowAtPoint(e.getPoint());
					int col = threadDumpTable.columnAtPoint(e.getPoint());

					if (col < 2) {
						return;
					}

					String nid = dataValues[row][0];
					ServerConnector serverConnector = serverList
							.get(selectedServerIndex);
					Map<String, ThreadInfoSeries> threadTable = serverConnector
							.getThreadTable();
					ThreadInfoSeries tis = threadTable.get(nid);
					ThreadInfo ti = tis.getInfo(col - 2);

					createThreadInfoPopup(col, ti);
				}
			}
		});

		threadDumpScrollPane.setViewportView(threadDumpTable);

		threadDumpScrollPane.revalidate();
		threadDumpScrollPane.repaint();
	}

	/**
	 * Creates the thread info popup.
	 *
	 * @param col
	 *            the col
	 * @param ti
	 *            the ti
	 */
	private void createThreadInfoPopup(int col, ThreadInfo ti) {
		JFrame popup = new JFrame(ti.getName() + " - " + columnNames[col]);
		popup.setMinimumSize(new Dimension(800, 300));

		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("", "[grow]", "[grow]"));
		popup.getContentPane().add(panel);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setAutoscrolls(true);
		panel.add(scrollPane, "cell 0 0,grow");

		JTextArea textArea = new JTextArea(ti.getStackInfo().toString());
		scrollPane.setViewportView(textArea);
		popup.getContentPane().add(scrollPane, BorderLayout.CENTER);
		popup.setFocusable(true);
		popup.setType(Type.POPUP);
		popup.setAlwaysOnTop(true);
		popup.setVisible(true);
	}
}

package com.sds.tech.ui;

import javax.swing.JFrame;
import javax.swing.JTable;
import java.awt.BorderLayout;
import javax.swing.JScrollBar;

public class Sample extends JFrame {
	public Sample() {
		
		table = new JTable();
		getContentPane().add(table, BorderLayout.CENTER);
	}
	private static final long serialVersionUID = 3266643929384170771L;
	private JTable table;

}

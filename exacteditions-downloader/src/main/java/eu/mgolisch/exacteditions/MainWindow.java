package eu.mgolisch.exacteditions;

import java.awt.EventQueue;


import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;



import javax.swing.JLabel;

import javax.swing.JTextArea;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.List;

import java.util.Properties;

import javax.swing.JToolBar;
import javax.swing.JButton;


import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;


import javax.swing.JTable;
import javax.swing.JComboBox;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class MainWindow {

	private JFrame frmExacteditionsDownloader;
	private JTextArea textAreaStatus;
	private JTable table;
	private JComboBox comboBox;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmExacteditionsDownloader.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmExacteditionsDownloader = new JFrame();
		frmExacteditionsDownloader.setTitle("exacteditions downloader");
		frmExacteditionsDownloader.setBounds(100, 100, 450, 300);
		frmExacteditionsDownloader.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmExacteditionsDownloader.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmPreferences = new JMenuItem("Preferences");
		mntmPreferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PreferencesDialog dialog = new PreferencesDialog();
				dialog.setVisible(true);
			}
		});
		mnFile.add(mntmPreferences);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnAbout = new JMenu("About");
		menuBar.add(mnAbout);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mnAbout.add(mntmAbout);
		
		JMenuItem mntmHelp = new JMenuItem("Help");
		mnAbout.add(mntmHelp);
		
		JPanel panel = new JPanel();
		frmExacteditionsDownloader.getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		textAreaStatus = new JTextArea();
		textAreaStatus.setRows(10);
		textAreaStatus.setEditable(false);
		textAreaStatus.setText("Lorem ipsum");
		scrollPane.setViewportView(textAreaStatus);
		System.setOut(new PrintStreamCapturer(textAreaStatus, System.out));
		System.setErr(new PrintStreamCapturer(textAreaStatus, System.err, "[ERROR] "));
		
		JToolBar toolBar = new JToolBar();
		frmExacteditionsDownloader.getContentPane().add(toolBar, BorderLayout.NORTH);
		
		JButton btnRefreshIssues = new JButton("Refresh Issues");
		btnRefreshIssues.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TitleComboItem item = (TitleComboItem) comboBox.getSelectedItem();
				if(item == null)
					JOptionPane.showMessageDialog(null, "Please add atleast one Title using the Manage Titles button");
				else{
				Utils.updateIssues(item.getTitleId());
				table.setModel(Utils.getIssuesModel(item.getTitleId()));
				}
			}
		});
		
		JButton btnManageTitles = new JButton("Manage Titles");
		btnManageTitles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ManageTitlesDialog dialog = new ManageTitlesDialog();
				dialog.setModal(true);
				dialog.setVisible(true);
				comboBox.removeAllItems();
				List<TitleComboItem> items = Utils.getMagazinesCombo();
				for(TitleComboItem item : items)
					comboBox.addItem(item);
			}
		});
		toolBar.add(btnManageTitles);
		
		JLabel lblSelectedTitle = new JLabel("Selected Title");
		toolBar.add(lblSelectedTitle);
		
		comboBox = new JComboBox();
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					TitleComboItem item = (TitleComboItem)e.getItem();
			          table.setModel(Utils.getIssuesModel(item.getTitleId()));
			          // do something with object
			       }
			}
		});
		toolBar.add(comboBox);
		toolBar.add(btnRefreshIssues);
		
		JButton btnDownloadSelected = new JButton("Download Selected");
		btnDownloadSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = table.getSelectedRow();
				if(index <0)
					JOptionPane.showMessageDialog(null, "Please select an issue from the table to download");
				else {
				String name = (String) table.getValueAt(index, 1);
				String titleid = ((Integer) table.getValueAt(index, 2)).toString();
				String issue = ((Integer) table.getValueAt(index, 3)).toString();
				int pages = Utils.getPagesforMagazine(titleid);
				Utils.DownloadPdf(issue, name.replace("/", "_"),pages);
				table.clearSelection();
				}
			}
		});
		toolBar.add(btnDownloadSelected);
		
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setViewportView(table);
		frmExacteditionsDownloader.getContentPane().add(scrollPane_1, BorderLayout.CENTER);
		List<TitleComboItem> items = Utils.getMagazinesCombo();
		for(TitleComboItem item : items)
			comboBox.addItem(item);
		Properties props = Utils.loadProperties();
		if(props.size() == 0)
			JOptionPane.showMessageDialog(null, "No configfile found. Please configure using File->Preferences");
	}

}

package eu.mgolisch.exacteditions;

import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JToolBar;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;

import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;

import javax.swing.JPasswordField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Properties;



public class PreferencesDialog extends JDialog {
	private JTextField textFieldUsername;
	private JPasswordField passwordField;
	private JTextField textField_download;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PreferencesDialog dialog = new PreferencesDialog();
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the dialog.
	 */
	public PreferencesDialog() {
		setTitle("Preferences");
		setModal(true);
		setBounds(100, 100, 450, 300);
		
		JToolBar toolBar = new JToolBar();
		getContentPane().add(toolBar, BorderLayout.NORTH);
		
		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Properties props = Utils.loadProperties();
				 if(props.size() != 0){
					 textFieldUsername.setText((String)props.get("username"));
					 passwordField.setText((String)props.get("password"));
					 textField_download.setText((String)props.get("downloaddir"));
				 }
			}
		});
		toolBar.add(btnLoad);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					Properties props = new Properties();
					props.setProperty("username",textFieldUsername.getText());
					props.setProperty("password",passwordField.getText());
					props.setProperty("downloaddir",textField_download.getText());
					Utils.storeProperties(props);
					
						 
			}
		});
		toolBar.add(btnSave);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		JButton btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				
			}
		});
		panel.add(btnOK);
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		JLabel lblUsername = new JLabel("Username:");
		panel_1.add(lblUsername, "2, 2");
		
		textFieldUsername = new JTextField();
		panel_1.add(textFieldUsername, "6, 2");
		textFieldUsername.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password:");
		panel_1.add(lblPassword, "2, 4");
		
		passwordField = new JPasswordField();
		panel_1.add(passwordField, "6, 4");
		
		JLabel lblDownloaddir = new JLabel("Downloaddir:");
		panel_1.add(lblDownloaddir, "2, 6");
		
		textField_download = new JTextField();
		panel_1.add(textField_download, "6, 6, fill, default");
		textField_download.setColumns(10);
		
		JButton btnSelectFolder = new JButton("Select Folder");
		btnSelectFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showOpenDialog(null);
		        File f;
		        if(returnVal == JFileChooser.APPROVE_OPTION){
		            f = fc.getSelectedFile();
		            textField_download.setText(f.getAbsolutePath());
		        }
		    }
		});
		panel_1.add(btnSelectFolder, "8, 6");

	}

}

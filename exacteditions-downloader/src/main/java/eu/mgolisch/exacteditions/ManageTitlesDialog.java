package eu.mgolisch.exacteditions;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.Box;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ManageTitlesDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField_name;
	private JTextField textField_titleid;
	private JTable table;
	private JTextField textField_pages;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ManageTitlesDialog dialog = new ManageTitlesDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ManageTitlesDialog() {
		setTitle("Manage Titles");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		{
			JLabel lblTitleName = new JLabel("Title name:");
			contentPanel.add(lblTitleName, "2, 2");
		}
		{
			textField_name = new JTextField();
			contentPanel.add(textField_name, "6, 2, fill, default");
			textField_name.setColumns(10);
		}
		{
			JLabel lblTitleid = new JLabel("Titleid:");
			contentPanel.add(lblTitleid, "2, 4");
		}
		{
			textField_titleid = new JTextField();
			contentPanel.add(textField_titleid, "6, 4, fill, default");
			textField_titleid.setColumns(10);
		}
		{
			JButton btnAdd = new JButton("Add");
			btnAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Utils.AddMagazine(textField_name.getText(), textField_titleid.getText(), textField_pages.getText());
					table.setModel(Utils.getMagazinesModel());
					
				}
			});
			{
				JLabel lblPages = new JLabel("Pages:");
				contentPanel.add(lblPages, "2, 6");
			}
			{
				textField_pages = new JTextField();
				contentPanel.add(textField_pages, "6, 6, fill, default");
				textField_pages.setColumns(10);
			}
			contentPanel.add(btnAdd, "2, 8");
		}
		{
			JSeparator separator = new JSeparator();
			contentPanel.add(separator, "4, 10");
		}
		{
			JButton btnDelete = new JButton("Delete");
			btnDelete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int index = table.getSelectedRow();
					if(index >=0){
					String id = ((Integer) table.getValueAt(index, 0)).toString();
					Utils.RemoveMagazine(id);
					table.setModel(Utils.getMagazinesModel());
					}
				}
			});
			contentPanel.add(btnDelete, "2, 12");
		}
		{
			table = new JTable();
			contentPanel.add(table, "6, 12, fill, fill");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setModel(Utils.getMagazinesModel());
	}

}

package views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * creates the graphical user interface to display the log data sent by the clients. 
 * 
 * !!!Currently not in use!!!
 * 
 * 
 * @author ulrike
 *
 */
public class Log_GUI extends JFrame {

	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public Log_GUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}//actionPerformed
		});//addActionListener
		btnClose.setBounds(145, 113, 117, 25);
		contentPane.add(btnClose);
	}//constructor

	
	//TODO display the logs here
	
}//class

package views;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import main.Main;
import models.Attack;

/**
 * creates the main graphical user interface of the STORM server application.
 * Gives the user an interface to control the STORM application and information
 * about the status.
 * 
 * @author ulrike
 *
 */
public class Main_Gui {

	// JFrames
	private JFrame frmStormControlCenter;

	// JTextFields
	private JTextField txtField_Ping_client_ip;
	private JTextField textField_HPing_Source;
	private JTextField textField_HPing_Dest;
	private JTextField textField_HPing_Payload;
	private JTextField txtFld_Ping_desIP;
	private JTextField txtFld_spoofedSource;
	private JTextField txtFld_DestPort;
	private JTextField txtFld_NumPackets;

	// JLists
	private JList list;

	// JComboBox
	private JComboBox comboBox_Ping_numClients;
	private JComboBox comboBox_HPing_NumClients;
	private JComboBox comboBox_HPing_Protocol;
	private JComboBox comboBox_HPing_Ampl;
	private JComboBox comboBox_Quiet_Verbose;

	// JTable & TableModel
	private JTable table_attacks;
	private DefaultTableModel tableModel;

	// JLabel
	private JLabel lblPayloadsize;
	private JLabel lblProtocol;
	private JLabel lblAttackAmplitude;
	private JLabel lblNumberOfClients;
	private JLabel lblDestPort;
	private JLabel lblNumberOfPackets;
	private JLabel lblQuietVerbose;
	private JLabel lblListOfCurrent;
	private JLabel lbl_spoofed_Source;
	private JLabel lblSourceIp;

	// JCheckBox
	private JCheckBox chckbxRandomAddress;
	private Logger logger;

	// HPing3 advanced options
	private boolean hping3advancedOptions = false;
	private JTextField textField_Port;

	/**
	 * Create the application.
	 */
	public Main_Gui() {
		logger = Main.getLogger();
		initialize();
	}// constructor

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		setFrame(new JFrame());
		getFrame().setBounds(100, 100, 950, 561);
		getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		getFrame().setJMenuBar(menuBar);

		JMenu mnStormcc = new JMenu("STORM_CC");
		menuBar.add(mnStormcc);

		final JMenuItem mntmQuit = new JMenuItem("Quit");
		mnStormcc.add(mntmQuit);
		getFrame().getContentPane().setLayout(null);

		/*
		 * add ActionListener to MenuItem -> Shut down application on "Quit"
		 */
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int reply = JOptionPane.showConfirmDialog(mntmQuit, "Do you really want to quit?");
				if (reply == JOptionPane.YES_OPTION) {
					// TODO Shut down any remaining attacks and clean up...
					logger.info("shutting down");
					Main.getDatabaseManager().getDatabaseConnection().deleteDatabase();
					System.exit(0);
				} // if
			}// actionPerformed()
		});// addActionListener()

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		tabbedPane.setBounds(10, 11, 924, 480);
		getFrame().getContentPane().add(tabbedPane);

		final JPanel panel_Clients = new JPanel();
		tabbedPane.addTab("Clients", null, panel_Clients, null);
		panel_Clients.setLayout(null);

		JLabel lblClients = new JLabel("Clients:");
		lblClients.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblClients.setBounds(52, 11, 106, 20);
		panel_Clients.add(lblClients);

		txtField_Ping_client_ip = new JTextField();
		txtField_Ping_client_ip.setBounds(52, 43, 149, 20);
		panel_Clients.add(txtField_Ping_client_ip);
		txtField_Ping_client_ip.setColumns(10);

		JButton btnAdd = new JButton("add");

		/*
		 * add ActionListener to button "add"
		 */
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.getClientcontroller().addClient();
			}// actionPerformed()
		});// addActionListener()
		btnAdd.setBounds(52, 80, 89, 23);
		panel_Clients.add(btnAdd);

		JButton btnRemove = new JButton("remove");
		/*
		 * add ActionListener to button "remove"
		 */
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(getSelectedClient() != null){
					String selectedClient = getSelectedClient().toString();
					list.clearSelection();
					Main.getClientcontroller().removeClient(selectedClient);
				}else{
					JOptionPane.showMessageDialog(getFrame(), "No client available to delete!");
				}
			}// actionPerformed()
		});// addActionListener()
		btnRemove.setBounds(625, 425, 89, 23);
		panel_Clients.add(btnRemove);

		// The label for displaying an image send by Storm_CL
		// The label is initialized with a text which is only shown if there is
		// no image available
		final JLabel clientImageLabel = new JLabel("");
		clientImageLabel.setBackground(Color.WHITE);
		System.out.println(this.getClass().getClassLoader().getResource("").getPath());
		clientImageLabel.setIcon(new ImageIcon(
				this.getClass().getClassLoader().getResource("").getPath() + "../D119-gaming-sc2-terran.jpg"));
		clientImageLabel.setBounds(220, 127, 192, 178);
		panel_Clients.add(clientImageLabel);

		// The Clientlist
		list = new JList();
		list.setBounds(536, 56, 237, 343);
		// An Actionlistener listening to the SelectionEvent
		// Displays the image last received from the selected Client
		list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// The image is pulled from the Clientcontroller
				/*if (list.getSelectedIndex() >= 0) {
					ImageIcon selectedClientIcon = Main.getClientcontroller()
							.getSelectedClientImage(list.getSelectedIndex());
					if (selectedClientIcon != null) { // Only set if an image
														// exists
						clientImageLabel.setIcon(selectedClientIcon);
					}
				}*/

			}
		});
		panel_Clients.add(list);

		JLabel lblCurrentlyAvailableClients = new JLabel("Currently available clients:");
		lblCurrentlyAvailableClients.setBounds(536, 14, 237, 15);
		panel_Clients.add(lblCurrentlyAvailableClients);
		
		textField_Port = new JTextField();
		textField_Port.setBounds(213, 43, 114, 19);
		panel_Clients.add(textField_Port);
		textField_Port.setColumns(10);
		
		JLabel lblPort = new JLabel("Port:");
		lblPort.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblPort.setBounds(220, 14, 70, 15);
		panel_Clients.add(lblPort);

		JTabbedPane panel_startAttack = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Start Attack", null, panel_startAttack, null);

		final JPanel panel_Ping = new JPanel();
		panel_startAttack.addTab("Ping", null, panel_Ping, null);
		panel_Ping.setLayout(null);

		txtFld_Ping_desIP = new JTextField();
		txtFld_Ping_desIP.setBounds(170, 11, 86, 20);
		panel_Ping.add(txtFld_Ping_desIP);
		txtFld_Ping_desIP.setColumns(10);

		JLabel lblDestinationIp_1 = new JLabel("Destination IP:");
		lblDestinationIp_1.setBounds(20, 14, 140, 14);
		panel_Ping.add(lblDestinationIp_1);

		comboBox_Ping_numClients = new JComboBox();
		comboBox_Ping_numClients.setBounds(170, 42, 86, 20);
		panel_Ping.add(comboBox_Ping_numClients);

		JLabel lblNumberOfClients_1 = new JLabel("Number of Clients:");
		lblNumberOfClients_1.setBounds(20, 45, 140, 14);
		panel_Ping.add(lblNumberOfClients_1);

		JButton btnStart = new JButton("Start");
		/*
		 * add ActionListener to button "start"
		 */
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Main.getAttackcontroller().startPingAttack();
			}// actionPerformed
		});// addActionListener
		btnStart.setBounds(20, 116, 89, 23);
		panel_Ping.add(btnStart);

		final JPanel panel_HPing = new JPanel();
		panel_startAttack.addTab("HPing 3", null, panel_HPing, null);
		panel_HPing.setLayout(null);

		textField_HPing_Source = new JTextField("");
		textField_HPing_Source.setEnabled(false);
		textField_HPing_Source.setBounds(211, 25, 162, 20);
		panel_HPing.add(textField_HPing_Source);
		textField_HPing_Source.setColumns(10);

		lblSourceIp = new JLabel("Source IP:");
		lblSourceIp.setBounds(24, 25, 177, 14);
		lblSourceIp.setEnabled(false);
		panel_HPing.add(lblSourceIp);

		textField_HPing_Dest = new JTextField();
		textField_HPing_Dest.setBounds(211, 56, 162, 20);
		panel_HPing.add(textField_HPing_Dest);
		textField_HPing_Dest.setColumns(10);

		JLabel lblDestinationIp = new JLabel("Destination IP:");
		lblDestinationIp.setBounds(24, 56, 177, 14);
		panel_HPing.add(lblDestinationIp);

		textField_HPing_Payload = new JTextField();
		textField_HPing_Payload.setEnabled(false);
		textField_HPing_Payload.setBounds(211, 118, 162, 20);
		panel_HPing.add(textField_HPing_Payload);
		textField_HPing_Payload.setText("64");
		textField_HPing_Payload.setColumns(10);

		lblPayloadsize = new JLabel("Payload size:");
		lblPayloadsize.setEnabled(false);
		lblPayloadsize.setBounds(24, 118, 177, 14);
		panel_HPing.add(lblPayloadsize);

		comboBox_HPing_Protocol = new JComboBox();
		comboBox_HPing_Protocol.setEnabled(false);
		comboBox_HPing_Protocol.setBounds(211, 149, 162, 20);
		comboBox_HPing_Protocol.addItem("UDP");
		comboBox_HPing_Protocol.addItem("ICMP");
		comboBox_HPing_Protocol.addItem("SYN-flag TCP");
		comboBox_HPing_Protocol.addItem("ACK-flag TCP");
		comboBox_HPing_Protocol.addItem("FIN-flag TCP");
		comboBox_HPing_Protocol.addItem("RESET-flag TCP");
		panel_HPing.add(comboBox_HPing_Protocol);

		lblProtocol = new JLabel("Protocol:");
		lblProtocol.setEnabled(false);
		lblProtocol.setBounds(24, 149, 177, 14);
		panel_HPing.add(lblProtocol);

		comboBox_HPing_Ampl = new JComboBox();
		comboBox_HPing_Ampl.setEnabled(false);
		comboBox_HPing_Ampl.setBounds(211, 180, 162, 20);
		comboBox_HPing_Ampl.addItem("fast");
		comboBox_HPing_Ampl.addItem("faster");
		comboBox_HPing_Ampl.addItem("flood");
		panel_HPing.add(comboBox_HPing_Ampl);

		lblAttackAmplitude = new JLabel("Attack amplitude:");
		lblAttackAmplitude.setEnabled(false);
		lblAttackAmplitude.setBounds(24, 180, 177, 14);
		panel_HPing.add(lblAttackAmplitude);

		comboBox_HPing_NumClients = new JComboBox();
		comboBox_HPing_NumClients.setEnabled(true);
		comboBox_HPing_NumClients.setBounds(211, 246, 162, 20);
		panel_HPing.add(comboBox_HPing_NumClients);

		lblNumberOfClients = new JLabel("Number of clients:");
		lblNumberOfClients.setEnabled(true);
		lblNumberOfClients.setBounds(24, 249, 177, 14);
		panel_HPing.add(lblNumberOfClients);

		JButton btnStart_1 = new JButton("Start");
		/*
		 * add ActionListener to button "start"
		 */
		btnStart_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.getAttackcontroller().startHPingAttack();
			}// actionPerformed()
		});// addActionListener()
		btnStart_1.setBounds(604, 25, 153, 23);
		panel_HPing.add(btnStart_1);

		JButton btnMoreOptions = new JButton("more options");
		btnMoreOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// set all not default compontents to visible
				if (hping3advancedOptions) {
					hping3advancedOptions = false;
					btnMoreOptions.setText("more options");
				} else {
					hping3advancedOptions = true;
					btnMoreOptions.setText("less options");
				}
				switchHPingMoreOptions();
			}// actionPerformed
		});// addActionListener
		btnMoreOptions.setBounds(604, 55, 153, 25);
		panel_HPing.add(btnMoreOptions);

		comboBox_Quiet_Verbose = new JComboBox();
		comboBox_Quiet_Verbose.setEnabled(false);
		comboBox_Quiet_Verbose.setBounds(211, 278, 162, 20);
		comboBox_Quiet_Verbose.addItem("Quiet");
		comboBox_Quiet_Verbose.addItem("Verbose");
		panel_HPing.add(comboBox_Quiet_Verbose);

		lblQuietVerbose = new JLabel("Quiet / Verbose");
		lblQuietVerbose.setEnabled(false);
		lblQuietVerbose.setBounds(24, 283, 169, 15);
		panel_HPing.add(lblQuietVerbose);

		lbl_spoofed_Source = new JLabel("spoofed source address");
		lbl_spoofed_Source.setEnabled(false);
		lbl_spoofed_Source.setBounds(24, 315, 196, 15);
		panel_HPing.add(lbl_spoofed_Source);

		txtFld_spoofedSource = new JTextField("");
		txtFld_spoofedSource.setEnabled(false);
		txtFld_spoofedSource.setColumns(10);
		txtFld_spoofedSource.setBounds(211, 310, 162, 20);
		panel_HPing.add(txtFld_spoofedSource);

		chckbxRandomAddress = new JCheckBox("random address");
		chckbxRandomAddress.setEnabled(false);
		chckbxRandomAddress.setBounds(433, 311, 183, 23);
		panel_HPing.add(chckbxRandomAddress);

		lblDestPort = new JLabel("Destination Port:");
		lblDestPort.setEnabled(false);
		lblDestPort.setBounds(24, 88, 177, 14);
		panel_HPing.add(lblDestPort);

		txtFld_DestPort = new JTextField();
		txtFld_DestPort.setEnabled(false);
		txtFld_DestPort.setColumns(10);
		txtFld_DestPort.setBounds(211, 88, 162, 20);
		panel_HPing.add(txtFld_DestPort);

		lblNumberOfPackets = new JLabel("Number of packets:");
		lblNumberOfPackets.setEnabled(false);
		lblNumberOfPackets.setBounds(24, 219, 196, 15);
		panel_HPing.add(lblNumberOfPackets);

		txtFld_NumPackets = new JTextField();
		txtFld_NumPackets.setEnabled(false);
		txtFld_NumPackets.setColumns(10);
		txtFld_NumPackets.setBounds(211, 214, 162, 20);
		panel_HPing.add(txtFld_NumPackets);

		final JPanel panel_current = new JPanel();
		tabbedPane.addTab("Current attacks", null, panel_current, null);
		panel_current.setLayout(null);

		lblListOfCurrent = new JLabel("List of current attacks:");
		lblListOfCurrent.setBounds(23, 11, 275, 14);
		panel_current.add(lblListOfCurrent);

		JButton btnStop = new JButton("stop");
		btnStop.setBounds(588, 33, 185, 23);
		/*
		 * add ActionListener to button "stop"
		 */
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.getAttackcontroller().stopAttack();
			}// actionPerformed()
		});// addActionListener()
		panel_current.add(btnStop);

		// JTable has to be created before the JScrollPane as it will be
		// invisible otherwise
		String col[] = { "ID", "Type", "Destination IP", "Number of Clients", "State" };
		tableModel = new DefaultTableModel(col, 0);
		table_attacks = new JTable(tableModel);

		JScrollPane scrollPane = new JScrollPane(table_attacks);
		scrollPane.setBounds(20, 33, 556, 431);
		panel_current.add(scrollPane);

		JButton btnShowGraphs = new JButton("show graphs");
		btnShowGraphs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.getGraph_GUI(getAttackTableEntryID());
			}// actionPerformed
		});// addActionListener
		btnShowGraphs.setBounds(588, 68, 185, 25);
		panel_current.add(btnShowGraphs);
	}// initialize()

	/**
	 * sets frame used in Main_Gui
	 * 
	 * @param frame
	 */
	public void setFrame(JFrame frame) {
		this.frmStormControlCenter = frame;
		frmStormControlCenter.setTitle("Storm Control Center");
	}// setFrame()

	/**
	 * fills client list with the current clients
	 * 
	 * @param obj
	 */
	public void setClientListData(Object[] obj) {
		if (obj == null) {
			Object[] emptyO = new Object[0];
			list.setListData(emptyO);
		}else{
			list.setListData(obj);
		}
	}// setClientListData()

	/**
	 * returns the JTextField containing the client IP address
	 * 
	 * @return JTextField txtField_Ping_client_ip
	 */
	public JTextField getTxtField_client_ip() {
		return txtField_Ping_client_ip;
	}// getTxtField_client_ip()

	/**
	 * returns the main JFrame of the GUI
	 * 
	 * @return JFrame frame
	 */
	public JFrame getFrame() {
		return frmStormControlCenter;
	}// getFrame()

	/**
	 * returns the currently selected client from the client list
	 * 
	 * @return
	 */
	public Object getSelectedClient() {
		return list.getSelectedValue();
	}// getSelectedClient()

	/**
	 * returns the JTextField containing the source address used for the HPing
	 * attack
	 * 
	 * @return JTextField textField_HPing_Source
	 */
	public JTextField getTextField_HPing_Source() {
		return textField_HPing_Source;
	}// getTextField_HPing_Source()

	/**
	 * returns the JTextfield containing the destination address used for the
	 * HPing attack
	 * 
	 * @return JTextField textField_HPing_Dest
	 */
	public JTextField getTextField_HPing_Dest() {
		return textField_HPing_Dest;
	}// getTextField_HPing_Dest()

	/**
	 * returns the JTextField used for the payload size used for the HPing
	 * attack
	 * 
	 * @return JTextField textField_HPing_Payload
	 */
	public JTextField getTextField_HPing_Payload() {
		return textField_HPing_Payload;
	}// getTextField_HPing_Payload()

	/**
	 * returns the JTextField used for the destination IP address used for the
	 * ping
	 * 
	 * @return JTextField txtFld_Ping_desIP
	 */
	public JTextField getTxtFld_Ping_desIP() {
		return txtFld_Ping_desIP;
	}// getTxtField_Ping_desIP()

	/**
	 * returns the selected number of clients for the ping attack as a String
	 * 
	 * @return String numClients
	 */
	public String getPingNumClients() {
		return this.comboBox_Ping_numClients.getSelectedItem().toString();
	}// getPingNumClients()

	/**
	 * set the number of clients in the combo box
	 * 
	 * @param i
	 */
	public void setPingNumClients(int i) {
		this.comboBox_Ping_numClients.removeAllItems();
		for (int j = 0; j <= i; j++) {
			this.comboBox_Ping_numClients.addItem(j);
		} // for
	}// setPingNumClients()

	/**
	 * returns the number of clients used in the HPing attack as a string
	 * 
	 * @return
	 */
	public String getHPingNumClients() {
		return this.comboBox_HPing_NumClients.getSelectedItem().toString();
	}// getHPingNumClients()

	/**
	 * set the number of clients in the combo box
	 * 
	 * @param i
	 */
	public void setHPingNumClients(int i) {
		this.comboBox_HPing_NumClients.removeAllItems();
		for (int j = 0; j <= i; j++) {
			this.comboBox_HPing_NumClients.addItem(j);
		} // for
	}// setHPingNumClients

	/**
	 * returns true if advanced options are available, else false
	 * 
	 * @return
	 */
	public boolean getHpingAdvanvedOptions() {
		return this.hping3advancedOptions;
	}// getHpingAdvancedOptions

	/**
	 * returns the spoofed Source JTextfield
	 * 
	 * @return JTextfield txtFld_spoofedSource
	 */
	public JTextField getTxtFld_spoofedSource() {
		return txtFld_spoofedSource;
	}// getTxtFld_spoofedSource

	/**
	 * returns the destination port JTextField
	 * 
	 * @return JTextField txtFld_spoofedSource
	 */
	public JTextField getTxtFld_DestPort() {
		return txtFld_DestPort;
	}// getTxtFld_DestPort()

	/**
	 * returns the TxtFld_NumPackets JTextField
	 * 
	 * @return JTextField TxtFld_NumPackets
	 */
	public JTextField getTxtFld_NumPackets() {
		return txtFld_NumPackets;
	}// getTxtFld_NumPackets()

	/**
	 * returns the Quiet/Verbose JComboBox
	 * 
	 * @return JComboBox comboBox_Quiet_Verbose
	 */
	public JComboBox getComboBox_Quiet_Verbose() {
		return comboBox_Quiet_Verbose;
	}// getComboBox_Quiet_Verbose()

	/**
	 * returns the random address JCheckbox
	 * 
	 * @return JCheckBox chckbxRandomAddress
	 */
	public JCheckBox getChckbxRandomAddress() {
		return chckbxRandomAddress;
	}// getChckbxRandomAddress()

	/**
	 * adds a new entry to the current attacks table using the data provided in
	 * the data object The data object consists of the following elements: <br>
	 * 
	 * - ID (int): ID of the attack<br>
	 * - Type (String): Type of the attack (for example ping or HPing)<br>
	 * - Destination IP (IPv4 address): the destination IP address for this
	 * attack<br>
	 * - Number of Clients (int): the number of clients used in this attack<br>
	 * 
	 * @param data
	 */
	public void addAttackTableEntry(Object[] data) {
		this.tableModel.addRow(data);
		// Activate trigger for an internal event to update the GUI
		this.table_attacks.setModel(tableModel);
	}// addAttackTableEntry()

	/**
	 * removes an entry from the attack table using its id as identifier.
	 * 
	 * @param id
	 */
	public void removeAttackTableEntry(int id) {
		this.tableModel.removeRow(id);
		// Activate trigger for an internal event to update the GUI
		this.table_attacks.setModel(tableModel);
	}// removeAttackTableEntry()

	/**
	 * returns the id of an entry in the current attack table. The id of the
	 * entry is the id of the selected row.
	 * 
	 * @return int id
	 */
	public int getAttackTableEntryID() {
		System.out.println("ID: " + this.table_attacks.getSelectedRow());
		return this.table_attacks.getSelectedRow();
	}// getAttackTableEntryID()

	/**
	 * returns the object of the current attack table on the position passed to
	 * this method
	 * 
	 * @param row
	 * @param col
	 * @return Object value
	 */
	public Object getAttackTableEntry(int row, int col) {
		System.out.println("Entry:" + this.table_attacks.getValueAt(row, col));
		return this.table_attacks.getValueAt(row, col);
	}// getAttaclTableEntry()

	/**
	 * returns the selected entry of the HPing protocol comboBox
	 * 
	 * @return Object selectedObject
	 */
	public Object getSelectedHPingProtocol() {
		return this.comboBox_HPing_Protocol.getSelectedItem();
	}// getSelectedHPingProtocol()

	
	/**
	 * returns the port for the client connection
	 * @return Port
	 */
	public String getPort(){
		return this.textField_Port.getText();
	}//getPort()
	
	
	/**
	 * returns the selected entry of the HPing amplitude comboBox
	 * 
	 * @return Object selectedObject
	 */
	public Object getSelectedHPingAmpl() {
		return this.comboBox_HPing_Ampl.getSelectedItem();
	}// getSelectedHPingAmpl()

	/**
	 * sets components used to select the advanced options for an HPing 3 attack
	 * to enabled
	 */
	public void switchHPingMoreOptions() {

		// Comboboxes
		this.comboBox_HPing_Ampl.setEnabled(this.hping3advancedOptions);
		this.comboBox_HPing_Protocol.setEnabled(this.hping3advancedOptions);

		this.comboBox_Quiet_Verbose.setEnabled(this.hping3advancedOptions);

		// Labels
		this.lblSourceIp.setEnabled(this.hping3advancedOptions);
		this.lblPayloadsize.setEnabled(this.hping3advancedOptions);
		this.lblAttackAmplitude.setEnabled(this.hping3advancedOptions);
		this.lblProtocol.setEnabled(this.hping3advancedOptions);
		this.lblDestPort.setEnabled(this.hping3advancedOptions);
		this.lblQuietVerbose.setEnabled(this.hping3advancedOptions);
		this.lblNumberOfPackets.setEnabled(this.hping3advancedOptions);
		this.lbl_spoofed_Source.setEnabled(this.hping3advancedOptions);

		// Textfields
		this.textField_HPing_Source.setEnabled(this.hping3advancedOptions);
		this.textField_HPing_Payload.setEnabled(this.hping3advancedOptions);
		this.txtFld_DestPort.setEnabled(this.hping3advancedOptions);
		this.txtFld_NumPackets.setEnabled(this.hping3advancedOptions);
		this.txtFld_spoofedSource.setEnabled(this.hping3advancedOptions);

		// Checkbox
		this.chckbxRandomAddress.setEnabled(this.hping3advancedOptions);
	}// setHPingMoreOptionsVisible()

	/**
	 * repaints the attack table
	 */
	public void updateAttackTable(Object[] o, int row) {
		/*
		 * int numrows=this.tableModel.getRowCount();
		 * 
		 * for (int i=0; i<numrows; i++){ this.tableModel.removeRow(i); }//for
		 * 
		 * 
		 * this.table_attacks.removeAll(); this.table_attacks.revalidate();
		 * this.table_attacks.repaint();
		 * 
		 * //"ID", "Type", "Destination IP", "Number of Clients", "State"
		 * List<Attack> list = Main.getAttackcontroller().getAttacklist(); for
		 * (Attack a: list){ Object[] o = {a.getId(),a.getAttackType(),
		 * a.getDestAddress(), a.getNumClients(),a.getState()};
		 * this.addAttackTableEntry(o); }//for
		 * 
		 * this.table_attacks.repaint();
		 */

		// Remove row from model
		if (this.tableModel.getRowCount() > 0) {
			this.tableModel.removeRow(row);
			// Insert row to the id
			this.tableModel.insertRow(row, o);
			// setModel to activate the internal update trigger of the GUI
			// object
			this.table_attacks.setModel(tableModel);
		} else {
			JOptionPane.showMessageDialog(this.getFrame(), "No attack available for an update");
		}
	}// updateAttackTable()
}// class
package controllers;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import controllers.DatabaseManager;
import main.Main;
import models.Attack;
import models.Attack_States;
import models.Client;
import models.HPing_Attack;
import models.Messagedata.MessageData;
import models.Ping_Attack;

/**
 * Defines the attack controller used by the STORM server application. This
 * controller creates, starts and stops all attacks generated by the STORM
 * application and provides some administrative functions.
 * 
 * @author ulrike
 *
 */
public class AttackController {

	private List<Attack> current_Attacks;
	private int attackCounter;

	/**
	 * creates a new AttackController
	 */
	public AttackController() {
		this.current_Attacks = new ArrayList<Attack>();
		this.attackCounter = 0;
	}// constructor

	/**
	 * starts a new ping attack by using the parameters provided in the GUI
	 */
	public void startPingAttack() {
		// get the destination IP as a String
		String options = Main.getMainGUI().getTxtFld_Ping_desIP().getText();

		// get the number of clients used as a string
		String num = Main.getMainGUI().getPingNumClients().trim();

		try {
			// try parsing the number of clients as an integer
			int numclients = Integer.parseInt(num);

			// check input values
			if (!options.isEmpty() && numclients > 0) {

				// create a new attack while trying to parse the IP address
				// strings as IPv4 IP addresses. If this fails, we know
				// that the user data was not valid

				Ping_Attack ping_attack = new Ping_Attack(attackCounter,
						(Inet4Address) Inet4Address.getByName(options));
				
				// set state to starting
				ping_attack.setState(Attack_States.STARTING);
				//Main.getMainGUI().updateAttackTable();
				addAttackToGUI(ping_attack);
				//TODO Update client table on main gui to reflect that some / all clients are busy
				
				// increase attackCounter as this generates the ID of this new
				// attack
				attackCounter++;
				
				// add attack to list of current attacks
				current_Attacks.add(ping_attack);

				int i = 0;
				// gather the necessary clients and set them to be busy
				// add them to the list of clients used in this attack
				for (Client c : Main.getClientcontroller().getClientList()) {
					if (i < numclients && c.isBusy() == false) {
						// Check whether the destination ip is the ip of a
						// client in the list (means, the client is the victim)
						// If true, set the victim state "true", else "false"
						if (c.getIp_address().toString() == options) {
							c.setIsVictim(true);
						} else {
							c.setIsVictim(false);
						}
						c.setBusy(true);
						ping_attack.addClient(c);
						i++;
					} // if
				} // for

				// update the list of attacks on the GUI
				//updateAttackList(ping_attack);
				
				// create the necessary database tables
				DatabaseManager dbm = Main.getDatabaseManager();
				dbm.getDatabaseConnection().createAttackAggTable(ping_attack);
				for (Client c : ping_attack.getClientList()) {
					dbm.addClientTables(c);
				} // for

				// start the aggregation on the tables
				dbm.aggregateTablesClient(ping_attack);

				// build the connection and send the message necessary to start
				// the attack
				List<Client> clientlist = ping_attack.getClientList();
		//		options = "-dstIP" + options;
				
				System.out.println("options:"+options);

				for (int j = 0; j < clientlist.size(); j++) {
					if (!clientlist.get(j).getIsVictim()) { // if the client is
															// not the victim,
															// send the attack
															// message
						MessageData message = clientlist.get(j).getConnection().createMessage("ping.run()", options, "Start",
								"This is only a ping test");
						clientlist.get(j).getConnection().sendMessage(message);
					}

					MessageData message = clientlist.get(j).getConnection().createMessage("ping.run()", options, "Transmit",
							"This is only a ping test");
					// clientlist.get(j).getConnection().sendMessage(message);
				} // for

				// set state to running
				ping_attack.setState(Attack_States.RUNNING);
				//Main.getMainGUI().updateAttackTable();
				updateAttackStateInTheGUI(ping_attack);
				// update the number of client fields to the current number of
				// free clients
				Main.getClientcontroller().updateNumClientFields();
				
			} // if
			else {
				// show the message dialog if the user data was not valid
				JOptionPane.showMessageDialog(null, "Please enter a destination IP and select a number of clients>0!");
			} // else
		} // try
		catch (Exception e) {
			// print the error trace if something goes wrong
			System.err.println(e);
			e.printStackTrace();
		} // catch
	}// startPingAttack()

	/**
	 * starts a new HPing attack using the parameters provided by the GUI
	 */
	public void startHPingAttack() {
		try {
			// try to parse the number of clients provided in the GUI to an
			// integer
			int numclients = Integer.parseInt(Main.getMainGUI().getHPingNumClients().trim());
			System.out.println("numclients: " + numclients);

			// check if the number of clients is a valid number (>0)
			if (numclients > 0) {

				// get the necessary data from the GUI and try to parse it in
				// the correct format
				Inet4Address source_ip = (Inet4Address) Inet4Address
						.getByName(Main.getMainGUI().getTextField_HPing_Source().getText());
				Inet4Address dest_ip = (Inet4Address) Inet4Address
						.getByName(Main.getMainGUI().getTxtFld_Ping_desIP().getText());
				int payloadsize = Integer.parseInt(Main.getMainGUI().getTextField_HPing_Payload().getText());
				String protocol = Main.getMainGUI().getSelectedHPingProtocol().toString();
				String amplitude = Main.getMainGUI().getSelectedHPingAmpl().toString();

				System.out.println("source IP: " + source_ip.getHostAddress());
				System.out.println("destination IP: " + dest_ip.getHostAddress());
				System.out.println("Payload size: " + payloadsize);
				System.out.println("Protocol: " + protocol);
				System.out.println("amplitude: " + amplitude);

				System.out.println("generating flags...");

				// generate the full flag string and perform a sanity check
				String flags = generateHPingFlags();
				System.out.println("flags generated");

				// if(flags.length()>0){

				System.out.println("flags >0");

				// try to create the new attack using the parameters provided
				HPing_Attack hping_attack = new HPing_Attack(attackCounter, "HPing3", dest_ip, source_ip, payloadsize,
						protocol, amplitude);

				// increase the attack counter as this generates the ID of the
				// new attack
				attackCounter++;

				// add the attack to the list of current attacks
				current_Attacks.add(hping_attack);

				// update the attack list on the GUI
				//updateAttackList(hping_attack);
				// set state to starting
				hping_attack.setState(Attack_States.STARTING);
				//Main.getMainGUI().updateAttackTable();
				//updateAttackStateInTheGUI(hping_attack); //this is for adding the attack
				addAttackToGUI(hping_attack);
				//TODO Update client table on main gui to reflect that some / all clients are busy
				
				
				// gather the necessary clients and set them to be busy
				// add them to the list of clients used in the attack
				int i = 0;
				for (Client c : Main.getClientcontroller().getClientList()) {
					if (i < numclients && c.isBusy() == false) {
						// Check whether the destination ip is the ip of a
						// client in the list (means, the client is the victim)
						// If true, set the victim state "true", else "false"
						if (c.getIp_address() == dest_ip) {
							c.setIsVictim(true);
						} else {
							c.setIsVictim(false);
						}
						c.setBusy(true);
						hping_attack.addClient(c);
						i++;
					} // if
					else {
						System.out.println("numclients größer als verfügbar oder client ist busy");
					}
				} // for

				// create the necessary database tables
				DatabaseManager dbm = Main.getDatabaseManager();
				dbm.getDatabaseConnection().createAttackAggTable(hping_attack);
				for (Client c : hping_attack.getClientList()) {
					dbm.addClientTables(c);
				} // for

				// start the aggregation on the tables
				dbm.aggregateTablesClient(hping_attack);

				System.out.println("flags for HPing attack: "+flags);
				
				// build the connection and send the message necessary to start
				// the attack
				List<Client> clientlist = hping_attack.getClientList();
				for (int j = 0; j < clientlist.size(); j++) {
					if (!clientlist.get(j).getIsVictim()) {// if the client is
															// not the victim,
															// send the attack
															// message
						MessageData message = clientlist.get(j).getConnection().createMessage("hping3", flags, "Start",
								"This is the start message");
						clientlist.get(j).getConnection().sendMessage(message);
						System.out.println("HpingMessage: " + message);
					} // if
					else {
						System.out.println("ist kein Victim");
					} // else

					MessageData message = clientlist.get(j).getConnection().createMessage("hping3", flags, "Transmit",
							"This is the start message");
					// clientlist.get(j).getConnection().sendMessage(message);
				} // for

				// set state to running
				hping_attack.setState(Attack_States.RUNNING);
				//Main.getMainGUI().updateAttackTable();
				updateAttackStateInTheGUI(hping_attack);
				// update the number of client fields to the current number of
				// free clients
				Main.getClientcontroller().updateNumClientFields();
				// }//if
				// else{
				System.out.println("keine Flags vorhanden");
				// }//else
			} // if
			else {
				// show a message dialog if the user data was not valid
				JOptionPane.showMessageDialog(null, "Number of clients !>0");
			} // else
		} // try
		catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "Please enter a valid IP address!");
			e.printStackTrace();
		}
		// if something goes wrong, print the debug trace
		catch (Exception e) {
			e.printStackTrace();
		} // catch
	}// startHPingAttack()

	/**
	 * generates the necessary string of flags and arguments for creating an
	 * HPing3 attack with the given parameters. Currently no support for -E flag
	 * 
	 * @return flags
	 */
	private String generateHPingFlags() {

		String flags = "";
		try {
			// get the necessary data from the GUI and try to parse it in the
			// correct format
			Inet4Address dest_ip = (Inet4Address) Inet4Address
					.getByName(Main.getMainGUI().getTextField_HPing_Dest().getText());

			if (Main.getMainGUI().getHpingAdvanvedOptions()) {
				Inet4Address source_ip = (Inet4Address) Inet4Address
						.getByName(Main.getMainGUI().getTextField_HPing_Source().getText());
				
				int dest_port = Integer.parseInt(Main.getMainGUI().getTxtFld_DestPort().getText());
				int payloadsize = Integer.parseInt(Main.getMainGUI().getTextField_HPing_Payload().getText());
				String protocol = Main.getMainGUI().getSelectedHPingProtocol().toString();

				String amplitude = Main.getMainGUI().getSelectedHPingAmpl().toString();

				int number_packets = Integer.parseInt(Main.getMainGUI().getTxtFld_NumPackets().getText());
				String quietness = Main.getMainGUI().getComboBox_Quiet_Verbose().getSelectedItem().toString();

				// Piece the flags together if they are applicable
				// Source IP: no extra flag or parameter needed as long as it
				// isn't spoofed
				if (!Main.getMainGUI().getTxtFld_spoofedSource().getText().isEmpty()) {
					source_ip = (Inet4Address) Inet4Address
							.getByName(Main.getMainGUI().getTxtFld_spoofedSource().getText());
					flags = flags + " -a" + source_ip;
				} // if
				else {
					if (Main.getMainGUI().getChckbxRandomAddress().isSelected()) {
						source_ip = getRandomIP();
						flags = flags + " -a" + source_ip;
					} // if
				} // else

				// destination port: -p flag
				if (dest_port > 0) {
					flags = flags + " -p " + dest_port;
				} // if

				// number of packets
				if (number_packets > 0) {
					flags = flags + " -c " + number_packets;
				} // if

				// payload size: -d flag (packet size)
				if (payloadsize > 0) {
					flags = flags + " -d " + payloadsize;
				} // if

				// protocols
				if (!protocol.isEmpty()) {
					switch (protocol) {
					case ("UDP"):
						flags = flags + " -2 ";
						break;
					case ("ICMP"):
						flags = flags + " -1 ";
						break;
					case ("SYN-flag TCP"):
						flags = flags + " -S ";
						break;
					case ("ACK-flag TCP"):
						flags = flags + " -A ";
						break;
					case ("FIN-flag TCP"):
						flags = flags + " -F ";
						break;
					case ("RESET-flag TCP"):
						flags = flags + " -R ";
						break;
					}// switch
				} // if

				// Amplitude
				if (!amplitude.isEmpty()) {
					switch (amplitude) {
					case "fast":
						flags = flags + " --fast ";
						break;

					case "faster":
						flags = flags + " --faster ";
						break;

					case "flood":
						flags = flags + " --flood ";
						break;
					}// switch
				} // if

				// Quiet/Verbose
				if (!quietness.isEmpty()) {
					switch (quietness) {
					case "Quiet":
						flags = flags + " -q ";
						break;

					case "Verbose":
						flags = flags + " -V ";
						break;
					}// switch
				} // if
			}
			// Destination IP flag
			flags = flags + dest_ip.getHostAddress();

			return flags;
		} // try
		catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "Please enter a valid IP address!");
			e.printStackTrace();
			return "";
		} // catch
		catch (NumberFormatException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Please enter valid numbers");
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Please enter valid data in the fields");
			return "";
		} // catch
	}// generateHPingFlags

	/**
	 * returns random IP address
	 * 
	 * @return spoofed_IP
	 */
	private Inet4Address getRandomIP() {
		try {
			// TODO generate a truly random IP address
			Inet4Address spoofed_ip = (Inet4Address) Inet4Address.getByName("172.0.0.1");
			return spoofed_ip;
		} // try
		catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		} // catch
	}// getRandomIP

	/**
	 * returns the list of current attacks
	 * 
	 * @return
	 */
	public List<Attack> getAttacklist() {
		return this.current_Attacks;
	}// getAttackList

	/**
	 * stop a current attack
	 */
	public void stopAttack() {
		// get the id of the attack that should be stopped
		int id = Main.getMainGUI().getAttackTableEntryID();

		// if the id is valid, proceed with stopping the attack (-1 would be no
		// row selected, row ids start with 0)
		if (id > -1) {
			// get the attack information from the table (ID)
			int attack_id = Integer.parseInt(Main.getMainGUI().getAttackTableEntry(id, 0).toString());

			// get the attack from the list of current attacks
			Attack stop_attack = getAttackbyID(attack_id);

			// if there is an ongoing attack with this id, then proceed with
			// stopping it
			if (stop_attack != null) {

				// set state to stopping
				stop_attack.setState(Attack_States.STOPPING);
				//Main.getMainGUI().updateAttackTable();
				updateAttackStateInTheGUI(stop_attack);
				// message each client used for this attack to stop the attack
				for (Client c : stop_attack.getClientList()) {

					try {
						/**
						 * select Clients used for this attack and remove them
						 * to the list of clients used for this attack use their
						 * connection and send them the messages needed to stop
						 * the attack break the connection to them
						 */
						// build the connection and send the message necessary
						// to start the attack
						List<Client> clientlist = stop_attack.getClientList();
						String options = "";

						for (int j = 0; j < clientlist.size(); j++) {
							MessageData message = clientlist.get(j).getConnection().createMessage("ping", options,
									"Stop", "Stop the attack");
							clientlist.get(j).getConnection().sendMessage(message);
						} // for
					} // try
					catch (Exception e) {
						System.err.println("something went wrong when stopping the attack");
						e.printStackTrace();
					} // catch

					// set the clients to be free to be used again
					c.setBusy(false);

					// update the number of client fields to the current number
					// of free clients
					Main.getClientcontroller().updateNumClientFields();
				} // for

				// set state to stopped
				stop_attack.setState(Attack_States.STOPPED);
				//Main.getMainGUI().updateAttackTable();
				updateAttackStateInTheGUI(stop_attack);
				// Remove the attack entry from table of current attacks on the
				// GUI
				// Main.getWindow().removeAttackTableEntry(Main.getWindow().getAttackTableEntryID());
			} // if
			else {
				// if the ID of the attack was not valid, do nothing and print
				// an error message for the log
				System.err.println("Attack not found in current_Attacks list");
			} // else
		} // if
		else {
			// if the user did not select a valid attack or if anything else was
			// wrong with the user input, show a message dialog
			// and write a comment in the log
			JOptionPane.showMessageDialog(null, "Please select an attack to stop!");
			System.err.println("no attack to stop selected");
		} // else
	}// stopAttack()

	public void addAttackToGUI(Attack attack){
		//Adds the attack to the GUI table
		Main.getMainGUI().addAttackTableEntry(attack.toArray());
	}
	
	public void removeAttackFromGUI(Attack attack){
		//Removes the attack from the GUI table
		Main.getMainGUI().removeAttackTableEntry(attack.getId());
	}
	
	/**
	 * update the attack list by adding a new attack
	 * 
	 * @param newAttack
	 *            Attack
	 */
	public void updateAttackStateInTheGUI(Attack attack) {
		// rewrite toArray output from newAttack to include number of clients
		/*
		Object[] newArray = new Object[newAttack.toArray().length + 1];
		for (int i = 0; i < newAttack.toArray().length; i++) {
			newArray[i] = newAttack.toArray()[i];
		} // for
		newArray[3] = newAttack.getNumClients();
		*/
		System.out.println("Updating attack at row #"+attack.getId());
		Main.getMainGUI().updateAttackTable(attack.toArray(), attack.getId());
		// add the attack in a new row in the list of attacks on the GUI
		//Main.getMainGUI().addAttackTableEntry(newArray);
	}// updateAttackList

	/**
	 * returns an attack to a given id from the list of known attacks
	 * 
	 * @param id
	 *            Integer
	 * @return attack Attack
	 */
	public Attack getAttackbyID(int id) {
		for (Attack a : current_Attacks) {
			if (a.getId() == id) {
				return a;
			} // if
		} // for
			// if there is no attack found for this id, return null
		return null;
	}// getAttackbyID

	/**
	 * adds an new attack to the current_attack list
	 * 
	 * @param a
	 */
	public void addAttacktoList(Attack a) {
		current_Attacks.add(a);
	}
}// class
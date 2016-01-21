package controllers;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import main.Main;
import models.Client;
import models.Messagedata.MessageData;


/**
 * This class defines the client controller used by the STORM server application. It creates and administers all the 
 * clients used in the application.
 * @author ulrike
 *
 */
public class ClientContoller {

	private List<Client> client_list;
	private int id_counter = 0;
	private ConnectionManager conManager;
	
	
	/**
	 * creates a new ClientController
	 */
	public ClientContoller(){
		this.client_list=new ArrayList<Client>();
		this.conManager = new ConnectionManager();
	}//constructor
	
	
	/**
	 * adds a new Client to the programm
	 */
	public void addClient(){
		//check if there is any text in the IP field
		if (Main.getMainGUI().getTxtField_client_ip().getText().length()!=0){
			
			//trim the text of the field to remove any beginning or trailing whitespaces
			String ip=Main.getMainGUI().getTxtField_client_ip().getText().trim();
			
			try{
				//check if there is already a client with this ip address
				
				int port = 9000;
				try{
					
					int port2 = Integer.parseInt(Main.getMainGUI().getPort());
					if(port2>0){
						port=port2;
					}//if
				}//try
				catch(Exception e){
					JOptionPane.showMessageDialog(null, "Not a valid port!");
				}//catch
				
				if(checkIfIpExists(ip, port)){
					//already in Client Manager, nothing to do
				}//if
				else{
					//try to convert the string provided to an IPv4 address and create a new client
					Client c = new Client((Inet4Address) Inet4Address.getByName(ip));
					
					c.setId(id_counter);
					id_counter++;
					
					//Creates, sets and starts the connection to the client
					conManager.manageConnection(c);
					
					//Creates two tables for the client
					Main.getDatabaseManager().getDatabaseConnection().createClientMessageTable(c);
					
					//add the new client to the list of clients
					client_list.add(c);
					
					//update the list of clients in the GUI
					updateClientList();
					
					//update the number of client fields to the current number of free clients
					updateNumClientFields();
				}//else
			}//try
			catch(Exception e){
				JOptionPane.showMessageDialog(null, "Not an valid IPv4 address!");
				System.err.println("not an valid IPv4 address");
				System.err.println(e);
			}//catch
		}//if
		else{
			JOptionPane.showMessageDialog(null, "Please enter a valid IPv4 address!");
		}//else
	}//addClient()
	
	
	/**
	 * remove a client from the list of clients
	 */
	public void removeClient(String selectedClientIp){
		try{
			//if there is any client that could be remove proceed with the removal
			if(client_list.size()>0){
				
				//try to convert the element that was selected to a string 
				String ip= selectedClientIp;
				
				//check which client should be removed
				for(int i= 0; i < client_list.size(); i++){
					//if the ip address gathered from the selected entry equals the one of the client, remove the corresponding 
					//client
					if(client_list.size() == 0){
						System.out.println("No client available to delete...");
						return;
					}
					Client c = client_list.get(i);
					if (c.getIp_address().getHostAddress().equals(ip)){
						//remove the client from the list of clients
						c.getConnection().getSocket().close();
						client_list.remove(c);
						
						//TODO 
						/*
						 * check if the client was busy when it was removed, if yes, stop its actions and then proceed with 
						 * removing it.
						 * Tell the client to clean up and to exit, then stop the connection to it
						 */
						
						//if this was the last client in the list, exit the loop
						//if(client_list.size()==0){
							break;
						//}//if
					}//if
				}//for
				
				//update the list of clients in the GUI
				updateClientList();
				
				//update the number of client fields to the current number of free clients
				updateNumClientFields();
			}//if
		}//try
		catch (Exception e){
			//if something goes wrong, tell the user about it and leave a message in the error log
			JOptionPane.showMessageDialog(null, "No client to remove selected!");
			System.err.println("no client selected to remove");
			//print the debug trace
			System.err.println(e);
		}//catch
	}//removeClient()
	
	
	/**
	 * update the list of clients displayed in the GUI
	 */
	public void updateClientList(){
		
		if(client_list.size() == 0){
			Main.getMainGUI().setClientListData(null);
			System.out.println("Clientlist size is 0");
			return;
		}
		//TODO Update the table when attack are started
		String[] ips = new String[client_list.size()];
	
		//check if clients are busy, select the ones currently free
		for (int i=0; i<client_list.size(); i++){
			
			//one Client can only be used in one attack
			if(!client_list.get(i).isBusy()){
				ips[i]=client_list.get(i).getIp_address().getHostAddress();
			}//if
		}//for
		
		
		//fill the list on the GUI with the new data
		Main.getMainGUI().setClientListData(ips);
	}//updateClientList()
	
	
	/**
	 * update the numbe of free clients in drop down menus on the GUI
	 */
	public void updateNumClientFields(){
		int free=0;
		
		//iterate over the list of clients and count the number of free clients
		for (Client c:client_list){
			if(c.isBusy()==false){
				free++;
			}//if
		}//for
		
		//set the number of clients in the drop down menus
		Main.getMainGUI().setPingNumClients(free);
		Main.getMainGUI().setHPingNumClients(free);
	}//updateNumClientFields()
	
	
	/**
	 * checks if a client with a given IPv4 address is already registered in the list of clients
	 * @param ip
	 * @return boolean
	 */
	private boolean checkIfIpExists(String ip, int port){
		for(Client c:client_list){
			if (c.getIp_address().getHostAddress().equals(ip) && c.getConnection().getSocket().getPort()==port){
				return true;
			}//if
		}//for
		return false;
	}//checkIfIpExists()
	
	
	/**
	 * Extracts the last image received from a specific client
	 * @param index
	 * @return an ImageIcon
	 */
	
	public ImageIcon getSelectedClientImage(int index){
		ImageIcon image = new ImageIcon();
		//Get the client by his index in the list 
		Client selectedClient = client_list.get(index);
		//Get the last message received by the client
		MessageData data = selectedClient.getConnection().getLastMessageReceived();
		if(data != null){
			//If a message exist, try to convert the image bytes from the payload
			byte[] imageBytes = data.getPayload().toByteArray();
			try{
				InputStream in = new ByteArrayInputStream(imageBytes);
				Image clientImage = ImageIO.read(in);
				image.setImage(clientImage);
			}//try
			catch(IOException e){
				e.printStackTrace();
			}//catch
		}//if
		return image; 
	}//getSelectedClientImage()
	
	
	/**
	 * returns the list of clients currently registered
	 * @return client_list
	 */
	public List<Client> getClientList(){
		return client_list;
	}//getClientList()
	
}//class

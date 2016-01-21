package controllers;

import javax.swing.JOptionPane;

import main.Main;
import models.Client;
import models.Connection;

public class ConnectionManager {

	/**
	 * Creates, sets and starts the connection object for a given connection object
	 * @param ip
	 * @param client
	 * @throws Exception
	 */
	public void manageConnection(Client client) throws Exception{
		int port = 9000;
		
		try{
			String portstring=Main.getMainGUI().getPort();
	//		System.out.println("Portstring #"+portstring+"#" );
			if(portstring!=null && portstring.length()>0){
				int p1 = Integer.parseInt(portstring);
	//			System.out.println("p1: "+p1);
				if(p1>0){
					port=p1;
				}//if
			}//if
		}//try
		catch(Exception e){
			JOptionPane.showMessageDialog(null, "The given port is not a valid number!");
	//		e.printStackTrace();
		}//catch
		
		//Create a new Connection object
		Connection connection = new Connection(client, port);
		//For Access
		client.setConnection(connection);
		
		//This is not a thread-method, but a method that starts a listener thread
		if(connection.getSocket().isBound()){
			connection.run();
		}//if
	}//manageConnection()
	
	
}//constructor

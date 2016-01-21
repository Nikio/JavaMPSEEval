package models;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.FutureTask;

import models.Messagedata.MessageData;

import com.google.protobuf.ByteString;

import database.DatabaseInsert;

/**
 * The connection class handles the communication with a specific client. 
 * Each client has its own connection object
 * @author Niklas
 */
public class Connection implements Observer{

	private Inet4Address ip;
	private Socket clientSocket;
	private List<MessageData> list;
	private String clientTableName;
	
	/**
	 * Creates a connection object
	 * The Connection object builds 
	 * - a new socket for a specific port and ip address and 
	 * - a list in which the messages send by the client are saved
	 * @param ip
	 * @param port
	 * @throws IOException
	 */
	public Connection(Client client, int port) throws IOException{
		this.ip = client.getIp_address();
		this.clientTableName = "raw" + Integer.toString(client.getId());
		clientSocket = new Socket(ip,port);
		list = new ArrayList<MessageData>();
	}//Constructor
	
	
	/**
	 * The listener method which is triggered by the ReceiveHandler when a message has been received
	 */
	@Override
	public void update(Observable o, Object arg) {
		//ReceiveHandler needs to be created to access its data attribute
		ReceiveHandler receiveHandler = (ReceiveHandler) o;
			if(receiveHandler.getData() != null){
				//Add the message to the list
				this.list.add(receiveHandler.getData());
				
				//MessageData data = this.list.get(0);
				//if(data.getCommand() == "storm.traffic"){
					Thread insertJob = new Thread(new DatabaseInsert(this.clientTableName, this.list.get(0)));
					insertJob.start();
				//}
				//Remove the oldest message object if the list holds 10 Messages
				this.list.remove(0);
			}//if
	}//update
	
	
	/**
	 * The run method is an artifact when Connection was an executable code class
	 * May have to be removed/changed later
	 */
	public void run(){
			//Create a new ReceiveHandler to be able to add the Connectionclass as an observer
			ReceiveHandler receiveHandler = new ReceiveHandler(clientSocket);
			receiveHandler.addObserver(this);
			//Run the ReceiveHandler to listen for messages from the STORM_Client
			Thread receiverThread = new Thread(receiveHandler);
			receiverThread.start();
			//Control Sysout, to be deleted later
			if(this.list.size() == 6){
				System.out.println(list.get(5));
				return;
			}//if
	}//run
		
	
	/**
	 * Sends a specific message to the connected STORM_Client in an asynchronous task
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(MessageData message) throws IOException{
			//Create a new ResponseHandler
			ResponseHandler responseHandler = new ResponseHandler(clientSocket, message);
		 	//Encapsulate the ResponseHandler for asynchronous execution
			FutureTask<String> responseTask = new FutureTask<String>(responseHandler);
			Thread responseThread = new Thread(responseTask);
		 	//Start the ResponseHandler to send the message
			responseThread.start();
		 	while(!responseTask.isDone()){
		 		Thread.yield();
		 	}//while
	}//sendMessage
	
	
	/**
	 * Returns the socket object which is used for the communication between STORM_CC and STORM_Client
	 * @return Socket
	 */
	public Socket getSocket(){
		return this.clientSocket;
	}//getSocket()
	
	
	/**
	 * Returns the last message object received from the STORM_Client
	 * @return MessageData or null
	 */
	public MessageData getLastMessageReceived(){
		if(this.list.size() > 0){
			return this.list.get(list.size()-1);
		}//if
		return null;
	}//getLastMessageReceived()

	
	/**
	 * Creates a STORM Message
	 * @param command
	 * @param commandArguments
	 * @param stormCommand
	 * @param payload
	 * @return MessageData
	 */
	public MessageData createMessage(String command, 
									 String commandArguments,
									 String stormCommand, 
									 String payload){
		
		MessageData.Builder messageBuilder = MessageData.newBuilder();
		messageBuilder.setCommand(command);
		messageBuilder.addCommandArguments(commandArguments);
		messageBuilder.addStormArguments(stormCommand);
		
		messageBuilder.setPayload(ByteString.copyFrom(payload.getBytes()));
		
		return messageBuilder.build();
	}//createMessage()

}//class
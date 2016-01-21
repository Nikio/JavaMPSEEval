package models;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Observable;
import java.util.logging.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

import main.Main;
import models.Messagedata.MessageData;

/**
 * This class handles the receival of messages from a STORM_Client
 * @author Niklas
 */
public class ReceiveHandler extends Observable implements Runnable {

	private Socket client;
	private MessageData message;
	private Logger logger;
	
	
	/**
	 * The constructor for the ReceiveHandler class.  
	 * @param client
	 */
	public ReceiveHandler(Socket client){
		this.client = client;
		this.logger = Main.getLogger();
	}//constructor
	
	
	/**
	 * The run()-method of the Runnable interface.
	 * The method reads from the InputStream of a client socket 
	 * and notifies all observers if a message has been received
	 */
	public void run(){
		//boolean finished = true;
		do{
			try{
				//Read from the client inputstream
				InputStream input = client.getInputStream();
				byte[] lengthArray = new byte[4];
				input.read(lengthArray, 0, 4);
			
				int length = 0;
				for(int i=0; i < lengthArray.length; i++){
					length = length + lengthArray[i];
				}
				byte[] messageArray = new byte[length];
				if(this.client.isClosed()){
					System.out.println("Socket has been closed, ReceiveHandler is ended");
					break;
				}
				input.read(messageArray, 0, length);	
				this.message = MessageData.parseFrom(messageArray);
			
				logger.finest("Message received: " + message);
				
				//If a message has been received all observers are notified
				setChanged();
				notifyObservers("Received_Message");
			}//try
			catch(InvalidProtocolBufferException e){
				//e.printStackTrace();
				System.out.println("Client is unreachable!");
				return;
			}//catch
			catch(SocketException er){
				if(this.client.isClosed()){
					System.out.println("Socket has been closed and receiveHandler is ending");
				}else{
					er.printStackTrace();
				}
			}
			catch(IOException ei){
				ei.printStackTrace();
			}
		}//do
		while(!this.client.isClosed());
	}//run()
	
	
	/**
	 * Returns the last message received from a STORM_Client
	 * @return MessageData
	 */
	public MessageData getData(){
		return this.message;
	}//getData()
}//class

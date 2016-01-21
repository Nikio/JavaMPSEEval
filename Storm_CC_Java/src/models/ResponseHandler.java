package models;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import main.Main;
import models.Messagedata.MessageData;

/**
 * This class handles the procedure of sending a message to a STORM_Client
 * @author Niklas
 */
public class ResponseHandler implements Callable<String> {

	private Socket client;
	private MessageData message;
	private Logger logger;
	
	
	/**
	 * The Constructor for the ResponseHandler class
	 * @param client
	 * @param message
	 */
	public ResponseHandler(Socket client, MessageData message){
		this.client = client;
		this.message = message;
		this.logger = Main.getLogger();
	}//constructor
	
	
	/**
	 * Call method used by the Callable interface
	 * writesTheMessage to the OutputStream of the client socket object
	 */
	public String call(){
		try{
			System.out.println("Sending Message: " + message);
			byte[] messageArray = this.message.toByteArray();
			byte[] lengthBytes = ByteBuffer.allocate(4).putInt(messageArray.length).array();
			for(int i=0; i < lengthBytes.length/2; i++){
				byte myByte = lengthBytes[i];
				lengthBytes[i] = lengthBytes[lengthBytes.length -i -1];
				lengthBytes[lengthBytes.length -i-1] = myByte;
			}
			
			System.out.println("lengthbytes:"+lengthBytes.length+" , lengtharray:"+messageArray.length);
			
			byte[] Message = new byte[lengthBytes.length + messageArray.length];
			
			System.arraycopy(lengthBytes, 0, Message, 0, lengthBytes.length);
			System.arraycopy(messageArray, 0, Message, lengthBytes.length, messageArray.length);
			
			
			OutputStream output = this.client.getOutputStream();
			output.write(Message);
			//message.writeDelimitedTo(client.getOutputStream());
			//Control sys, to be deleted later
			logger.finest("Sever - Message sent: " + message);
		}//try
		catch(IOException e){
			e.printStackTrace();
		}//catch
		return "Message has been sent";
	}//call()

}//class

package database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

import com.google.protobuf.ByteString;

import main.Main;
import models.Messagedata.MessageData;

public class DatabaseInsert implements Runnable {

	final MessageData data;
	final String tablename;
	final DatabaseConnection dbc;
	Logger logger;
	long time;
	String value;
	String value2;
	
	/**
	 * Constructor of the runnable code
	 * @param tablename
	 * @param message
	 */
	public DatabaseInsert(String tablename, MessageData message){
		this.data = message;
		this.tablename = tablename;
		this.dbc = Main.getDatabaseManager().getDatabaseConnection();
		this.logger=Main.getLogger();
	}//constructor
	
	
	/**
	 * The run method for the Runnable interface
	 */
	@Override
	public void run() {
		// TODO Check out how the payload is designed 
		// TODO Change the methods based on the structure of the payload
		//DatabaseConnection dbconnection = new DatabaseConnection();
		if(data.getPayload() != null){
			ByteString bstring = data.getPayload();
			
			byte[] barray = bstring.toByteArray();
			
			String payloadAsAString = new String(barray);
			
			extractData(payloadAsAString);
			
			this.dbc.insertInto(this.tablename, this.time, this.value, this.value2);
		}//if
		else{
			System.out.println("Payload was empty");
		}//else
	}//run()
	
	
	/**
	 * Extracts the traffic timestamp and values from the payload
	 * @param payloadAsAString
	 */
	public void extractData(String payloadAsAString){
		
		System.out.println("PayloadAsAString: " + payloadAsAString);
		StringBuffer sb = new StringBuffer();
		int i=0;
		while(payloadAsAString.charAt(i) != ';'){ //Extract the timestamp
			sb.append(payloadAsAString.charAt(i));
			i++;
		}//while
		i++;
		String timevalue = sb.toString();

		SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm:ss");

		Calendar cal = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		
		try{
			cal.setTimeInMillis(timeFormat.parse(timevalue).getTime());
			cal.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.get(Calendar.DATE));
	
		}//try
		catch(ParseException e){
			logger.fine("Error: Failed to pass time value");
		}//catch
		
		this.time = cal.getTime().getTime();
		this.time /= 1000L;
	
		//Extract the bit/s value
		sb = new StringBuffer();
		while(payloadAsAString.charAt(i) != ';'){ //Extract the timestamp
			char c=payloadAsAString.charAt(i);
			sb.append(c);
			i++;
		}//while
		
		this.value = sb.toString();

		//Extract the 2nd bit/s value
		this.value2 = payloadAsAString.substring(i+1, payloadAsAString.length());

	}//extractData()

}//class

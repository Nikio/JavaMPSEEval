package controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import database.DatabaseConnection;
import main.Main;
import models.Attack;
import models.Client;

/**
 * Defines the database manager used in the STORM server application. This database manager controls the database actions
 * and controls the aggregation of the data sent by the clients in the respective tables. 
 * @author ulrike
 *
 */
public class DatabaseManager {
	
	private DatabaseConnection  dbc;
	private int sleeptime;
	private int sleeptime2;
	private Timer timer;
	private Logger logger;
	
	
	/**
	 * creates a new DatabaseManager
	 */
	public DatabaseManager(){
		this.dbc= new DatabaseConnection();
		
		//sleeptime is in miliseconds!
		setSleeptime(5000); //=5 seconds
		this.sleeptime2 = (this.sleeptime/1000)*2;
		
		logger = Main.getLogger();
		logger.finest("DatabaseManager created");
	}//DatabaseManager()
	
	
	/**
	 * creates and executes the main aggregation task for the database
	 */
	public void aggregateTablesMain(){
		timer=new Timer();
			
		//Main timer task for main part
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				logger.finer("Timer: Database aggregation main part started");

				long currenttime = System.currentTimeMillis();

				Calendar cal = Calendar.getInstance();
				Calendar cal2 = Calendar.getInstance();		
				
				try{
					cal.setTimeInMillis(currenttime);
					cal.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.get(Calendar.DATE));
				}//try
				catch(Exception e){
					e.printStackTrace();
				}//catch

				currenttime = cal.getTime().getTime();
				currenttime /= 1000L;
				
				try{
					//select all aggClient tables and create the average over the values over the last timeframe
					
					List<Attack> attacks = Main.getAttackcontroller().getAttacklist();
					int insum =0;
					int outsum=0;
					
					
					//only execute aggregation if there is something to aggregate
					if(attacks.size()>0){
						for (int i=0; i<attacks.size();i++){
							int sumIn = 0;
							int sumOut = 0;
							
							
							for(int k=0; k<attacks.get(i).getNumClients(); k++){
								//System.out.println("Client: "+attacks.get(i).getClientList().get(k).getId());
								ResultSet inresult = Main.getDatabaseManager().getDatabaseConnection().executeQuery("select avg(invalue) from agg"+attacks.get(i).getClientList().get(k).getId()+" where time> "+(currenttime-sleeptime2)+" and time<"+currenttime+" ;");
								ResultSet outresult = Main.getDatabaseManager().getDatabaseConnection().executeQuery("select avg(outvalue) from agg"+attacks.get(i).getClientList().get(k).getId()+" where time> "+(currenttime-sleeptime2)+" and time<"+currenttime+" ;");
											
								if(inresult == null){
									logger.finest("!!!--- Inresult was null ---!!!");
								}//if
								else{
									insum=inresult.getInt(1);
								}//else
								
								if(outresult == null){
									logger.finest("!!!--- Outresult was null ---!!!");
								}//if
								else{
									System.out.println("Inresult: "+outresult.getInt(1));
									outsum=outresult.getInt(1);
								}//else
								
								
								
								sumIn=sumIn+insum;
								sumOut=sumOut+outsum;
								
							}
							Main.getDatabaseManager().getDatabaseConnection().insertInto("Att"+attacks.get(i).getId(), currenttime, ""+sumIn, ""+sumOut);
							logger.finest("Aggregated values for current time: "+currenttime);
							
						}//for
					}//if
					else{
						logger.finest("nothing to aggregate, trying again in "+sleeptime/1000+" seconds");
					}//else
				}//try
				catch(ArithmeticException e){
					e.printStackTrace();
				}//catch
				catch(SQLException e){
					e.printStackTrace();
				}//catch
				logger.finer("Timer: Database aggregation main part done");
			}//run
		}, 0, sleeptime);//scheduleAtFixedRate
		
	}//aggregateTablesMain
	
	
	
	/**
	 * creates and executes the aggregation task for each attack
	 */
	public void aggregateTablesClient(Attack a){
		//Timer task for client table aggregations 
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				//get current client list for each of the attacks
				List <Client>clientlist = a.getClientList();
				
				for (Client c: clientlist){
					logger.fine("starting DB thread for client "+c.getId());
					
					long currenttime = System.currentTimeMillis();

					Calendar cal = Calendar.getInstance();
					Calendar cal2 = Calendar.getInstance();
					
					try{
						cal.setTimeInMillis(currenttime);
						cal.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.get(Calendar.DATE));
					}//try
					catch(Exception e){
						e.printStackTrace();
					}//catch

					currenttime = cal.getTime().getTime();
					currenttime /= 1000L;
					
					try{
						//select data from the last few seconds and create the average value
						ResultSet inresult = dbc.executeQuery("select avg(invalue) from raw"+c.getId()+" where time> "+(currenttime-sleeptime2)+" and time<"+currenttime+" ;");
						ResultSet outresult = dbc.executeQuery("select avg(outvalue) from raw"+c.getId()+" where time> "+(currenttime-sleeptime2)+" and time<"+currenttime+" ;");
					
						//Save result in string to catch string == null
						String inresultString = inresult.getString(1);
						String outresultString = outresult.getString(1);
						
						if(inresultString == null){
							inresultString = "0";
							logger.finest("inresultstring was null");
						}//if
						if(outresultString == null){
							outresultString ="0";
							logger.finest("outresultstring was null");
						}//if

						//insert the value into the aggregation table for this client
						dbc.insertInto("agg"+c.getId(), currenttime, inresultString, outresultString);

						logger.finest("Aggregated values for current time: "+currenttime);
					}//try
					catch(SQLException e){
						e.printStackTrace();
					}//catch
					logger.fine("DB aggregation done with client "+c.getId());
				}//for
			}//run
		}, 0, sleeptime);//scheduleAtFixedRate
	}//aggregateTablesClient()
	
	
	/**
	 * adds the tables for the client in the database
	 * @param c
	 */
	public void addClientTables(Client c){
		this.dbc.createClientMessageTable(c);
		logger.finer("created client message tables");		
	}//addClientTables()

	
	/**
	 * returns the sleeptime for the clients
	 * @return sleeptime: int
	 */
	public int getSleeptime(){
		return this.sleeptime;
	}//getSleeptime()
	
	
	/**
	 * sets the sleeptime for the database threads
	 * @param sleeptime
	 */
	private void setSleeptime(int sleeptime){
		this.sleeptime = sleeptime;
	}//setSleeptime()
	
	
	/**
	 * returns the DatabaseConnection
	 * @return
	 */
	public DatabaseConnection getDatabaseConnection(){
		return this.dbc;
	}//getDatabaseConnection()
}//class

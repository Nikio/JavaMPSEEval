package database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import main.Main;
import models.Attack;
import models.Client;

/**
 * Defines the properties of the database connection and provides basic database functions that can be used by 
 * other classes.
 * @author ulrike
 *
 */
public class DatabaseConnection {
	
	private Connection connection;
	private Statement statement;
	private Logger logger;
	private String filename;

	
	/**
	 * creates the database file used by the programm
	 * @return filename: String
	 */
	public void createDatabaseFile(){
		logger = Main.getLogger();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String date = df.format(new Date());
		try{
			String path = this.getClass().getClassLoader().getResource("").getPath();
			File f = new File(path+"Database-"+date+".sqlite");
			f.createNewFile();
			
		}//try
		catch(Exception e){
			e.printStackTrace();
		}//catch
		filename="Database-"+date+".sqlite";
	}//createDatabaseFile()
	
	
	/**
	 * deletes the used database to clean up temporarily used files
	 * @param filename
	 */
	public void deleteDatabase(){
		logger.finest("deleting Database started");
		this.disconnect();
		File f = new File(this.getClass().getClassLoader().getResource("").getPath()+getFilename());
		boolean test = f.delete();
		if (test){
			logger.info("Database file deleted!");
		}//if
		else{
			logger.severe("Database file could not be deleted!");
		}//else		
	}//deleteDatabase()
		
	
	/**
	 * creates the connection to a SQlite Database of a given name (location has to be the classpath!)
	 * @param filename
	 * @throws ClassNotFoundException
	 */
	public void connect() throws ClassNotFoundException{
		
		try{
			// load the sqlite-JDBC driver using the current class loader
			logger.finest("loading SQlite-JDBC driver");
			// load the sqlite-JDBC driver using the current class loader
		    Class.forName("org.sqlite.JDBC");
		    
		    connection= null;
		    
	    	// create a database connection
		    String relpath = this.getClass().getClassLoader().getResource("").getPath()+getFilename();
	    	logger.finest("opening database at "+relpath);
	    	
	        connection = DriverManager.getConnection("jdbc:sqlite:"+relpath);
	        
	        logger.finest("creating Statement for database connection");
		    
	        statement = connection.createStatement();
	        statement.setQueryTimeout(30);  // set timeout to 30 sec.
	        
	        logger.info("Database connection to "+relpath+" established");
	    }//try
		catch (ClassNotFoundException e){
			e.printStackTrace();
		}//catch
	    catch(Exception e){
	    	e.printStackTrace();
	    }//catch
	}//connect
	
	
	/**
	 * execute a given SQL query on the connected data base
	 * @param query
	 */
	public void execute(String query){
		try {
			if (statement!=null){
				logger.finest("executing query: "+query);
				statement.execute(query);
			}//if
			else{
				logger.severe("No statement to use!");
			}//else
		} //try
		catch (SQLException e) {
			e.printStackTrace();
		}//catch
	}//execute
	
	
	/**
	 * executes a given SQL query on the connected database and returns the result set
	 * @param query
	 * @return
	 */
	public ResultSet executeQuery(String query){
		try {
			if (statement!=null){
				logger.finest("executing query: "+query);
				ResultSet rs = statement.executeQuery(query);
				return rs;
			}//if
			else{
				logger.severe("No statement to use!");
				return null;
			}//else
		} //try
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}//catch
	}//executeQuery
	
	
	/**
	 * disconnects the database
	 */
	public void disconnect(){
		try{
			if(connection != null){
				connection.close();
				logger.info("Disconnected from database");
			}//if
	    }//try
	    catch(SQLException e){
	        // connection close failed.
	    	e.printStackTrace();
	    }//catch	
	}//disconnect
	
	
	/**
	 * creates a table in the connected database to store the values received by this client
	 * @param c
	 */
	public void createClientMessageTable(Client c){
		int id = c.getId();
		this.execute("create table if not exists "+"raw"+id+" (time integer, invalue float, outvalue float);");
		this.execute("create table if not exists "+"agg"+id+" (time integer, invalue float, outvalue float);");
	}//createClientMessageTable
	
	
	/**
	 * creates aggregation table for an attack
	 * @param a
	 */
	public void createAttackAggTable(Attack a){
		this.execute("create table if not exists Att"+a.getId()+" (time integer, invalue float, outvalue float);");
	}//createAttackAggTable()
		
	
	/**
	 * inserts a time-value pair into a given table on the connected database
	 * @param table
	 * @param time Time in ms since epoch (will be saved as integer value in sqlite database)
	 * @param value
	 */
	public void insertInto(String table, long time, String value, String value2){
		this.execute("Insert into "+table+"(time, invalue, outvalue) values("+time+", "+value+", "+value2+");");
	}//insertInto
	
	
	/**
	 * returns the connection to the database
	 * @return Connection connection
	 */
	public Connection getConnection(){
		return this.connection;
	}//getConnection()
	
	
	/**
	 * returns the database file name
	 * @return String filename
	 */
	private String getFilename() {
		return this.filename;
	}//getFilename()
}//class

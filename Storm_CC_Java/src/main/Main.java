package main;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import models.Attack;
import views.Graph_GUI;
import views.Log_GUI;
import views.Main_Gui;
import controllers.AttackController;
import controllers.ClientContoller;
import controllers.DatabaseManager;
import controllers.GraphController;
import database.DatabaseConnection;


/**
 * Main class for the STORM server application. This class starts and controls all the major components of this 
 * application.
 * @author ulrike
 *
 */
public class Main {
	
	private static Main_Gui window;
	private static ClientContoller clientcontroller;
	private static AttackController attackcontroller;
	private static GraphController graphcontroller;
	private static DatabaseManager dbm;
	private static final Logger logger=Logger.getLogger(Main.class.getName());
	
	/**
	 * Launch the application.
	 * @param args String[]
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//set up logger, Logging level is fine, set down for more detail
					logger.setLevel(Level.FINE);
					createLogfile();
				    logger.info("Starting to log");
					
					//Create Client Controller
					setClientcontroller(new ClientContoller());
					logger.finest("Client controller created");
					
					//Create Attack Controller
					setAttackcontroller(new AttackController());
					logger.finest("Attack controller created");
					
					//Create the necessary database connection (production database!)
					dbm = new DatabaseManager();
					DatabaseConnection dbc = dbm.getDatabaseConnection();
					dbc.createDatabaseFile();
					logger.finest("Database file created");
									
					//connect to the database
					dbc.connect();
					logger.finest("Database connction established");
					
					//Create GraphController
					setGraphcontroller(new GraphController(getDatabaseManager().getDatabaseConnection()));
					logger.finest("graph controller created");
					
					//start main aggregation task
					dbm.aggregateTablesMain();
					logger.finest("started main aggregation task");
					
					//Create and build GUI
					window = new Main_Gui();
					logger.finest("Main GUI is being created..");
					
					//set the GUI to visible
					window.getFrame().setVisible(true);
					logger.finest("Main Gui ready");
				}//try 
				catch (Exception e) {
					//if an Exception occurs during the execution of the programm, print the debug trace and terminate the programm	
					e.printStackTrace();
				}//catch
			}//run()
		});//invokeLater
	}//main
	
	
	/**
	 * get the GUI window object
	 * @return Main_Gui window
	 */
	public static Main_Gui getMainGUI() {
		return window;
	}//getWindow()
	
	
	/**
	 * sets the Window used in the programm
	 * @param window Main_Gui
	 */
	
	public static void setMainGui(Main_Gui window) {
		Main.window = window;
	}//setWindow
	
	
	/**
	 * gets the ClientController used in this programm
	 * @return ClientController clientcontroller
	 */
	public static ClientContoller getClientcontroller() {
		return clientcontroller;
	}//getClientcontroller()
	
	
	/**
	 * sets the ClientManager used in this programm
	 * @param clientmanager Clientcontroller
	 */
	public static void setClientcontroller(ClientContoller clientmanager) {
		Main.clientcontroller = clientmanager;
	}//setClientcontroller
	
	
	/**
	 * returns the AttackController used by this programm
	 * @return AttackController attackcontroller
	 */
	public static AttackController getAttackcontroller() {
		return attackcontroller;
	}//getAttackcontroller
	
	
	/**
	 * sets the AttackController used in this programm
	 * @param attackcontroller AttackController
	 */
	public static void setAttackcontroller(AttackController attackcontroller) {
		Main.attackcontroller = attackcontroller;
	}//setAttackcontroller
	
	
	/**
	 * sets the graph controller
	 * @param graphcontroller
	 */
	public static void setGraphcontroller(GraphController graphcontroller){
		Main.graphcontroller = graphcontroller;
	}//setGraphcontroller()
	
	
	/**
	 * returns the graph controller for this programm
	 * @return graphcontroller
	 */
	public static GraphController getGraphcontroller(){
		return Main.graphcontroller;
	}//getGraphController()
	
	
	/**
	 * creates a new Graph_GUI window
	 */
	public static void getGraph_GUI(int attack_id){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					logger.finest("creating graph GUI");
					Attack attack = getAttackcontroller().getAttackbyID(attack_id);

					if(attack != null){
						Main.graphcontroller.setAttack(attack);
						Graph_GUI frame = new Graph_GUI();
						frame.setVisible(true);
						logger.finest("graph GUI now ready");
					}//if
					
				}//try 
				catch (Exception e) {
					e.printStackTrace();
				}//catch
			}//run
		});//invokeLater
	}//getGraph_Gui
	
	
	/**
	 * creates a new Log_GUI window
	 */
	//TODO pass attack id, so the right logs can be displayed
	public static void getLog_GUI(int attack_id) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					logger.finest("creating log GUI");
					Log_GUI frame = new Log_GUI();
					frame.setVisible(true);
					logger.finest("log GUI now ready");
				}//try 
				catch (Exception e) {
					e.printStackTrace();
				}//catch
			}//run
		});//invokeLater
	}//getLog_GUI
	
	
	/**
	 * returns the database connection
	 * @return dbc
	 */
	public static DatabaseManager getDatabaseManager(){
		return dbm;
	}//getDatabaseConnetion()


	/**
	 * returns the logger (logger for this programm)
	 * @return logger
	 */
	public static Logger getLogger() {
		return logger;
	}//getLogger

	
	/**
	 * creates the logfile for this programm
	 */
	private static  void createLogfile(){
		FileHandler fh;  
	    try {  
	        //configure the logger with file handler and formatter  
	        fh = new FileHandler("Logfile.log");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  
	    } //try
	    catch (SecurityException e) {  
	        e.printStackTrace();  
	    } //catch
	    catch (IOException e) {  
	        e.printStackTrace(); 
	    }//catch  
	}//createLogfile
	
}//class

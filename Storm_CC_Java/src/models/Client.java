package models;

import java.net.Inet4Address;

/**
 * Model for a client. Defines properties and functions of a client.
 * @author ulrike
 *
 */
public class Client {
	
	private Inet4Address ipAddress;
	private boolean isBusy;
	private Connection connection; 
	private int id;
	private boolean isVictim;

	
	/**
	 * creates a new client with a given IPv4 address
	 * @param addr Inet4Address
	 */
	public Client(Inet4Address addr){
		this.ipAddress=addr;
		this.isBusy=false;
	}//constructor
	
	
	/**
	 * returns the IPv4 address of the client
	 * @return ip_address
	 */
	public Inet4Address getIp_address() {
		return ipAddress;
	}//getIp_address()
	
	
	/**
	 * sets the IPv4 address of a client
	 * @param ip_address Inet4Address
	 */
	public void setIp_address(Inet4Address ip_address) {
		this.ipAddress = ip_address;
	}//setIp_address()

	
	/**
	 * returns the status of the client
	 * @return boolean
	 */
	public boolean isBusy() {
		return isBusy;
	}//isBusy()

	
	/**
	 * sets the status of the client
	 * @param isBusy boolean
	 */
	public void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}//setBusy()
	
	
	/**
	 * returns the connection object of the client
	 * @return Connection
	 */
	public Connection getConnection(){
		return this.connection;
	}//getConnection()
	
	
	/**
	 * sets the connection object for the communication with the client
	 * @param connection
	 */
	public void setConnection(Connection connection){
		this.connection = connection;
	}//setConnection()

	
	/**
	 * returns the ID of the client
	 * @return id
	 */
	public int getId() {
		return id;
	}//getClient()


	/**
	 * sets the ID of the client
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}//setId()
	
	
	/**
	 * returns the victim state of the client (true/false)
	 * @return
	 */
	public boolean getIsVictim(){
		return this.isVictim;
	}//getIsVictim()
	
	
	/**
	 * sets the victim state of the client (true/false)
	 * @param isVictim
	 */
	public void setIsVictim(boolean isVictim){
		this.isVictim = isVictim;
	}//setIsVictim()
	
}//class

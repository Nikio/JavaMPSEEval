package models;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

/**
 * abstract model for an attack. Defines basic parameters and functions of an attack.
 * @author ulrike
 *
 */
public abstract class Attack {

	private int id;
	private String attackType;
	private Inet4Address destAddress;
	private List<Client> Clients_used = new ArrayList<Client>();
	private Attack_States state;

	
	/**
	 * returns the properties of an attack as an Object [] array
	 * @return o
	 */
	public Object[] toArray(){
		Object [] o = {
				this.getId(),
				this.getAttackType(),
				this.getDestAddress().getHostAddress(),
				this.getNumClients(),
				this.getState()
		};
		return o;
	}//toArray()
	
	
	/**
	 * returns the number of clients used in this attack
	 * @return clients_used
	 */
	public int getNumClients() {
		return Clients_used.size();
	}//getNumClients()
	
	/**
	 * returns the attack type
	 * @return attackType
	 */
	public String getAttackType() {
		return attackType;
	}//getAttackType()
	

	/**
	 * sets the attack type
	 * @param attackType String
	 */
	public void setAttackType(String attackType) {
		this.attackType = attackType;
	}//setAttackType()

	
	/**
	 * returns the destination IPv4 address of the attack
	 * @return destAddress
	 */
	public Inet4Address getDestAddress() {
		return destAddress;
	}//getDestAddress()

	
	/**
	 * sets the destination address of the attack
	 * @param destAddress Inet4Address
	 */
	public void setDestAddress(Inet4Address destAddress) {
		this.destAddress = destAddress;
	}//setDestAddress()
	

	/**
	 * returns the ID of the attack
	 * @return id
	 */
	public int getId() {
		return id;
	}//getId()
	
	
	/**
	 * sets the ID of an attack
	 * @param id Integer
	 */
	public void setId(int id) {
		this.id = id;
	}//setId()
	
	
	/**
	 * adds an client to the list of clients used in this attack
	 * @param c Client
	 */
	public void addClient(Client c){
		this.Clients_used.add(c);
	}//addClient()
	
	
	/**
	 * removes a client from the list of clients used in this attack
	 * @param c Client
	 */
	public void removeClient(Client c){
		this.Clients_used.remove(this.Clients_used.indexOf(c));
	}//removeClient()
	
	
	/**
	 * returns the list of clients used in this attack
	 * @return clients_used List
	 */
	public List<Client> getClientList(){
		return this.Clients_used;
	}//getClientList()
	
	
	/**
	 * returns the attack state
	 * @return state
	 */
	public Attack_States getState() {
		return state;
	}//getState()


	/**
	 * sets state of attack
	 * @param state
	 */
	public void setState(Attack_States state) {
		this.state = state;
	}//setState()
	
}//class

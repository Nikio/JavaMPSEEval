package models;

import java.net.Inet4Address;

/**
 * Extends the attack class and defines the properties and functions of a Ping attack.
 * @author ulrike
 *
 */
public class Ping_Attack extends Attack{
	
	/**
	 * creates a new ping attack
	 * @param id integer
	 * @param dest Inet4Address
	 */
	public Ping_Attack(int id,Inet4Address dest){
		this.setId(id);
		this.setAttackType("Ping");
		this.setDestAddress(dest);
	}//constructor

}//class

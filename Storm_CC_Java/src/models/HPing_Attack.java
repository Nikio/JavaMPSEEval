package models;

import java.net.Inet4Address;

/**
 * Model for a HPing3 attack. Defines its parameters and functions.
 * @author ulrike
 *
 */
public class HPing_Attack extends Attack{

	private Inet4Address source;
	private int payloadsize;
	private String protocol;
	private String amplitude;
	
	
	/**
	 * creates a new HPing3 attack with the given parameters
	 * @param id Integer
	 * @param attackType String
	 * @param destAddress Inet4Address
	 * @param source Inet4Address
	 * @param payloadsize Integer
	 * @param protocol String
	 * @param amplitude String
	 */
	public HPing_Attack(int id, String attackType, Inet4Address destAddress, Inet4Address source,
			int payloadsize, String protocol, String amplitude){
		this.setId(id);
		this.setDestAddress(destAddress);
		this.setAttackType("HPing");
		this.setSource(source);
		this.setPayloadsize(payloadsize);
		this.setProtocol(protocol);
		this.setAmplitude(amplitude);
	}//constructor

	
	/**
	 * returns the source address used for this attack
	 * @return source: Inet4Address
	 */
	public Inet4Address getSource() {
		return source;
	}//getSource()

	
	/**
	 * sets the source IPv4 address used for this attack
	 * @param source Inet4Address
	 */
	public void setSource(Inet4Address source) {
		this.source = source;
	}//setSource()

	
	/**
	 * returns the payload size of the HPing attack
	 * @return payloadsize
	 */
	public int getPayloadsize() {
		return payloadsize;
	}//getPayloadsize()

	
	/**
	 * sets the payload size for this attack
	 * @param payloadsize Integer
	 */
	public void setPayloadsize(int payloadsize) {
		this.payloadsize = payloadsize;
	}//setPayloadsize()

	
	/**
	 * returns the protocol used
	 * @return protocol: String
	 */
	public String getProtocol() {
		return protocol;
	}//getProtocol()

	
	/**
	 * sets the protocol of the attack
	 * @param protocol String
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}//setProtocol()

	
	/**
	 * returns the amplitude of the attack
	 * @return amplitude
	 */
	public String getAmplitude() {
		return amplitude;
	}//getAmplitude()

	
	/**
	 * sets the amplitude of the attack
	 * @param amplitude String
	 */
	public void setAmplitude(String amplitude) {
		this.amplitude = amplitude;
	}//setAmplitude()
}//class

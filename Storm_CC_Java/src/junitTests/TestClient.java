package junitTests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import models.Client;

import org.junit.Test;

public class TestClient {

	
	/**
	 * checks client creation
	 * @throws UnknownHostException 
	 */
	@Test
	public void testClientCreation() throws UnknownHostException {
		Client c = new Client((Inet4Address) Inet4Address.getByName("1.1.1.1"));
		assertNotNull(c);
	}//testClientCreation()
	
	
	/**
	 * checks getHostname function
	 * @throws UnknownHostException
	 */
	@Test
	public void testClientGetHostname() throws UnknownHostException{
		Client c = new Client((Inet4Address) Inet4Address.getByName("1.1.1.1"));
		assertTrue(c.getIp_address().getHostName().toString().equals("1.1.1.1"));
	}
	
	
	/**
	 * test isBusy function
	 * @throws UnknownHostException
	 */
	@Test
	public void testClientIsBusy() throws UnknownHostException{
		Client c = new Client((Inet4Address) Inet4Address.getByName("1.1.1.1"));
		assertTrue(c.isBusy()==false);
	}
	
	
	/**
	 * tests setIsBusy() function
	 * @throws UnknownHostException
	 */
	@Test
	public void testClientSetIsBusy() throws UnknownHostException{
		Client c = new Client((Inet4Address) Inet4Address.getByName("1.1.1.1"));
		c.setBusy(true);
		assertTrue(c.isBusy()==true);
	}
}


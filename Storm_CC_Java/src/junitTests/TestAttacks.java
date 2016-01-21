package junitTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import models.Attack;
import models.Attack_States;
import models.HPing_Attack;
import models.Ping_Attack;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestAttacks {
	
	static Attack a,b;

	@BeforeClass
	public static void setup() throws UnknownHostException{
	a = new HPing_Attack(1, "HPing3", (Inet4Address) Inet4Address.getByName("1.1.1.2"), 
			(Inet4Address) Inet4Address.getByName("1.1.1.1"), 32, "udp", "flood");
	b = new Ping_Attack(2, (Inet4Address) Inet4Address.getByName("1.1.1.2"));
	
	}
	
	@Test
	public void testcreateHPingAttack() {
		assertNotNull(a);
	}
	
	@Test
	public void testcreatePingAttack() {
		assertNotNull(b);
	}
	
	@Test
	public void testHPingParameters() throws UnknownHostException{
		assertEquals(a.getDestAddress(), (Inet4Address) Inet4Address.getByName("1.1.1.2"));
		assertEquals(a.getId(), 1);
		assertNull(a.getState());
	}
	@Test
	public void testPingParameters() throws UnknownHostException{
		assertEquals(b.getDestAddress(), (Inet4Address) Inet4Address.getByName("1.1.1.2"));
		assertEquals(b.getId(), 2);
		assertNull(b.getState());
	}
	
	@Test
	public void testsetState(){
		a.setState(Attack_States.STARTING);
		assertEquals(a.getState(), Attack_States.STARTING);
	}

}

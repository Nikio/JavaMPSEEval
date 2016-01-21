package junitTests;

import static org.junit.Assert.*;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import models.Attack;
import models.Attack_States;
import models.Ping_Attack;

import org.junit.BeforeClass;
import org.junit.Test;

import controllers.AttackController;

public class TestAttackController {
	
	static AttackController ac;
	static Attack a;
	

	/**
	 * sets up the JUnit test class for the testing
	 * @throws UnknownHostException 
	 */
	@BeforeClass
	public static void setup() throws UnknownHostException{
		ac = new AttackController();
		a = new Ping_Attack(1, (Inet4Address) Inet4Address.getByName("1.1.1.1"));
		ac.addAttacktoList(a);
	}
	
	/**
	 * tests the attack controller creation
	 */
	@Test
	public void testcreateAttackController() {
		assertNotNull(ac);
	}
	
	/**
	 * tests the getAttackbyID function of the attack controller class
	 * @throws UnknownHostException 
	 */
	@Test
	public void testgetAttackbyID() throws UnknownHostException{
		assertNotNull(ac.getAttackbyID(1));
		Attack c = ac.getAttackbyID(1);
		assertEquals(1, c.getId());
		assertEquals((Inet4Address) Inet4Address.getByName("1.1.1.1"), c.getDestAddress());
	}
	
	
	/**
	 * tests the set state function of the attack controller class
	 */
	@Test
	public void testsetStates(){
		ac.getAttackbyID(1).setState(Attack_States.STARTING);
		assertEquals(Attack_States.STARTING, ac.getAttackbyID(1).getState());
	}

}

package junitTests;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import controllers.ClientContoller;

public class TestClientController {
	
	static ClientContoller c;
	
	
	/**
	 * sets up the JUnit test class for testing
	 */
	@BeforeClass
	public static void setup(){
		c= new ClientContoller();
	}
	
	/**
	 * tests the client controller creation
	 */
	@Test
	public void test() {
		assertNotNull(c);
	}

}

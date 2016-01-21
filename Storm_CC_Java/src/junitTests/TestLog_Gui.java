package junitTests;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestLog_Gui {
	static TestLog_Gui g;
	
	/**
	 * setup JUnit tests by creating a Log_Gui to test
	 */
	@BeforeClass
	public static void setup(){
		g = new TestLog_Gui();
	}

	/**
	 * tests if a Log_Gui was created
	 */
	@Test
	public void testcreateLogGui() {
		assertNotNull(g);
	}

}

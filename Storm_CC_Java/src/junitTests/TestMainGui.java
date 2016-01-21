package junitTests;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import views.Main_Gui;

public class TestMainGui {
	static Main_Gui m;

	/**
	 * sets up the JUnit test class for the testing
	 */
	@BeforeClass
	public static void setup(){
		m = new Main_Gui();
	}
	
	/**
	 * tests the creation of the main gui
	 */
	@Test
	public void test() {
		assertNotNull(m);
	}

}

package junitTests;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import views.Graph_GUI;

public class TestGraph_Gui {

	static Graph_GUI g;

	/**
	 * sets up the JUnit test class for the testing
	 */
	@BeforeClass
	public static void setup(){
		g = new Graph_GUI();
	}
	
	/**
	 * tests the creation of the graph gui
	 */
	@Test
	public void test() {
		assertNotNull(g);
	}
}

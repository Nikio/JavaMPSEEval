package junitTests;

import static org.junit.Assert.assertNotNull;

import java.sql.ResultSet;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import database.DatabaseConnection;

public class TestDatabaseConnection {
	
	static DatabaseConnection d;

	@BeforeClass
/**	public static void setup(){
		d = new DatabaseConnection();
		try{
			d.connect("TestDB.sqlite");
			
			//Create some test data to work on
			d.createClientMessageTable();
			d.insertInto("test", System.currentTimeMillis(), "2");
			
		}//try
		catch(Exception e){
			e.printStackTrace();
		}//catch
	}//setup
*/

	@Test
	public void testExecuteQuery() {
		try{
			ResultSet rs =d.executeQuery("select * from test");
			assertNotNull(rs);
			while (rs.next()){
				System.out.println(rs.getString(1)+" "+rs.getString(2));
			}//while	
		}//try
		catch(Exception e){
			e.printStackTrace();
		}//catch
	}//testExecuteQuery
	
	@AfterClass
	public static void teardown(){
		//empty test database
		d.execute("drop table test;");
		
		//Disconnect Database
		d.disconnect();	
	}//teardown

}

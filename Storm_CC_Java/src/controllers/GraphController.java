package controllers;

import java.sql.SQLException;
import java.util.Calendar;

import models.Attack;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.jdbc.JDBCCategoryDataset;

import database.DatabaseConnection;

public class GraphController {

	private DatabaseConnection dbc;
	private String tablename;
	private Attack attack;
	private int timeDifference;
	
	
	public GraphController(DatabaseConnection dbc){
		this.dbc = dbc;
		this.timeDifference = 30;
	}//constructor
	
	
	/**
	 * Creates a linechart
	 * @return a linechart
	 */
	public JFreeChart createChart(){
		JFreeChart chart = null;
		//int currenttime = (int) (System.currentTimeMillis() / 1000L);
		long currenttime = System.currentTimeMillis();
		//System.out.println("currenttime: " + currenttime);
		//SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm:ss");
		Calendar cal = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		try{
			cal.setTimeInMillis(currenttime);
			cal.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.get(Calendar.DATE));
		}catch(Exception e){
			e.printStackTrace();
		}
		currenttime = cal.getTime().getTime();
		currenttime /= 1000L;
		try{
			String query = "SELECT time,outvalue FROM " + this.tablename + " WHERE time >="+(currenttime-timeDifference);
			JDBCCategoryDataset dataset = new JDBCCategoryDataset(dbc.getConnection(), query);
			chart = ChartFactory.createLineChart(this.tablename, "time (epoch)", "value in kB/s", dataset, PlotOrientation.VERTICAL, false, true, true);

		}//try
		catch(SQLException e){
			e.printStackTrace();
		}//catch
		return chart;
	}//createChart()
	
	
	/**
	 * Sets the DatabaseConnection of the GraphController
	 * @param dbc
	 */
	public void setDatabaseConnection(DatabaseConnection dbc){
		this.dbc = dbc;
	}//setDatabaseConnection()
	
	
	/**
	 * Sets the Tablename of the Maintable for the SQLTable of the GraphController
	 * @param tablename
	 */
	public void setTablename(String tablename){
		this.tablename = tablename;
	}//setTablename()
	
	
	/**
	 * sets the attack for this graph
	 * @param attack
	 */
	public void setAttack(Attack attack){
		this.attack = attack;
		this.tablename = "Att"+Integer.toString(this.attack.getId());
	}//setAttack()
	
	
	/**
	 * returns the tablename
	 * @return tablename
	 */
	public String getTablename(){
		return this.tablename;
	}//getTablename()
	
	
	
}//class
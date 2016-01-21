package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import main.Main;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;

/**
 * Creates the graphical user interface that displays the graphs generated from the data sent by the clients for the 
 * selected attack.
 * @author ulrike
 * @author niklas
 *
 */
public class Graph_GUI extends JFrame {

	//private JPanel contentPane;
	//private Graph_GUI frame;

	private ChartPanel chartPanel;
	private Timer timer = new Timer(1000, new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			chartPanel.removeAll();
			chartPanel.setChart(Main.getGraphcontroller().createChart());
			chartPanel.revalidate();
		}
	});
	
	/**
	 * creates the Graph_GUI frame
	 */
	public Graph_GUI() {
		setBounds(100, 100, 614, 423);
		getContentPane().setLayout(new BorderLayout(0,0));
		setMinimumSize(new Dimension(750,400));
		
		JPanel panel = new JPanel();
		panel.setLayout(new java.awt.BorderLayout());
		panel.setBackground(Color.DARK_GRAY);
		getContentPane().add(panel);
		
		JFreeChart chart = Main.getGraphcontroller().createChart();
		BarRenderer renderer = null;
		CategoryPlot plot = null;
		renderer = new BarRenderer();
		ChartPanel CP = new ChartPanel(chart);
		this.chartPanel = CP;
		this.chartPanel.validate();
		panel.add(this.chartPanel, BorderLayout.CENTER);
		
		timer.start();
		/*JButton btnQueryChart = new JButton("Query Chart");
		btnQueryChart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					JFreeChart chart = Main.getGraphcontroller().createChart();
					BarRenderer renderer = null;
					CategoryPlot plot = null;
					renderer = new BarRenderer();
					ChartPanel CP = new ChartPanel(chart);
					panel.add(CP, BorderLayout.CENTER);
					panel.validate();
				}catch(Exception e){
					JOptionPane.showMessageDialog(null, e);
				}
			}
		});*/
		//getContentPane().add(btnQueryChart, BorderLayout.SOUTH);
		
		JButton btnClose = new JButton("Close");
 		btnClose.addActionListener(new ActionListener() {
 			public void actionPerformed(ActionEvent e) {
 				timer.stop();
				dispose();
 			}//actionPerformed
 		});
 		
 add(btnClose, BorderLayout.SOUTH);
		
	}//constructor
	
	
	//TODO show proper graphs for the attack chosen
	
}//class

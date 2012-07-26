package cbir.gui.panels;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.Date;
import java.util.Random;

import cbir.backend.MultiArchiveIndex;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;
import javafx.util.converter.TimeStringConverter;

public class StoreContentsChart extends JFXPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -422753755081812750L;
	private static final int TICKS_DIVISOR = 5;

	private LineChart chart;
	ObservableList<XYChart.Data<Float, Integer>> storeSeries;
	long startTime;
	private NumberAxis xAxis;

	public StoreContentsChart() {
		super();
		Dimension size = new Dimension(256,256); 
		setMinimumSize(size);
		setMaximumSize(size);
		setPreferredSize(size);
		startTime = -1;
		storeSeries = FXCollections.observableArrayList();
		// create timeline to add new data every 60th of second
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				initFX();
			}
		});
	}

	public void updateGraph(MultiArchiveIndex currentIndex) {
		final long currentTime = System.currentTimeMillis();
		final int elements = currentIndex.size();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if(startTime == -1) {
					startTime = System.currentTimeMillis();
				}
				float time = ((float)(currentTime-startTime))/60000f;
				if(time > xAxis.getUpperBound()) {
					xAxis.setUpperBound(5+xAxis.getUpperBound());
					xAxis.setTickUnit(xAxis.getUpperBound()/TICKS_DIVISOR);
				}
				storeSeries.add(new XYChart.Data<Float, Integer>(time,
						elements));
				
				
			}
		});

	}

	private void initFX() {
		// This method is invoked on the JavaFX thread
		Scene scene = createScene();
		setScene(scene);
	}

	/**
	 * A chart in which lines connect a series of data points. Useful for
	 * viewing data trends over time.
	 * 
	 * @see javafx.scene.chart.LineChart
	 * @see javafx.scene.chart.Chart
	 * @see javafx.scene.chart.Axis
	 * @see javafx.scene.chart.NumberAxis
	 * @related charts/area/AreaChart
	 * @related charts/scatter/ScatterChart
	 */
	private Scene createScene() {
//		Group root = new Group();
//		Scene scene = new Scene(root, getPreferredSize().width, getPreferredSize().height);

		xAxis = new NumberAxis(0, 5, 5/TICKS_DIVISOR);
		xAxis.setLabel("Time (m)");
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("# images");
		yAxis.setTickUnit(10);
		ObservableList<XYChart.Series<Float, Integer>> lineChartData = FXCollections
				.observableArrayList(new XYChart.Series<Float, Integer>(
						"Store Contents", storeSeries));

//		storeSeries.add(new XYChart.Data<Float, Integer>(0f, 0));
		chart = new LineChart(xAxis, yAxis, lineChartData);
		chart.setAnimated(false);
		chart.setLegendVisible(false);
		chart.setCreateSymbols(false);
		
//		chart.setManaged(true);
		
		
//		root.getChildren().add(chart);
		Scene scene = new Scene(chart, getPreferredSize().width, getPreferredSize().height);
		
		return scene;
	}
}

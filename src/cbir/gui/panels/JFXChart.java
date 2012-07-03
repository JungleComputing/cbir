package cbir.gui.panels;

import java.util.Random;

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

public class JFXChart extends JFXPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -422753755081812750L;
	
	private Timeline animation;
	private LineChart chart;

	public JFXChart() {
		super();
	    // create timeline to add new data every 60th of second
	    animation = new Timeline();
	    animation.getKeyFrames().add(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
	        @Override public void handle(ActionEvent actionEvent) {
	                modifyGraph();
	        }
	    }));
	    animation.setCycleCount(Animation.INDEFINITE);
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				initFX();
			}
		});
	}
	
	
	private void modifyGraph() {
		Random random = new Random();
		ObservableList<XYChart.Series<Double, Double>> data = chart.getData();
		ObservableList<XYChart.Data<Double, Double>> series = data.get(random.nextInt(data.size())).getData();
		int index = random.nextInt(series.size());
		series.get(index).setYValue(random.nextDouble()*3);
        repaint();
    }
	

	private void initFX() {
		// This method is invoked on the JavaFX thread
		Scene scene = createScene();
		setScene(scene);
		play();
	}

	private void play() {
		animation.play();
		
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
		Group root = new Group();
		Scene scene = new Scene(root);
		NumberAxis xAxis = new NumberAxis("Values for X-Axis", 0, 3, 1);
		NumberAxis yAxis = new NumberAxis("Values for Y-Axis", 0, 3, 1);
		ObservableList<XYChart.Series<Double, Double>> lineChartData = FXCollections
				.observableArrayList(
						new XYChart.Series<Double, Double>("Series 1",
								FXCollections.observableArrayList(
										new XYChart.Data<Double, Double>(0.0,
												1.0),
										new XYChart.Data<Double, Double>(1.2,
												1.4),
										new XYChart.Data<Double, Double>(2.2,
												1.9),
										new XYChart.Data<Double, Double>(2.7,
												2.3),
										new XYChart.Data<Double, Double>(2.9,
												0.5))),
						new XYChart.Series<Double, Double>("Series 2",
								FXCollections.observableArrayList(
										new XYChart.Data<Double, Double>(0.0,
												1.6),
										new XYChart.Data<Double, Double>(0.8,
												0.4),
										new XYChart.Data<Double, Double>(1.4,
												2.9),
										new XYChart.Data<Double, Double>(2.1,
												1.3),
										new XYChart.Data<Double, Double>(2.6,
												0.9))));
		
		chart = new LineChart(xAxis, yAxis, lineChartData);
		chart.setAnimated(false);
		root.getChildren().add(chart);
		return scene;
	}
}

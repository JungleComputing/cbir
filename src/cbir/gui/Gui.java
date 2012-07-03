package cbir.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import cbir.MatchTable;
import cbir.backend.MultiArchiveIndex;
import cbir.envi.FloatImage;
import cbir.envi.ImageIdentifier;
import cbir.envi.PreviewImage;
import cbir.gui.panels.BandComboBoxModel;
import cbir.gui.panels.JFXChart;
import cbir.gui.panels.LogPanel;
import cbir.gui.panels.ScrollableImagePanel;

public class Gui implements ActionListener {

	protected static final String FILE_TEXT = "fileText";
	protected static final String BROWSE = "browse";
	protected static final String RED = "red";
	protected static final String GREEN = "green";
	protected static final String BLUE = "blue";
	protected static final String REFRESH = "refresh";
	protected static final String SEARCH = "search";
	protected static final String UPDATE_INDEX = "update";

	private static final String IBIS_LOGO = "images/ibis-logo.png";
	private static final String VU_LOGO = "images/vu-new-logo.png";
	private static final String UNEX_LOGO = "images/unex-logo.png";
	private static final String NLESC_LOGO = "images/nlesc-logo.jpg";
	private JFileChooser fc;

	private QueryInput queryInput;

	private JFrame frame;

	// private ImagePanel imagePanel;

	private JTextField fileNameField;

	private JComboBox<String> redComboBox;
	private JComboBox<String> greenComboBox;
	private JComboBox<String> blueComboBox;

	private LogPanel log;

	private ResultsListModel results;
	private JList<ResultElement> resultPreview;
	private JList<ResultElement> tables;

	private JScrollPane rpContainer;
	private ScrollableImagePanel previewPanel;
	// private JScrollPane resultsScrollPane;
	private Controller controller;
	private MultiArchiveIndex index;

	/**
	 * Create the application.
	 */
	public Gui() {
		queryInput = new QueryInput();
		initialize();
		frame.setTitle("CBIR Search");
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		fc = new JFileChooser();
		fc.setMultiSelectionEnabled(false);
		fc.addChoosableFileFilter(new FileNameExtensionFilter(
				"ENVI header files", "hdr"));

		frame = new JFrame();
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1024, 768);
		frame.getContentPane().setLayout(new BorderLayout());

		JPanel topBorder = new JPanel();
		((FlowLayout) topBorder.getLayout()).setAlignment(FlowLayout.LEFT);

		frame.getContentPane().add(topBorder, BorderLayout.NORTH);

		fileNameField = new JTextField("/home/timo/oilspill/f100930t01p00r06rdn_b_sc01_ort_img");
		fileNameField.setColumns(40);
		fileNameField.setActionCommand(FILE_TEXT);
		fileNameField.addActionListener(this);
		topBorder.add(fileNameField);

		JButton fileChooserButton = new JButton("Browse");
		fileChooserButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
		fileChooserButton.setActionCommand(BROWSE);
		fileChooserButton.addActionListener(this);
		topBorder.add(fileChooserButton);

		JPanel bandSelectionPanel = new JPanel();
		topBorder.add(bandSelectionPanel);

		JLabel redLabel = new JLabel("Red");
		bandSelectionPanel.add(redLabel);
		redComboBox = new JComboBox<String>();
		redComboBox.setActionCommand(RED);
		bandSelectionPanel.add(redComboBox);

		JLabel greenLabel = new JLabel("Green");
		bandSelectionPanel.add(greenLabel);
		greenComboBox = new JComboBox<String>();
		greenComboBox.setActionCommand(GREEN);
		bandSelectionPanel.add(greenComboBox);

		JLabel blueLabel = new JLabel("Blue");
		bandSelectionPanel.add(blueLabel);
		blueComboBox = new JComboBox<String>();
		blueComboBox.setActionCommand(BLUE);
		bandSelectionPanel.add(blueComboBox);

		JButton applyButton = new JButton("Apply");
		applyButton.setActionCommand(REFRESH);
		applyButton.addActionListener(this);
		topBorder.add(applyButton);

		JButton searchButton = new JButton("Search");
		searchButton.setActionCommand(SEARCH);
		searchButton.addActionListener(this);
		topBorder.add(searchButton);

		JButton updateButton = new JButton("Update");
		updateButton.setActionCommand(UPDATE_INDEX);
		updateButton.addActionListener(this);
		topBorder.add(updateButton);

		JSplitPane horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		horizontalSplit.setResizeWeight(0);
		frame.getContentPane().add(horizontalSplit, BorderLayout.CENTER);

		// ---- left panel----
		JSplitPane left = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		left.setResizeWeight(0.66);
		left.setDividerLocation(0.5);
		horizontalSplit.add(left, JSplitPane.TOP);

		previewPanel = new ScrollableImagePanel("Preview");
		left.add(previewPanel, JSplitPane.TOP);

		log = new LogPanel("Log");
		left.add(log, JSplitPane.BOTTOM);

		// ---- right panel----
		
		//FIXME disabled JFXgraph in splitpane for demo movie
//		JSplitPane right = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
//		horizontalSplit.add(right, JSplitPane.BOTTOM);

		results = new ResultsListModel();

		tables = new JList<ResultElement>(results);
		tables.setCellRenderer(new ListCellRenderer<ResultElement>() {
			@Override
			public Component getListCellRendererComponent(
					JList<? extends ResultElement> list, ResultElement value,
					int index, boolean isSelected, boolean cellHasFocus) {
				return new JLabel(value.getImageID().getName());
			}
		});

		resultPreview = new JList<ResultElement>(results);
		//FIXME disabled JFXgraph in splitpane for demo movie
//		resultPreview.setBackground(right.getBackground());
		resultPreview.setBackground(horizontalSplit.getBackground());

		resultPreview.setCellRenderer(new ListCellRenderer<ResultElement>() {
			@Override
			public Component getListCellRendererComponent(
					JList<? extends ResultElement> list, ResultElement value,
					int index, boolean isSelected, boolean cellHasFocus) {
				return value.getResultTile(isSelected, cellHasFocus);
			}
		});

		resultPreview.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultPreview.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		resultPreview.setVisibleRowCount(-1);

		// resultsPanel = new ResultsPanel();
		// resultsPanel.setPreferredSize(new Dimension(512, 512));
		// resultsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));

		// rpContainer = new JScrollPane(resultsPanel);


		rpContainer = new JScrollPane(resultPreview);

		//FIXME disabled JFXgraph in splitpane for demo movie
		horizontalSplit.add(rpContainer, JSplitPane.BOTTOM);
//		right.add(rpContainer, JSplitPane.TOP);

//		right.add(new JFXChart(), JSplitPane.BOTTOM);

		// ------------------------------------------

		JPanel logoBar = new JPanel();
		FlowLayout flowLayout = (FlowLayout) logoBar.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		JLabel ibisLabel = new JLabel(new ImageIcon(IBIS_LOGO));
		logoBar.add(ibisLabel);
		JLabel vuLabel = new JLabel(new ImageIcon(VU_LOGO));
		logoBar.add(vuLabel);
		JLabel unexLabel = new JLabel(new ImageIcon(UNEX_LOGO));
		logoBar.add(unexLabel);
		JLabel nlescLabel = new JLabel(new ImageIcon(NLESC_LOGO));
		logoBar.add(nlescLabel);

		frame.getContentPane().add(logoBar, BorderLayout.SOUTH);
		updateFile();
		updatePreviews();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if (command.equals(BROWSE)) {
			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				String queryURI = file.getAbsolutePath();
				// remove file extension
				queryURI = queryURI.substring(0, queryURI.length() - 4);
				fileNameField.setText(queryURI);
				updateFile();
				updatePreviews();
			}
		} else if (command.equals(FILE_TEXT)) {
			updateFile();
			updatePreviews();
		} else if (command.equals(REFRESH)) {
			updatePreviews();
		} else if (command.equals(SEARCH)) {
			startSearch(queryInput);
		} else if (command.equals(UPDATE_INDEX)) {
			updateIndex();
		} else {
			System.out.println("actionPerformed(): unknown actionCommand: "
					+ command);
		}
	}

	private void startSearch(QueryInput query) {
		log.addLine("Search started for " + query.getHeader().getID().getName());
		controller.doQuery(query);
	}

	private void updateIndex() {
		log.addLine("Updating index");
		controller.requestIndex();
	}

	private void updatePreviews() {
		BufferedImage image = queryInput.getRGBImage(
				redComboBox.getSelectedIndex(),
				greenComboBox.getSelectedIndex(),
				blueComboBox.getSelectedIndex());
		previewPanel.setImage(image);
		fetchResultPreviews();
		resultPreview.revalidate();
		resultPreview.repaint();
	}

	private void updateFile() {
		String image = fileNameField.getText();
		if (queryInput.getFile().equals(image)) {
			log.addLine("Image not changed: " + image);
			return;
		}
		queryInput.setFromFile(image);

		log.addLine("new image loaded: " + image);

		String[] bands = queryInput.getBandNames();

		// ((BandComboBoxModel<String>)redComboBox.getModel()).setElements(bands);
		// ((BandComboBoxModel<String>)greenComboBox.getModel()).setElements(bands);
		// ((BandComboBoxModel<String>)blueComboBox.getModel()).setElements(bands);

		redComboBox.setModel(new BandComboBoxModel<String>(bands, redComboBox
				.getSelectedIndex()));
		greenComboBox.setModel(new BandComboBoxModel<String>(bands,
				greenComboBox.getSelectedIndex()));
		blueComboBox.setModel(new BandComboBoxModel<String>(bands, blueComboBox
				.getSelectedIndex()));

		results.clear();
	}

	// function below should be called from the EventQueue Thread

	private void fetchResultPreviews() {
		int red = redComboBox.getSelectedIndex();
		int green = greenComboBox.getSelectedIndex();
		int blue = blueComboBox.getSelectedIndex();

		// fetch preview images
		for (ResultElement result : results.getContents()) {
			ImageIdentifier imageID = result.getImageID();
			Set<String> storeSet = index.getStoresFor(imageID);
			String[] stores = storeSet.toArray(new String[storeSet.size()]);
			controller.requestPreviewImage(imageID, red, green, blue, stores);
		}
	}

	public void enable() {
		frame.setVisible(true);
	}

	// functions below should be called from the EventQueue Thread

	protected void deliverResult(MatchTable[] matchData) {
		log.addLine("Results received");
		// update results pane
		results.updateResults(matchData);
//		resultPreview.revalidate();

		fetchResultPreviews();
	}

	protected void deliverImage(FloatImage image) {
		log.addLine("Image received");
		queryInput.setFromImage(image);
	}

	protected void deliverPreview(PreviewImage preview) {
//		log.addLine("Preview received");
		results.deliverPreview(preview);
//		resultPreview.revalidate();
//		resultPreview.repaint();
	}

	protected void deliverIndex(MultiArchiveIndex index) {
		log.addLine(String.format("Store contains %d images", index.size()));
		this.index = index;

	}

}

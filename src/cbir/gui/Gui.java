package cbir.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import cbir.Config;
import cbir.MatchTable;
import cbir.backend.MultiArchiveIndex;
import cbir.backend.SingleArchiveIndex;
import cbir.envi.EnviHeader;
import cbir.envi.FloatImage;
import cbir.envi.ImageIdentifier;
import cbir.envi.PreviewImage;
import cbir.gui.panels.BandComboBoxModel;
import cbir.gui.panels.ImageIdentifierListCellRenderer;
import cbir.gui.panels.LogPanel;
import cbir.gui.panels.PopupMouseAdapter;
import cbir.gui.panels.ResultElement;
import cbir.gui.panels.ResultsListModel;
import cbir.gui.panels.ResultsPopupMenu;
import cbir.gui.panels.ScrollableImagePanel;
import cbir.gui.panels.StoreContentsChart;
import cbir.gui.panels.StoreIndexListModel;

public class Gui implements ActionListener {

    // private static final String INPUT_FILE =
    // "/home/timo/oilspill/f100930t01p00r06rdn_b_sc01_ort_img";

    protected static final String BROWSE = "browse";
    protected static final String RED = "red";
    protected static final String GREEN = "green";
    protected static final String BLUE = "blue";
    protected static final String REFRESH_BANDS = "refresh";
    protected static final String SEARCH = "search";
    protected static final String UPDATE_INDEX = "update";
    protected static final String SELECT_FROM_STORE = "fromStore";
    protected static final String SELECT_FROM_RESULTS = "fromResults";

    private static final String IBIS_LOGO = "images/ibis-logo.png";
    private static final String VU_LOGO = "images/vu-new-logo.png";
    private static final String UNEX_LOGO = "images/unex-logo.png";
    private static final String NLESC_LOGO = "images/nlesc-logo.jpg";

    private static final String BANDS_WAVELENGTH = "BANDS_WAVELENGTH";
    private static final String BANDS_NAME = "BANDS_NAME";
    private static final String BANDS_INDEX = "BANDS_INDEX";

    // create a Thread that polls for the database
//    private class IndexUpdater extends Thread {
//        private static final int MIN_SLEEP_TIME = 100;
//        private static final long POLL_RATE = 5000;
//        private final Gui gui;
//        private volatile boolean updateDelivered;
//
//        public IndexUpdater(Gui gui) {
//            this.gui = gui;
//            updateDelivered = true; // initially all request are fulfilled
//        }
//
//        public void run() {
//            long nextUpdate = System.currentTimeMillis() + POLL_RATE;
//            while (true) {
//                while (!updateDelivered
//                        || System.currentTimeMillis() < nextUpdate) {
//                    try {
//                        Thread.sleep(Math.max(
//                                nextUpdate - System.currentTimeMillis(),
//                                MIN_SLEEP_TIME));
//                    } catch (InterruptedException e) {
//                        // ignore
//                    }
//                }
//                
//                gui.updateIndex();
//                nextUpdate = System.currentTimeMillis() + POLL_RATE;
//                updateDelivered = false;
//
//            }
//        }
//
//        private void updated() {
//            updateDelivered = true;
//        }
//    }

    private JFileChooser fc;

    private QueryInput queryInput;

//    private IndexUpdater indexUpdater;
    private JFrame frame;

    // private ImagePanel imagePanel;

    // private JTextField fileNameField;

    private JComboBox<String> redComboBox;
    private JComboBox<String> greenComboBox;
    private JComboBox<String> blueComboBox;

    private LogPanel log;

    private ResultsListModel results;
    private JList<ResultElement> resultPreview;

    private ScrollableImagePanel previewPanel;
    // private JScrollPane resultsScrollPane;
    private Controller controller;
    private MultiArchiveIndex index;
    StoreIndexListModel storeIndexModel;
    private JList<ImageIdentifier> imageList;
    private StoreContentsChart storeChart;

    protected String bandUnits;

    /**
     * Create the application.
     */
    public Gui() {
        queryInput = new QueryInput();
        index = new MultiArchiveIndex();
        storeIndexModel = new StoreIndexListModel();
        storeChart = new StoreContentsChart();
        bandUnits = BANDS_INDEX;
//        indexUpdater = new IndexUpdater(this);
    }

    public void setController(Controller controller) {
        this.controller = controller;
        initialize();
    }

    private JPanel CreateBandUnitPanel() {
        JPanel result = new JPanel();
        result.setBorder(new TitledBorder(new LineBorder(new Color(184, 207,
                229)), "Band Labels", TitledBorder.LEADING, TitledBorder.TOP,
                null, null));
        // result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
        result.setLayout(new GridBagLayout());
        // JLabel bandUnitLabel = new JLabel("Band Naming");
        // GridBagConstraints gbc_bandUnitLabel = new GridBagConstraints();
        // gbc_bandUnitLabel.gridwidth = 2;
        // gbc_bandUnitLabel.anchor = GridBagConstraints.WEST;
        // gbc_bandUnitLabel.insets = new Insets(5, 5, 5, 5);
        // gbc_bandUnitLabel.gridx = 2;
        // gbc_bandUnitLabel.gridy = 0;
        // result.add(bandUnitLabel, gbc_bandUnitLabel);

        JRadioButton rdbtnBandIndex = new JRadioButton("Band Number");
        rdbtnBandIndex.setActionCommand(BANDS_INDEX);
        rdbtnBandIndex.setSelected(true);
        GridBagConstraints gbc_rdbtnBandIndex = new GridBagConstraints();
        gbc_rdbtnBandIndex.gridwidth = 1;
        gbc_rdbtnBandIndex.anchor = GridBagConstraints.WEST;
        gbc_rdbtnBandIndex.insets = new Insets(5, 5, 5, 5);
        gbc_rdbtnBandIndex.gridx = 0;
        gbc_rdbtnBandIndex.gridy = 0;
        result.add(rdbtnBandIndex, gbc_rdbtnBandIndex);
        JRadioButton rdbtnWavelength = new JRadioButton("Wavelength");
        rdbtnWavelength.setMargin(new Insets(3, 2, 3, 2));
        rdbtnWavelength.setActionCommand(BANDS_WAVELENGTH);
        GridBagConstraints gbc_rdbtnWavelength = new GridBagConstraints();
        gbc_rdbtnWavelength.gridwidth = 1;
        gbc_rdbtnWavelength.anchor = GridBagConstraints.WEST;
        gbc_rdbtnWavelength.insets = new Insets(5, 5, 5, 5);
        gbc_rdbtnWavelength.gridx = 0;
        gbc_rdbtnWavelength.gridy = 1;
        result.add(rdbtnWavelength, gbc_rdbtnWavelength);

        JRadioButton rdbtnBandName = new JRadioButton("Band Name");
        rdbtnBandName.setActionCommand(BANDS_NAME);
        GridBagConstraints gbc_rdbtnBandName = new GridBagConstraints();
        gbc_rdbtnBandName.gridwidth = 1;
        gbc_rdbtnBandName.anchor = GridBagConstraints.WEST;
        gbc_rdbtnBandName.insets = new Insets(5, 5, 5, 5);
        gbc_rdbtnBandName.gridx = 0;
        gbc_rdbtnBandName.gridy = 2;
        result.add(rdbtnBandName, gbc_rdbtnBandName);

        ActionListener BandUnitListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                bandUnits = e.getActionCommand();
                updateBandBoxes();
            }
        };
        rdbtnBandIndex.addActionListener(BandUnitListener);
        rdbtnWavelength.addActionListener(BandUnitListener);
        rdbtnBandName.addActionListener(BandUnitListener);

        ButtonGroup bandUnit = new ButtonGroup();
        bandUnit.add(rdbtnBandIndex);
        bandUnit.add(rdbtnWavelength);
        bandUnit.add(rdbtnBandName);

        return result;
    }

    private JPanel createStoreContentsPanel() {
        JPanel storeContents = new JPanel();
        storeContents.setBorder(new TitledBorder(null, "Store Contents",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagLayout gbl = new GridBagLayout();
        storeContents.setLayout(gbl);

        imageList = new JList<ImageIdentifier>(storeIndexModel);

        imageList.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
                null, null));
        imageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        imageList.setLayoutOrientation(JList.VERTICAL);
        imageList.setVisibleRowCount(-1);
        imageList.setCellRenderer(new ImageIdentifierListCellRenderer());

        JScrollPane ilContainer = new JScrollPane(imageList);
        GridBagConstraints gbc_imageList = new GridBagConstraints();
        gbc_imageList.anchor = GridBagConstraints.NORTH;
        gbc_imageList.insets = new Insets(5, 5, 5, 5);
        gbc_imageList.weighty = 1.0;
        gbc_imageList.fill = GridBagConstraints.BOTH;
        gbc_imageList.gridy = 0;
        gbc_imageList.gridx = 0;
        storeContents.add(ilContainer, gbc_imageList);

        // JPanel buttonBar = new JPanel();

        GridBagConstraints gbc_chart = new GridBagConstraints();
        gbc_chart.fill = GridBagConstraints.BOTH;
        gbc_chart.anchor = GridBagConstraints.SOUTH;
        gbc_chart.insets = new Insets(5, 5, 5, 5);
        gbc_chart.gridy = 2;
        gbc_chart.gridx = 0;
        storeContents.add(storeChart, gbc_chart);

        JButton selectFromStoreButton = new JButton("Use as Query");
        selectFromStoreButton.setActionCommand(SELECT_FROM_STORE);
        selectFromStoreButton.addActionListener(this);
        GridBagConstraints gbc_select = new GridBagConstraints();
        gbc_select.insets = new Insets(5, 5, 5, 5);
        gbc_select.anchor = GridBagConstraints.NORTH;
        gbc_select.gridy = 1;
        gbc_select.gridx = 0;
        gbc_select.fill = GridBagConstraints.HORIZONTAL;
        storeContents.add(selectFromStoreButton, gbc_select);

        // JButton updateButton = new JButton("Update");
        // updateButton.setHorizontalAlignment(SwingConstants.RIGHT);
        // updateButton.setActionCommand(UPDATE_INDEX);
        // updateButton.addActionListener(this);
        // GridBagConstraints gbc_update = new GridBagConstraints();
        // gbc_update.insets = new Insets(5, 5, 5, 5);
        // gbc_update.anchor = GridBagConstraints.SOUTHEAST;
        // gbc_update.gridy = 2;
        // gbc_update.gridx = 1;
        // storeContents.add(updateButton, gbc_update);

        return storeContents;
    }

    private JPanel createQueryImagePanel() {
        JPanel queryImagePanel = new JPanel();
        queryImagePanel.setBorder(new TitledBorder(null, "Query",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagLayout gbl_topLeftPanel = new GridBagLayout();
        queryImagePanel.setLayout(gbl_topLeftPanel);

        JButton searchButton = new JButton("Start Query");
        searchButton.setActionCommand(SEARCH);
        searchButton.addActionListener(this);

        previewPanel = new ScrollableImagePanel(new Dimension(256, 256));
        previewPanel.setViewportBorder(new LineBorder(Color.BLACK, 2));
        GridBagConstraints gbc_previewPanel = new GridBagConstraints();
        gbc_previewPanel.anchor = GridBagConstraints.NORTH;
        gbc_previewPanel.fill = GridBagConstraints.HORIZONTAL;
        gbc_previewPanel.gridwidth = 2;
        gbc_previewPanel.insets = new Insets(5, 5, 5, 5);
        gbc_previewPanel.gridx = 0;
        gbc_previewPanel.gridy = 0;
        queryImagePanel.add(previewPanel, gbc_previewPanel);

        JButton fileChooserButton = new JButton("Open File");
        fileChooserButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        fileChooserButton.setActionCommand(BROWSE);
        fileChooserButton.addActionListener(this);
        GridBagConstraints gbc_fileChooserButton = new GridBagConstraints();
        gbc_fileChooserButton.anchor = GridBagConstraints.SOUTH;
        gbc_fileChooserButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_fileChooserButton.insets = new Insets(5, 5, 5, 5);
        gbc_fileChooserButton.gridx = 0;
        gbc_fileChooserButton.gridy = 1;
        queryImagePanel.add(fileChooserButton, gbc_fileChooserButton);
        GridBagConstraints gbc_searchButton = new GridBagConstraints();
        gbc_searchButton.anchor = GridBagConstraints.SOUTH;
        gbc_searchButton.insets = new Insets(5, 5, 5, 5);
        gbc_searchButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_searchButton.gridx = 1;
        gbc_searchButton.gridy = 1;
        queryImagePanel.add(searchButton, gbc_searchButton);
        return queryImagePanel;
    }

    private JPanel createBandSelectionPanel() {
        JPanel bandSelectionPanel = new JPanel();
        bandSelectionPanel.setBorder(new TitledBorder(null, "Bands",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagLayout gbl_bandSelectionPanel = new GridBagLayout();
        bandSelectionPanel.setLayout(gbl_bandSelectionPanel);

        JLabel redLabel = new JLabel("Red");
        GridBagConstraints gbc_redLabel = new GridBagConstraints();
        gbc_redLabel.anchor = GridBagConstraints.WEST;
        gbc_redLabel.fill = GridBagConstraints.HORIZONTAL;
        gbc_redLabel.insets = new Insets(5, 5, 5, 5);
        gbc_redLabel.gridx = 0;
        gbc_redLabel.gridy = 0;
        bandSelectionPanel.add(redLabel, gbc_redLabel);
        redComboBox = new JComboBox<String>();
        GridBagConstraints gbc_redComboBox = new GridBagConstraints();
        gbc_redComboBox.anchor = GridBagConstraints.NORTHEAST;
        gbc_redComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_redComboBox.insets = new Insets(5, 5, 5, 5);
        gbc_redComboBox.gridx = 1;
        gbc_redComboBox.gridy = 0;
        bandSelectionPanel.add(redComboBox, gbc_redComboBox);
        redComboBox.setActionCommand(RED);

        JLabel greenLabel = new JLabel("Green");
        GridBagConstraints gbc_greenLabel = new GridBagConstraints();
        gbc_greenLabel.anchor = GridBagConstraints.WEST;
        gbc_greenLabel.fill = GridBagConstraints.HORIZONTAL;
        gbc_greenLabel.insets = new Insets(5, 5, 5, 5);
        gbc_greenLabel.gridx = 0;
        gbc_greenLabel.gridy = 1;
        bandSelectionPanel.add(greenLabel, gbc_greenLabel);
        greenComboBox = new JComboBox<String>();
        GridBagConstraints gbc_greenComboBox = new GridBagConstraints();
        gbc_greenComboBox.anchor = GridBagConstraints.NORTHEAST;
        gbc_greenComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_greenComboBox.insets = new Insets(5, 5, 5, 5);
        gbc_greenComboBox.gridx = 1;
        gbc_greenComboBox.gridy = 1;
        bandSelectionPanel.add(greenComboBox, gbc_greenComboBox);
        greenComboBox.setActionCommand(GREEN);

        JLabel blueLabel = new JLabel("Blue");
        GridBagConstraints gbc_blueLabel = new GridBagConstraints();
        gbc_blueLabel.anchor = GridBagConstraints.WEST;
        gbc_blueLabel.fill = GridBagConstraints.HORIZONTAL;
        gbc_blueLabel.insets = new Insets(5, 5, 5, 5);
        gbc_blueLabel.gridx = 0;
        gbc_blueLabel.gridy = 2;
        bandSelectionPanel.add(blueLabel, gbc_blueLabel);
        blueComboBox = new JComboBox<String>();
        GridBagConstraints gbc_blueComboBox = new GridBagConstraints();
        gbc_blueComboBox.anchor = GridBagConstraints.NORTHEAST;
        gbc_blueComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_blueComboBox.insets = new Insets(5, 5, 5, 5);
        gbc_blueComboBox.gridx = 1;
        gbc_blueComboBox.gridy = 2;
        bandSelectionPanel.add(blueComboBox, gbc_blueComboBox);
        blueComboBox.setActionCommand(BLUE);

        JButton applyButton = new JButton("Apply");
        applyButton.setActionCommand(REFRESH_BANDS);
        applyButton.addActionListener(this);

        // JCheckBox showBandNamesBox = new JCheckBox("Bandnames");
        // showBandNamesBox.setSelected(showBandNames);
        // showBandNamesBox.addItemListener(new ItemListener() {
        // @Override
        // public void itemStateChanged(ItemEvent e) {
        // showBandNames = !showBandNames;
        // updateBandBoxes();
        // }
        // });
        // GridBagConstraints gbc_showBandNamesBox = new GridBagConstraints();
        // gbc_showBandNamesBox.fill = GridBagConstraints.BOTH;
        // gbc_showBandNamesBox.insets = new Insets(0, 0, 5, 5);
        // gbc_showBandNamesBox.gridx = 0;
        // gbc_showBandNamesBox.gridy = 4;
        // bandSelectionPanel.add(showBandNamesBox, gbc_showBandNamesBox);

        GridBagConstraints gbc_applyButton = new GridBagConstraints();
        gbc_applyButton.anchor = GridBagConstraints.NORTHEAST;
        gbc_applyButton.insets = new Insets(5, 5, 5, 5);
        gbc_applyButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_applyButton.gridx = 1;
        gbc_applyButton.gridy = 3;
        bandSelectionPanel.add(applyButton, gbc_applyButton);

        return bandSelectionPanel;
    }

    private JPanel createResultPreviewPanel() {
        JPanel container = new JPanel();
        container.setLayout(new GridBagLayout());

        results = new ResultsListModel();
        resultPreview = new JList<ResultElement>(results);
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

        resultPreview.addMouseListener(new PopupMouseAdapter(
                new ResultsPopupMenu(this)));

        JScrollPane scrollPane = new JScrollPane(resultPreview);
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.insets = new Insets(5, 5, 5, 5);
        gbc_scrollPane.weightx = 1.0;
        gbc_scrollPane.weighty = 1.0;
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.anchor = GridBagConstraints.NORTH;
        gbc_scrollPane.gridy = 0;
        gbc_scrollPane.gridx = 0;
        container.add(scrollPane, gbc_scrollPane);

        JButton selectAsQueryButton = new JButton("Use as Query");
        selectAsQueryButton.setHorizontalAlignment(SwingConstants.LEFT);
        selectAsQueryButton.setActionCommand(SELECT_FROM_RESULTS);
        selectAsQueryButton.addActionListener(this);
        GridBagConstraints gbc_select = new GridBagConstraints();
        gbc_select.insets = new Insets(5, 5, 5, 5);
        gbc_select.anchor = GridBagConstraints.SOUTHWEST;
        gbc_select.gridy = 1;
        gbc_select.gridx = 0;
        container.add(selectAsQueryButton, gbc_select);

        container.setBorder(new TitledBorder(new LineBorder(new Color(184, 207,
                229)), "Results", TitledBorder.LEADING, TitledBorder.TOP, null,
                null));

        return container;
    }

    /**
     * Initialize the contents of the frame.
     * 
     * @wbp.parser.entryPoint
     */
    private void initialize() {
        fc = new JFileChooser();
        fc.setMultiSelectionEnabled(false);
        fc.addChoosableFileFilter(new FileNameExtensionFilter(
                "ENVI header files", "hdr"));

        frame = new JFrame();
        frame.setTitle("CBIR Search");
        frame.setLocationByPlatform(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Config.GUI_WIDTH, Config.GUI_HEIGHT);
        frame.setMinimumSize(frame.getSize());

        // fileNameField = new JTextField(
        // "/home/timo/oilspill/f100930t01p00r06rdn_b_sc01_ort_img");
        // fileNameField.setColumns(40);
        // fileNameField.setActionCommand(FILE_TEXT);
        // fileNameField.addActionListener(this);
        // topBorder.add(fileNameField);

        Container mainPanel = frame.getContentPane();
        // JSplitPane horizontalSplit = new
        // JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        // horizontalSplit.setResizeWeight(0);
        // frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
        GridBagLayout gbl_mainPanel = new GridBagLayout();
        mainPanel.setLayout(gbl_mainPanel);

        // ---- left panel----
        // JSplitPane left = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        // left.setResizeWeight(0.66);
        // left.setDividerLocation(0.5);

        // horizontalSplit.add(left, JSplitPane.TOP);

        JPanel queryImagePanel = createQueryImagePanel();
        GridBagConstraints gbc_queryImagePanel = new GridBagConstraints();
        gbc_queryImagePanel.gridwidth = 2;
        gbc_queryImagePanel.insets = new Insets(5, 5, 5, 5);
        gbc_queryImagePanel.gridx = 0;
        gbc_queryImagePanel.gridy = 0;
        gbc_queryImagePanel.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(queryImagePanel, gbc_queryImagePanel);

        JPanel bandUnitPanel = CreateBandUnitPanel();
        GridBagConstraints gbc_bandUnitPanel = new GridBagConstraints();
        gbc_bandUnitPanel.gridwidth = 1;
        gbc_bandUnitPanel.insets = new Insets(5, 5, 5, 5);
        gbc_bandUnitPanel.anchor = GridBagConstraints.NORTH;
        gbc_bandUnitPanel.gridx = 0;
        gbc_bandUnitPanel.gridy = 1;
        gbc_bandUnitPanel.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(bandUnitPanel, gbc_bandUnitPanel);

        JPanel bandSelectionPanel = createBandSelectionPanel();
        GridBagConstraints gbc_bandSelectionPanel = new GridBagConstraints();
        gbc_bandSelectionPanel.gridwidth = 1;
        gbc_bandSelectionPanel.insets = new Insets(5, 5, 5, 5);
        gbc_bandSelectionPanel.anchor = GridBagConstraints.NORTH;
        gbc_bandSelectionPanel.gridx = 1;
        gbc_bandSelectionPanel.gridy = 1;
        gbc_bandSelectionPanel.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(bandSelectionPanel, gbc_bandSelectionPanel);

        JPanel storeContentsPanel = createStoreContentsPanel();
        GridBagConstraints gbc_storeContentsPanel = new GridBagConstraints();
        gbc_storeContentsPanel.insets = new Insets(5, 5, 5, 5);
        gbc_storeContentsPanel.anchor = GridBagConstraints.NORTH;
        gbc_storeContentsPanel.gridx = 2;
        gbc_storeContentsPanel.gridy = 0;
        gbc_storeContentsPanel.gridwidth = 1;
        gbc_storeContentsPanel.gridheight = 3;
        gbc_storeContentsPanel.fill = GridBagConstraints.BOTH;
        mainPanel.add(storeContentsPanel, gbc_storeContentsPanel);

        // ---- right panel----

        // FIXME disabled JFXgraph in splitpane for demo movie
        // JSplitPane right = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        // horizontalSplit.add(right, JSplitPane.BOTTOM);

        // create a popup for the results panel
        JPanel rpContainer = createResultPreviewPanel();

        GridBagConstraints gbc_rpContainer = new GridBagConstraints();
        gbc_rpContainer.insets = new Insets(5, 5, 5, 5);
        gbc_rpContainer.weighty = 1.0;
        gbc_rpContainer.weightx = 1.0;
        gbc_rpContainer.gridx = 3;
        gbc_rpContainer.gridy = 0;
        gbc_rpContainer.gridheight = 3;
        gbc_rpContainer.fill = GridBagConstraints.BOTH;

        // FIXME disabled JFXgraph in splitpane for demo movie
        // horizontalSplit.add(rpContainer, JSplitPane.BOTTOM);
        mainPanel.add(rpContainer, gbc_rpContainer);

        JPanel logoBar = new JPanel();
        GridBagLayout gbl_logoBar = new GridBagLayout();
        logoBar.setLayout(gbl_logoBar);
        // right.add(rpContainer, JSplitPane.TOP);

        // right.add(new JFXChart(), JSplitPane.BOTTOM);

        // ------------------------------------------

        JLabel vuLabel = new JLabel(new ImageIcon(VU_LOGO));
        GridBagConstraints gbc_vuLabel = new GridBagConstraints();
        gbc_vuLabel.gridwidth = 3;
        gbc_vuLabel.anchor = GridBagConstraints.SOUTH;
        gbc_vuLabel.insets = new Insets(5, 5, 5, 5);
        gbc_vuLabel.gridx = 0;
        gbc_vuLabel.gridy = 0;
        logoBar.add(vuLabel, gbc_vuLabel);

        JLabel ibisLabel = new JLabel(new ImageIcon(IBIS_LOGO));
        GridBagConstraints gbc_ibisLabel = new GridBagConstraints();
        gbc_ibisLabel.anchor = GridBagConstraints.SOUTHWEST;
        gbc_ibisLabel.insets = new Insets(5, 5, 5, 5);
        gbc_ibisLabel.gridx = 0;
        gbc_ibisLabel.gridy = 1;
        logoBar.add(ibisLabel, gbc_ibisLabel);
        JLabel unexLabel = new JLabel(new ImageIcon(UNEX_LOGO));
        GridBagConstraints gbc_unexLabel = new GridBagConstraints();
        gbc_unexLabel.anchor = GridBagConstraints.SOUTH;
        gbc_unexLabel.insets = new Insets(5, 5, 5, 5);
        gbc_unexLabel.gridx = 1;
        gbc_unexLabel.gridy = 1;
        logoBar.add(unexLabel, gbc_unexLabel);
        JLabel nlescLabel = new JLabel(new ImageIcon(NLESC_LOGO));
        GridBagConstraints gbc_nlescLabel = new GridBagConstraints();
        gbc_nlescLabel.insets = new Insets(5, 5, 5, 5);
        gbc_nlescLabel.anchor = GridBagConstraints.SOUTHEAST;
        gbc_nlescLabel.gridx = 2;
        gbc_nlescLabel.gridy = 1;
        logoBar.add(nlescLabel, gbc_nlescLabel);

        GridBagConstraints gbc_logos = new GridBagConstraints();
        gbc_logos.anchor = GridBagConstraints.SOUTH;
        gbc_logos.insets = new Insets(5, 5, 5, 5);
        gbc_logos.weighty = 0.0;
        gbc_logos.weightx = 0.0;
        gbc_logos.gridx = 0;
        gbc_logos.gridy = 2;
        gbc_logos.gridheight = 2;
        gbc_logos.gridwidth = 2;
        gbc_logos.fill = GridBagConstraints.HORIZONTAL;

        // frame.getContentPane().add(logoBar, BorderLayout.SOUTH);
        mainPanel.add(logoBar, gbc_logos);

        log = new LogPanel("Log");
        log.setViewportBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
                null, null));
        GridBagConstraints gbc_log = new GridBagConstraints();
        gbc_log.fill = GridBagConstraints.BOTH;
        gbc_log.gridx = 2;
        gbc_log.gridy = 3;
        gbc_log.gridwidth = 2;
        gbc_log.weightx = 1.0;
        gbc_log.weighty = 0.0;
        gbc_log.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(log, gbc_log);

        // updateFile(INPUT_FILE);
        updatePreviews();
        updateQueryPreview();

        controller.requestIndexUpdates();
//        indexUpdater.setDaemon(true);
//        indexUpdater.start();
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
                updateFile(queryURI);
                updateQueryPreview();
            }
        } else if (command.equals(REFRESH_BANDS)) {
            updatePreviews();
            updateQueryPreview();
        } else if (command.equals(SEARCH)) {
            startSearch(queryInput);
//        } else if (command.equals(UPDATE_INDEX)) {
//            updateIndex();
        } else if (command.equals(SELECT_FROM_STORE)) {
            ImageIdentifier image = imageList.getSelectedValue();
            if (image != null) {
                setQueryFromStore(image);
            }
        } else if (command.equals(SELECT_FROM_RESULTS)) {
            ResultElement re = resultPreview.getSelectedValue();
            if (re != null) {
                setQueryFromResults(re);
            }
        } else {
            System.out.println("actionPerformed(): unknown actionCommand: "
                    + command);
        }
    }

    private void startSearch(QueryInput query) {
        if (!query.hasHeader()) {
            // no image, do nothing
            return;
        }
        log.addLine("Search started for "
                + query.getHeader().getID().tryGetPrettyName());
        controller.doQuery(query);
    }

//    private void updateIndex() {
//        log.addLine("Updating index");
//        controller.requestIndex();
//    }

    private void updatePreviews() {
        // results.add(new
        // ResultElement(MatchTable.createFromAngles(queryInput.getID(),
        // queryInput.getID(), new float[][] {{1,1},{1,1}}, 2), this));
        fetchResultPreviews();
        // resultPreview.revalidate();
        // resultPreview.repaint();
    }

    private void updateQueryPreview() {
        if (queryInput.hasHeader()) {
            if (queryInput.hasImage()) {
                BufferedImage image = queryInput.getRGBImage(
                        redComboBox.getSelectedIndex(),
                        greenComboBox.getSelectedIndex(),
                        blueComboBox.getSelectedIndex());
                previewPanel.setImage(image);
            } else {
                int red = redComboBox.getSelectedIndex();
                int green = greenComboBox.getSelectedIndex();
                int blue = blueComboBox.getSelectedIndex();
                System.out.print(String.format(
                        "Getting queryImage preview %s[%d,%d,%d] from stores",
                        queryInput.getID(), red, green, blue));
                controller.requestPreviewImage(queryInput.getID(), red, green,
                        blue, index.getStoresFor(queryInput.getID()));
            }
        }
    }

    private void updateFile(String newFile) {
        if (queryInput.getFile().equals(newFile)) {
            log.addLine("Image not changed: " + newFile);
            return;
        }
        queryInput.setFromFile(newFile);

        log.addLine("new image loaded: " + newFile);
        updateBandBoxes();
        results.clear();
    }

    private void updateBandBoxes() {
        String[] bands;
        switch (bandUnits) {
        case BANDS_WAVELENGTH:
            float[] wl = queryInput.getWavelengths();
            if (wl == null) {
                bands = queryInput.getBandNumbers();
            } else {
                bands = new String[wl.length];
                for (int i = 0; i < wl.length; i++) {
                    bands[i] = String.format("%.1f", wl[i]);
                }
            }
            break;
        case BANDS_NAME:
            bands = queryInput.getBandNames();
            break;
        case BANDS_INDEX:
        default:
            bands = queryInput.getBandNumbers();
            break;

        }
        // ((BandComboBoxModel<String>)redComboBox.getModel()).setElements(bands);
        // ((BandComboBoxModel<String>)greenComboBox.getModel()).setElements(bands);
        // ((BandComboBoxModel<String>)blueComboBox.getModel()).setElements(bands);

        redComboBox.setModel(new BandComboBoxModel<String>(bands, redComboBox
                .getSelectedIndex()));
        greenComboBox.setModel(new BandComboBoxModel<String>(bands,
                greenComboBox.getSelectedIndex()));
        blueComboBox.setModel(new BandComboBoxModel<String>(bands, blueComboBox
                .getSelectedIndex()));
    }

    // function below should be called from the EventQueue Thread

    private void fetchResultPreviews() {
        int red = redComboBox.getSelectedIndex();
        int green = greenComboBox.getSelectedIndex();
        int blue = blueComboBox.getSelectedIndex();

        // fetch preview images
        for (ResultElement result : results.getContents()) {
            ImageIdentifier imageID = result.getImageID();
            String[] stores = index.getStoresFor(imageID);
            System.out.print("Getting previewimage " + result.getImageID()
                    + " from stores ");
            for (String store : stores) {
                System.out.print(store + " ");
            }
            System.out.println();
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
        results.updateResults(matchData, this);
        // resultPreview.revalidate();

        fetchResultPreviews();
    }

    protected void deliverResult(MatchTable[] matchData, long queryTime) {
        log.addLine("Results received");
        log.addLine(String.format("Query took %d ms", queryTime / 1000000));
        // update results pane
        results.updateResults(matchData, this);
        // resultPreview.revalidate();

        fetchResultPreviews();
    }

    protected void deliverImage(FloatImage image) {
        log.addLine("Image received: " + image.getID().tryGetPrettyName());
        queryInput.setFromImage(image);
        updateBandBoxes();
        updateQueryPreview();
        results.clear();
    }

    protected void deliverHeader(EnviHeader header) {
        log.addLine("Header received: " + header.getID().tryGetPrettyName());
        queryInput.setFromHeader(header);
        updateBandBoxes();
        updateQueryPreview();
        results.clear();
    }

    protected void deliverPreview(PreviewImage preview) {
        log.addLine("Preview received: "
                + preview.getImageID().tryGetPrettyName());
        if (preview.getImageID().equals(queryInput.getID())) {
            log.addLine("Is a QueryPreview");
            previewPanel.setImage(preview.getImage());
        }
        results.deliverPreview(preview);

        // resultPreview.revalidate();
        // resultPreview.repaint();
    }

    // protected void deliverIndex(MultiArchiveIndex index) {
    // log.addLine(String.format("Store contains %d images", index.size()));
    // this.index = index;
    // storeIndexModel.updateIndex(index);
    // storeChart.updateGraph(index);
    // indexUpdater.updated();
    // }

    protected void deliverIndex(SingleArchiveIndex sai) {
        if (sai.size() > 0) {
            index.add(sai);
            storeIndexModel.updateIndex(sai);
            storeChart.updateGraph(index);
            log.addLine(String.format("Store contains %d images", index.size()));
//            indexUpdater.updated();
        }
    }

    public void setQueryFromResults(ResultElement re) {
        setQueryFromStore(re.getImageID());
    }

    private void setQueryFromStore(ImageIdentifier imageID) {
        String[] stores = index.getStoresFor(imageID);
        System.out.print("Getting queryHeader " + imageID.tryGetPrettyName()
                + " from stores ");
        for (String store : stores) {
            System.out.print(store + " ");
        }
        System.out.println();
        controller.requestHeader(imageID, stores);
        // controller.requestImage(imageID, stores);
        log.addLine("Getting QueryImage from stores: "
                + imageID.tryGetPrettyName());
    }

}

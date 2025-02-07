package MapOptics;

import Algorithms.DetectSV;
import DataTypes.Query;
import DataTypes.Reference;
import DataTypes.SV;
import Datasets.Default.*;
import Datasets.UserEdited.*;
import FileHandling.*;
import UserInterface.ModelsAndRenderers.EditableHeaderRenderer;
import UserInterface.ModelsAndRenderers.MapOpticsModel;
import UserInterface.ModelsAndRenderers.MyChartRenderer;
import UserInterface.ModelsAndRenderers.TableModels;
import com.opencsv.CSVWriter;
import com.qoppa.pdfWriter.PDFDocument;
import com.qoppa.pdfWriter.PDFPage;
import org.apache.commons.io.FilenameUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
//import org.jfree.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleAnchor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.*;
import startScreen.startScreen;

/**
 * Frame of the application
 * 
 * @author Josie
 */
public class MapOptics extends JFrame {

    private final JTable conflictsTable = new JTable();
    private final JTable imageTable = new JTable();
    private final JCheckBox selectAllImages = new JCheckBox();
    private final MapOpticsModel model;
    private final DetectSV detectSV;

    private String xmapPath, refPath, qryPath;
    private JDialog chimSettings, confidenceSettings, coverageSettings, fastaLoader, fileLoader, saveQueries;
    private JCheckBox confidenceSetting;
    private JButton exportQryButton, exportRefButton, exportSVButton;
    private JTextField fastaFile, keyFile, qryDataset, qryFileTextField, qryIdSearch, refDataset,
            refFileTextField, refIdSearch, regionSearch, xmapFileTextField;
    private JSpinner highConf, highCov, highQual, lowConf, lowCov, lowQual, indelMinSize, indelMaxSize, flankSignal;
    private JPanel labelDensityGraph, referencesGraph;
    private JTable labelTable, qryContigTable, svTable, qryViewRefTable, refContigTable;
    private JCheckBox overlapSetting;
    private JComboBox<String> refOrQry, regionType;
    private JRadioButton styleChim, styleCoverage, styleMatch, styleCigar, styleMatchSV;
    private JTabbedPane tabPaneFiles;
    private JCheckBox allIndels;

    private QueryView queryView;
    private ReferenceView referenceView;
    private SVView svView;
    private static final String EMPTY_STRING = "";
    private static final int DEFAULT = 0;
    private static final int LAST_SAVED = 1;
    
    //Exit button
    private javax.swing.JButton exitVerifyAssembly;

    /**
     * Constructor
     */
    public MapOptics() {
        System.setProperty("sun.java2d.opengl", "true");
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setName("MapOptics");
        // this.setDefaultCloseOperation(closeWindow());

        model = new MapOpticsModel();
        detectSV = new DetectSV(model);
        initComponents();

        setRefContigTable();
        setQryContigTable();
        setSVTable();
        setLabelTable();
        setQryViewRefTable();
        setImageTable();
        setConflictsTable();

        ButtonGroup buttongroup = new ButtonGroup();
        buttongroup.add(styleMatch);
        buttongroup.add(styleCoverage);
        buttongroup.add(styleChim);

        fileLoader.setVisible(false);
        fileLoader.pack();

        fastaLoader.setVisible(false);
        fastaLoader.pack();

        confidenceSettings.setVisible(false);
        confidenceSettings.pack();

        coverageSettings.setVisible(false);
        coverageSettings.pack();

        chimSettings.setVisible(false);
        chimSettings.pack();

        ButtonGroup buttongroupSV = new ButtonGroup();
        buttongroupSV.add(styleMatchSV);
        buttongroupSV.add(styleCigar);

        saveQueries.setVisible(false);
        saveQueries.pack();

        refDataset.setVisible(false);
        qryDataset.setVisible(false);

        conflictsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // when conflicts table is clicked, set ref and qry
                if ((conflictsTable.getSelectedRow() & 1) == 0) {
                    conflictsTable.setRowSelectionInterval(conflictsTable.getSelectedRow(), conflictsTable.getSelectedRow() + 1);
                } else {
                    conflictsTable.setRowSelectionInterval(conflictsTable.getSelectedRow(), conflictsTable.getSelectedRow() - 1);
                }

                // get selected ref and qry
                String chosenRef = conflictsTable.getValueAt(conflictsTable.getSelectedRow(), 2).toString();
                String chosenQry = conflictsTable.getValueAt(conflictsTable.getSelectedRow() + 1, 2).toString();
                changeRef(chosenRef);
                changeQry(chosenQry);
                repaint();
            }
        });

        selectAllImages.setText("Select all images  ");
        selectAllImages.addActionListener(evt -> {
            // select all images
            if (selectAllImages.isSelected()) {
                for (int i = 0; i < imageTable.getRowCount(); i++) {
                    imageTable.setValueAt(true, i, 1);
                }
            }
            if (!selectAllImages.isSelected()) {
                for (int i = 0; i < imageTable.getRowCount(); i++) {
                    imageTable.setValueAt(false, i, 1);
                }
            }
        });

        imageTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // set chosen ref
                String chosenRef = imageTable.getValueAt(imageTable.getSelectedRow(), 0).toString();
                changeRef(chosenRef);
                repaint();
            }
        });

        repaint();
    }


    private void initComponents() {

        fileLoader = new javax.swing.JDialog();
        JPanel jPanel4 = new JPanel();
        xmapFileTextField = new javax.swing.JTextField();
        JLabel jLabel2 = new JLabel();
        JButton browseXmap = new JButton();
        JButton browseRef = new JButton();
        JLabel jLabel6 = new JLabel();
        qryFileTextField = new javax.swing.JTextField();
        JButton browseQry = new JButton();
        JLabel jLabel8 = new JLabel();
        JLabel jLabel7 = new JLabel();
        JButton runAnalysis = new JButton();
        refFileTextField = new javax.swing.JTextField();
        confidenceSettings = new javax.swing.JDialog();
        JPanel jPanel5 = new JPanel();
        JLabel jLabel17 = new JLabel();
        JLabel jLabel18 = new JLabel();
        JLabel jLabel19 = new JLabel();
        JLabel jLabel20 = new JLabel();
        JLabel jLabel21 = new JLabel();
        JLabel jLabel22 = new JLabel();
        lowConf = new javax.swing.JSpinner();
        highConf = new javax.swing.JSpinner();
        JButton saveConfThresholds = new JButton();
        JLabel jLabel23 = new JLabel();
        JLabel jLabel24 = new JLabel();
        JLabel jLabel25 = new JLabel();
        JLabel jLabel26 = new JLabel();
        coverageSettings = new javax.swing.JDialog();
        JPanel jPanel8 = new JPanel();
        JLabel jLabel47 = new JLabel();
        JLabel jLabel48 = new JLabel();
        JLabel jLabel49 = new JLabel();
        JLabel jLabel50 = new JLabel();
        JLabel jLabel51 = new JLabel();
        JLabel jLabel52 = new JLabel();
        lowCov = new javax.swing.JSpinner();
        highCov = new javax.swing.JSpinner();
        JButton saveCovThresholds = new JButton();
        JLabel jLabel53 = new JLabel();
        JLabel jLabel54 = new JLabel();
        JLabel jLabel55 = new JLabel();
        JLabel jLabel56 = new JLabel();
        chimSettings = new javax.swing.JDialog();
        JPanel jPanel9 = new JPanel();
        JLabel jLabel57 = new JLabel();
        JLabel jLabel58 = new JLabel();
        JLabel jLabel59 = new JLabel();
        JLabel jLabel60 = new JLabel();
        JLabel jLabel61 = new JLabel();
        JLabel jLabel62 = new JLabel();
        lowQual = new javax.swing.JSpinner();
        highQual = new javax.swing.JSpinner();
        JButton saveQualitySettings = new JButton();
        JLabel jLabel63 = new JLabel();
        JLabel jLabel64 = new JLabel();
        JLabel jLabel65 = new JLabel();
        JLabel jLabel66 = new JLabel();
        fastaLoader = new javax.swing.JDialog();
        JPanel jPanel11 = new JPanel();
        keyFile = new javax.swing.JTextField();
        JLabel jLabel29 = new JLabel();
        JButton browseKey = new JButton();
        JButton browseFasta = new JButton();
        JLabel jLabel30 = new JLabel();
        JLabel jLabel31 = new JLabel();
        fastaFile = new javax.swing.JTextField();
        JButton loadFastaFile = new JButton();
        JLabel jLabel32 = new JLabel();
        refOrQry = new javax.swing.JComboBox<>();
        JTabbedPane tabPane = new JTabbedPane();
        JLayeredPane summaryPane = new JLayeredPane();
        JSplitPane jSplitPane3 = new JSplitPane();
        JPanel rightPanel = new JPanel();
        JPanel alignmentNamePanel = new JPanel();
        JLabel jLabel14 = new JLabel();
        JLabel jLabel15 = new JLabel();
        refDataset = new javax.swing.JTextField();
        qryDataset = new javax.swing.JTextField();
        JPanel referenceGraphPanel = new JPanel();
        SummaryView summaryView = new SummaryView(model);
        referencesGraph = new javax.swing.JPanel();
        labelDensityGraph = new javax.swing.JPanel();
        JLabel jLabel16 = new JLabel();
        JPanel leftPanel = new JPanel();
        JScrollPane refContigTableScroll = new JScrollPane();
        refContigTable = new javax.swing.JTable();
        JLayeredPane refViewPane = new JLayeredPane();
        JSplitPane jSplitPane2 = new JSplitPane();
        JLayeredPane jLayeredPane2 = new JLayeredPane();
        JLayeredPane refViewNew = new JLayeredPane();
        JLabel refDataNameLabel = new JLabel();
        JLabel qryDataNameLabel = new JLabel();
        JTextField refDataNameField = new JTextField();
        JTextField qryDataNameField = new JTextField();
        JPanel labelPanel = new JPanel();
        referenceView = new ReferenceView(model);
        exportRefButton = new javax.swing.JButton();
        JPanel jPanel3 = new JPanel();
        JLabel jLabel4 = new JLabel();
        JButton reCentre = new JButton();
        JButton zoomIn = new JButton();
        JButton zoomOut = new JButton();
        JLabel jLabel1 = new JLabel();
        styleMatch = new javax.swing.JRadioButton();
        styleCoverage = new javax.swing.JRadioButton();
        styleChim = new javax.swing.JRadioButton();
        JLabel jLabel5 = new JLabel();
        confidenceSetting = new javax.swing.JCheckBox();
        overlapSetting = new javax.swing.JCheckBox();
        JSeparator jSeparator1 = new JSeparator();
        JLabel jLabel3 = new JLabel();
        JButton reOrientate = new JButton();
        JButton deleteContig = new JButton();
        JButton resetButton = new JButton();
        JButton save = new JButton();
        tabPaneFiles = new javax.swing.JTabbedPane();
        JScrollPane refViewTableScroll = new JScrollPane();
        qryContigTable = new javax.swing.JTable();
        JLayeredPane queryViewPane = new JLayeredPane();
        JSplitPane jSplitPane1 = new JSplitPane();
        JLayeredPane jLayeredPane1 = new JLayeredPane();
        queryView = new QueryView( model);
        exportQryButton = new javax.swing.JButton();
        JPanel jPanel1 = new JPanel();
        qryIdSearch = new javax.swing.JTextField();
        refIdSearch = new javax.swing.JTextField();
        JLabel jLabel9 = new JLabel();
        JLabel jLabel10 = new JLabel();
        JLabel jLabel11 = new JLabel();
        JButton search = new JButton();
        JLabel jLabel12 = new JLabel();
        regionSearch = new javax.swing.JTextField();
        JLabel jLabel13 = new JLabel();
        regionType = new javax.swing.JComboBox<>();
        JScrollPane jScrollPane1 = new JScrollPane();
        qryViewRefTable = new javax.swing.JTable();
        JScrollPane queryViewTableScroll = new JScrollPane();
        // Add SV View
        svView = new SVView(model, detectSV);
        JLabel displayToolsSV = new JLabel();
        JLayeredPane svPane = new JLayeredPane();
        JLabel labelStyleSV = new JLabel();
        JLabel labelParametersSV = new JLabel();
        styleMatchSV = new javax.swing.JRadioButton();
        styleCigar = new javax.swing.JRadioButton();
        allIndels = new JCheckBox();
        JLabel viewLabelSV = new JLabel();
        JLabel contigToolsSV = new JLabel();
        JButton reOrientateSV = new JButton();
        JSplitPane svSplitPlane = new JSplitPane();
        JLayeredPane svLayeredPane = new JLayeredPane();
        JScrollPane svViewTableScroll = new JScrollPane();
        exportSVButton = new JButton();
        JPanel svPanel = new JPanel();
        svTable = new javax.swing.JTable();
        JLayeredPane svViewPane = new JLayeredPane();
        JTabbedPane tabPaneFilesSV = new JTabbedPane();
        indelMinSize = new javax.swing.JSpinner();
        indelMaxSize = new javax.swing.JSpinner();
        flankSignal = new javax.swing.JSpinner();
        JLabel labelIndelMin = new JLabel();
        JLabel labelIndelMax = new JLabel();
        JLabel labelFlankSig = new JLabel();
        JButton saveSVSettings = new JButton();
        labelTable = new javax.swing.JTable();
        JMenuBar menuBar = new JMenuBar();
        JMenu jMenu1 = new JMenu();
        JMenuItem loadMaps = new JMenuItem();
        JMenuItem fastaLoad = new JMenuItem();
        JMenu jMenu5 = new JMenu();
        JMenuItem manualConflict = new JMenuItem();
        JMenuItem saveConflictFile = new JMenuItem();
        JMenu jMenu2 = new JMenu();
        JMenu Save = new JMenu();
        JMenuItem saveQueryContigs = new JMenuItem();
        JMenuItem saveQueryLabels = new JMenuItem();
        JMenuItem saveSVTable = new JMenuItem();
        saveQueries = new javax.swing.JDialog();
        JMenuItem chooseImages = new JMenuItem();
        JMenuItem exportImages = new JMenuItem();
        JMenuItem close = new JMenuItem();
        JMenu jMenu3 = new JMenu();
        JMenuItem swapContigs = new JMenuItem();
        JMenuItem orientateContigs = new JMenuItem();
        JMenuItem saveAllContigs = new JMenuItem();
        JMenu jMenu4 = new JMenu();
        JMenuItem confidenceSet = new JMenuItem();
        JMenuItem coverageSet = new JMenuItem();
        JMenuItem chimqualSet = new JMenuItem();
        exitVerifyAssembly = new javax.swing.JButton();
        
        // Button to exit the view verify genome assembly and go back to start screen
        exitVerifyAssembly.setText("Exit");
        exitVerifyAssembly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitVerifyAssemblyActionPerformed(evt);
            }
        });

        fileLoader.setTitle("Load Maps");
        fileLoader.setLocation(new java.awt.Point(100, 100));
        fileLoader.setName("Load Files"); // NOI18N
        fileLoader.setPreferredSize(new java.awt.Dimension(280, 300));

        jLabel2.setText("XMAP:");

        browseXmap.setText("Browse...");
        browseXmap.addActionListener(this::browseXmapActionPerformed);

        browseRef.setText("Browse...");
        browseRef.addActionListener(this::browseRefActionPerformed);

        jLabel6.setText("Reference CMAP:");

        browseQry.setText("Browse...");
        browseQry.addActionListener(this::browseQryActionPerformed);

        jLabel8.setFont(new java.awt.Font("Tahoma", Font.ITALIC, 11)); // NOI18N
        jLabel8.setText("Load XMAP file and corresponding CMAP files");

        jLabel7.setText("Query CMAP:");

        runAnalysis.setFont(new java.awt.Font("Tahoma", Font.BOLD, 11)); // NOI18N
        runAnalysis.setText("Run");
        runAnalysis.addActionListener(this::runAnalysisActionPerformed);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGap(22, 22, 22)
                                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(xmapFileTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                                .addComponent(jLabel2)
                                                                .addGap(0, 0, Short.MAX_VALUE)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(browseXmap))
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGap(20, 20, 20)
                                                .addComponent(jLabel8))
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGap(22, 22, 22)
                                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                                .addComponent(refFileTextField)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(browseRef))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                                                .addComponent(qryFileTextField)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(browseQry))
                                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jLabel6)
                                                                        .addComponent(jLabel7))
                                                                .addGap(0, 0, Short.MAX_VALUE)))))
                                .addGap(24, 24, 24))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(runAnalysis)
                                .addGap(18, 18, 18))
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel8)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(xmapFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(browseXmap))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(browseRef)
                                        .addComponent(refFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(browseQry)
                                        .addComponent(qryFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(runAnalysis)
                                .addContainerGap(75, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout fileLoaderLayout = new javax.swing.GroupLayout(fileLoader.getContentPane());
        fileLoader.getContentPane().setLayout(fileLoaderLayout);
        fileLoaderLayout.setHorizontalGroup(
                fileLoaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        fileLoaderLayout.setVerticalGroup(
                fileLoaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        fileLoader.getAccessibleContext().setAccessibleName(EMPTY_STRING);
        fileLoader.getAccessibleContext().setAccessibleDescription(EMPTY_STRING);

        confidenceSettings.setTitle("Confidence Threshold Settings");
        confidenceSettings.setLocation(new java.awt.Point(100, 100));

        jLabel17.setFont(new java.awt.Font("Tahoma", Font.ITALIC, 11)); // NOI18N
        jLabel17.setText("Change thresholds of the confidence alignment view:");

        jLabel18.setText("Low Confidence");

        jLabel19.setText("Medium Confidence");

        jLabel20.setText("High Confidence");

        jLabel21.setText("<");

        jLabel22.setText("<");

        lowConf.setPreferredSize(new java.awt.Dimension(60, 25));
        lowConf.setValue(20);
        lowConf.setVerifyInputWhenFocusTarget(false);

        highConf.setPreferredSize(new java.awt.Dimension(60, 25));
        highConf.setValue(40);

        saveConfThresholds.setFont(new java.awt.Font("Tahoma", Font.BOLD, 11)); // NOI18N
        saveConfThresholds.setText("Save Changes");
        saveConfThresholds.addActionListener(this::saveConfThresholdsActionPerformed);

        jLabel23.setFont(new java.awt.Font("Tahoma", Font.ITALIC, 11)); // NOI18N
        jLabel23.setText("Default thresholds are set to :");

        jLabel24.setText("Low Confidence  < 20");

        jLabel25.setText("20 <= Medium Confidence <= 40 ");

        jLabel26.setText(" High Confidence > 40");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                                                .addComponent(jLabel18)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                .addComponent(jLabel21)
                                                                                .addGap(9, 9, 9)
                                                                                .addComponent(jLabel19)
                                                                                .addGap(13, 13, 13)
                                                                                .addComponent(jLabel22)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                .addComponent(jLabel20))
                                                                        .addComponent(jLabel23)
                                                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                                                .addGap(71, 71, 71)
                                                                                .addComponent(lowConf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addGap(63, 63, 63)
                                                                                .addComponent(highConf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                .addGap(0, 47, Short.MAX_VALUE))))
                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addGap(79, 79, 79)
                                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel25)
                                                        .addComponent(jLabel26))
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(saveConfThresholds)
                                .addGap(43, 43, 43))
        );
        jPanel5Layout.setVerticalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel17)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel18)
                                        .addComponent(jLabel19)
                                        .addComponent(jLabel20)
                                        .addComponent(jLabel21)
                                        .addComponent(jLabel22))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lowConf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(highConf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(saveConfThresholds)
                                .addGap(19, 19, 19))
        );

        javax.swing.GroupLayout confidenceSettingsLayout = new javax.swing.GroupLayout(confidenceSettings.getContentPane());
        confidenceSettings.getContentPane().setLayout(confidenceSettingsLayout);
        confidenceSettingsLayout.setHorizontalGroup(
                confidenceSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        confidenceSettingsLayout.setVerticalGroup(
                confidenceSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(confidenceSettingsLayout.createSequentialGroup()
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 2, Short.MAX_VALUE))
        );

        coverageSettings.setTitle("Coverage Threshold Settings");
        coverageSettings.setLocation(new java.awt.Point(100, 100));

        jLabel47.setFont(new java.awt.Font("Tahoma", Font.ITALIC, 11)); // NOI18N
        jLabel47.setText("Change thresholds of the confidence alignment view:");
        jLabel48.setText("Low Coverage");
        jLabel49.setText("Medium Coverage");
        jLabel50.setText("High Coverage");
        jLabel51.setText("<");
        jLabel52.setText("<");

        lowCov.setPreferredSize(new java.awt.Dimension(60, 25));
        lowCov.setValue(20);
        lowCov.setVerifyInputWhenFocusTarget(false);

        highCov.setPreferredSize(new java.awt.Dimension(60, 25));
        highCov.setValue(50);

        saveCovThresholds.setFont(new java.awt.Font("Tahoma", Font.BOLD, 11)); // NOI18N
        saveCovThresholds.setText("Save Changes");
        saveCovThresholds.addActionListener(this::saveCovThresholdsActionPerformed);

        jLabel53.setFont(new java.awt.Font("Tahoma", Font.ITALIC, 11)); // NOI18N
        jLabel53.setText("Default thresholds are set to :");

        jLabel54.setText("Low Coverage  < 20");

        jLabel55.setText("20 <= Medium Coverage <= 50 ");

        jLabel56.setText(" High Coverage > 50");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
                jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel8Layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addGroup(jPanel8Layout.createSequentialGroup()
                                                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                        .addComponent(saveCovThresholds)
                                                                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(jLabel53)
                                                                                .addGroup(jPanel8Layout.createSequentialGroup()
                                                                                        .addGap(71, 71, 71)
                                                                                        .addComponent(lowCov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addGap(63, 63, 63)
                                                                                        .addComponent(highCov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                .addGroup(jPanel8Layout.createSequentialGroup()
                                                                                        .addComponent(jLabel48)
                                                                                        .addGap(26, 26, 26)
                                                                                        .addComponent(jLabel51)
                                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                        .addComponent(jLabel49)
                                                                                        .addGap(18, 18, 18)
                                                                                        .addComponent(jLabel52)
                                                                                        .addGap(18, 18, 18)
                                                                                        .addComponent(jLabel50))))
                                                                .addGap(0, 123, Short.MAX_VALUE))))
                                        .addGroup(jPanel8Layout.createSequentialGroup()
                                                .addGap(79, 79, 79)
                                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel55)
                                                        .addComponent(jLabel56))
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
                jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel8Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel47)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel48)
                                        .addComponent(jLabel49)
                                        .addComponent(jLabel50)
                                        .addComponent(jLabel51)
                                        .addComponent(jLabel52))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lowCov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(highCov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                                .addComponent(jLabel53)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel54)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel55)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel56)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(saveCovThresholds)
                                .addGap(19, 19, 19))
        );

        javax.swing.GroupLayout coverageSettingsLayout = new javax.swing.GroupLayout(coverageSettings.getContentPane());
        coverageSettings.getContentPane().setLayout(coverageSettingsLayout);
        coverageSettingsLayout.setHorizontalGroup(
                coverageSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        coverageSettingsLayout.setVerticalGroup(
                coverageSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(coverageSettingsLayout.createSequentialGroup()
                                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 59, Short.MAX_VALUE))
        );

        chimSettings.setTitle("Chimeric Quality Threshold Settings");
        chimSettings.setLocation(new java.awt.Point(100, 100));

        jLabel57.setFont(new java.awt.Font("Tahoma", Font.ITALIC, 11)); // NOI18N
        jLabel57.setText("Change thresholds of the label chimeric quality view:");
        jLabel58.setText("Low Quality");
        jLabel59.setText("Medium Quality");
        jLabel60.setText("High Quality");
        jLabel61.setText("<");
        jLabel62.setText("<");

        lowQual.setPreferredSize(new java.awt.Dimension(60, 25));
        lowQual.setValue(20);
        lowQual.setVerifyInputWhenFocusTarget(false);

        highQual.setPreferredSize(new java.awt.Dimension(60, 25));
        highQual.setValue(90);

        saveQualitySettings.setFont(new java.awt.Font("Tahoma", Font.BOLD, 11)); // NOI18N
        saveQualitySettings.setText("Save Changes");
        saveQualitySettings.addActionListener(this::saveQualitySettingsActionPerformed);

        jLabel63.setFont(new java.awt.Font("Tahoma", Font.ITALIC, 11)); // NOI18N
        jLabel63.setText("Default thresholds are set to :");
        jLabel64.setText("Low Quality  < 20");
        jLabel65.setText("20 <= Medium Quality <= 90 ");
        jLabel66.setText(" High Quality > 90");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
                jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel9Layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel57, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addGroup(jPanel9Layout.createSequentialGroup()
                                                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jLabel63)
                                                                        .addGroup(jPanel9Layout.createSequentialGroup()
                                                                                .addGap(71, 71, 71)
                                                                                .addComponent(lowQual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addGap(63, 63, 63)
                                                                                .addComponent(highQual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                .addGap(0, 0, Short.MAX_VALUE))
                                                        .addGroup(jPanel9Layout.createSequentialGroup()
                                                                .addComponent(jLabel58)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                                                                .addComponent(jLabel61)
                                                                .addGap(30, 30, 30)
                                                                .addComponent(jLabel59)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jLabel62)
                                                                .addGap(31, 31, 31)
                                                                .addComponent(jLabel60)
                                                                .addGap(17, 17, 17))))
                                        .addGroup(jPanel9Layout.createSequentialGroup()
                                                .addGap(79, 79, 79)
                                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel65)
                                                        .addComponent(jLabel66))
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(saveQualitySettings)))
                                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
                jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel9Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel57)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel58)
                                        .addComponent(jLabel59)
                                        .addComponent(jLabel60)
                                        .addComponent(jLabel61)
                                        .addComponent(jLabel62))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lowQual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(highQual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                                .addComponent(jLabel63)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel64)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel65)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel66)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(saveQualitySettings)
                                .addGap(19, 19, 19))
        );

        javax.swing.GroupLayout chimSettingsLayout = new javax.swing.GroupLayout(chimSettings.getContentPane());
        chimSettings.getContentPane().setLayout(chimSettingsLayout);
        chimSettingsLayout.setHorizontalGroup(
                chimSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(chimSettingsLayout.createSequentialGroup()
                                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 73, Short.MAX_VALUE))
        );
        chimSettingsLayout.setVerticalGroup(
                chimSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(chimSettingsLayout.createSequentialGroup()
                                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 59, Short.MAX_VALUE))
        );

        fastaLoader.setTitle("Load Fasta and Key");
        fastaLoader.setLocation(new java.awt.Point(100, 100));
        fastaLoader.setName("Load Files"); // NOI18N

        jLabel29.setText("KEY file:");

        browseKey.setText("Browse...");
        browseKey.addActionListener(this::browseKeyActionPerformed);

        browseFasta.setText("Browse...");
        browseFasta.addActionListener(this::browseFastaActionPerformed);

        jLabel30.setText("FASTA file:");

        jLabel31.setFont(new java.awt.Font("Tahoma", Font.ITALIC, 11)); // NOI18N
        jLabel31.setText("Load FASTA file and corresponding KEY file");

        loadFastaFile.setFont(new java.awt.Font("Tahoma", Font.BOLD, 11)); // NOI18N
        loadFastaFile.setText("Load");
        loadFastaFile.addActionListener(this::loadFastaFileActionPerformed);

        jLabel32.setText("Which contig is the fasta relative to?");

        refOrQry.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Reference", "Query" }));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
                jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel11Layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(loadFastaFile))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel11Layout.createSequentialGroup()
                                                .addGap(22, 22, 22)
                                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(keyFile, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                                                        .addGroup(jPanel11Layout.createSequentialGroup()
                                                                .addComponent(jLabel29)
                                                                .addGap(0, 0, Short.MAX_VALUE)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(browseKey))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel11Layout.createSequentialGroup()
                                                .addGap(20, 20, 20)
                                                .addComponent(jLabel31)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel11Layout.createSequentialGroup()
                                                .addGap(22, 22, 22)
                                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel11Layout.createSequentialGroup()
                                                                .addComponent(fastaFile)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(browseFasta))
                                                        .addGroup(jPanel11Layout.createSequentialGroup()
                                                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jLabel32)
                                                                        .addComponent(jLabel30)
                                                                        .addComponent(refOrQry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(0, 0, Short.MAX_VALUE)))))
                                .addGap(24, 24, 24))
        );
        jPanel11Layout.setVerticalGroup(
                jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel11Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel31)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel29)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(keyFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(browseKey))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(browseFasta)
                                        .addComponent(fastaFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(refOrQry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                                .addComponent(loadFastaFile)
                                .addContainerGap())
        );

        javax.swing.GroupLayout fastaLoaderLayout = new javax.swing.GroupLayout(fastaLoader.getContentPane());
        fastaLoader.getContentPane().setLayout(fastaLoaderLayout);
        fastaLoaderLayout.setHorizontalGroup(
                fastaLoaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        fastaLoaderLayout.setVerticalGroup(
                fastaLoaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MapOptics");

        jSplitPane3.setDividerLocation(240);
        jSplitPane3.setDividerSize(6);
        jSplitPane3.setContinuousLayout(true);
        jSplitPane3.setOneTouchExpandable(true);
        jSplitPane3.setSize(new java.awt.Dimension(244, 244));

        rightPanel.setPreferredSize(new java.awt.Dimension(0, 0));

        alignmentNamePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        alignmentNamePanel.setPreferredSize(new java.awt.Dimension(145, 130));

        jLabel14.setFont(new java.awt.Font("Tahoma", Font.BOLD, 11)); // NOI18N
        jLabel14.setText("Reference Dataset:");

        jLabel15.setFont(new java.awt.Font("Tahoma", Font.BOLD, 11)); // NOI18N
        jLabel15.setText("Query Dataset:");

        javax.swing.GroupLayout alignmentNamePanelLayout = new javax.swing.GroupLayout(alignmentNamePanel);
        alignmentNamePanel.setLayout(alignmentNamePanelLayout);
        alignmentNamePanelLayout.setHorizontalGroup(
                alignmentNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(alignmentNamePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(alignmentNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(alignmentNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(refDataset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(qryDataset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        alignmentNamePanelLayout.setVerticalGroup(
                alignmentNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(alignmentNamePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(alignmentNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel14)
                                        .addComponent(refDataset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(alignmentNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(qryDataset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel15))
                                .addContainerGap(29, Short.MAX_VALUE))
        );

        summaryView.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        summaryView.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                summaryViewComponentResized();
            }
        });

        javax.swing.GroupLayout summaryViewLayout = new javax.swing.GroupLayout(summaryView);
        summaryView.setLayout(summaryViewLayout);
        summaryViewLayout.setHorizontalGroup(
                summaryViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE)
        );
        summaryViewLayout.setVerticalGroup(
                summaryViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 468, Short.MAX_VALUE)
        );

        referenceGraphPanel.setBackground(new java.awt.Color(255, 255, 255));
        referenceGraphPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        referencesGraph.setBackground(new java.awt.Color(255, 255, 255));
        referencesGraph.setLayout(new javax.swing.BoxLayout(referencesGraph, javax.swing.BoxLayout.LINE_AXIS));

        labelDensityGraph.setBackground(new java.awt.Color(255, 255, 255));
        labelDensityGraph.setLayout(new javax.swing.BoxLayout(labelDensityGraph, javax.swing.BoxLayout.LINE_AXIS));

        jLabel16.setFont(new java.awt.Font("Tahoma", Font.BOLD, 11)); // NOI18N
        jLabel16.setText("Reference Graphs:");

        javax.swing.GroupLayout referenceGraphPanelLayout = new javax.swing.GroupLayout(referenceGraphPanel);
        referenceGraphPanel.setLayout(referenceGraphPanelLayout);
        referenceGraphPanelLayout.setHorizontalGroup(
                referenceGraphPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, referenceGraphPanelLayout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addGap(0, 1344, Short.MAX_VALUE))
                         .addGroup(referenceGraphPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(labelDensityGraph, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(referencesGraph, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        referenceGraphPanelLayout.setVerticalGroup(
                referenceGraphPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(referenceGraphPanelLayout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(referencesGraph, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(labelDensityGraph, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                                .addContainerGap())
        );

        javax.swing.GroupLayout rightPanelLayout = new javax.swing.GroupLayout(rightPanel);
        rightPanel.setLayout(rightPanelLayout);
        rightPanelLayout.setHorizontalGroup(
                rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(summaryView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(referenceGraphPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(alignmentNamePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1453, Short.MAX_VALUE)
                        .addGroup(rightPanelLayout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(exitVerifyAssembly, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        rightPanelLayout.setVerticalGroup(
                rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(rightPanelLayout.createSequentialGroup()
                                .addComponent(alignmentNamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(summaryView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(12, 12, 12)
                                .addComponent(referenceGraphPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap()
                                .addGroup(rightPanelLayout.createParallelGroup()
                                    .addComponent(exitVerifyAssembly))
                                    
                                )
        );

        jSplitPane3.setRightComponent(rightPanel);

        leftPanel.setPreferredSize(new java.awt.Dimension(0, 0));

        refContigTable.setAutoCreateRowSorter(true);
        refContigTable.setModel(new DefaultTableModel());
        refContigTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        refContigTable.setMinimumSize(new java.awt.Dimension(600, 640));
        refContigTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        refContigTable.setShowGrid(false);
        refContigTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                refContigTableMouseClicked();
            }
        });
        refContigTableScroll.setViewportView(refContigTable);

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
                leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(leftPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(refContigTableScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                                .addContainerGap())
        );
        leftPanelLayout.setVerticalGroup(
                leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(refContigTableScroll, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1096, Short.MAX_VALUE)
        );

        jSplitPane3.setLeftComponent(leftPanel);

        summaryPane.setLayer(jSplitPane3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout summaryPaneLayout = new javax.swing.GroupLayout(summaryPane);
        summaryPane.setLayout(summaryPaneLayout);
        summaryPaneLayout.setHorizontalGroup(
                summaryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, summaryPaneLayout.createSequentialGroup()
                                .addComponent(jSplitPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1597, Short.MAX_VALUE)
                                .addContainerGap())
        );
        summaryPaneLayout.setVerticalGroup(
                summaryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jSplitPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 1100, Short.MAX_VALUE)
        );

        tabPane.addTab("Summary View", summaryPane);

        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        referenceView.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        referenceView.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                referenceViewMouseMoved(evt);
            }
        });
        referenceView.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                referenceViewMouseClicked(evt);
            }
        });

        exportRefButton.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 10)); // NOI18N
        exportRefButton.setText("Export Image");
        exportRefButton.addActionListener(this::exportRefButtonActionPerformed);
        exportRefButton.setText("Export Image");

        javax.swing.GroupLayout referenceViewLayout = new javax.swing.GroupLayout(referenceView);
        referenceView.setLayout(referenceViewLayout);
        referenceViewLayout.setHorizontalGroup(
                referenceViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, referenceViewLayout.createSequentialGroup()
                                .addContainerGap(1295, Short.MAX_VALUE)
                                .addComponent(exportRefButton)
                                .addContainerGap())
        );
        referenceViewLayout.setVerticalGroup(
                referenceViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(referenceViewLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(exportRefButton)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel4.setFont(new java.awt.Font("Tahoma", Font.BOLD, 11)); // NOI18N
        jLabel4.setText("Display tools:");

        reCentre.setText("reCentre");
        reCentre.addActionListener(this::reCentreActionPerformed);

        zoomIn.setText("+");
        zoomIn.addActionListener(this::zoomInActionPerformed);

        zoomOut.setText("-");
        zoomOut.addActionListener(this::zoomOutActionPerformed);

        jLabel1.setFont(new java.awt.Font("Tahoma", Font.ITALIC, 11)); // NOI18N
        jLabel1.setText("Label style:");

        styleMatch.setSelected(true);
        styleMatch.setText("Matches");
        styleMatch.addActionListener(this::styleMatchActionPerformed);

        styleCoverage.setText("Coverage");
        styleCoverage.addActionListener(this::styleCoverageActionPerformed);

        styleChim.setText("Chim Quality");
        styleChim.addActionListener(this::styleChimActionPerformed);

        jLabel5.setFont(new java.awt.Font("Tahoma", Font.ITALIC, 11)); // NOI18N
        jLabel5.setText("View:");

        confidenceSetting.setText("Confidence");
        confidenceSetting.addActionListener(this::confidenceSettingActionPerformed);

        overlapSetting.setText("Overlap");
        overlapSetting.addActionListener(this::overlapSettingActionPerformed);

        jLabel3.setFont(new java.awt.Font("Tahoma", Font.BOLD, 11)); // NOI18N
        jLabel3.setText("Contig tools:");

        reOrientate.setText("reOrientate");
        reOrientate.addActionListener(this::reOrientateActionPerformed);

        deleteContig.setText("delete");
        deleteContig.addActionListener(this::deleteContigActionPerformed);

        resetButton.setText("RESET");
        resetButton.addActionListener(this::resetButtonActionPerformed);

        save.setText("SAVE");
        save.addActionListener(this::saveActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addGroup(jPanel3Layout.createSequentialGroup()
                                                        .addComponent(zoomIn, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(zoomOut, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addComponent(jLabel1)
                                                .addComponent(styleMatch)
                                                .addComponent(styleCoverage)
                                                .addComponent(styleChim)
                                                .addComponent(jLabel5)
                                                .addComponent(confidenceSetting)
                                                .addComponent(overlapSetting)
                                                .addComponent(jLabel3)
                                                .addComponent(reOrientate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(deleteContig, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(resetButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel4)
                                                .addComponent(save, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jSeparator1))
                                        .addComponent(reCentre, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(reCentre)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(zoomIn)
                                        .addComponent(zoomOut))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(styleMatch)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(styleCoverage)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(styleChim)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(confidenceSetting)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(overlapSetting)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(reOrientate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deleteContig)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(resetButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(save)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        // trying to manually add in the labels and not draw them on the panel
        SpringLayout layout2 = new SpringLayout();
        labelPanel.setLayout(layout2);
        layout2.putConstraint(SpringLayout.NORTH, refDataNameLabel, +5, SpringLayout.NORTH, this);
        layout2.putConstraint(SpringLayout.EAST, qryDataNameLabel, -5, SpringLayout.EAST, this);
        labelPanel.add(refDataNameLabel);
        labelPanel.add(qryDataNameLabel);
        labelPanel.add(refDataNameField);
        labelPanel.add(qryDataNameField);


        jLayeredPane2.setLayer(referenceView, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane2.setLayer(jPanel3, javax.swing.JLayeredPane.DEFAULT_LAYER);


        javax.swing.GroupLayout jLayeredPane2Layout = new javax.swing.GroupLayout(jLayeredPane2);
        jLayeredPane2.setLayout(jLayeredPane2Layout);
        jLayeredPane2Layout.setHorizontalGroup(
                jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jLayeredPane2Layout.createSequentialGroup()
                                .addComponent(referenceView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        jLayeredPane2Layout.setVerticalGroup(
                jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(referenceView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jLayeredPane2Layout.createSequentialGroup()
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );

        jSplitPane2.setLeftComponent(jLayeredPane2);

        qryContigTable.setAutoCreateRowSorter(true);
        qryContigTable.setModel(new DefaultTableModel());
        qryContigTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        qryContigTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                qryContigTableMouseClicked();
            }
        });
        refViewTableScroll.setViewportView(qryContigTable);

        tabPaneFiles.addTab("Query Contigs", refViewTableScroll);

        jSplitPane2.setRightComponent(tabPaneFiles);

        refViewPane.setLayer(jSplitPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout refViewPaneLayout = new javax.swing.GroupLayout(refViewPane);
        refViewPane.setLayout(refViewPaneLayout);
        refViewPaneLayout.setHorizontalGroup(
                refViewPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(refViewPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jSplitPane2))
        );
        refViewPaneLayout.setVerticalGroup(
                refViewPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jSplitPane2)
        );

        tabPane.addTab("Reference View", refViewPane);

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        queryView.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        queryView.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                queryViewMouseMoved(evt);
            }
        });

        exportQryButton.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 10)); // NOI18N
        exportQryButton.setText("Export Image");
        exportQryButton.addActionListener(this::exportQryButtonActionPerformed);

        javax.swing.GroupLayout queryViewLayout = new javax.swing.GroupLayout(queryView);
        queryView.setLayout(queryViewLayout);
        queryViewLayout.setHorizontalGroup(
                queryViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(queryViewLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(exportQryButton)
                                .addContainerGap())
        );
        queryViewLayout.setVerticalGroup(
                queryViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(queryViewLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(exportQryButton)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setPreferredSize(new java.awt.Dimension(254, 235));

        jLabel9.setFont(new java.awt.Font("Tahoma", Font.BOLD, 11)); // NOI18N
        jLabel9.setText("Search");

        jLabel10.setText("Reference ID :");

        jLabel11.setText("Query ID :");

        search.setFont(new java.awt.Font("Tahoma", Font.BOLD, 11)); // NOI18N
        search.setText("Search");
        search.addActionListener(this::searchActionPerformed);

        jLabel12.setText("Region :");

        jLabel13.setFont(new java.awt.Font("Tahoma", Font.ITALIC, 11)); // NOI18N
        jLabel13.setText("Format region search as start-end in bp e.g. 20-200");

        regionType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Reference", "Query" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                              //  .addComponent(qryorientate)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap())
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(regionSearch)
                                                .addContainerGap())
                                        .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(jLabel9)
                                                                .addGap(0, 0, Short.MAX_VALUE))
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(jLabel12)
                                                                .addGap(60, 60, 60)
                                                                .addComponent(regionType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                                .addContainerGap())
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel10)
                                                        .addComponent(jLabel11))
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(refIdSearch)
                                                        .addComponent(qryIdSearch))
                                                .addGap(6, 6, 6))))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel10)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(refIdSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(qryIdSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jLabel11))))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(regionType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jLabel12))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(regionSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(search)
                                        )
                                .addContainerGap())
        );

        qryViewRefTable.setAutoCreateRowSorter(true);
        qryViewRefTable.setModel(new DefaultTableModel());
        qryViewRefTable.setPreferredSize(new java.awt.Dimension(254, 64));
        qryViewRefTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        qryViewRefTable.setSize(new java.awt.Dimension(254, 0));
        qryViewRefTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                qryViewRefTableMouseClicked();
            }
        });
        jScrollPane1.setViewportView(qryViewRefTable);

        jLayeredPane1.setLayer(queryView, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jPanel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
                jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jLayeredPane1Layout.createSequentialGroup()
                                .addComponent(queryView, javax.swing.GroupLayout.DEFAULT_SIZE, 1133, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(jLayeredPane1Layout.createSequentialGroup()
                                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                                                .addContainerGap())
                                        .addComponent(jScrollPane1)))
        );
        jLayeredPane1Layout.setVerticalGroup(
                jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jLayeredPane1Layout.createSequentialGroup()
                                .addComponent(queryView, javax.swing.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE)
                                .addGap(12, 12, 12))
                        .addGroup(jLayeredPane1Layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 336, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(jLayeredPane1);

        labelTable.setAutoCreateRowSorter(true);
        labelTable.setModel(new DefaultTableModel());
        labelTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        labelTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelTableMouseClicked();
            }
        });
        queryViewTableScroll.setViewportView(labelTable);

        jSplitPane1.setRightComponent(queryViewTableScroll);

        queryViewPane.setLayer(jSplitPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout queryViewPaneLayout = new javax.swing.GroupLayout(queryViewPane);
        queryViewPane.setLayout(queryViewPaneLayout);
        queryViewPaneLayout.setHorizontalGroup(
                queryViewPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(queryViewPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jSplitPane1))
        );
        queryViewPaneLayout.setVerticalGroup(
                queryViewPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(queryViewPaneLayout.createSequentialGroup()
                                .addComponent(jSplitPane1)
                                .addGap(0, 0, Short.MAX_VALUE))
        );

        saveQueries.setTitle("Save Query Contig table");
        saveQueries.setLocation(new java.awt.Point(100, 100));

        tabPane.addTab("Query View", queryViewPane);

        // SV view
        svSplitPlane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        svView.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        svView.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                svViewMouseMoved(evt);
            }
        });
        svView.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                svViewMouseClicked(evt);
            }
        });
        svView.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                svViewComponentResized();
            }
        });

        exportSVButton.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 10)); // NOI18N
        exportSVButton.setText("Export Image");
        exportSVButton.addActionListener(this::exportSVButtonActionPerformed);
        exportSVButton.setText("Export Image");

        javax.swing.GroupLayout svViewLayout = new javax.swing.GroupLayout(svView);
        svView.setLayout(svViewLayout);
        svViewLayout.setHorizontalGroup(
                svViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, svViewLayout.createSequentialGroup()
                                .addContainerGap(1295, Short.MAX_VALUE)
                                .addComponent(exportSVButton)
                                .addContainerGap())
        );
        svViewLayout.setVerticalGroup(
                svViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(svViewLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(exportSVButton)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        svPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        displayToolsSV.setFont(new java.awt.Font("Tahoma", Font.BOLD, 11)); // NOI18N
        displayToolsSV.setText("Display tools:");

        labelStyleSV.setFont(new java.awt.Font("Tahoma", Font.ITALIC, 11)); // NOI18N
        labelStyleSV.setText("Label style:");

        styleMatchSV.setSelected(true);
        styleMatchSV.setText("Matches");
        styleMatchSV.addActionListener(this::styleMatchSVActionPerformed);

        labelParametersSV.setFont(new java.awt.Font("Tahoma", Font.BOLD, 11)); // NOI18N
        labelParametersSV.setText("SV Parameters:");

        contigToolsSV.setFont(new java.awt.Font("Tahoma", Font.BOLD, 11)); // NOI18N
        contigToolsSV.setText("Contig tools:");

        reOrientateSV.setText("reOrientate");
        reOrientateSV.addActionListener(this::reOrientateActionPerformed);

        styleCigar.setText("CIGAR");
        styleCigar.addActionListener(this::styleCigarActionPerformed);

        indelMinSize.setPreferredSize(new java.awt.Dimension(70, 25));
        indelMinSize.setValue(500);

        indelMaxSize.setPreferredSize(new java.awt.Dimension(100, 25));
        indelMaxSize.setValue(1000000);

        flankSignal.setPreferredSize(new java.awt.Dimension(60, 25));
        flankSignal.setValue(5);

        labelIndelMin.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 11)); // NOI18N
        labelIndelMin.setText("Min indel size:");

        labelIndelMax.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 11)); // NOI18N
        labelIndelMax.setText("Max indel size:");

        labelFlankSig.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 11)); // NOI18N
        labelFlankSig.setText("Flank Signals:");

        saveSVSettings.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 11)); // NOI18N
        saveSVSettings.setText("Save Changes");
        saveSVSettings.addActionListener(this::saveSVParamsActionPerformed);

        allIndels.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 11)); // NOI18N
        allIndels.setText("All Indels");
        allIndels.addActionListener(this::allIndelsActionPerformed);

        javax.swing.GroupLayout svPanelLayout = new javax.swing.GroupLayout(svPanel);
        svPanel.setLayout(svPanelLayout);
        svPanelLayout.setHorizontalGroup(
                svPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(svPanelLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(svPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(svPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(labelStyleSV)
                                                .addComponent(styleMatchSV)
                                                .addComponent(styleCigar)
                                                .addComponent(labelParametersSV)
                                                .addGap(100, 100, 100)
                                                .addComponent(labelIndelMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(100, 100, 100)
                                                .addComponent(indelMinSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(100, 100, 100)
                                                .addComponent(labelIndelMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(100, 100, 100)
                                                .addComponent(indelMaxSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(100, 100, 100)
                                                .addComponent(labelFlankSig, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(100, 100, 100)
                                                .addComponent(flankSignal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(100, 100, 100)
                                                .addComponent(saveSVSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(100, 100, 100)
                                                .addComponent(allIndels, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(100, 100, 100)
                                                .addComponent(contigToolsSV)
                                                .addComponent(reOrientateSV, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(displayToolsSV)
                                                .addComponent(jSeparator1))
        )));
        svPanelLayout.setVerticalGroup(
                svPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(svPanelLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(displayToolsSV)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelStyleSV)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(styleMatchSV)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(styleCigar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(labelParametersSV)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelIndelMin)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(indelMinSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(labelIndelMax)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(indelMaxSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelFlankSig)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(flankSignal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(saveSVSettings)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                                .addComponent(allIndels)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(contigToolsSV)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(reOrientateSV)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        svLayeredPane.setLayer(svView, javax.swing.JLayeredPane.DEFAULT_LAYER);
        svLayeredPane.setLayer(svPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout svLayeredPaneLayout = new javax.swing.GroupLayout(svLayeredPane);
        svLayeredPane.setLayout(svLayeredPaneLayout);
        svLayeredPaneLayout.setHorizontalGroup(
                svLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(svLayeredPaneLayout.createSequentialGroup()
                                .addComponent(svView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(svPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        svLayeredPaneLayout.setVerticalGroup(
                svLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(svView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(svLayeredPaneLayout.createSequentialGroup()
                                .addComponent(svPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );

        svSplitPlane.setLeftComponent(svLayeredPane);

        svTable.setAutoCreateRowSorter(true);
        svTable.setModel(new DefaultTableModel());
        svTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        svTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                svTableMouseClicked();
            }
        });

        svViewTableScroll.setViewportView(svTable);

        tabPaneFilesSV.addTab("Structural Variants", svViewTableScroll);

        svSplitPlane.setRightComponent(tabPaneFilesSV);

        svViewPane.setLayer(svSplitPlane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout svViewPaneLayout = new javax.swing.GroupLayout(svViewPane);
        svViewPane.setLayout(svViewPaneLayout);
        svViewPaneLayout.setHorizontalGroup(
                svViewPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(svViewPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(svSplitPlane))
        );
        svViewPaneLayout.setVerticalGroup(
                svViewPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(svSplitPlane)
        );

        tabPane.addTab("SV View", svViewPane);

        jMenu1.setText("File");

        loadMaps.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        loadMaps.setText("Load Maps");
        loadMaps.addActionListener(this::loadMapsActionPerformed);
        jMenu1.add(loadMaps);

        fastaLoad.setText("Load FASTA File");
        fastaLoad.addActionListener(this::fastaLoadActionPerformed);
        jMenu1.add(fastaLoad);

        jMenu5.setText("Manual Conflict Resolution");

        manualConflict.setText("Load conflicts_cut_status file");
        manualConflict.addActionListener(this::manualConflictActionPerformed);
        jMenu5.add(manualConflict);

        saveConflictFile.setText("Save manual conflict resolution file");
        saveConflictFile.addActionListener(this::saveConflictFileActionPerformed);
        jMenu5.add(saveConflictFile);

        jMenu1.add(jMenu5);

        jMenu2.setText("Export images");

        chooseImages.setText("Choose images to export");
        chooseImages.addActionListener(this::chooseImagesActionPerformed);
        jMenu2.add(chooseImages);

        exportImages.setText("Export chosen images");
        exportImages.addActionListener(this::exportImagesActionPerformed);
        jMenu2.add(exportImages);

        jMenu1.add(jMenu2);

        close.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        close.setText("Close");
        close.addActionListener(this::closeActionPerformed);
        jMenu1.add(close);

        menuBar.add(jMenu1);

        jMenu3.setText("Quick-tools");

        swapContigs.setText("Swap reference and query");
        swapContigs.addActionListener(this::swapContigsActionPerformed);
        jMenu3.add(swapContigs);

        orientateContigs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        orientateContigs.setText("Orientate all contigs");
        orientateContigs.addActionListener(this::orientateContigsActionPerformed);
        jMenu3.add(orientateContigs);

        saveAllContigs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveAllContigs.setText("Save view for all contigs");
        saveAllContigs.addActionListener(this::saveAllContigsActionPerformed);
        jMenu3.add(saveAllContigs);

        menuBar.add(jMenu3);

        jMenu4.setText("Settings");

        confidenceSet.setText("Confidence thresholds");
        confidenceSet.addActionListener(this::confidenceSetActionPerformed);
        jMenu4.add(confidenceSet);

        coverageSet.setText("Coverage thresholds");
        coverageSet.addActionListener(this::coverageSetActionPerformed);
        jMenu4.add(coverageSet);

        chimqualSet.setText("Chimeric quality thresholds");
        chimqualSet.addActionListener(this::chimqualSetActionPerformed);
        jMenu4.add(chimqualSet);

        menuBar.add(jMenu4);

        Save.setText("Export Tables");
        saveQueryContigs.setText("Save Query Contigs");
        saveQueryContigs.addActionListener(this::saveQryContigsSetActionPerformed);

        saveQueryLabels.setText("Query Labels");
        saveQueryLabels.addActionListener(this::saveQryLabelsSetActionPerformed);

        saveSVTable.setText("Save SV Table");
        saveSVTable.addActionListener(this::saveSVTableSetActionPerformed);
        
        Save.add(saveQueryContigs);
        Save.add(saveQueryLabels);
        Save.add(saveSVTable);

        menuBar.add(Save);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tabPane)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tabPane)
        );

        pack();
    }

    private void allIndelsActionPerformed(ActionEvent actionEvent) {
        if(allIndels.isSelected()){
            SVView.setIndels(detectSV);
            SVView.setAllIndels(true);
        } else if (!allIndels.isSelected()){
            SVView.setAllIndels(false);
        }
        repaint();
    }

    private void styleMatchSVActionPerformed(ActionEvent actionEvent) {
        if (styleMatch.isSelected()) {
            SVView.setStyle("match");
            repaint();
        }
    }

    private void svViewMouseClicked(java.awt.event.MouseEvent evt) {
    }

    private void svViewMouseMoved(java.awt.event.MouseEvent evt) {

    }

    private void exportSVButtonActionPerformed(ActionEvent actionEvent) {
        // export chosen image into chosen directory
        // Opens a dialog box for user to choose directory of file
        FileDialog fileBox;
        fileBox = new FileDialog(this, "Save PDF of reference alignment view", FileDialog.SAVE);
        fileBox.setVisible(true);

        if (fileBox.getFile() != null) {
            String chosenPath = fileBox.getDirectory();
            String chosenFile = fileBox.getFile();

            exportSVButton.setVisible(false);
            try {
                PDFDocument doc = new PDFDocument ();

                // Use a Paper instance to change page dimensions, some plots can be long
                Paper p = new Paper();
                p.setSize(svView.getWidth(), svView.getHeight());
                p.setImageableArea(0, 0, svView.getWidth(), svView.getHeight());
                PageFormat pf = new PageFormat ();
                pf.setPaper(p);

                PDFPage page = doc.createPage(pf);
                doc.addPage(page);

                // Directly paint the panel to the pdf page
                Graphics2D g2d = page.createGraphics();
                svView.paint(g2d);

                doc.saveDocument(chosenPath + chosenFile + ".pdf");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error saving image to pdf file", "Error", JOptionPane.ERROR_MESSAGE);
            }

            exportSVButton.setVisible(true);

        } else {
            JOptionPane.showMessageDialog(null, "No filename given", "Invalid input", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void svTableMouseClicked() {
        if (svTable.getRowCount() != 0) {
            String chosenQry = svTable.getValueAt(svTable.getSelectedRow(), 0).toString();
            changeQry(chosenQry);
            String qryStart = svTable.getValueAt(svTable.getSelectedRow(), 1).toString();
            String qryEnd = svTable.getValueAt(svTable.getSelectedRow(), 2).toString();
            String refStart = svTable.getValueAt(svTable.getSelectedRow(), 3).toString();
            String refEnd = svTable.getValueAt(svTable.getSelectedRow(), 4).toString();
            String type = svTable.getValueAt(svTable.getSelectedRow(), 5).toString();
            SV chosenSV = detectSV.getSV(qryStart, qryEnd, refStart, refEnd, type);
            changeSV(chosenSV);
            repaint();
        }

    }

    private void svViewComponentResized() {
    }


    private void qryContigTableMouseClicked() {
        // get selected contig from query table
        if (qryContigTable.getRowCount() != 0) {
            String chosenQry = qryContigTable.getValueAt(qryContigTable.getSelectedRow(), 0).toString();
            changeQry(chosenQry);

        }
    }


    private void exportTables (JTable saveTable) {
        // Table to be exported to CSV
        if (saveTable.getRowCount() != 0) {
            // Saves Query Contig table from Reference View to CSV output
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Enter File Name");

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                // Uses CSV Writer class to write to user defined file
                CSVWriter qryContigsOut;
                try {
                    if (fileToSave.getPath().endsWith(".csv")) {
                        qryContigsOut = new CSVWriter(new FileWriter(fileToSave));
                    } else {
                        qryContigsOut = new CSVWriter(new FileWriter(fileToSave + ".csv"));
                    }

                    // A string array for table headers
                    String[] header = new String[saveTable.getColumnCount()];

                    // A string array for table output
                    String[] qryOut = new String[saveTable.getColumnCount()];
                    // Check that table is populated

                    // Get column names and add as CSV header
                    for (int name = 0; name < saveTable.getColumnCount(); name++) {
                        header[name] = saveTable.getColumnName(name);
                    }
                    qryContigsOut.writeNext(header);

                    for (int i = 0; i < saveTable.getRowCount(); i++) {
                        // Nest for loops to take values from table and add to CSV file
                        for (int j = 0; j < saveTable.getColumnCount(); j++) {

                            qryOut[j] = saveTable.getValueAt(i, j).toString();

                        }

                        qryContigsOut.writeNext(qryOut);
                    }
                    // Close CSV file
                    qryContigsOut.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else {
            JOptionPane.showMessageDialog(this, "Table is not populated please select " +
                    "a reference", "Error in Export Table", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void orientateContigsActionPerformed(java.awt.event.ActionEvent evt) {
        // orientates all query contigs that are negatively oriented
        if (!model.getSelectedRefID().isEmpty()) {
            for (Query qry : model.getSelectedRef().getQueries()) {
                if ((qry.getOrientation().equals("-") && !qry.isFlipped()) ||
                        (qry.getOrientation().equals("+") && qry.isFlipped())) {
                    qry.reOrientate();
                }
            }
        }
    }

    private void browseXmapActionPerformed(java.awt.event.ActionEvent evt) {
        // browse for xmap file
        // Opens a dialog box for user to choose directory of file
        FileDialog fileBox;
        fileBox = new FileDialog(this, "Open XMAP File", FileDialog.LOAD);
        fileBox.setVisible(true);
        if (fileBox.getFile() != null) {
            String fileDirectory = fileBox.getDirectory();
            String filename = fileDirectory.concat(fileBox.getFile());

            // set text field to display name
            xmapFileTextField.setText(filename);
        }
    }

    private void browseRefActionPerformed(java.awt.event.ActionEvent evt) {
        // browse for ref cmap file
        // Opens a dialog box for user to choose directory of file
        FileDialog fileBox;
        fileBox = new FileDialog(this, "Open Reference CMAP File", FileDialog.LOAD);
        fileBox.setVisible(true);
        if (fileBox.getFile() != null) {
            String fileDirectory = fileBox.getDirectory();
            String filename = fileDirectory.concat(fileBox.getFile());

            // set text field to display name
            refFileTextField.setText(filename);
        }
    }

    private void browseQryActionPerformed(java.awt.event.ActionEvent evt) {
        // browse for qry cmap file
        // Opens a dialog box for user to choose directory of file
        FileDialog fileBox;
        fileBox = new FileDialog(this, "Open Query CMAP File", FileDialog.LOAD);
        fileBox.setVisible(true);
        if (fileBox.getFile() != null) {
            String fileDirectory = fileBox.getDirectory();
            String filename = fileDirectory.concat(fileBox.getFile());

            // set text field to display name
            qryFileTextField.setText(filename);
        }
    }

    private void runAnalysisActionPerformed(java.awt.event.ActionEvent evt) {
        fileLoader.setVisible(false);
        // reset all data
        resetData();
        model.setReversed(false);

        String qryPath = qryFileTextField.getText();
        String refPath = refFileTextField.getText();
        String xmapPath = xmapFileTextField.getText();

        if (!(qryPath + refPath + xmapPath).equals(EMPTY_STRING)) {

            this.refPath = refPath;
            this.qryPath = qryPath;
            this.xmapPath = xmapPath;

            boolean validFiles = CmapReader.validateCmap(refPath) &&
                    CmapReader.validateCmap(qryPath) &&
                    XmapReader.validateXmap(xmapPath);

            if (validFiles) {
                model.setQryFile(new File(qryPath));
                model.setRefFile(new File(refPath));
                model.setXmapFile(new File(xmapPath));

                refDataset.setVisible(true);
                qryDataset.setVisible(true);
                String refCmapDataset = FilenameUtils.getName(refPath);
                String qryCmapDataset = FilenameUtils.getName(qryPath);
                refDataset.setText(refCmapDataset);
                qryDataset.setText(qryCmapDataset);

                setAllData();
            }

        } else {
            JOptionPane.showMessageDialog(null, "Not all files have been declared", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exitVerifyAssemblyActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        this.setVisible(false);
        startScreen screen = new startScreen();
        screen.setVisible(true);
    } 

    private void loadMapsActionPerformed(java.awt.event.ActionEvent evt) {
        // displays menu
        fileLoader.setVisible(true);
    }

    private void swapContigsActionPerformed(java.awt.event.ActionEvent evt) {
        // swap the query and the reference around
        if (!qryPath.equals(EMPTY_STRING) && !xmapPath.equals(EMPTY_STRING) && !refPath.equals(EMPTY_STRING)) {
            int swap = JOptionPane.showConfirmDialog(
                    null, "Are you sure you would like to swap the query " +
                            "and the reference dataset? The data will reset to default",
                    "Swap Contigs", JOptionPane.YES_NO_OPTION);
            if (swap == JOptionPane.YES_OPTION) {
                // reset all data
                resetData();
                model.swapRefQry();

                String refData = refDataset.getText();
                refDataset.setText(qryDataset.getText());
                qryDataset.setText(refData);
                setAllData();
            }
        } else {
            JOptionPane.showMessageDialog(
                    null, "Not all files have been declared - load files first",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void qryViewRefTableMouseClicked() {
        // get selected contig
        if (qryViewRefTable.getRowCount() != 0) {
            String chosenRef = qryViewRefTable.getValueAt(qryViewRefTable.getSelectedRow(), 0).toString();
            changeRef(chosenRef);
            repaint();
        }
    }

    private void labelTableMouseClicked() {
        // set label and draw on diagram
        if (labelTable.getRowCount() != 0) {
            String chosenLabel = labelTable.getValueAt(labelTable.getSelectedRow(), 0).toString();
            QueryView.setChosenLabel(chosenLabel);
            repaint();
        }
    }

    private void referenceViewMouseClicked(java.awt.event.MouseEvent evt) {
        // Set clicked contig
        Rectangle2D qryRect;
        boolean qryMatch = false;
        Reference chosenRef = model.getSelectedRef();
        if (!chosenRef.getRefID().equals(EMPTY_STRING)) {
            for (Query qry : chosenRef.getQueries()) {
                qryRect = qry.getRefViewRect();
                if (qryRect != null && qryRect.contains(evt.getPoint())) {
                    qryMatch = true;
                    changeQry(qry.getID());
                }
            }
            if (!qryMatch) {
                changeQry(EMPTY_STRING);
            }
            repaint();
        }
    }

    private void zoomInActionPerformed(java.awt.event.ActionEvent evt) {
        // zoom in reference view
        if (!ReferenceView.getChosenRef().equals(EMPTY_STRING)) {
            referenceView.zoomIn();
            referenceView.repaint();
        }
    }

    private void zoomOutActionPerformed(java.awt.event.ActionEvent evt) {
        // zoom out reference view
        if (!ReferenceView.getChosenRef().equals(EMPTY_STRING)) {
            referenceView.zoomOut();
            referenceView.repaint();
        }
    }

    private void styleMatchActionPerformed(java.awt.event.ActionEvent evt) {
        // when button clicked set style in reference view
        if (styleMatch.isSelected()) {
            ReferenceView.setStyle("match");
            QueryView.setStyle("match");
            repaint();
        }
    }

    private void styleCoverageActionPerformed(java.awt.event.ActionEvent evt) {
        // when button clicked set style in reference view
        if (styleCoverage.isSelected()) {
            ReferenceView.setStyle("coverage");
            QueryView.setStyle("coverage");
            repaint();
        }
    }

    private void styleChimActionPerformed(java.awt.event.ActionEvent evt) {
        // set style to chimeric quality values
        if (styleChim.isSelected()) {
            ReferenceView.setStyle("chimQual");
            QueryView.setStyle("chimQual");
            SVView.setStyle("chimQual");
            repaint();
        }
    }

    private void styleCigarActionPerformed(java.awt.event.ActionEvent evt) {
        // set style to CIGAR labelling
        if (styleCigar.isSelected()) {
            SVView.setStyle("cigar");
            repaint();
        }
    }

    private void confidenceSettingActionPerformed(java.awt.event.ActionEvent evt) {
        // when check box ticked, colour query contigs by confidence score
        boolean checked = confidenceSetting.isSelected();
        QueryView.setConfidenceView(checked);
        ReferenceView.setConfidenceView(checked);
        repaint();
    }

    private void reOrientateActionPerformed(java.awt.event.ActionEvent evt) {
        // reorientate chosen contig
        if (!ReferenceView.getChosenRef().equals(EMPTY_STRING) && !ReferenceView.getChosenQry().equals(EMPTY_STRING)) {
            for (Query qry : model.getSelectedRef().getQueries()) {
                if (qry.getID().equals(ReferenceView.getChosenQry())) {
                    qry.reOrientate();
                }
            }
            repaint();
        }
    }

    private void deleteContigActionPerformed(java.awt.event.ActionEvent evt) {
        if (!model.getSelectedRefID().equals(EMPTY_STRING) && !ReferenceView.getChosenQry().equals(EMPTY_STRING)) {
            int delete = JOptionPane.showConfirmDialog(null, "Are you sure you would like to delete this query contig?", "Delete", JOptionPane.YES_NO_OPTION);
            if (delete == JOptionPane.YES_OPTION) {

                // create ref object for selected ref
                Reference chosenRef = model.getSelectedRef();
                // convert selected qryID string to integer
                int chosenQry = Integer.parseInt(ReferenceView.getChosenQry());
                // populate deleted contigs list with deleted qryIDs
                chosenRef.setDelQryIDs(chosenQry);
                ReferenceView.setChosenQry(EMPTY_STRING);
                repaint();
            }
        }
    }

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // check the user would really like to reset
        if (!model.getSelectedRefID().equals(EMPTY_STRING)) {
            Object[] choices = {"Default", "Last saved", "Cancel"};

            int n = JOptionPane.showOptionDialog(null,
                    "Would you like to reset to default? Or last saved?",
                    "Reset Reference View",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    choices,
                    choices[2]);

            switch (n) {
                case DEFAULT:
                    // reset view to default overlap
                    Reference refD = model.getSelectedRef();
                    refD.getDelQryIDs().clear();
                    for (Query qry : refD.getQueries()) {
                        qry.setRefViewOffsetX(-qry.getRefViewOffsetX());
                        qry.setRefViewOffsetY(-qry.getRefViewOffsetY());
                        if (qry.isFlipped()) {
                            qry.reOrientate();
                        }
                    }
                    break;
                case LAST_SAVED:
                    // reset view to last saved
                    Reference refLS = model.getSelectedRef();
                    refLS.getDelQryIDs().clear();
                    for (int delQry : refLS.getSavedDelQryIDs()) {
                        refLS.setDelQryIDs(delQry);
                    }
                    break;
                default:
                    break;
            }

            referenceView.reCenter();
            repaint();
        }
    }

    private void saveActionPerformed(java.awt.event.ActionEvent evt) {
        // data is saved to summary view
        if (!ReferenceView.getChosenRef().equals(EMPTY_STRING)) {
            int save = JOptionPane.showConfirmDialog(null, "Would you like to save changes for this reference contig?", "Save Changes", JOptionPane.YES_NO_OPTION);
            if (save == JOptionPane.YES_OPTION) {
                Reference ref =  model.getSelectedRef();
                ref.getSavedDelQryIDs().clear();
                ref.setSavedDelQryIDs(new ArrayList<>(ref.getDelQryIDs()));
                repaint();
            }
        }
    }

    private void reCentreActionPerformed(java.awt.event.ActionEvent evt) {
        // recentre the view
        if (!ReferenceView.getChosenRef().equals(EMPTY_STRING)) {
            referenceView.reCenter();
            referenceView.repaint();
        }
    }

    private void searchActionPerformed(java.awt.event.ActionEvent evt) {
        // clear

        // set IDs of reference and query to that searched
        boolean refMatch = false;
        boolean qryMatch = false;
        String refSearch = refIdSearch.getText();
        String qrySearch = qryIdSearch.getText();
        String region = regionSearch.getText();
        String type;
        type = Objects.requireNonNull(regionType.getSelectedItem()).toString();
        List<String> refcontig = new ArrayList<>();
        List<String> qrycontig = new ArrayList<>();
        String currentref=model.getSelectedRefID();
        // check if qry Id or ref Id exist otherwise give error message
        if (!refSearch.equals(EMPTY_STRING)) {
            int coln=0;

            for(int i=0;i<refContigTable.getRowCount();i++){
                String refID = refContigTable.getValueAt(i,coln).toString();
                refcontig.add(refID);
            }
            if(!refcontig.contains(refSearch)){
                JOptionPane.showMessageDialog(null, "No such reference ID", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }else{refMatch =true;}
        }else{
            JOptionPane.showMessageDialog(null, "Please provide a reference ID", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }

        if (!qrySearch.equals(EMPTY_STRING) && refMatch) {
            changeRef(refSearch);
            int col=0;
            for(int i=0;i<qryContigTable.getRowCount();i++){
                String qryID = qryContigTable.getValueAt(i,col).toString();
                qrycontig.add(qryID);
            }
            if(!qrycontig.contains(qrySearch)){
                changeRef(currentref);
                JOptionPane.showMessageDialog(null, "No such qry ID", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }else{
                qryMatch =true;}
        }
        //then display the qryview
        if(refMatch&qryMatch) {

            if(region.equals("")){
                QueryView.setRegionView(false);
                QueryView.setReferenceViewSelect(false);
                QueryView.setQryViewSelect(false);
                changeRef(refSearch);
                changeQry(qrySearch);
                repaint();

            }else{
                String[] regions = region.split("-");
                int regionstart = Integer.parseInt(regions[0]);
                int regionend = Integer.parseInt(regions[1]);
                if(regionstart<0 || regionstart>=regionend){
                    JOptionPane.showMessageDialog(null, "Invalid region", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }else{
                    QueryView.setRegionscale(regions);
                    if(type.equals("Query")){

                        changeRef(refSearch);
                        changeQry(qrySearch);
                        Reference ref = model.getSelectedRef();
                        Query qry = ref.getQuery(qrySearch);
                        if(regionstart >= qry.getLength()||regionend>= qry.getLength())
                        {
                            JOptionPane.showMessageDialog(null, "Invalid region", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        }else{
                            QueryView.setRegionView(true);
                            QueryView.setReferenceViewSelect(false);
                            QueryView.setQryViewSelect(true);
                        }



                    }else if(type.equals("Reference")){
                        changeRef(refSearch);
                        changeQry(qrySearch);
                        Reference ref = model.getSelectedRef();
//                        Query qry = ref.getQuery(qrySearch);
                        if(regionstart >= ref.getLength()||regionend>= ref.getLength())
                        {
                            JOptionPane.showMessageDialog(null, "Invalid region", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        }else{
                            QueryView.setRegionView(true);
                            QueryView.setQryViewSelect(false);
                            QueryView.setReferenceViewSelect(true);}
                    }
                    repaint();
                }
            }
        }
    }


    private void referenceViewMouseMoved(java.awt.event.MouseEvent evt) {
        // when mouse is hovered over, display the position
        if (!ReferenceView.getChosenRef().equals(EMPTY_STRING)) {
            double positionScale;
            String position = EMPTY_STRING;
            Rectangle2D ref = model.getSelectedRef().getRefViewRect();
            if (ref.contains(evt.getPoint())) {
                // display position
                positionScale = model.getSelectedRef().getLength() / ref.getWidth();
                position = String.format("%.2f", (evt.getPoint().getX() - ref.getMinX()) * positionScale);
            }
            Rectangle2D qryRect;
            for (Query qry : model.getSelectedRef().getQueries()) {
                // don't display deleted query contig position
//                int refID = Integer.parseInt(model.getSelectedRef().getRefID());
                if (model.getSelectedRef().getDelQryIDs().contains(Integer.parseInt(qry.getID()))) {
                    continue;
                }
                qryRect = qry.getRefViewRect();
                if (qryRect.contains(evt.getPoint())) {
                    // display position
                    positionScale = qry.getLength() / qryRect.getWidth();
                    position = String.format("%.2f", (evt.getPoint().getX() - qryRect.getMinX()) * positionScale);
                }
            }

            ReferenceView.setPosition(position);
            ReferenceView.setMouseX(evt.getX());
            ReferenceView.setMouseY(evt.getY());

            referenceView.repaint(evt.getX() - 500, evt.getY() - 500, 1000, 1000);
        }
    }

    private void queryViewMouseMoved(java.awt.event.MouseEvent evt) {

        // when mouse is hovered over, display the position
        if (!QueryView.getChosenQry().isEmpty()) {

            if (!model.getSelectedRef().getDelQryIDs().contains(Integer.parseInt(QueryView.getChosenQry()))
                    && !QueryView.getChosenRef().isEmpty() && !QueryView.getChosenQry().isEmpty()) {
                double positionScale;
                String position = "";

                String qryId = QueryView.getChosenQry();

                double refstart = QueryViewData.getRefStart();

                //get qryrectangle
                Rectangle2D ref = model.getSelectedRef().getQryViewRect();
                if (ref.contains(evt.getPoint())) {
                    //get the information of the displayed region from QueryViewData
                    positionScale =QueryViewData.gethScale();
                    // display position
                    position = String.format("%.2f", (evt.getPoint().getX() - ref.getMinX()) * positionScale+refstart);
                }
                Rectangle2D qryRect;

                Reference ref1 = model.getSelectedRef();
                Query qry= ref1.getQuery(qryId);
                qryRect = qry.getQryViewRect();

                if (qryRect.contains(evt.getPoint())) {
                    //check if is negative orientated
                   boolean isFlipped= model.getSelectedRef().getQuery(qryId).isFlipped();

                    if(isFlipped){
                        // display position
                        positionScale = QueryViewData.gethScale();
                        position = String.format("%.2f", ((this.getWidth()- evt.getPoint().getX()) - (this.getWidth()- qryRect.getMaxX()))* positionScale);
                    }else{
                        // display position
                        positionScale = QueryViewData.gethScale();
                        position = String.format("%.2f", (evt.getPoint().getX() - qryRect.getMinX()) * positionScale);
                    }
                }


                QueryView.setPosition(position);
                QueryView.setMouseX(evt.getX());
                QueryView.setMouseY(evt.getY());

                queryView.repaint(evt.getX() - 500, evt.getY() - 500, 1000, 1000);

            }
        }
    }


    private void confidenceSetActionPerformed(java.awt.event.ActionEvent evt) {
        // show confidence settings
        confidenceSettings.setVisible(true);
    }

    private void coverageSetActionPerformed(java.awt.event.ActionEvent evt) {
        // show coverage settings
        coverageSettings.setVisible(true);
    }

    private void chimqualSetActionPerformed(java.awt.event.ActionEvent evt) {
        // show chimeric quality settings
        chimSettings.setVisible(true);
    }

    private void saveQryContigsSetActionPerformed(java.awt.event.ActionEvent evt) {
        // show chimeric quality settings
        //saveQueries.setVisible(true);
        exportTables(qryContigTable);
    }

    private void saveQryLabelsSetActionPerformed(java.awt.event.ActionEvent evt) {
        // show chimeric quality settings
        //saveQueries.setVisible(true);
        exportTables(labelTable);
    }

    private void saveSVTableSetActionPerformed(java.awt.event.ActionEvent evt) {
        // show chimeric quality settings
        //saveQueries.setVisible(true);
        JOptionPane.showMessageDialog(
                    null, "Saving the contents of the SV table",
                    "Save table", JOptionPane.DEFAULT_OPTION);
        exportTables(svTable);
    }

    private void saveConfThresholdsActionPerformed(java.awt.event.ActionEvent evt) {
        // save all thresholds set in confidence settings
        ReferenceView.setLowConf((int) lowConf.getValue());
        ReferenceView.setHighConf((int) highConf.getValue());
        QueryView.setQryHighConf((int) highConf.getValue());
        QueryView.setQryLowConf((int) lowConf.getValue());
        confidenceSettings.setVisible(false);
        repaint();
    }

    private void saveCovThresholdsActionPerformed(java.awt.event.ActionEvent evt) {
        // save all thresholds in coverage settings
        ReferenceView.setLowCov((int) lowCov.getValue());
        ReferenceView.setHighCov((int) highCov.getValue());
        QueryView.setQryLowCov((int) lowCov.getValue());
        QueryView.setQryHighCov((int) highCov.getValue());
        coverageSettings.setVisible(false);
        repaint();
    }

    private void saveQualitySettingsActionPerformed(java.awt.event.ActionEvent evt) {
        // save all thresholds in chim quality settings
        ReferenceView.setLowQual((int) lowQual.getValue());
        ReferenceView.setHighQual((int) highQual.getValue());
        QueryView.setQryLowQual((int) lowQual.getValue());
        QueryView.setQryHighQual((int) highQual.getValue());
        chimSettings.setVisible(false);
        repaint();
    }

    private void saveAllContigsActionPerformed(java.awt.event.ActionEvent evt) {
        // save the view of all contigs
        if (!model.getReferences().isEmpty()) {
            int saveAll = JOptionPane.showConfirmDialog(null, "Are you sure you would like to save the view of all contigs?", "Save All Contigs", JOptionPane.YES_NO_OPTION);
            if (saveAll == JOptionPane.YES_OPTION) {
                for (Reference ref : model.getReferences()) {
                    ref.getSavedDelQryIDs().clear();
                    ref.setSavedDelQryIDs(new ArrayList<>(ref.getDelQryIDs()));
                }
                repaint();
            }
        } else {
            JOptionPane.showMessageDialog(null, "No data loaded", "Invalid input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveSVParamsActionPerformed(java.awt.event.ActionEvent evt) {
        // save all SV parameters changed
        SVViewData.resetData();
        SVViewData.setSVViewData(model, detectSV);
        detectSV.setMinIndelSize((int) indelMinSize.getValue());
        detectSV.setMaxIndelSize((int) indelMaxSize.getValue());
        detectSV.setFlankSig((int) flankSignal.getValue());
        detectSV.setSVList();
        fillSVTable(model.getSelectedRef().getRefID(), detectSV);
        repaint();
    }

    private void overlapSettingActionPerformed(java.awt.event.ActionEvent evt) {
        // when check box ticked, colour regions of overlap
        boolean checked = overlapSetting.isSelected();
        ReferenceView.setOverlapView(checked);
        repaint();
    }

    private void chooseImagesActionPerformed(java.awt.event.ActionEvent evt) {
        if (tabPaneFiles.indexOfTab("Choose Images") == -1) {
            fillImageTable();
            JScrollPane scrollPane = new JScrollPane();
            tabPaneFiles.addTab("Choose Images", scrollPane);
            scrollPane.getViewport().add(imageTable);
            imageTable.getTableHeader().setLayout(new BorderLayout());
            imageTable.getColumnModel().getColumn(1).setHeaderRenderer(new EditableHeaderRenderer(selectAllImages));
        } else {
            JOptionPane.showMessageDialog(null, "Choose Images tab exists already in Reference View - use this to choose images to export", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportQryButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // export chosen image into chosen directory
        // Opens a dialog box for user to choose directory of file
        FileDialog fileBox;
        fileBox = new FileDialog(this, "Save PDF of query alignment view", FileDialog.SAVE);
        fileBox.setVisible(true);

        if (fileBox.getFile() != null) {
            String chosenPath = fileBox.getDirectory();
            String chosenFile = fileBox.getFile();

            exportQryButton.setVisible(false);
            try {
                PDFDocument doc = new PDFDocument ();

                // Use a Paper instance to change page dimensions, some plots can be long
                Paper p = new Paper();
                p.setSize(queryView.getWidth(), queryView.getHeight());
                p.setImageableArea(0, 0, queryView.getWidth(), queryView.getHeight());
                PageFormat pf = new PageFormat ();
                pf.setPaper(p);

                PDFPage page = doc.createPage(pf);
                doc.addPage(page);

                // Directly paint the panel to the pdf page
                Graphics2D g2d = page.createGraphics();
                queryView.paint(g2d);

                doc.saveDocument(chosenPath + chosenFile + ".pdf");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error saving image to pdf file", "Error", JOptionPane.ERROR_MESSAGE);
            }

            exportQryButton.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "No filename given", "Invalid input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportRefButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // export chosen image into chosen directory
        // Opens a dialog box for user to choose directory of file
        FileDialog fileBox;
        fileBox = new FileDialog(this, "Save PDF of reference alignment view", FileDialog.SAVE);
        fileBox.setVisible(true);

        if (fileBox.getFile() != null) {
            String chosenPath = fileBox.getDirectory();
            String chosenFile = fileBox.getFile();

            exportRefButton.setVisible(false);
            try {
                PDFDocument doc = new PDFDocument ();

                // Use a Paper instance to change page dimensions, some plots can be long
                Paper p = new Paper();
                p.setSize(referenceView.getWidth(), referenceView.getHeight());
                p.setImageableArea(0, 0, referenceView.getWidth(), referenceView.getHeight());
                PageFormat pf = new PageFormat ();
                pf.setPaper(p);

                PDFPage page = doc.createPage(pf);
                doc.addPage(page);

                // Directly paint the panel to the pdf page
                Graphics2D g2d = page.createGraphics();
                referenceView.paint(g2d);

                doc.saveDocument(chosenPath + chosenFile + ".pdf");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error saving image to pdf file", "Error", JOptionPane.ERROR_MESSAGE);
            }

            exportRefButton.setVisible(true);

        } else {
            JOptionPane.showMessageDialog(null, "No filename given", "Invalid input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void manualConflictActionPerformed(java.awt.event.ActionEvent evt) {
        // Opens a dialog box for user to choose directory of file
        if (tabPaneFiles.indexOfTab("Conflict Resolution") == -1) {
            FileDialog fileBox;
            fileBox = new FileDialog(this, "Open conflicts_cut_status File", FileDialog.LOAD);
            fileBox.setVisible(true);

            if (fileBox.getFile() != null) {
                // Get file name and directory
                String fileDirectory = fileBox.getDirectory();
                String filename = fileDirectory.concat(fileBox.getFile());

                ConflictFileReader.readConflictFile(conflictsTable, filename);
                JScrollPane scrollPane = new JScrollPane();
                tabPaneFiles.addTab("Conflict Resolution", scrollPane);
                scrollPane.getViewport().add(conflictsTable);
            }

        } else {
            JOptionPane.showMessageDialog(null, "Conflict resolution file already open", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fastaLoadActionPerformed(java.awt.event.ActionEvent evt) {
        // Open dialog for fasta loading
        fastaLoader.setVisible(true);
    }

    private void closeActionPerformed(java.awt.event.ActionEvent evt) {
        // close program
        System.exit(0);
    }

    private void browseKeyActionPerformed(java.awt.event.ActionEvent evt) {
        // browse for key file
        // Opens a dialog box for user to choose directory of file
        FileDialog fileBox;
        fileBox = new FileDialog(this, "Open KEY File", FileDialog.LOAD);
        fileBox.setVisible(true);

        if (fileBox.getFile() != null) {
            // Get file name and directory
            String fileDirectory = fileBox.getDirectory();
            String filename = fileDirectory.concat(fileBox.getFile());

            keyFile.setText(filename);
        }
    }

    private void browseFastaActionPerformed(java.awt.event.ActionEvent evt) {
        // browse for fasta file
        // Opens a dialog box for user to choose directory of file
        FileDialog fileBox;
        fileBox = new FileDialog(this, "Open FASTA File", FileDialog.LOAD);
        fileBox.setVisible(true);

        if (fileBox.getFile() != null) {
            // Get file name and directory
            String fileDirectory = fileBox.getDirectory();
            String filename = fileDirectory.concat(fileBox.getFile());

            fastaFile.setText(filename);
        }
    }

    private void loadFastaFileActionPerformed(java.awt.event.ActionEvent evt) {
        // load both files into the program
        if (!fastaFile.getText().equals(EMPTY_STRING)) {
            fastaLoader.setVisible(false);
            ArrayList<String> qryIds = model.getQueryList();
            // System.out.println(qryIds);

            String refQry = Objects.requireNonNull(refOrQry.getSelectedItem()).toString();

            if (refQry.equals("Reference")) {
                ArrayList<String> refIds = new ArrayList<>();
                for (Reference ref : model.getReferences()) {
                    refIds.add(ref.getRefID());
                }

                LinkedHashMap<String, String> names = FastaReader.readKeyFile(keyFile.getText(), refIds,"ref",model);
                LinkedHashMap<String, ArrayList<Integer>> sequences = FastaReader.readFasta(fastaFile.getText(), names);
                QueryViewData.addSequences(sequences);
                // add sequences to contigs
                QueryView.setQrySequences(false);
                QueryView.setRefSequences(true);
                //UserQryData.addSequences(names, sequences, "ref");

            } else if (refQry.equals("Query")) {
                //get query ids

                LinkedHashMap<String, String> names = FastaReader.readKeyFile(keyFile.getText(), qryIds,"qry",model);
                LinkedHashMap<String, ArrayList<Integer>> sequences = FastaReader.readFasta(fastaFile.getText(), names);
                QueryViewData.addSequences(sequences);
                // add sequences to contigs
                QueryView.setRefSequences(false);
                QueryView.setQrySequences(true);

                //UserQryData.addSequences(names, sequences, "qry");
            }
            repaint();
        } else {
            JOptionPane.showMessageDialog(null, "No FASTA file has be chosen", "Invalid input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveConflictFileActionPerformed(java.awt.event.ActionEvent evt) {
        // save conflict file with changes if tab is open
        if (tabPaneFiles.indexOfTab("Conflict Resolution") != -1) {
            // Opens a dialog box for user to save file
            FileDialog fileBox;
            fileBox = new FileDialog(this, "Open conflicts_cut_status File", FileDialog.SAVE);
            fileBox.setVisible(true);

            // Get file name and directory
            if (fileBox.getFile() != null) {
                String fileDirectory = fileBox.getDirectory();
                String filename = fileDirectory.concat(fileBox.getFile());

                ConflictFileWriter.writeConflictFile(filename, conflictsTable, ConflictFileReader.getFirstRows());

                tabPaneFiles.remove(tabPaneFiles.indexOfTab("Conflict Resolution"));

            } else {
                JOptionPane.showMessageDialog(null, "File name not entered", "Invalid input", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No conflict resolution file is loaded", "No file found", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportImagesActionPerformed(java.awt.event.ActionEvent evt) {
        // export chosen images into chosen directory
        if (tabPaneFiles.indexOfTab("Choose Images") != -1) {
            // Opens a dialog box for user to choose directory of file
            FileDialog fileBox;
            fileBox = new FileDialog(this, "Choose a directory to save images", FileDialog.SAVE);
            fileBox.setVisible(true);

            String chosenPath = fileBox.getDirectory();
            String chosenDir = fileBox.getFile();

            if (chosenDir != null) {

                new File(chosenPath + chosenDir);

                PDFDocument doc;
                Paper p = new Paper();
                PageFormat pf;
                PDFPage page;
                Graphics2D g2d;

                exportRefButton.setVisible(false);
                // loop through all chosen images
                for (int i = 0; i < imageTable.getRowCount(); i++) {
                    imageTable.setRowSelectionInterval(i, i);
                    boolean chosen = imageTable.getValueAt(imageTable.getSelectedRow(), 1).toString().equals("true");
                    if (chosen) {
                        try {
                            String refId = imageTable.getValueAt(imageTable.getSelectedRow(), 0).toString();
                            changeRef(refId);
                            repaint();

                            doc = new PDFDocument ();
                            // Use a Paper instance to change page dimensions, some plots can be long

                            p.setSize(referenceView.getWidth(), referenceView.getHeight());
                            p.setImageableArea(0, 0, referenceView.getWidth(), referenceView.getHeight());
                            pf = new PageFormat ();
                            pf.setPaper(p);

                            page = doc.createPage(pf);
                            doc.addPage(page);

                            // Directly paint the panel to the pdf page
                            g2d = page.createGraphics();
                            referenceView.paint(g2d);
                            String OS = System.getProperty("os.name");
                            if(OS.startsWith("Window")){
                                doc.saveDocument(chosenPath + chosenDir + "\\reference_" + refId + "_alignments.pdf");
                            }
                            else{
                                doc.saveDocument(chosenPath + chosenDir + "//reference_" + refId + "_alignments.pdf");
                            }

                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, "Error saving image to file"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                tabPaneFiles.remove(tabPaneFiles.indexOfTab("Choose Images"));
                exportRefButton.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Directory name not entered", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No images chosen - choose images before exporting", "No images chosen", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refContigTableMouseClicked() {
        String chosenRef = refContigTable.getValueAt(refContigTable.getSelectedRow(), 0).toString();
        model.setSelectedRefID(chosenRef);
        changeRef(chosenRef);
    }

    private void summaryViewComponentResized() {
    }

    private void setRefContigTable() {
        DefaultTableModel refModel = TableModels.getRefModel();
        refModel.addColumn("Ref ID");
        refModel.addColumn("Length");
        refModel.addColumn("Labels");
        refModel.addColumn("Density");
        refModel.addColumn("Alignments");
        refModel.addColumn("Overlaps");

        refContigTable.setModel(refModel);
        refContigTable.setUpdateSelectionOnSort(true);
        refContigTable.getRowSorter().toggleSortOrder(0);

        refContigTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "refUp");
        refContigTable.getActionMap().put("refUp", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (refContigTable.getSelectedRow() != 0) {
                    refContigTable.setRowSelectionInterval(refContigTable.getSelectedRow() - 1, refContigTable.getSelectedRow() - 1);
                }
                // get selected contig
                String chosenRef = refContigTable.getValueAt(refContigTable.getSelectedRow(), 0).toString();
                changeRef(chosenRef);
                repaint();
            }
        });
        refContigTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "refDown");
        refContigTable.getActionMap().put("refDown", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (refContigTable.getSelectedRow() != refContigTable.getRowCount() - 1) {
                    refContigTable.setRowSelectionInterval(refContigTable.getSelectedRow() + 1, refContigTable.getSelectedRow() + 1);
                }
                // get selected contig
                String chosenRef = refContigTable.getValueAt(refContigTable.getSelectedRow(), 0).toString();
                changeRef(chosenRef);
                repaint();
            }
        });
    }

    private void setQryContigTable() {
        DefaultTableModel qryModel = TableModels.getQryModel();
        qryModel.addColumn("Query ID");
        qryModel.addColumn("Length");
        qryModel.addColumn("Orientation");
        qryModel.addColumn("Confidence");
        qryModel.addColumn("HitEnum");
        qryModel.addColumn("Num Labels");
        qryModel.addColumn("Num Matches");

        qryContigTable.setModel(qryModel);
        qryContigTable.setUpdateSelectionOnSort(true);
        qryContigTable.getRowSorter().toggleSortOrder(0);

        qryContigTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "qryUp");
        qryContigTable.getActionMap().put("qryUp", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (qryContigTable.getSelectedRow() != 0) {
                    qryContigTable.setRowSelectionInterval(qryContigTable.getSelectedRow() - 1, qryContigTable.getSelectedRow() - 1);
                }
                // get selected contig
                String chosenQry = qryContigTable.getValueAt(qryContigTable.getSelectedRow(), 0).toString();
                changeQry(chosenQry);
                repaint();
            }
        });
        qryContigTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "qryDown");
        qryContigTable.getActionMap().put("qryDown", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (qryContigTable.getSelectedRow() != qryContigTable.getRowCount() - 1) {
                    qryContigTable.setRowSelectionInterval(qryContigTable.getSelectedRow() + 1, qryContigTable.getSelectedRow() + 1);
                }
                // get selected contig
                String chosenQry = qryContigTable.getValueAt(qryContigTable.getSelectedRow(), 0).toString();
                changeQry(chosenQry);
                repaint();
            }
        });

        qryContigTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "qryDown");
        qryContigTable.getActionMap().put("qryDown", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (qryContigTable.getSelectedRow() != qryContigTable.getRowCount() - 1) {
                    qryContigTable.setRowSelectionInterval(qryContigTable.getSelectedRow() + 1, qryContigTable.getSelectedRow() + 1);
                }
                // get selected contig
                String chosenQry = qryContigTable.getValueAt(qryContigTable.getSelectedRow(), 0).toString();
//                model.setSelectedRow(chosenQry);
                changeQry(chosenQry);
                repaint();
            }
        });
    }

    private void setQryViewRefTable() {
        // Construct empty table model and set headings for protein table
        DefaultTableModel qryViewRefModel = TableModels.getQryViewRefModel();
        qryViewRefModel.addColumn("Reference ID");
        qryViewRefModel.addColumn("Orientation");
        qryViewRefModel.addColumn("Confidence");
        qryViewRefTable.setModel(qryViewRefModel);
        qryViewRefTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "qryUp");
        qryViewRefTable.getActionMap().put("qryUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (qryViewRefTable.getSelectedRow() != 0) {
                    qryViewRefTable.setRowSelectionInterval(qryViewRefTable.getSelectedRow() - 1, qryViewRefTable.getSelectedRow() - 1);
                }
                // get selected contig
                String chosenRef = qryViewRefTable.getValueAt(qryViewRefTable.getSelectedRow(), 0).toString();
                changeRef(chosenRef);
                repaint();
            }
        });
        qryViewRefTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "qryMDown");
        qryViewRefTable.getActionMap().put("qryMDown", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (qryViewRefTable.getSelectedRow() != qryViewRefTable.getRowCount() - 1) {
                    qryViewRefTable.setRowSelectionInterval(qryViewRefTable.getSelectedRow() + 1, qryViewRefTable.getSelectedRow() + 1);
                }
                // get selected contig
                String chosenRef = qryViewRefTable.getValueAt(qryViewRefTable.getSelectedRow(), 0).toString();
                changeRef(chosenRef);
                repaint();


            }
        });

    }
    private void setSVTable() {
        DefaultTableModel svModel = TableModels.getSVModel();
        svModel.addColumn("Query ID");
        svModel.addColumn("Qry Start Pos");
        svModel.addColumn("Qry End Pos");
        svModel.addColumn("Ref Start Pos");
        svModel.addColumn("Ref End Pos");
        svModel.addColumn("Type");
        svModel.addColumn("SV Size");

        svTable.setModel(svModel);
        svTable.setUpdateSelectionOnSort(true);
        svTable.getRowSorter().toggleSortOrder(0);

        svTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "refUp");
        svTable.getActionMap().put("refUp", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (svTable.getSelectedRow() != 0) {
                    svTable.setRowSelectionInterval(svTable.getSelectedRow() - 1, svTable.getSelectedRow() - 1);
                }
                // get selected contigs
                String chosenQry = svTable.getValueAt(svTable.getSelectedRow(), 0).toString();
                SVView.setChosenQry(chosenQry);
                String chosenRef = model.getSelectedRefID();
                SVView.setChosenRef(chosenRef);
                svTableMouseClicked();
                repaint();
            }
        });
        svTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "refDown");
        svTable.getActionMap().put("refDown", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (svTable.getSelectedRow() != svTable.getRowCount() - 1) {
                    svTable.setRowSelectionInterval(svTable.getSelectedRow() + 1, svTable.getSelectedRow() + 1);
                }
                // get selected contigs
                String chosenQry = svTable.getValueAt(svTable.getSelectedRow(), 0).toString();
                SVView.setChosenQry(chosenQry);
                String chosenRef = model.getSelectedRefID();
                SVView.setChosenRef(chosenRef);
                svTableMouseClicked();
                repaint();
            }
        });
    }

    private void setLabelTable() {
        DefaultTableModel labelModel = TableModels.getLabelModel();
        labelModel.addColumn("Site ID");
        labelModel.addColumn("Position");
        labelModel.addColumn("Coverage");
        labelModel.addColumn("Occurance");
        labelModel.addColumn("ChimQuality");
        labelModel.addColumn("Std Dev");
        labelTable.setModel(labelModel);
        labelTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "lUp");
        labelTable.getActionMap().put("lUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (labelTable.getSelectedRow() != 0) {
                    labelTable.setRowSelectionInterval(labelTable.getSelectedRow() - 1, labelTable.getSelectedRow() - 1);
                }
                // get selected label
                String chosenLabel = labelTable.getValueAt(labelTable.getSelectedRow(), 0).toString();
                QueryView.setChosenLabel(chosenLabel);
                repaint();
            }
        });
        labelTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "lDown");
        labelTable.getActionMap().put("lDown", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (labelTable.getSelectedRow() != labelTable.getRowCount() - 1) {
                    labelTable.setRowSelectionInterval(labelTable.getSelectedRow() + 1, labelTable.getSelectedRow() + 1);
                }
                // get selected label
                String chosenLabel = labelTable.getValueAt(labelTable.getSelectedRow(), 0).toString();
                QueryView.setChosenLabel(chosenLabel);
                repaint();
            }
        });
    }

    private void setConflictsTable() {
        DefaultTableModel conflictModel = new DefaultTableModel();
        conflictModel.addColumn("xMapId");
        conflictModel.addColumn("refQry");
        conflictModel.addColumn("Id");
        conflictModel.addColumn("leftRefBkpt");
        conflictModel.addColumn("rightRefBkpt");
        conflictModel.addColumn("alignmentOrientation");
        conflictModel.addColumn("leftBkpt-toCut");
        conflictModel.addColumn("rightBkpt_toCut");
        conflictModel.addColumn("toDiscard");
        conflictsTable.setModel(conflictModel);
        conflictsTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "cUp");
        conflictsTable.getActionMap().put("cUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (conflictsTable.getSelectedRow() != 0) {
                    conflictsTable.setRowSelectionInterval(conflictsTable.getSelectedRow() - 2, conflictsTable.getSelectedRow() - 1);
                }
                // get selected ref and qry
                String chosenRef = conflictsTable.getValueAt(conflictsTable.getSelectedRow(), 2).toString();
                String chosenQry = conflictsTable.getValueAt(conflictsTable.getSelectedRow() + 1, 2).toString();
                changeRef(chosenRef);
                changeQry(chosenQry);
                repaint();
            }
        });
        conflictsTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "cDown");
        conflictsTable.getActionMap().put("cDown", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (conflictsTable.getSelectedRow() != conflictsTable.getRowCount() - 2) {
                    conflictsTable.setRowSelectionInterval(conflictsTable.getSelectedRow() + 2, conflictsTable.getSelectedRow() + 3);
                }
                // get selected ref and qry
                String chosenRef = conflictsTable.getValueAt(conflictsTable.getSelectedRow(), 2).toString();
                String chosenQry = conflictsTable.getValueAt(conflictsTable.getSelectedRow() + 1, 2).toString();
                changeRef(chosenRef);
                changeQry(chosenQry);
                repaint();
            }
        });
    }

    private void setImageTable() {
        DefaultTableModel imageModel = TableModels.getImageModel();
        imageModel.addColumn("Reference ID");
        imageModel.addColumn("Export Image");

        imageTable.setModel(imageModel);
        imageTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "iUp");
        imageTable.getActionMap().put("iUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (imageTable.getSelectedRow() != 0) {
                    imageTable.setRowSelectionInterval(imageTable.getSelectedRow() - 1, imageTable.getSelectedRow() - 1);
                }
                // get selected contig
                String chosenRef = imageTable.getValueAt(imageTable.getSelectedRow(), 0).toString();
                changeRef(chosenRef);
                repaint();
            }
        });
        imageTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "iDown");
        imageTable.getActionMap().put("iDown", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (imageTable.getSelectedRow() != imageTable.getRowCount() - 1) {
                    imageTable.setRowSelectionInterval(imageTable.getSelectedRow() + 1, imageTable.getSelectedRow() + 1);
                }
                // get selected contig
                String chosenRef = imageTable.getValueAt(imageTable.getSelectedRow(), 0).toString();
                changeRef(chosenRef);
                repaint();
            }
        });
    }

    private ChartPanel makeLengthChartPanel(List<Double> lengths, String refId) {
        // Create dataset with sorted length array
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < lengths.size(); i++) {
            dataset.addValue(lengths.get(i) / 1000, EMPTY_STRING, Integer.toString(i));
        }
        // Create horizontal bar chart using dataset
        JFreeChart chart = ChartFactory.createBarChart(EMPTY_STRING, EMPTY_STRING, "Contig Length (kbp)",
                dataset, PlotOrientation.VERTICAL, false, false, false);
        // Alter parameters of plot
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangeGridlinePaint(Color.lightGray);
        plot.getDomainAxis().setVisible(false);
        plot.setBackgroundPaint(new Color(244, 244, 244));

        plot.setRenderer(new MyChartRenderer());
        ((BarRenderer) plot.getRenderer()).setBarPainter(new StandardBarPainter());
        ((BarRenderer) plot.getRenderer()).setShadowVisible(false);
        plot.getRenderer().setSeriesPaint(0, Color.black);

        // if chosen contig, draw line to show what length
        if (!refId.equals(EMPTY_STRING)) {
            double length = (Double) refContigTable.getValueAt(refContigTable.getSelectedRow(), 1);
            int selectedBar = lengths.indexOf(length);
            ((MyChartRenderer) plot.getRenderer()).setSelectedBar(selectedBar);
            // Marker can be added to plots to highlight value / range of values
            Marker line = new ValueMarker(length / 1000);
            line.setPaint(new Color(97, 204, 10));
            line.setLabel(" ID: " + refId + " ");
            line.setLabelFont(new Font("Tahoma", Font.BOLD, 10));
            /**
             * Set the label anchor
             * The anchore defines the position of the label anchor
             * relative to the bounds of the marker
            */
            line.setLabelAnchor(RectangleAnchor.CENTER);
            line.setLabelBackgroundColor(new Color(244, 244, 244));
            line.setLabelPaint(new Color(0, 153, 0));
            plot.addRangeMarker(line);

        }
        // Create panel for this chart
        return new ChartPanel(chart);
    }

    private ChartPanel makeDensityChartPanel(List<Double> densities, String refId) {
        // Create dataset with sorted length array
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < densities.size(); i++) {
            dataset.addValue(densities.get(i), EMPTY_STRING, Integer.toString(i));
        }
        // Create horizontal bar chart using dataset
        JFreeChart chart = ChartFactory.createLineChart(EMPTY_STRING, EMPTY_STRING, "Label Density (/100 kbp)",
                dataset, PlotOrientation.VERTICAL, false, false, false);
        // Alter parameters of plot
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangeGridlinePaint(Color.lightGray);
        plot.getDomainAxis().setVisible(false);
        plot.setBackgroundPaint(new Color(244, 244, 244));
        plot.getRenderer().setSeriesPaint(0, Color.black);

        Marker goodZone = new IntervalMarker(8, 15, new Color(204, 255, 179), new BasicStroke(2.0f), null, null, 0.5f);
        goodZone.setPaint(new Color(204, 255, 179));
        plot.addRangeMarker(goodZone);

        // if chosen contig, draw line to show what length
        if (!refId.equals(EMPTY_STRING)) {
            double density = (Double) refContigTable.getValueAt(refContigTable.getSelectedRow(), 3);
            Marker line = new ValueMarker(density);
            line.setPaint(new Color(97, 204, 10));
            line.setLabel(" Label Density: " + String.format("%.2f", density) + " ");
            line.setLabelFont(new Font("Tahoma", Font.BOLD, 10));
            line.setLabelAnchor(RectangleAnchor.CENTER);
            line.setLabelBackgroundColor(new Color(244, 244, 244));
            line.setLabelPaint(new Color(0, 153, 0));
            plot.addRangeMarker(line);
        }
        // Create panel for this chart
        return new ChartPanel(chart);
    }

    private void fillQryTable(String refId) {
        // Format table to list all queries of selected reference
        DefaultTableModel tmRefContigs = (DefaultTableModel) qryContigTable.getModel();
        // Empty table
        tmRefContigs.setRowCount(0);
        // Add rows to table
        if (!refId.isEmpty()) {
            Reference ref = model.getSelectedRef();

            for (Query qry : ref.getQueries()) {
                tmRefContigs.addRow(new Object[]{
                        Integer.parseInt(qry.getID()),
                        qry.getLength(),
                        qry.getOrientation(),
                        qry.getConfidence(),
                        qry.getHitEnum(),
                        qry.getLabels(),
                        qry.getNumMatches()
                });
            }
        }
    }

    private void fillSVTable(String refId, DetectSV detectSV) {
        // Format table to list all SV detected
        DefaultTableModel tmSV = (DefaultTableModel) svTable.getModel();
        // Empty table
        tmSV.setRowCount(0);
        // Add rows to table
        if (!refId.isEmpty()) {
            SVView.setSVList(detectSV.getSVList());
            for (SV sv : detectSV.getSVList()) {
                tmSV.addRow(new Object[]{
                        sv.getQryID(),
                        sv.getQryStartPos(),
                        sv.getQryEndPos(),
                        sv.getRefStartPos(),
                        sv.getRefEndPos(),
                        sv.getType(),
                        sv.getSVSize()

                });
            }
        }
    }

    private void fillRefTable() {
        // Format table to list all contigs with matches
        DefaultTableModel tmRefContigs = (DefaultTableModel) refContigTable.getModel();
        // Empty table
        tmRefContigs.setRowCount(0);
        // Add rows to table

        for (Reference ref : model.getReferences()) {
            tmRefContigs.addRow(new Object[]{
                    Integer.parseInt(ref.getRefID()),
                    ref.getLength(),
                    ref.getLabels(),
                    ref.getDensity(),
                    ref.getQueryIDs().size(),
                    ref.getOverlaps()
            });
        }
    }

    private void fillImageTable() {
        // Format table to list all contigs with matches
        DefaultTableModel imageModel = (DefaultTableModel) imageTable.getModel();
        // Empty table
        imageModel.setRowCount(0);
        // Add rows to table
        for (String refId : RawFileData.getReferences().keySet()) {
            imageModel.addRow(new Object[]{
                    refId,
                    false
            });
        }
    }

    private void fillQryViewRefTable(String qryId) {

        DefaultTableModel tmQryMatch = (DefaultTableModel) qryViewRefTable.getModel();
        Map<String,String[]> refData= QueryViewData.getConnection();
        // Empty table
        tmQryMatch.setRowCount(0);
        // Add rows to table

        if (!qryId.equals(EMPTY_STRING)&refData!=null) {
            for (String s:refData.keySet()){
                if (!qryId.isEmpty()) {
                    tmQryMatch.addRow(new Object[]{
                            s,
                            refData.get(s)[0],
                            Double.parseDouble(refData.get(s)[1])
                    });
                }
            }}
    }

    private void fillLabelTable(String qryId) {
        DefaultTableModel labelModel = (DefaultTableModel) labelTable.getModel();
        // Empty table
        labelModel.setRowCount(0);
        // Add rows to table
        if (!qryId.equals(EMPTY_STRING)) {
            Query qry = model.getSelectedRef().getQuery(qryId);
            Map<Integer, List<Double>> sites = qry.getSites();
            for (Integer siteId : sites.keySet()) {
                List<Double> data = sites.get(siteId);

                labelModel.addRow(new Object[]{
                        siteId,
                        data.get(0),
                        data.get(2),
                        data.get(3),
                        data.get(4),
                        data.get(1)
                });
            }
        }
    }

    private void setAllData() {

        SummaryViewData.setSummaryData(model);
        fillRefTable();

        // Displays graph of reference contigs
        String selectedRow = model.getSelectedRefID();

        referencesGraph.removeAll();
        ChartPanel refChartPanel = makeLengthChartPanel(model.getLengths(), selectedRow);
        referencesGraph.add(refChartPanel, BorderLayout.CENTER);
        referencesGraph.setVisible(true);

        // Displays graph of query contigs
        labelDensityGraph.removeAll();
        ChartPanel labDenseChartPanel = makeDensityChartPanel(model.getDensities(), selectedRow);
        labelDensityGraph.add(labDenseChartPanel, BorderLayout.CENTER);
        labelDensityGraph.setVisible(true);
    }

    private void resetData() {
        RawFileData.resetData();
        RefViewData.resetData();
        SummaryViewData.resetData();
        QueryViewData.resetData();
        QueryView.setQryViewSelect(false);
        QueryView.setReferenceViewSelect(false);

        UserRefData.resetData();
        UserQryData.resetData();
        SavedRefData.resetData();
        SavedQryData.resetData();
        SearchRegionData.resetData();

        SVViewData.resetData();

        changeQry(EMPTY_STRING);
        changeRef(EMPTY_STRING);
    }

    private void changeRef(String refId) {
        model.setSelectedRefID(refId);

        //SUMMARY VIEW TAB
        // Redraw the graph with contig marked
        referencesGraph.removeAll();
        ChartPanel chartPanel1 = makeLengthChartPanel(model.getLengths(), refId);
        referencesGraph.add(chartPanel1, BorderLayout.CENTER);
        referencesGraph.setVisible(true);

        // Redraw the graph with contig marked
        labelDensityGraph.removeAll();
        ChartPanel chartPanel2 = makeDensityChartPanel(model.getDensities(), refId);
        labelDensityGraph.add(chartPanel2, BorderLayout.CENTER);
        labelDensityGraph.setVisible(true);
        refIdSearch.setText(refId);

        //REFERENCE VIEW TAB
        if (!refId.isEmpty()) {
            RefViewData.setReferenceData(model);
            SVViewData.setSVViewData(model, detectSV);
        }

        fillQryTable(refId);
        ReferenceView.setChosenRef(refId);
        referenceView.reCenter();
        ReferenceView.setChosenQry(EMPTY_STRING);

        //QUERY VIEW TAB
        QueryView.setChosenLabel(EMPTY_STRING);
        SearchRegionData.resetData();
        QueryView.setChosenRef(refId);

        fillSVTable(refId, detectSV);
        SVView.setRefDataset(refDataset.getText());
        SVView.setChosenRef(refId);

        repaint();
    }

    private void changeQry(String qryId) {
        ReferenceView.setChosenQry(qryId);
        if(!qryId.equals("")){    QueryViewData.setQueryData(model,qryId);}
        QueryView.setChosenQry(qryId);
        QueryView.setRegionView(false);
        QueryView.setChosenLabel(EMPTY_STRING);
        fillLabelTable(qryId);
        fillQryViewRefTable(qryId);
        qryIdSearch.setText(qryId);

        SVView.setRefDataset(refDataset.getText());
        SVView.setQryDataset(qryDataset.getText());
        SVView.resetChosenSV();
        SVView.setChosenQry(qryId);
        SVView.setIndels(detectSV);
        repaint();
    }

    private void changeSV(SV sv) {
        SVView.resetChosenSV();
        SVView.setChosenSV(sv);
        repaint();
    }

//    private int closeWindow(){
//        startScreen screen = new startScreen();
//        screen.setVisible(true);
//        return JFrame.DISPOSE_ON_CLOSE;
//    }
}

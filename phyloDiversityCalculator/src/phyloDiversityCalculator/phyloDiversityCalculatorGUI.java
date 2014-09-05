package phyloDiversityCalculator;

import java.awt.EventQueue;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JRadioButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

import pal.tree.SimpleTree;
import phyloGeneticAnalysis.community.diversity.CommunityDiversityContext;
import phyloGeneticAnalysis.community.diversity.NearestNeighborDistance;
import phyloGeneticAnalysis.community.diversity.NearestNeighborPairwiseDistance;
import phyloGeneticAnalysis.community.diversity.PhyloSorDistance;
import phyloGeneticAnalysis.community.diversity.SorensenIndexDistance;
import phyloGeneticAnalysis.community.diversity.UnifracDistance;
import phyloGeneticAnalysis.community.diversity.UnifracDistance.UNIFRAC_VARIANTS;
import phyloGeneticAnalysis.io.EnvFileIO;
import phyloGeneticAnalysis.io.MatrixTxtFileIO;
import phyloGeneticAnalysis.io.newickFileIO;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

class txtFilter extends javax.swing.filechooser.FileFilter {
	@Override
	public boolean accept(File file) {
		// Allow only directories, or files with ".txt" extension
		return file.isDirectory() || file.getAbsolutePath().endsWith(".txt");
	}

	@Override
	public String getDescription() {
		// This description will be displayed in the dialog,
		// hard-coded = ugly, should be done via I18N
		return "Text documents (*.txt)";
	}
}

class treFilter extends javax.swing.filechooser.FileFilter {
	@Override
	public boolean accept(File file) {
		// Allow only directories, or files with ".txt" extension
		return file.isDirectory() || file.getAbsolutePath().endsWith(".tre");
	}

	@Override
	public String getDescription() {
		// This description will be displayed in the dialog,
		// hard-coded = ugly, should be done via I18N
		return "Tre documents (*.tre)";
	}
}

public class phyloDiversityCalculatorGUI {
	protected static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger
			.getLogger(phyloDiversityCalculatorGUI.class);

	private JFrame frame;
	private JPanel panel;
	private JTextField textFieldTreeFile;
	private ButtonGroup outputMetricsButtonGroup;
	private JTextField textFieldLocFile;
	private JLabel lblChoosePhylogeneticTree;
	private JLabel lblChooseOutputDistance;
	private JRadioButton rdbtnUnifrac;
	private JRadioButton rdbtnWeightedUnifrac;
	private JRadioButton rdbtnNormWeightedUnifrac;
	private JRadioButton rdbtnVAWUnifrac;
	private JRadioButton rdbtnAlphaUnifrac;
	private JRadioButton rdbtnPhyloSor;
	private JRadioButton rdbtnSorensenIndex;
	private JRadioButton rdbtnNearestNeighbor;
	private JRadioButton rdbtnNearestNeighborPariwise;

	private String phyloFilePath;
	private String locFilePath;
	private JTextField textField_Alpha;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					phyloDiversityCalculatorGUI window = new phyloDiversityCalculatorGUI();
					window.frame.setVisible(true);
				} catch (Exception ex) {
					ex.printStackTrace();
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					ex.printStackTrace(pw);
					LOGGER.error(sw.toString());
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public phyloDiversityCalculatorGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 526, 455);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		panel = new JPanel();
		panel.setBounds(0, 0, 527, 432);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		JPanel panel_step1 = new JPanel();
		panel_step1
				.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_step1.setBounds(16, 37, 499, 106);
		panel.add(panel_step1);
		panel_step1.setLayout(null);

		textFieldTreeFile = new JTextField();
		textFieldTreeFile.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				phyloFilePath = textFieldTreeFile.getText();
			}
		});
		textFieldTreeFile.setBounds(6, 18, 350, 30);
		panel_step1.add(textFieldTreeFile);
		textFieldTreeFile.setColumns(10);

		JButton btnOpenTreeFile = new JButton("Open PhyloTree File");
		btnOpenTreeFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setFileFilter(new treFilter());
					fileChooser.setFileHidingEnabled(false);
					int returnVal = fileChooser.showOpenDialog(frame);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fileChooser.getSelectedFile();
						phyloFilePath = file.getAbsolutePath();
						textFieldTreeFile.setText(phyloFilePath);
					} else {
						System.out.println("File access cancelled by user.");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					ex.printStackTrace(pw);
					LOGGER.error(sw.toString());
				}
			}
		});
		btnOpenTreeFile.setBounds(350, 19, 149, 29);
		panel_step1.add(btnOpenTreeFile);

		textFieldLocFile = new JTextField();
		textFieldLocFile.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				locFilePath = textFieldLocFile.getText();
			}
		});

		textFieldLocFile.setBounds(6, 60, 350, 30);
		panel_step1.add(textFieldLocFile);
		textFieldLocFile.setColumns(10);

		JButton btnOpenLocFile = new JButton("Open Location File");
		btnOpenLocFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setFileFilter(new txtFilter());
					fileChooser.setFileHidingEnabled(false);
					int returnVal = fileChooser.showOpenDialog(frame);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fileChooser.getSelectedFile();
						locFilePath = file.getAbsolutePath();
						textFieldLocFile.setText(locFilePath);
					} else {
						System.out.println("File access cancelled by user.");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					ex.printStackTrace(pw);
					LOGGER.error(sw.toString());
				}
			}
		});
		btnOpenLocFile.setBounds(350, 61, 149, 29);
		panel_step1.add(btnOpenLocFile);

		JPanel panel_Step2 = new JPanel();
		panel_Step2
				.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_Step2.setBounds(16, 181, 499, 200);
		panel.add(panel_Step2);
		panel_Step2.setLayout(null);

		outputMetricsButtonGroup = new ButtonGroup();

		rdbtnUnifrac = new JRadioButton("Unweighted Unifrac dU");
		rdbtnUnifrac.setSelected(true);
		rdbtnUnifrac.setBounds(6, 11, 179, 28);
		outputMetricsButtonGroup.add(rdbtnUnifrac);
		panel_Step2.add(rdbtnUnifrac);

		rdbtnWeightedUnifrac = new JRadioButton("Weighted Unifrac dW");
		rdbtnWeightedUnifrac.setSelected(true);
		rdbtnWeightedUnifrac.setBounds(242, 11, 172, 28);
		outputMetricsButtonGroup.add(rdbtnWeightedUnifrac);
		panel_Step2.add(rdbtnWeightedUnifrac);

		rdbtnNormWeightedUnifrac = new JRadioButton(
				"Normalized Weighted Unifrac d0");
		rdbtnNormWeightedUnifrac.setBounds(242, 49, 235, 28);
		panel_Step2.add(rdbtnNormWeightedUnifrac);
		rdbtnNormWeightedUnifrac.setSelected(true);
		outputMetricsButtonGroup.add(rdbtnNormWeightedUnifrac);

		rdbtnVAWUnifrac = new JRadioButton("VAW-UniFrac distance dVAW");
		rdbtnVAWUnifrac.setBounds(6, 49, 235, 28);
		panel_Step2.add(rdbtnVAWUnifrac);
		rdbtnVAWUnifrac.setSelected(true);
		outputMetricsButtonGroup.add(rdbtnVAWUnifrac);

		rdbtnAlphaUnifrac = new JRadioButton("Generalized UniFrac  dAlpha");
		rdbtnAlphaUnifrac.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (rdbtnAlphaUnifrac.isSelected())
					textField_Alpha.setEnabled(true);
			}
		});

		rdbtnAlphaUnifrac.setBounds(6, 89, 235, 28);
		panel_Step2.add(rdbtnAlphaUnifrac);
		rdbtnAlphaUnifrac.setSelected(true);
		outputMetricsButtonGroup.add(rdbtnAlphaUnifrac);

		rdbtnPhyloSor = new JRadioButton("PhyloSor");
		rdbtnPhyloSor.setBounds(242, 128, 141, 23);
		outputMetricsButtonGroup.add(rdbtnPhyloSor);
		panel_Step2.add(rdbtnPhyloSor);

		rdbtnSorensenIndex = new JRadioButton("Sorensen Index");
		rdbtnSorensenIndex.setBounds(6, 128, 141, 23);
		outputMetricsButtonGroup.add(rdbtnSorensenIndex);
		panel_Step2.add(rdbtnSorensenIndex);

		rdbtnNearestNeighbor = new JRadioButton("Nearest Neighbor");
		rdbtnNearestNeighbor.setBounds(6, 162, 158, 23);
		outputMetricsButtonGroup.add(rdbtnNearestNeighbor);
		panel_Step2.add(rdbtnNearestNeighbor);

		rdbtnNearestNeighborPariwise = new JRadioButton(
				"Nearest Neighbor Pairwise");
		rdbtnNearestNeighborPariwise.setBounds(242, 162, 197, 23);
		outputMetricsButtonGroup.add(rdbtnNearestNeighborPariwise);
		panel_Step2.add(rdbtnNearestNeighborPariwise);

		NumberFormat digitFormat = NumberFormat.getPercentInstance();
		digitFormat = NumberFormat.getNumberInstance();
		digitFormat.setMinimumFractionDigits(3);
		textField_Alpha = new JFormattedTextField(digitFormat);
		textField_Alpha.setText("0.5");
		textField_Alpha.setHorizontalAlignment(SwingConstants.RIGHT);
		textField_Alpha.setBounds(252, 89, 61, 28);
		textField_Alpha.setEnabled(false);
		panel_Step2.add(textField_Alpha);
		textField_Alpha.setColumns(10);

		lblChoosePhylogeneticTree = new JLabel(
				"Choose Phylogenetic Tree File & Location File:");
		lblChoosePhylogeneticTree.setBounds(6, 16, 344, 16);
		panel.add(lblChoosePhylogeneticTree);

		lblChooseOutputDistance = new JLabel("Choose Output Distance Metrics:");
		lblChooseOutputDistance.setBounds(6, 159, 234, 16);
		panel.add(lblChooseOutputDistance);

		JButton btnSaveDistanceMetrics = new JButton("Save Distance Metrics");
		btnSaveDistanceMetrics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {

					if (phyloFilePath == null || phyloFilePath.isEmpty()) {
						JOptionPane.showMessageDialog(null,
								"Please choose a Phylogenetic Tree File!",
								"Error", JOptionPane.ERROR_MESSAGE);
					} else if (locFilePath == null || locFilePath.isEmpty()) {
						JOptionPane.showMessageDialog(null,
								"Please choose a Location File!", "Error",
								JOptionPane.ERROR_MESSAGE);
					} else {
						JFileChooser outputFileChooser = new JFileChooser();
						int returnVal = outputFileChooser.showSaveDialog(frame);
						if (returnVal == outputFileChooser.APPROVE_OPTION) {
							File file = outputFileChooser.getSelectedFile();
							String outfilename = file.getAbsolutePath();
							// Input Phylogenetic Tree
							SimpleTree m_palTree;
							newickFileIO nfio = new newickFileIO();
							m_palTree = (SimpleTree) nfio
									.inputNewickFile(phyloFilePath);

							// Input Enviroment File
							EnvFileIO envio = new EnvFileIO();
							HashMap resultMap = envio.readEnvToMap(locFilePath);
							ArrayList envList = (ArrayList) resultMap
									.get("EnvList");
							HashMap envNameToSpeciesSetMap = (HashMap) resultMap
									.get("EnvToSpeciesMap");
							HashMap envNameToSpeciesAbundanceMap = (HashMap) resultMap
									.get("EnvNameToSpeciesAbundanceMap");

							// Calculate the phylogenetic distance matrix
							CommunityDiversityContext cdc;
							if (rdbtnUnifrac.isSelected()) {
								UnifracDistance distanceStrategy = new UnifracDistance(
										m_palTree);
								cdc = new CommunityDiversityContext(
										distanceStrategy);
							} else if (rdbtnWeightedUnifrac.isSelected()) {
								UnifracDistance distanceStrategy = new UnifracDistance(
										m_palTree);
								distanceStrategy.setOption(UNIFRAC_VARIANTS.DW);
								cdc = new CommunityDiversityContext(
										distanceStrategy);
							} else if (rdbtnNormWeightedUnifrac.isSelected()) {
								UnifracDistance distanceStrategy = new UnifracDistance(
										m_palTree);
								distanceStrategy.setOption(UNIFRAC_VARIANTS.D0);
								cdc = new CommunityDiversityContext(
										distanceStrategy);
							} else if (rdbtnVAWUnifrac.isSelected()) {
								UnifracDistance distanceStrategy = new UnifracDistance(
										m_palTree);
								distanceStrategy
										.setOption(UNIFRAC_VARIANTS.DVAW);
								cdc = new CommunityDiversityContext(
										distanceStrategy);
							} else if (rdbtnAlphaUnifrac.isSelected()) {
								UnifracDistance distanceStrategy = new UnifracDistance(
										m_palTree);
								distanceStrategy
										.setOption(UNIFRAC_VARIANTS.DALPHA);
								double alpha = Double.valueOf(textField_Alpha
										.getText());
								distanceStrategy.setAlpha(alpha);
								cdc = new CommunityDiversityContext(
										distanceStrategy);
							} else if (rdbtnPhyloSor.isSelected()) {
								PhyloSorDistance distanceStrategy = new PhyloSorDistance(
										m_palTree);
								cdc = new CommunityDiversityContext(
										distanceStrategy);
							} else if (rdbtnSorensenIndex.isSelected()) {
								SorensenIndexDistance distanceStrategy = new SorensenIndexDistance(
										m_palTree);
								cdc = new CommunityDiversityContext(
										distanceStrategy);
							} else if (rdbtnNearestNeighbor.isSelected()) {
								NearestNeighborDistance distanceStrategy = new NearestNeighborDistance(
										m_palTree);
								cdc = new CommunityDiversityContext(
										distanceStrategy);
							} else {
								NearestNeighborPairwiseDistance distanceStrategy = new NearestNeighborPairwiseDistance(
										m_palTree);
								cdc = new CommunityDiversityContext(
										distanceStrategy);
							}

							int envCount = envList.size();
							double[][] distanceMatrix = new double[envCount][envCount];
							for (int i = 0; i < envCount; i++) {
								for (int j = i + 1; j < envCount; j++) {
									Set<String> A = (Set<String>) envNameToSpeciesSetMap
											.get(envList.get(i));
									Set<String> B = (Set<String>) envNameToSpeciesSetMap
											.get(envList.get(j));

									HashMap speciesToAbundanceMap = (HashMap) envNameToSpeciesAbundanceMap
											.get(envList.get(j));
									

									double distance = cdc.communityDiversity(A,
											B, speciesToAbundanceMap);
									distanceMatrix[i][j] = distance;
								}
							}

							// output distance matrix
							MatrixTxtFileIO matrixfile = new MatrixTxtFileIO();
							matrixfile.saveMatrixFile(outfilename, envList,
									distanceMatrix);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					ex.printStackTrace(pw);
					LOGGER.error(sw.toString());
				}
			}
		});
		btnSaveDistanceMetrics.setBounds(183, 397, 167, 29);
		panel.add(btnSaveDistanceMetrics);

	}
}

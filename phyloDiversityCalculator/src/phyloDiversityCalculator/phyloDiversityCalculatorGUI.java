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
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.Timer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

import pal.tree.SimpleTree;
import phyloDiversityCalculator.phyloDiversityCalculatorProcessSubject.DISTANCE_STRATEGY;
import phyloGeneticAnalysis.community.diversity.CommunityDiversityContext;
import phyloGeneticAnalysis.community.diversity.NearestNeighborDistance;
import phyloGeneticAnalysis.community.diversity.NearestNeighborPairwiseDistance;
import phyloGeneticAnalysis.community.diversity.PhyloSorDistance;
import phyloGeneticAnalysis.community.diversity.SorensenIndexDistance;
import phyloGeneticAnalysis.community.diversity.UnifracDistance;
import phyloGeneticAnalysis.community.diversity.UnifracDistance.UNIFRAC_VARIANTS;
import phyloGeneticAnalysis.io.EnvFileIO;
import phyloGeneticAnalysis.io.MatrixTxtFileIO;
import phyloGeneticAnalysis.io.NewickFileIO;
import processListen.ProcessListener;

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

public class phyloDiversityCalculatorGUI extends javax.swing.JFrame{
	protected static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger
			.getLogger(phyloDiversityCalculatorGUI.class);

	private JFrame frame;
	private JPanel panel;
	private JTextField textFieldTreeFile;
	private ButtonGroup outputMetricsButtonGroup;
	private JTextField textFieldLocFile;
	private JLabel lblChoosePhylogeneticTree;
	private JLabel lblChooseOutputDistance;
	private JLabel lblChooseOuputFile;
	private JLabel lblResult;
	private JRadioButton rdbtnUnifrac;
	private JRadioButton rdbtnWeightedUnifrac;
	private JRadioButton rdbtnNormWeightedUnifrac;
	private JRadioButton rdbtnVAWUnifrac;
	private JRadioButton rdbtnAlphaUnifrac;
	private JRadioButton rdbtnPhyloSor;
	private JRadioButton rdbtnSorensenIndex;
	private JRadioButton rdbtnNearestNeighbor;
	private JRadioButton rdbtnNearestNeighborPariwise;
	private JTextField textField_Alpha;
	private JPanel panelProgress;
    private JProgressBar stepProgressBar;
    private JTextArea processOutputTextArea;

	private String phyloFilePath;
	private String locFilePath;
    private ProcessListener processListener;
	private String lastS = "";
	private boolean finished = false;


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
		frame.setBounds(100, 100, 526, 520);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		panel = new JPanel();
		panel.setBounds(0, 0, 526, 500);
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
				"Step 1: Choose Phylogenetic Tree File & Location File:");
		lblChoosePhylogeneticTree.setBounds(6, 16, 350, 16);
		panel.add(lblChoosePhylogeneticTree);

		lblChooseOutputDistance = new JLabel("Step 2: Choose Output Distance Metrics:");
		lblChooseOutputDistance.setBounds(6, 159, 350, 16);
		panel.add(lblChooseOutputDistance);
		
		lblChooseOuputFile = new JLabel("Step 3: Save to Matrix File:");
		lblChooseOuputFile.setBounds(6, 397, 350, 16);
		panel.add(lblChooseOuputFile);
		
	/*	lblResult = new JLabel("Progress:");
		lblResult.setBounds(6, 473, 350, 16);
		panel.add(lblResult);*/

		JPanel panel_Step3 = new JPanel();
		panel_Step3
				.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_Step3.setBounds(16, 419, 499, 40);
		panel.add(panel_Step3);
		panel_Step3.setLayout(null);
		panel_Step3.setOpaque(false);
		
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
							int distanceStrategy;
						
							if (rdbtnUnifrac.isSelected()) {
								distanceStrategy=DISTANCE_STRATEGY.UNIFRAC_DU;
							} else if (rdbtnWeightedUnifrac.isSelected()) {
								distanceStrategy=DISTANCE_STRATEGY.UNIFRAC_DW;
							} else if (rdbtnNormWeightedUnifrac.isSelected()) {
								distanceStrategy=DISTANCE_STRATEGY.UNIFRAC_D0;
							} else if (rdbtnVAWUnifrac.isSelected()) {
								distanceStrategy=DISTANCE_STRATEGY.UNIFRAC_DVAW;
							} else if (rdbtnAlphaUnifrac.isSelected()) {
								distanceStrategy=DISTANCE_STRATEGY.UNIFRAC_DALPHA;
							} else if (rdbtnPhyloSor.isSelected()) {
								distanceStrategy=DISTANCE_STRATEGY.PhyloSor;
							} else if (rdbtnSorensenIndex.isSelected()) {
								distanceStrategy=DISTANCE_STRATEGY.SorensenIndex;
							} else if (rdbtnNearestNeighbor.isSelected()) {
								distanceStrategy=DISTANCE_STRATEGY.NearestNeighbor;
							} else {
								distanceStrategy=DISTANCE_STRATEGY.NearestNeighborPairwise;
							}
							
							phyloDiversityCalculatorProcessSubject ps;
							if (rdbtnAlphaUnifrac.isSelected()) {
								double alpha = Double.valueOf(textField_Alpha
										.getText());
								ps = new phyloDiversityCalculatorProcessSubject(
										phyloFilePath, locFilePath,
										outfilename, distanceStrategy, alpha);
							} else
								ps = new phyloDiversityCalculatorProcessSubject(
										phyloFilePath, locFilePath,
										outfilename, distanceStrategy);
							processListener = new ProcessListener(ps);
							ps.attach(processListener);
							ps.execute();
							timer.start();

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
		btnSaveDistanceMetrics.setBounds(180, 425, 167, 29);
		panel.add(btnSaveDistanceMetrics);
		
	/*	panelProgress = new JPanel();
		panelProgress
		.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelProgress.setBounds(16, 495, 499, 40);
		panel.add(panelProgress);*/

		stepProgressBar = new JProgressBar(0,100);
        stepProgressBar.setBounds(26, 471, 469, 20);
        stepProgressBar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        panel.add(stepProgressBar);

	}
	
	Timer timer = new Timer(1, new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            stepProgressBar.setValue(processListener.getCurrentPercentage());
            String s = processListener.getCurrentMessage();
            String newline = "\n";
            if (s != null&& !s.equals(lastS)) {
                lastS = s;
        //        processOutputTextArea.append(s + newline);
        //        processOutputTextArea.setCaretPosition(
        //                processOutputTextArea.getDocument().getLength());
            }
            if (processListener.getCurrentPercentage() == 100) {
                //    Toolkit.getDefaultToolkit().beep();
                finished = true;
                timer.stop();
                setCursor(null); //turn off the wait cursor
                stepProgressBar.setValue(stepProgressBar.getMinimum());
            }
        }
    });
}

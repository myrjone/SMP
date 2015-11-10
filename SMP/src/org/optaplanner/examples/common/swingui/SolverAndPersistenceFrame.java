/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.common.swingui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.io.FilenameUtils;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.examples.common.business.SolutionBusiness;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.optaplanner.examples.tarostering.domain.Ta;
import org.optaplanner.examples.tarostering.domain.TaRoster;
import org.optaplanner.examples.tarostering.domain.contract.MinMaxContractLine;
import org.optaplanner.examples.tarostering.persistence.TaRosteringCourseImporter;
import org.optaplanner.examples.tarostering.persistence.TaRosteringPdfExporter;
import org.optaplanner.examples.tarostering.persistence.TaRosteringTaImporter;
import org.optaplanner.examples.tarostering.swingui.ConstraintFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolverAndPersistenceFrame extends JFrame {


    public static final ImageIcon OPTA_PLANNER_ICON = new ImageIcon(
            SolverAndPersistenceFrame.class.getResource("optaPlannerIcon.png"));
    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final SolutionBusiness solutionBusiness;

    private final SolutionPanel solutionPanel;
    private final ConstraintMatchesDialog constraintMatchesDialog;

    private JPanel quickOpenUnsolvedPanel;
    private List<Action> quickOpenUnsolvedActionList;
    private JPanel quickOpenSolvedPanel;
    private List<Action> quickOpenSolvedActionList;
    private Action openAction;
    private Action saveAction;
    private Action importAction;
    private Action exportAction;
    private JCheckBox refreshScreenDuringSolvingCheckBox;
    private Action solveAction;
    private JButton solveButton;
    private Action terminateSolvingEarlyAction;
    private JButton terminateSolvingEarlyButton;
    private JPanel middlePanel;
    private JProgressBar progressBar;
    private JTextField scoreField;
    private ShowConstraintMatchesDialogAction showConstraintMatchesDialogAction;
    private Action constraintAction;
    private JButton constraintButton;
    private Action emailAction;
    private JToggleButton homeButton;

    public SolverAndPersistenceFrame(SolutionBusiness solutionBusiness, SolutionPanel solutionPanel) {
        super(solutionBusiness.getAppName());
        this.solutionBusiness = solutionBusiness;
        this.solutionPanel = solutionPanel;
        setIconImage(OPTA_PLANNER_ICON.getImage());
        solutionPanel.setSolutionBusiness(solutionBusiness);
        solutionPanel.setSolverAndPersistenceFrame(this);
        registerListeners();
        constraintMatchesDialog = new ConstraintMatchesDialog(this, solutionBusiness);
    }

    private void registerListeners() {
        solutionBusiness.registerForBestSolutionChanges(this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // This async, so it doesn't stop the solving immediately
                solutionBusiness.terminateSolvingEarly();
            }
        });
    }

    public void bestSolutionChanged() {
        Solution solution = solutionBusiness.getSolution();
        if (refreshScreenDuringSolvingCheckBox.isSelected()) {
            solutionPanel.updatePanel(solution);
            validate(); // TODO remove me?
        }
        refreshScoreField(solution);
    }

    public void init(Component centerForComponent) {
        setContentPane(createContentPane());
        pack();
        setLocationRelativeTo(centerForComponent);
    }

    private JComponent createContentPane() {
        JComponent quickOpenPanel = createQuickOpenPanel();
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createToolBar(), BorderLayout.NORTH);
        mainPanel.add(createMiddlePanel(), BorderLayout.CENTER);
        mainPanel.add(createScorePanel(), BorderLayout.SOUTH);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, quickOpenPanel, mainPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.2);
        return splitPane;
    }

    private JComponent createQuickOpenPanel() {
        JPanel quickOpenPanel = new JPanel(new BorderLayout());
        JLabel quickOpenLabel = new JLabel("Quick open");
        quickOpenLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        quickOpenPanel.add(quickOpenLabel, BorderLayout.NORTH);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                createQuickOpenUnsolvedPanel(), createQuickOpenSolvedPanel());
        splitPane.setResizeWeight(0.8);
        splitPane.setBorder(null);
        quickOpenPanel.add(splitPane, BorderLayout.CENTER);
        return quickOpenPanel;
    }

    private JComponent createQuickOpenUnsolvedPanel() {
        quickOpenUnsolvedPanel = new JPanel();
        quickOpenUnsolvedActionList = new ArrayList<>();
        List<File> unsolvedFileList = solutionBusiness.getUnsolvedFileList();
        return createQuickOpenPanel(quickOpenUnsolvedPanel, "Unsolved dataset", quickOpenUnsolvedActionList,
                unsolvedFileList);
    }

    private JComponent createQuickOpenSolvedPanel() {
        quickOpenSolvedPanel = new JPanel();
        quickOpenSolvedActionList = new ArrayList<>();
        List<File> solvedFileList = solutionBusiness.getSolvedFileList();
        return createQuickOpenPanel(quickOpenSolvedPanel, "Solved dataset", quickOpenSolvedActionList,
                solvedFileList);
    }

    private JComponent createQuickOpenPanel(JPanel panel, String title, List<Action> quickOpenActionList, List<File> fileList) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        refreshQuickOpenPanel(panel, quickOpenActionList, fileList);
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(25);
        scrollPane.setMinimumSize(new Dimension(100, 80));
        // Size fits into screen resolution 1024*768
        scrollPane.setPreferredSize(new Dimension(180, 200));
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(scrollPane, BorderLayout.CENTER);
        titlePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createTitledBorder(title)));
        return titlePanel;
    }

    private void refreshQuickOpenPanel(JPanel panel, List<Action> quickOpenActionList, List<File> fileList) {
        panel.removeAll();
        quickOpenActionList.clear();
        if (fileList.isEmpty()) {
            JLabel noneLabel = new JLabel("None");
            noneLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            panel.add(noneLabel);
        } else {
            for (File file : fileList) {
                Action quickOpenAction = new QuickOpenAction(file);
                quickOpenActionList.add(quickOpenAction);
                JButton quickOpenButton = new JButton(quickOpenAction);
                quickOpenButton.setHorizontalAlignment(SwingConstants.LEFT);
                quickOpenButton.setMargin(new Insets(0, 0, 0, 0));
                panel.add(quickOpenButton);
            }
        }
    }


    private JComponent createToolBar() {
        JToolBar toolBar = new JToolBar("File operations");
        toolBar.setFloatable(false);

        importAction = new ImportAction();
        importAction.setEnabled(solutionBusiness.hasImporter());
        toolBar.add(new JButton(importAction));
        openAction = new OpenAction();
        openAction.setEnabled(true);
        toolBar.add(new JButton(openAction));
        saveAction = new SaveAction();
        saveAction.setEnabled(false);
        toolBar.add(new JButton(saveAction));
        exportAction = new ExportAction();
        exportAction.setEnabled(false);
        toolBar.add(new JButton(exportAction));
        toolBar.addSeparator();
        emailAction = new EmailAction();
        emailAction.setEnabled(false);
        toolBar.add(new JButton(emailAction));
        toolBar.addSeparator();

        progressBar = new JProgressBar(0, 100);
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        toolBar.add(progressBar);
        toolBar.addSeparator();

        solveAction = new SolveAction();
        solveAction.setEnabled(false);
        solveButton = new JButton(solveAction);
        terminateSolvingEarlyAction = new TerminateSolvingEarlyAction();
        terminateSolvingEarlyAction.setEnabled(false);
        terminateSolvingEarlyButton = new JButton(terminateSolvingEarlyAction);
        terminateSolvingEarlyButton.setVisible(false);
        toolBar.add(solveButton, "solveAction");
        toolBar.add(terminateSolvingEarlyButton, "terminateSolvingEarlyAction");
        solveButton.setMinimumSize(terminateSolvingEarlyButton.getMinimumSize());
        solveButton.setPreferredSize(terminateSolvingEarlyButton.getPreferredSize());
        return toolBar;
    }

    private JPanel createMiddlePanel() {
        middlePanel = new JPanel(new CardLayout());
        JPanel usageExplanationPanel = new JPanel(new BorderLayout(5, 5));
        ImageIcon usageExplanationIcon = new ImageIcon(getClass().getResource(solutionPanel.getUsageExplanationPath()));
        JLabel usageExplanationLabel = new JLabel(usageExplanationIcon);
        // Allow splitPane divider to be moved to the right
        usageExplanationLabel.setMinimumSize(new Dimension(100, 100));
        usageExplanationPanel.add(usageExplanationLabel, BorderLayout.CENTER);
        JPanel descriptionPanel = new JPanel(new BorderLayout(2, 2));
        descriptionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        descriptionPanel.add(new JLabel("Description"), BorderLayout.NORTH);
        JTextArea descriptionTextArea = new JTextArea(8, 70);
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setText(solutionBusiness.getAppDescription());
        descriptionPanel.add(new JScrollPane(descriptionTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
        usageExplanationPanel.add(descriptionPanel, BorderLayout.SOUTH);
        middlePanel.add(usageExplanationPanel, "usageExplanationPanel");
        JComponent wrappedSolutionPanel;
        if (solutionPanel.isWrapInScrollPane()) {
            wrappedSolutionPanel = new JScrollPane(solutionPanel);
        } else {
            wrappedSolutionPanel = solutionPanel;
        }
        middlePanel.add(wrappedSolutionPanel, "solutionPanel");
        return middlePanel;
    }

    private JPanel createScorePanel() {
        JPanel scorePanel = new JPanel();
        GroupLayout layout = new GroupLayout(scorePanel);
        scorePanel.setLayout(layout);
        scorePanel.setBorder(BorderFactory.createEtchedBorder());
        showConstraintMatchesDialogAction = new ShowConstraintMatchesDialogAction();
        showConstraintMatchesDialogAction.setEnabled(false);
        JButton constraintMatchesButton = new JButton(showConstraintMatchesDialogAction);
        constraintAction = new ConstraintAction();
        constraintButton = new JButton(constraintAction);
        constraintButton.setText("Edit Constraints");
        constraintButton.setEnabled(false);
        homeButton = new JToggleButton(new HomeAction());
        homeButton.setSelected(true);
        scoreField = new JTextField("Score:");
        scoreField.setEditable(false);
        scoreField.setForeground(Color.BLACK);
        scoreField.setBorder(BorderFactory.createLoweredBevelBorder());
        refreshScreenDuringSolvingCheckBox = new JCheckBox("Refresh screen during solving",
                solutionPanel.isRefreshScreenDuringSolving());
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                    .addComponent(constraintMatchesButton)
                    .addComponent(constraintButton)
                    .addComponent(scoreField)
                    .addComponent(refreshScreenDuringSolvingCheckBox)
                    .addComponent(homeButton));
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(constraintMatchesButton)
                        .addComponent(constraintButton)
                        .addComponent(scoreField)
                        .addComponent(refreshScreenDuringSolvingCheckBox)
                        .addComponent(homeButton));
        return scorePanel;
    }

    private void setSolutionLoaded() {
        setTitle(solutionBusiness.getAppName() + " - " + solutionBusiness.getSolutionFileName());
        ((CardLayout) middlePanel.getLayout()).show(middlePanel, "solutionPanel");
        solveAction.setEnabled(true);
        saveAction.setEnabled(true);
        exportAction.setEnabled(solutionBusiness.hasExporter());
        showConstraintMatchesDialogAction.setEnabled(true);
        constraintButton.setEnabled(true);
        homeButton.setSelected(false);
        resetScreen();
    }

    private void setSolvingState(boolean solving) {
        for (Action action : quickOpenUnsolvedActionList) {
            action.setEnabled(!solving);
        }
        for (Action action : quickOpenSolvedActionList) {
            action.setEnabled(!solving);
        }
        importAction.setEnabled(!solving && solutionBusiness.hasImporter());
        openAction.setEnabled(!solving);
        saveAction.setEnabled(!solving);
        exportAction.setEnabled(!solving && solutionBusiness.hasExporter());
        solveAction.setEnabled(!solving);
        solveButton.setVisible(!solving);
        emailAction.setEnabled(!solving);
        terminateSolvingEarlyAction.setEnabled(solving);
        terminateSolvingEarlyButton.setVisible(solving);
        solutionPanel.setEnabled(!solving);
        progressBar.setIndeterminate(solving);
        progressBar.setStringPainted(solving);
        progressBar.setString(solving ? "Solving..." : null);
        showConstraintMatchesDialogAction.setEnabled(!solving);
        solutionPanel.setSolvingState(solving);
    }

    public void resetScreen() {
        Solution solution = solutionBusiness.getSolution();
        solutionPanel.resetPanel(solution);
        validate();
        refreshScoreField(solution);
    }

    public void refreshScoreField(Solution solution) {
        scoreField.setForeground(determineScoreFieldForeground(solution.getScore()));
        scoreField.setText("Latest best score: " + solution.getScore());
    }

    private Color determineScoreFieldForeground(Score<?> score) {
        if (!(score instanceof FeasibilityScore)) {
            return Color.BLACK;
        } else {
            FeasibilityScore<?> feasibilityScore = (FeasibilityScore<?>) score;
            return feasibilityScore.isFeasible() ? TangoColorFactory.CHAMELEON_3 : TangoColorFactory.SCARLET_3;
        }
    }

    private class QuickOpenAction extends AbstractAction {
        private final File file;

        QuickOpenAction(File file) {
            super(file.getName().replaceAll("\\.xml$", ""));
            this.file = file;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                solutionBusiness.openSolution(file);
                setSolutionLoaded();
                TaRoster ta = (TaRoster)solutionBusiness.getSolution();
                emailAction.setEnabled(true);
            } finally {
                setCursor(Cursor.getDefaultCursor());
            }
        }

    }

    private class SolveAction extends AbstractAction {


        SolveAction() {
            super("Solve", new ImageIcon(SolverAndPersistenceFrame.class.getResource("solveAction.png")));
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            setSolvingState(true);
            Solution planningProblem = solutionBusiness.getSolution();
            new SolveWorker(planningProblem).execute();
        }


    }

    protected class SolveWorker extends SwingWorker<Solution, Void> {
        protected final Solution planningProblem;

        SolveWorker(Solution planningProblem) {
            this.planningProblem = planningProblem;
        }
        @Override
        protected Solution doInBackground() throws Exception {
            return solutionBusiness.solve(planningProblem);
        }

        @Override
        protected void done() {
            try {
                Solution bestSolution = get();
                solutionBusiness.setSolution(bestSolution);
            } catch (InterruptedException e) {
                throw new IllegalStateException("Solving interrupted.", e);
            } catch (ExecutionException e) {
                throw new IllegalStateException("Solving failed.", e.getCause());
            } finally {
                setSolvingState(false);
                resetScreen();
            }
        }

    }

    private class TerminateSolvingEarlyAction extends AbstractAction {


        TerminateSolvingEarlyAction() {
            super("Terminate solving early",
                    new ImageIcon(SolverAndPersistenceFrame.class.getResource("terminateSolvingEarlyAction.png")));
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            terminateSolvingEarlyAction.setEnabled(false);
            progressBar.setString("Terminating...");
            // This async, so it doesn't stop the solving immediately
            solutionBusiness.terminateSolvingEarly();
        }

    }

    private class OpenAction extends AbstractAction {
        private static final String NAME = "Open...";
        private JFileChooser fileChooser;

        OpenAction() {
            super(NAME, new ImageIcon(SolverAndPersistenceFrame.class.getResource("openAction.png")));
            fileChooser = new JFileChooser(solutionBusiness.getSolvedDataDir());
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory() || file.getName().endsWith(".xml");
                }

                @Override
                public String getDescription() {
                    return "Solution XStream XML files";
                }
            });
            fileChooser.setDialogTitle(NAME);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            int approved = fileChooser.showOpenDialog(SolverAndPersistenceFrame.this);
            if (approved == JFileChooser.APPROVE_OPTION) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    solutionBusiness.openSolution(fileChooser.getSelectedFile());
                    setSolutionLoaded();
                    emailAction.setEnabled(true);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }

    }

    private class SaveAction extends AbstractAction {
        private static final String NAME = "Save as...";
        private JFileChooser fileChooser;

        SaveAction() {
            super(NAME, new ImageIcon(SolverAndPersistenceFrame.class.getResource("saveAction.png")));
            fileChooser = new JFileChooser(solutionBusiness.getSolvedDataDir());
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory() || file.getName().endsWith(".xml");
                }

                @Override
                public String getDescription() {
                    return "Solution XStream XML files";
                }
            });
            fileChooser.setDialogTitle(NAME);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            fileChooser.setSelectedFile(new File(solutionBusiness.getSolvedDataDir(),
                    FilenameUtils.getBaseName(solutionBusiness.getSolutionFileName()) + ".xml"));
            int approved = fileChooser.showSaveDialog(SolverAndPersistenceFrame.this);
            if (approved == JFileChooser.APPROVE_OPTION) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    solutionBusiness.saveSolution(fileChooser.getSelectedFile());
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
                refreshQuickOpenPanel(quickOpenUnsolvedPanel, quickOpenUnsolvedActionList,
                        solutionBusiness.getUnsolvedFileList());
                refreshQuickOpenPanel(quickOpenSolvedPanel, quickOpenSolvedActionList,
                        solutionBusiness.getSolvedFileList());
                SolverAndPersistenceFrame.this.validate();
            }
        }

    }

    private class ImportAction extends AbstractAction {
        private static final String NAME = "Import...";
        private JFileChooser courseFileChooser;
        private JFileChooser taFileChooser;
        private Solution solution;


        ImportAction() {
            super(NAME, new ImageIcon(SolverAndPersistenceFrame.class.getResource("importAction.png")));
            if (!solutionBusiness.hasImporter()) {
                courseFileChooser = null;
                return;
            }            courseFileChooser = new JFileChooser(solutionBusiness.getImportDataDir());
            FileFilter courseFilter;
            if (solutionBusiness.isImportFileDirectory()) {
                courseFilter = new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return "Import directory";
                    }
                };
                courseFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            } else {
                courseFilter = new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory() || solutionBusiness.acceptImportFile(file);
                    }

                    @Override
                    public String getDescription() {
                        return "Import files (*." + solutionBusiness.getImportFileSuffix() + ")";
                    }
                };
            }
            courseFileChooser.setFileFilter(courseFilter);
            courseFileChooser.setDialogTitle("Import Course List");

            taFileChooser = new JFileChooser(solutionBusiness.getImportDataDir());
            FileFilter taFilter;
            if (solutionBusiness.isImportFileDirectory()) {
                taFilter = new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return "Import directory";
                    }
                };
            } else {
                taFilter = new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory() || solutionBusiness.acceptImportFile(file);
                    }

                    @Override
                    public String getDescription() {
                        return "Import files (*." + solutionBusiness.getImportFileSuffix() + ")";
                    }
                };
            }
            taFileChooser.setFileFilter(taFilter);
            taFileChooser.setDialogTitle("Import TA Directory");
            taFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            int approved = courseFileChooser.showOpenDialog(SolverAndPersistenceFrame.this);
            if (approved == JFileChooser.APPROVE_OPTION) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    AbstractTxtSolutionImporter courseImporter = new TaRosteringCourseImporter();
                    File courseFile = courseFileChooser.getSelectedFile();
                    solution = courseImporter.readSolution(courseFile);
                    int approved2 = taFileChooser.showOpenDialog(SolverAndPersistenceFrame.this);
                    if (approved2 == JFileChooser.APPROVE_OPTION) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        AbstractTxtSolutionImporter taImporter = new TaRosteringTaImporter((TaRoster) solution);
                        File directory = taFileChooser.getSelectedFile();
                        String suffix = solutionBusiness.getImportFileSuffix();
                        for (File file : directory.listFiles()) {
                            String fileExtension = FilenameUtils.getExtension(file.getAbsolutePath());
                            if (suffix.equals(fileExtension)) {
                                solution = taImporter.readSolution(file);
                            }
                        }
                        solutionBusiness.setSolution(solution);
                        JFrame frame = new JFrame();
                        Object result = JOptionPane.showInputDialog(frame, "Enter the schedule title");
                        TaRoster taRoster = (TaRoster) solution;
                        taRoster.setCode(result.toString());
                        solutionBusiness.setSolutionFileName(result.toString());
                        setSolutionLoaded();
                        emailAction.setEnabled(true);
                    }
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }

    }


    private class ExportAction extends AbstractAction {
        private static final String NAME = "Export as...";
        private final JFileChooser fileChooser;

        ExportAction() {
            super(NAME, new ImageIcon(SolverAndPersistenceFrame.class.getResource("exportAction.png")));
            if (!solutionBusiness.hasExporter()) {
                fileChooser = null;
                return;
            }
            fileChooser = new JFileChooser(solutionBusiness.getExportDataDir());

            fileChooser.setDialogTitle(NAME);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fileChooser.setSelectedFile(new File(solutionBusiness.getExportDataDir(),
                    FilenameUtils.getBaseName(solutionBusiness.getSolutionFileName())
                            + ".pdf"
            ));

            FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV Files", "csv", "txt");
            FileNameExtensionFilter pdfFilter = new FileNameExtensionFilter("PDF Files", "pdf");
            fileChooser.addChoosableFileFilter(pdfFilter);
            fileChooser.addChoosableFileFilter(csvFilter);
            fileChooser.setFileFilter(pdfFilter);


            int approved = fileChooser.showSaveDialog(SolverAndPersistenceFrame.this);
            if (approved == JFileChooser.APPROVE_OPTION) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    File exportFile = fileChooser.getSelectedFile();
                    String path = exportFile.getPath();
                    String[] splitPath = path.split(FileSystems.getDefault().getSeparator());
                    String fileName = splitPath[splitPath.length-1];

                    String[] fileNameSplit = fileName.split("\\.");
                    String fileExtension = fileNameSplit[fileNameSplit.length-1];

                    if (fileExtension.equals("csv") || fileExtension.equals("txt")) {
                        solutionBusiness.exportSolution(fileChooser.getSelectedFile());
                    }
                    else if (fileExtension.equals("pdf")) {
                        TaRosteringPdfExporter taRosteringPdfExporter = new TaRosteringPdfExporter((TaRoster) solutionBusiness.getSolution());
                        taRosteringPdfExporter.ExportToPdf(path);
                    }
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }

    }

    private class ShowConstraintMatchesDialogAction extends AbstractAction {

        ShowConstraintMatchesDialogAction() {
            super("Constraint matches", new ImageIcon(SolverAndPersistenceFrame.class.getResource("showConstraintMatchesDialogAction.png")));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            constraintMatchesDialog.resetContentPanel();
            constraintMatchesDialog.setVisible(true);
        }
    }

    private class ConstraintAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            TaRoster taRoster = (TaRoster) solutionBusiness.getSolution();
            new ConstraintFrame((MinMaxContractLine) taRoster.getContractLineList().get(0));
        }
    }

    private class EmailAction extends AbstractAction {
        public EmailAction() {
            super("Email All", new ImageIcon(SolverAndPersistenceFrame.class.getResource("mailIcon.jpg")));
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            TaRoster taRoster = (TaRoster) solutionBusiness.getSolution();
            List<Ta> taList = taRoster.getTaList();
            String taExportPath = solutionBusiness.getExportDataDir().getAbsoluteFile()
                    + FileSystems.getDefault().getSeparator() + "TA_Files";
            File taDirectory = new File(taExportPath);
            if (!taDirectory.exists()) {
                taDirectory.mkdir();
            }

            JPanel credentialPanel = new JPanel(new GridLayout(2,2));
            JLabel eidLabel = new JLabel("Enter your E-ID: ");
            JLabel passwordLabel = new JLabel("Enter your password: ");
            JTextField eidField = new JTextField(20);
            JPasswordField passField = new JPasswordField(20);
            credentialPanel.add(eidLabel);
            credentialPanel.add(eidField);
            credentialPanel.add(passwordLabel);
            credentialPanel.add(passField);
            String[] options = new String[]{"OK", "Cancel"};
            int option = JOptionPane.showOptionDialog(null, credentialPanel, "Email Credentials",
                                     JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                                     null, options, options[0]);
            if(option == 0) // pressing OK button
            {
                char[] password = passField.getPassword();
                EmailFrame emailFrame = new EmailFrame(eidField.getText(), password);
                TaRosteringPdfExporter taRosteringPdfExporter = new TaRosteringPdfExporter((TaRoster) solutionBusiness.getSolution());
                Map<String, String> taToFileLocMap = new HashMap<>();
                for (Ta ta : taList) {
                   String taFilePath = taRosteringPdfExporter.ExportTaPdf(ta, taExportPath);
                   taToFileLocMap.put(ta.getEmail(), taFilePath);
                }
                emailFrame.emailAll(taToFileLocMap);
                JOptionPane.showMessageDialog(new JFrame(), "Emails successfully sent.");
            }
        }
    }

    private class HomeAction extends AbstractAction {
        public HomeAction() {
            super("Home", new ImageIcon(SolverAndPersistenceFrame.class.getResource("home_icon.gif")));
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!homeButton.isSelected()) {
                ((CardLayout) middlePanel.getLayout()).show(middlePanel, "solutionPanel");
                homeButton.setSelected(false);
            }
            else {
                ((CardLayout) middlePanel.getLayout()).show(middlePanel, "usageExplanationPanel");
                homeButton.setSelected(true);
            }
        }
    }
}

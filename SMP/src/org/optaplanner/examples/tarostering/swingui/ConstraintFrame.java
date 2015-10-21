package org.optaplanner.examples.tarostering.swingui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.optaplanner.examples.tarostering.domain.contract.MinMaxContractLine;

public class ConstraintFrame extends JFrame {
    private static final String MINSTRING = "Minimum number of courses per TA: ";
    private static final String MAXSTRING = "Maximum number of courses per TA: ";
    protected final MinMaxContractLine contractLine;
    private final JLabel minLabel;
    private final JLabel maxLabel;
    protected final JTextField minField;
    protected final JTextField maxField;
    private final JButton submitButton;
    private final JButton cancelButton;
    private Action submitAction;
    private Action cancelAction;

    public ConstraintFrame(MinMaxContractLine contractLine) {
        super("Constraint Editior");
        this.contractLine = contractLine;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(400,200);
        this.setResizable(false);

        submitAction = new SubmitAction();
        cancelAction = new CancelAction(this);
        minLabel = new JLabel(MINSTRING);
        maxLabel = new JLabel(MAXSTRING);
        minField = new JTextField(String.valueOf(contractLine.getMinimumValue()));
        maxField = new JTextField(String.valueOf(contractLine.getMaximumValue()));
        submitButton = new JButton(submitAction);
        cancelButton = new JButton(cancelAction);
        submitButton.setText("Submit");
        cancelButton.setText("Cancel");

        JPanel constraintPanel = new JPanel();
        GroupLayout layout = new GroupLayout(constraintPanel);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(minLabel,GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addComponent(minField,GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE)
                    )
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(maxLabel,GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addComponent(maxField,GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE)
                    )
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cancelButton)
                        .addComponent(submitButton)
                    )
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(minLabel,GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addComponent(minField,GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE)
                    )
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(maxLabel,GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addComponent(maxField,GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE)
                    )
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(cancelButton)
                        .addComponent(submitButton)
                    )

        );
        constraintPanel.setLayout(layout);
        add(constraintPanel);
        setVisible(true);
        pack();

    }

    private class SubmitAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            contractLine.setMinimumValue(Integer.parseInt(minField.getText()));
            contractLine.setMaximumValue(Integer.parseInt(maxField.getText()));
        }
    }

    private class CancelAction extends AbstractAction {
        private final JFrame jFrame;

        public CancelAction(JFrame jFrame) {
            this.jFrame = jFrame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            jFrame.dispose();
        }
    }

}

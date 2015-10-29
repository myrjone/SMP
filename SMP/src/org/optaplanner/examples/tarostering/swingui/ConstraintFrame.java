/*
 * Copyright 2015 JBoss Inc
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


package org.optaplanner.examples.tarostering.swingui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
        super("Constraint Editor");
        this.contractLine = contractLine;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(400,200);
        this.setResizable(false);

        submitAction = new SubmitAction(this);
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
        private final JFrame jFrame;

        public SubmitAction(JFrame jFrame) {
            this.jFrame = jFrame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int min = Integer.parseInt(minField.getText());
            int max = Integer.parseInt(maxField.getText());
            if (max >= min) {
                contractLine.setMinimumValue(min);
                contractLine.setMaximumValue(max);
                jFrame.dispose();
            }
            else {
                JOptionPane.showMessageDialog(jFrame, "Error: " + "Maximum value should be greater than"
                        + "or equal to the minimum value",
                "Error", JOptionPane.ERROR_MESSAGE);
            }
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

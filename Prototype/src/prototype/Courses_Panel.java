/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototype;

import java.awt.Font;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.JTableHeader;
/**
 *
 * @author alex
 */
public class Courses_Panel extends javax.swing.JPanel {
    private static File importFile;

    /**
     * Creates new form Courses_Panel
     */
    public Courses_Panel() {
        initComponents();
        fillTable();
        courseTable.getTableHeader().setFont(new Font("Tahoma", 1, 14));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        importCoursesButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        courseTable = new javax.swing.JTable();

        importCoursesButton.setText("Import Courses");
        importCoursesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importCoursesButtonActionPerformed(evt);
            }
        });

        courseTable.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        courseTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Course Name", "Sections", "Times", "Assigned Faculty"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        courseTable.setRowHeight(20);
        courseTable.setRowMargin(2);
        courseTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(courseTable);
        if (courseTable.getColumnModel().getColumnCount() > 0) {
            courseTable.getColumnModel().getColumn(0).setResizable(false);
            courseTable.getColumnModel().getColumn(1).setResizable(false);
            courseTable.getColumnModel().getColumn(2).setResizable(false);
            courseTable.getColumnModel().getColumn(3).setResizable(false);
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 741, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(importCoursesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(importCoursesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void importCoursesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importCoursesButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Comma-Separated Value (CSV) file", "csv");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            importFile = chooser.getSelectedFile();
        }
    }//GEN-LAST:event_importCoursesButtonActionPerformed

    private void fillTable() {
        courseTable.setValueAt("Chem 103", 0, 0);
        courseTable.setValueAt("001", 0, 1);
        courseTable.setValueAt("MW 11:00-12:15", 0, 2);
        courseTable.setValueAt("Myron Jones", 0, 3);
        
        courseTable.setValueAt("Chem 103", 1, 0);
        courseTable.setValueAt("002", 1, 1);
        courseTable.setValueAt("TR 3:00-4:15", 1, 2);
        courseTable.setValueAt("Myron Jones", 1, 3);
        
        courseTable.setValueAt("Chem 104", 2, 0);
        courseTable.setValueAt("001", 2, 1);
        courseTable.setValueAt("TR 9:00-10:15", 2, 2);
        courseTable.setValueAt("Walter White", 2, 3);
        
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable courseTable;
    private javax.swing.JButton importCoursesButton;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}

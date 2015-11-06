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
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.io.FilenameUtils;
import org.optaplanner.examples.common.business.SolutionBusiness;

public class EmailFrame extends JFrame {
    private final SolutionBusiness solutionBusiness;
    private static final int TEXT_WIDTH = 30;
    private static final String DEFAULT_FROM = "sfurlow@siue.edu";
    private final JTextField toField;
    private final JTextField ccField;
    private final JTextField bccField;
    private final JTextField fromField;
    private final JTextField subjectField;
    protected JTextField attachmentField;
    private final JTextArea bodyArea;
    private final JButton sendButton;
    private final JButton cancelButton;
    private final JButton attachButton;
    private final Action sendAction;
    private final Action cancelAction;
    private final Action attachAction;
    private String username;
    private char[] password;
    protected final List<String> emailToList;

    public EmailFrame(String username, char[] password, SolutionBusiness solutionBusiness, List<String> emailToList) {
        super("Send Email");
        this.username = username + "@siue.edu";
        this.password = password;
        this.solutionBusiness = solutionBusiness;
        this.emailToList = emailToList;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(false);

        toField = new JTextField(TEXT_WIDTH);
        String[] toFieldArray = emailToList.toArray(new String[emailToList.size()]);
        toField.setText(String.join(";",toFieldArray));
        ccField = new JTextField(TEXT_WIDTH);
        bccField = new JTextField(TEXT_WIDTH);
        fromField = new JTextField(TEXT_WIDTH);
        fromField.setText(DEFAULT_FROM);
        subjectField = new JTextField(TEXT_WIDTH);
        bodyArea = new JTextArea(10,TEXT_WIDTH);

        sendAction = new SendAction(this);
        cancelAction = new CancelAction(this);
        attachAction = new AttachAction(this);

        sendButton = new JButton(sendAction);
        cancelButton = new JButton(cancelAction);
        attachButton = new JButton(attachAction);
        sendButton.setText("Send");
        cancelButton.setText("Cancel");

        attachButton.setBorder(BorderFactory.createEmptyBorder());
        attachmentField = new JTextField(TEXT_WIDTH - 1);

        JPanel emailPanel = new JPanel(new BorderLayout());

        JPanel passwordPanel = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(2,2,2,2);
        c.anchor = GridBagConstraints.LINE_END;
        passwordPanel.add(new JLabel("To: "), c);
        c.gridy++;
        passwordPanel.add(new JLabel("CC: "), c);
        c.gridy++;
        passwordPanel.add(new JLabel("BCC: "), c);
        c.gridy++;
        passwordPanel.add(new JLabel("From: "), c);
        c.gridy++;
        passwordPanel.add(new JLabel("Attachment: "), c);
        c.gridy++;
        passwordPanel.add(new JLabel("Subject: "), c);
        c.gridy++;
        c.anchor = GridBagConstraints.FIRST_LINE_END;
        passwordPanel.add(new JLabel("Body: "), c);

        c.gridy = 0;
        c.gridx++;
        c.anchor = GridBagConstraints.LINE_START;
        passwordPanel.add(toField, c);
        c.gridy++;
        passwordPanel.add(ccField, c);
        c.gridy++;
        passwordPanel.add(bccField, c);
        c.gridy++;
        passwordPanel.add(fromField, c);
        c.gridy++;

        JPanel attachPanel = new JPanel();
        FlowLayout layout = new FlowLayout();
        layout.setVgap(0);
        layout.setHgap(0);
        attachPanel.setLayout(layout);
        attachPanel.setBorder(BorderFactory.createEtchedBorder(1));
        attachmentField.setBorder(BorderFactory.createEmptyBorder());
        attachPanel.add(attachmentField);
        attachPanel.add(attachButton);

        passwordPanel.add(attachPanel, c);
        c.gridy++;
        passwordPanel.add(subjectField, c);
        c.gridy++;
        passwordPanel.add(bodyArea, c);

        emailPanel.add(passwordPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1,2));
        buttonPanel.add(sendButton);
        buttonPanel.add(cancelButton);

        emailPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(emailPanel);
        setVisible(true);
        pack();
        setLocationRelativeTo(null);
    }

    private class SendAction extends AbstractAction {
        private final JFrame jFrame;

        public SendAction(JFrame jFrame) {
            this.jFrame = jFrame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try
            {
                Properties props = System.getProperties();
                props.put("mail.transport.protocol", "smtp" );
                props.put("mail.smtp.starttls.enable","true" );
                props.put("mail.smtp.host","smtp.office365.com");
                props.put("mail.smtp.auth", "true" );
                props.put("mail.smtp.port", 587);
                Authenticator auth = new SMTPAuthenticator();
                Session session = Session.getInstance(props, auth);
                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(fromField.getText()));

                String toFieldString = toField.getText();
                toFieldString = toFieldString.replace(",", ";");
                String[] toAddressArray = toFieldString.split(";");
                for (String address : toAddressArray) {
                    msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(address));
                }

                String ccFieldString = ccField.getText();
                ccFieldString = ccFieldString.replace(",",";");
                String[] ccArray = ccFieldString.split(";");
                for (String address : ccArray) {
                    msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse(address));
                }

                String bccFieldString = bccField.getText();
                bccFieldString = bccFieldString.replace(",",";");
                String[] bccArray = bccFieldString.split(";");
                for (String address : bccArray) {
                    msg.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(address));
                }

                msg.setSubject(subjectField.getText());
                msg.setHeader("Chemistry Schedule", "Myron Jones" );
                msg.setSentDate(new Date());

                Multipart multipart = new MimeMultipart();

                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(bodyArea.getText());
                messageBodyPart.setContent(bodyArea.getText(), "text/html");
                multipart.addBodyPart(messageBodyPart);

                BodyPart attachmentBodyPart = new MimeBodyPart();
                String attachmentPath = attachmentField.getText();
                if (!("".equals(attachmentPath))) {
                    DataSource source = new FileDataSource(attachmentPath);
                    attachmentBodyPart.setDataHandler(new DataHandler(source));
                    attachmentBodyPart.setFileName(new File(attachmentPath).getName());
                    multipart.addBodyPart(attachmentBodyPart);
                }

                msg.setContent(multipart);
                Transport.send(msg);
                JOptionPane.showMessageDialog(jFrame, "Emails sent successfully!");
                jFrame.dispose();
            }
            catch (Exception ex)
            {
              throw new RuntimeException("Error sending emails");
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

    private class AttachAction extends AbstractAction {
        private final JFileChooser fileChooser;
        private final EmailFrame emailFrame;

        AttachAction(EmailFrame emailFrame) {
            super("", new ImageIcon(EmailFrame.class.getResource("attachment_icon_black.gif")));
            this.emailFrame = emailFrame;
            fileChooser = new JFileChooser(solutionBusiness.getExportDataDir());
            fileChooser.setDialogTitle(NAME);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fileChooser.setSelectedFile(new File(solutionBusiness.getExportDataDir(),
                    FilenameUtils.getBaseName(solutionBusiness.getSolutionFileName())
                            + ".pdf"
            ));

            FileNameExtensionFilter pdfFilter = new FileNameExtensionFilter("PDF Files", "pdf");
            fileChooser.addChoosableFileFilter(pdfFilter);
            fileChooser.setFileFilter(pdfFilter);


            int approved = fileChooser.showSaveDialog(emailFrame);
            if (approved == JFileChooser.APPROVE_OPTION) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    File exportFile = fileChooser.getSelectedFile();
                    String path = exportFile.getPath();
                    String[] splitPath = path.split("\\\\");
                    String fileName = splitPath[splitPath.length-1];

                    String[] fileNameSplit = fileName.split("\\.");
                    String fileExtension = fileNameSplit[fileNameSplit.length-1];

                    if (fileExtension.equals("pdf")) {
                        emailFrame.attachmentField.setText(path);
                    }
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {
        @Override
        public javax.mail.PasswordAuthentication getPasswordAuthentication() {
            return new javax.mail.PasswordAuthentication(fromField.getText(), String.valueOf(password));
        }
    }
}

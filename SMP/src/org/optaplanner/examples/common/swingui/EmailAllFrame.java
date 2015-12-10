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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class EmailAllFrame extends JDialog {
    private static final int TEXT_WIDTH = 30;
    private static final String DEFAULT_SUBJECT = "Chemistry TA schedule";
    private static final String DEFAULT_BODY = "This is your assigned schedule for the upcoming semester.";
    private JTextField toField;
    private JTextField ccField;
    private JTextField bccField;
    private JTextField fromField;
    private JTextField subjectField;
    protected JTextField attachmentField;
    private JTextArea bodyArea;
    private JButton sendButton;
    private JButton cancelButton;
    private Action sendAction;
    private Action cancelAction;
    private final String username;
    private final char[] password;
    private final Map<String, String> emailToAttachmentMap;

    public EmailAllFrame(String username, char[] password,Map<String, String> emailToAttachmentMap) {
        this.username = username + "@siue.edu";
        this.password = password;
        this.emailToAttachmentMap = emailToAttachmentMap;
        createUI();
    }

    private void createUI() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setModal(true);
        this.setTitle("Email Form");
        toField = new JTextField(TEXT_WIDTH);

        toField.setText("ALL TA's");
        toField.setEnabled(false);
        ccField = new JTextField(TEXT_WIDTH);
        bccField = new JTextField(TEXT_WIDTH);
        fromField = new JTextField(TEXT_WIDTH);
        fromField.setText(username);
        subjectField = new JTextField(TEXT_WIDTH);
        subjectField.setText(DEFAULT_SUBJECT);
        bodyArea = new JTextArea(10,TEXT_WIDTH);
        bodyArea.setText(DEFAULT_BODY);

        sendAction = new SendAction(this);
        cancelAction = new CancelAction(this);

        sendButton = new JButton(sendAction);
        cancelButton = new JButton(cancelAction);
        sendButton.setText("Send");
        cancelButton.setText("Cancel");

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
        passwordPanel.add(subjectField, c);
        c.gridy++;
        passwordPanel.add(bodyArea, c);

        emailPanel.add(passwordPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1,2));
        buttonPanel.add(sendButton);
        buttonPanel.add(cancelButton);

        emailPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(emailPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void emailAll() {
        Authenticator auth = new SMTPAuthenticator();
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp" );
        props.put("mail.smtp.starttls.enable","true" );
        props.put("mail.smtp.host","smtp.office365.com");
        props.put("mail.smtp.auth", "true" );
        props.put("mail.smtp.port", 587);
        Session session = Session.getInstance(props, auth);
        for (String to : emailToAttachmentMap.keySet()) {
            try {
                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(this.username));
                msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

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
                messageBodyPart.setContent(bodyArea.getText(), "text/html");
                multipart.addBodyPart(messageBodyPart);

                BodyPart attachmentBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(emailToAttachmentMap.get(to));
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                attachmentBodyPart.setFileName(new File(emailToAttachmentMap.get(to)).getName());
                multipart.addBodyPart(attachmentBodyPart);

                msg.setContent(multipart);
                Transport.send(msg);
            } catch (MessagingException ex) {
                throw new RuntimeException("Error sending email to " + to + ".Aborting email action.");
            }
        }
    }

    private class SendAction extends AbstractAction {
        private final JDialog jDialog;

        public SendAction(JDialog jDialog) {
            this.jDialog = jDialog;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try
            {
                emailAll();
                JOptionPane.showMessageDialog(jDialog, "Emails sent successfully!");
                jDialog.dispose();
            }
            catch (Exception ex)
            {
              throw new RuntimeException("Error sending emails");
            }
        }
    }

    private class CancelAction extends AbstractAction {
        private final JDialog jDialog;

        public CancelAction(JDialog jDialog) {
            this.jDialog = jDialog;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            jDialog.dispose();
        }
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {
        @Override
        public javax.mail.PasswordAuthentication getPasswordAuthentication() {
            return new javax.mail.PasswordAuthentication(username, String.valueOf(password));
        }
    }
}

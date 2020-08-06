/*
 * Copyright 2014 - 2017 Cognizant Technology Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognizant.cognizantits.engine.mail;

import com.cognizant.cognizantits.engine.constants.FilePath;
import com.cognizant.cognizantits.engine.core.Control;
import com.cognizant.cognizantits.engine.core.RunManager;
import com.cognizant.cognizantits.engine.reporting.impl.ConsoleReport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 *
 */
public class Mailer {

    private static final Logger LOG = Logger.getLogger("Mailer");

    private static final List<File> TEMP_ZIP = new ArrayList<>();

    public static void send() {
        if (iCanSend()) {
            try {
                LOG.info("Sending Reports to Mail");
                sendMail();
            } catch (MessagingException | IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }

    public static Boolean connect(Properties props)
            throws MessagingException, IOException {
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        props.getProperty("username"),
                        props.getProperty("password"));
            }
        });
        Transport transport = session.getTransport("smtp");
        transport.connect();
        transport.close();
        return true;
    }

    private static void sendMail()
            throws MessagingException, IOException {
        Session session = Session.getInstance(getMailProps(), new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        getVal("username"),
                        getVal("password"));
            }
        });
        session.setDebug(getBoolVal("mail.debug"));
        LOG.info("Compiling Mail before Sending");
        Message message = createMessage(session);
        Transport transport = session.getTransport("smtp");
        LOG.info("Connecting to Mail Server");
        transport.connect();
        LOG.info("Sending Mail");
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
        LOG.info("Reports are sent to Mail");
        clearTempZips();
    }

    private static Message createMessage(Session session)
            throws MessagingException, IOException {
        Message msg = new MimeMessage(session);
        InternetAddress fromAddress = new InternetAddress(
                getVal("from.mail"), getVal("username"));
        msg.setFrom(fromAddress);
        Optional.ofNullable(getVal("to.mail")).ifPresent((String tos) -> {
            for (String to : tos.split(";")) {
                try {
                    msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
                            to, to));
                } catch (Exception ex) {
                    Logger.getLogger(Mailer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        msg.setSubject(parseSubject(getVal("msg.subject")));
        msg.setContent(getMessagePart());
        return msg;
    }

    private static String parseSubject(String subject) {
        String status = Control.ReportManager.isPassed() ? "Passed" : "Failed";
        String component = RunManager.getRunName();
        return subject.replace("{{component}}", component).replace("{{status}}", status);
    }

    private static Multipart getMessagePart() throws MessagingException, IOException {
        Multipart multipart = new MimeMultipart();
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(getVal("msg.Body"));
        multipart.addBodyPart(messageBodyPart);
        if (getBoolVal("attach.reports")) {
            LOG.info("Attaching Reports as zip");
            multipart.addBodyPart(getReportsBodyPart());
        } else {
            if (getBoolVal("attach.standaloneHtml")) {
                multipart.addBodyPart(getStandaloneHtmlBodyPart());
            }
            if (getBoolVal("attach.console")) {
                multipart.addBodyPart(getConsoleBodyPart());
            }
            if (getBoolVal("attach.screenshots")) {
                multipart.addBodyPart(getScreenShotsBodyPart());
            }
        }
        messageBodyPart.setContent(getVal("msg.Body")
                .concat("\n\n\n")
                .concat(MailComponent.getHTMLBody()), "text/html");
        return multipart;
    }

    private static BodyPart getConsoleBodyPart() throws MessagingException, IOException {
        return getBodyPart(ConsoleReport.consoleFile.getAbsolutePath(), null);
    }

    private static BodyPart getStandaloneHtmlBodyPart() throws MessagingException, IOException {
        return getBodyPart(FilePath.getLatestResultsLocation(),
                (file, name)
                -> name.endsWith(".html")
                && !(name.startsWith("summary")
                && name.startsWith("detailed")));
    }

    private static BodyPart getScreenShotsBodyPart() throws MessagingException, IOException {
        return getBodyPart(new File(FilePath.getCurrentResultsPath(), "img").getAbsolutePath(), null);
    }

    private static BodyPart getReportsBodyPart() throws MessagingException, IOException {
        return getBodyPart(FilePath.getLatestResultsLocation(), null);
    }

    private static BodyPart getBodyPart(String fileLoc, FilenameFilter fexFilter) throws MessagingException, IOException {
        BodyPart messageBodyPart = new MimeBodyPart();
        File fileName = new File(fileLoc);
        if (fileName.isDirectory()) {
            fileName = zipFolder(fileLoc, fexFilter);
        }
        DataSource source = new FileDataSource(fileName);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(fileName.getName());
        return messageBodyPart;
    }

    private static File zipFolder(String folderLoc, FilenameFilter fexFilter) {
        File reportPath = new File(folderLoc);
        File reportZipPath = new File(reportPath.getPath() + ".zip");
        try {
            try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(reportZipPath))) {
                addFolderToZip(reportPath, fexFilter, zip, reportPath.getParentFile().getAbsolutePath());
            }
            return reportZipPath;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        TEMP_ZIP.add(reportZipPath);
        return reportZipPath;
    }

    private static void addFolderToZip(File folder, FilenameFilter fexFilter, ZipOutputStream zip, String baseName) throws IOException {
        File[] files = fexFilter == null ? folder.listFiles() : folder.listFiles(fexFilter);
        for (File file : files) {
            if (file.isDirectory()) {
                addFolderToZip(file, fexFilter, zip, baseName);
            } else {
                String name = file.getAbsolutePath().substring(baseName.length() + 1);
                ZipEntry zipEntry = new ZipEntry(name);
                zip.putNextEntry(zipEntry);
                IOUtils.copy(new FileInputStream(file), zip);
                zip.closeEntry();
            }
        }
    }

    private static Boolean getBoolVal(String prop) {
        return getVal(prop).equals("true");
    }

    private static String getVal(String prop) {
        return getMailProps().getProperty(prop, "");
    }

    private static Properties getMailProps() {
        return Control.exe.getProject().getProjectSettings().getMailSettings();
    }

    private static Boolean iCanSend() {
        return Control.exe.getExecSettings().getRunSettings()
                .isMailSend();
    }

    private static void clearTempZips() {
        TEMP_ZIP.stream().forEach(FileUtils::deleteQuietly);
    }
}

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
package com.cognizant.cognizantits.engine.reporting.sync.zephyr;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class SoapZephyrUtil {

    public static SOAPMessage sendSOAPMessage(SOAPMessage message, String url, final Proxy p) throws SOAPException, MalformedURLException {
        SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = factory.createConnection();

        URL endpoint = new URL(null, url, new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL url) throws IOException {
                // The url is the parent of this stream handler, so must
                // create clone
                URL clone = new URL(url.toString());

                URLConnection connection = null;
                if (p.address().toString().equals("0.0.0.0/0.0.0.0:80")) {
                    connection = clone.openConnection();
                } else {
                    connection = clone.openConnection(p);
                }
                connection.setConnectTimeout(5 * 1000);
                connection.setReadTimeout(5 * 1000);
                return connection;
            }
        });

        try {
            SOAPMessage response = connection.call(message, endpoint);
            connection.close();
            return response;
        } catch (Exception ex) {
            // Re-try if the connection failed
            Logger.getLogger(SoapZephyrUtil.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);           
        }
        return null;
    }

    public static SOAPMessage loginToZephyr(ZephyrClient client) throws Exception {
        String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.service.thed.com/\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <soap:login>\n"
                + "         <!--Optional:-->\n"
                + "         <username>" + client.userName + "</username>\n"
                + "         <password>" + client.password + "</password>\n"
                + "      </soap:login>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        return getSoapMessageFromString(xml);
    }

    public static SOAPMessage getSoap() throws Exception {
        String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.service.thed.com/\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <soap:login>\n"
                + "         <!--Optional:-->\n"
                + "         <username>mike.betts</username>\n"
                + "         <password>mike.betts</password>\n"
                + "      </soap:login>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        return getSoapMessageFromString(xml);
    }

    public static SOAPMessage updateTestStatus(int entityId, int status, String token) throws Exception {
        String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.service.thed.com/\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <soap:updateTestStatus>\n"
                + "         <testResults>\n"
                + "           <executionStatus>" + status + "</executionStatus>\n"
                + "         <releaseTestScheduleId>" + entityId + "</releaseTestScheduleId>\n"
                + "         </testResults>\n"
                + "         <token>" + token + "</token>\n"
                + "      </soap:updateTestStatus>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        return getSoapMessageFromString(xml);
    }

    public static SOAPMessage uploadAttachment(int entityId, String token, File file) throws Exception {
        String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.service.thed.com/\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <soap:addAttachments>\n"
                + "         <!--Zero or more repetitions:-->\n"
                + "         <remoteAttachments>\n"
                + "            <id>3</id>\n"
                + "            <entityId>" + entityId + "</entityId>\n"
                + "            <entityName>releaseTestSchedule</entityName>\n"
                + "            <fileName>" + file.getName() + "</fileName>\n"
                + "            <attachment>cid:855360235663</attachment>\n"
                + "            <attachmentURI>" + "'" + file.getAbsolutePath() + "'" + "</attachmentURI>\n"
                + "         </remoteAttachments>\n"
                + "         <!--Optional:-->\n"
                + "         <token>" + token + "</token>\n"
                + "      </soap:addAttachments>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        return getSoapMessageFromString(xml);
    }

    private static SOAPMessage getSoapMessageFromString(String xml) throws SOAPException, IOException {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage(new MimeHeaders(), new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8"))));
        return message;
    }

    public static String parseSOAPResponse(SOAPMessage soapResponse, String expression) {
        String retStr = null;
        try {
            final StringWriter writer = new StringWriter();
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(soapResponse.getSOAPPart()),
                    new StreamResult(writer));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream stream = new ByteArrayInputStream(writer.toString().getBytes("UTF-8"));
            Document doc = builder.parse(stream);
            XPath xPath = XPathFactory.newInstance().newXPath();
            //String expression = "//return";
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
            for (int index = 0; index < nodeList.getLength(); index++) {
                org.w3c.dom.Node node = nodeList.item(index);
                retStr = node.getTextContent();
            }
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(SoapZephyrUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            Logger.getLogger(SoapZephyrUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return retStr;
    }
}

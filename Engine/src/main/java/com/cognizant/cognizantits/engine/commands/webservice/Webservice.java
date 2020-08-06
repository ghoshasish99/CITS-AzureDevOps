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
package com.cognizant.cognizantits.engine.commands.webservice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.cognizant.cognizantits.datalib.settings.RunSettings;
import com.cognizant.cognizantits.engine.commands.General;
import com.cognizant.cognizantits.engine.core.CommandControl;
import com.cognizant.cognizantits.engine.core.Control;
import com.cognizant.cognizantits.engine.support.Status;
import com.cognizant.cognizantits.engine.support.methodInf.Action;
import com.cognizant.cognizantits.engine.support.methodInf.InputType;
import com.cognizant.cognizantits.engine.support.methodInf.ObjectType;
import com.jayway.jsonpath.JsonPath;

public class Webservice extends General {

	public Webservice(CommandControl cc) {
		super(cc);
		key = userData.getScenario() + userData.getTestCase();
	}

	public enum RequestMethod {

		POST, PUT, GET, DELETE

	}

	static private Map<String, String> endPoints = new HashMap<>();
	static private Map<String, ArrayList<String>> headers = new HashMap<>();
	static private Map<String, String> responsebodies = new HashMap<>();
	static private Map<String, String> responsecodes = new HashMap<>();
	static private Map<String, String> responsemessages = new HashMap<>();
	static private Map<String, HttpURLConnection> httpConnections = new HashMap<>();
	static private Map<String, String> httpagents = new HashMap<>();
	static private Map<String, Long> starttime = new HashMap<>();
	static private Map<String, Long> endtime = new HashMap<>();
	static private Map<String, String> basicAuthorization = new HashMap<>();
	private String key;
	

	@Action(object = ObjectType.WEBSERVICE, desc = "PUT Rest Request ", input = InputType.YES, condition = InputType.OPTIONAL)
	public void putRestRequest() {
		createhttpRequest(RequestMethod.PUT);
	}

	@Action(object = ObjectType.WEBSERVICE, desc = "POST Rest Request ", input = InputType.YES, condition = InputType.OPTIONAL)
	public void postRestRequest() {
		createhttpRequest(RequestMethod.POST);
	}

	@Action(object = ObjectType.WEBSERVICE, desc = "POST SOAP Request ", input = InputType.YES, condition = InputType.OPTIONAL)
	public void postSoapRequest() {
		createhttpRequest(RequestMethod.POST);
	}

	@Action(object = ObjectType.WEBSERVICE, desc = "GET Rest Request ", input = InputType.NO, condition = InputType.OPTIONAL)
	public void getRestRequest() {
		createhttpRequest(RequestMethod.GET);
	}

	@Action(object = ObjectType.WEBSERVICE, desc = "DELETE Rest Request ", input = InputType.NO)
	public void deleteRestRequest() {
		createhttpRequest(RequestMethod.DELETE);
	}

	@Action(object = ObjectType.WEBSERVICE, desc = "Assert Response Code ", input = InputType.YES)
	public void assertResponseCode() {
		try {
			if (responsecodes.get(key).equals(Data)) {
				Report.updateTestLog(Action, "Status code is : " + Data, Status.PASSNS);
			} else {
				Report.updateTestLog(Action, "Status code is : " + responsecodes.get(key) + " but should be " + Data,
						Status.FAILNS);
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, ex);
			Report.updateTestLog(Action, "Error in validating response code :" + "\n" + ex.getMessage(), Status.DEBUG);
		}
	}

	@Action(object = ObjectType.WEBSERVICE, desc = "Assert Response Message ", input = InputType.YES)
	public void assertResponseMessage() {
		try {
			if (responsemessages.get(key).equals(Data)) {
				Report.updateTestLog(Action, "Response message is : " + Data, Status.PASSNS);
			} else {
				Report.updateTestLog(Action,
						"Response message is : " + responsemessages.get(key) + " but should be " + Data, Status.FAILNS);
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, ex);
			Report.updateTestLog(Action, "Error in validating response message :" + "\n" + ex.getMessage(),
					Status.DEBUG);
		}
	}

	@Action(object = ObjectType.WEBSERVICE, desc = "Assert Response Body contains ", input = InputType.YES)
	public void assertResponsebodycontains() {
		try {
			if (responsebodies.get(key).contains(Data)) {
				Report.updateTestLog(Action, "Response body contains : " + Data, Status.PASSNS);
			} else {
				Report.updateTestLog(Action, "Response body does not contain : " + Data, Status.FAILNS);
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, ex);
			Report.updateTestLog(Action, "Error in validating response body :" + "\n" + ex.getMessage(), Status.DEBUG);
		}
	}

	@Action(object = ObjectType.WEBSERVICE, desc = "Assert JSON Element Equals ", input = InputType.YES, condition = InputType.YES)
	public void assertJSONelementEquals() {
		try {
			String response = responsebodies.get(key);
			String jsonpath = Condition;
			String value = JsonPath.read(response, jsonpath).toString();
			if (value.equals(Data)) {
				Report.updateTestLog(Action, "Element text [" + value + "] is as expected", Status.PASSNS);
			} else {
				Report.updateTestLog(Action, "Element text is [" + value + "] but is expected to be [" + Data + "]",
						Status.FAILNS);
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, ex);
			Report.updateTestLog(Action, "Error in validating JSON element :" + "\n" + ex.getMessage(), Status.DEBUG);
		}
	}

	@Action(object = ObjectType.WEBSERVICE, desc = "Store JSON Element In DataSheet ", input = InputType.YES, condition = InputType.YES)
	public void storeJSONelementInDataSheet() {

		try {
			String strObj = Input;
			if (strObj.matches(".*:.*")) {
				try {
					System.out.println("Updating value in SubIteration " + userData.getSubIteration());
					String sheetName = strObj.split(":", 2)[0];
					String columnName = strObj.split(":", 2)[1];
					String response = responsebodies.get(key);
					String jsonpath = Condition;
					String value = JsonPath.read(response, jsonpath).toString();
					userData.putData(sheetName, columnName, value);
					Report.updateTestLog(Action, "Element text [" + value + "] is stored in " + strObj, Status.DONE);
				} catch (Exception ex) {
					Logger.getLogger(this.getClass().getName()).log(Level.OFF, ex.getMessage(), ex);
					Report.updateTestLog(Action, "Error Storing JSON element in datasheet :" + "\n" + ex.getMessage(),
							Status.DEBUG);
				}
			} else {
				Report.updateTestLog(Action,
						"Given input [" + Input + "] format is invalid. It should be [sheetName:ColumnName]",
						Status.DEBUG);
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, ex);
			Report.updateTestLog(Action, "Error Storing JSON element in datasheet :" + "\n" + ex.getMessage(),
					Status.DEBUG);
		}
	}

	@Action(object = ObjectType.WEBSERVICE, desc = "Store XML Element In DataSheet ", input = InputType.YES, condition = InputType.YES)
	public void storeXMLelementInDataSheet() {

		try {
			String strObj = Input;
			if (strObj.matches(".*:.*")) {
				try {
					System.out.println("Updating value in SubIteration " + userData.getSubIteration());
					String sheetName = strObj.split(":", 2)[0];
					String columnName = strObj.split(":", 2)[1];
					String xmlText = responsebodies.get(key);
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder;
					InputSource inputSource = new InputSource();
					inputSource.setCharacterStream(new StringReader(xmlText));
					dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.parse(inputSource);
					doc.getDocumentElement().normalize();
					XPath xPath = XPathFactory.newInstance().newXPath();
					String expression = Condition;
					NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
					Node nNode = nodeList.item(0);
					String value = nNode.getNodeValue();
					userData.putData(sheetName, columnName, value);
					Report.updateTestLog(Action, "Element text [" + value + "] is stored in " + strObj, Status.DONE);
				} catch (IOException | ParserConfigurationException | XPathExpressionException | DOMException
						| SAXException ex) {
					Logger.getLogger(this.getClass().getName()).log(Level.OFF, ex.getMessage(), ex);
					Report.updateTestLog(Action, "Error Storing XML element in datasheet :" + "\n" + ex.getMessage(),
							Status.DEBUG);
				}
			} else {
				Report.updateTestLog(Action,
						"Given input [" + Input + "] format is invalid. It should be [sheetName:ColumnName]",
						Status.DEBUG);
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, ex);
			Report.updateTestLog(Action, "Error Storing XML element in datasheet :" + "\n" + ex.getMessage(),
					Status.DEBUG);
		}
	}

	@Action(object = ObjectType.WEBSERVICE, desc = "Store JSON Element", input = InputType.YES, condition = InputType.YES)
	public void storeJSONelement() {
		try {
			String variableName = Condition;
			String jsonpath = Data;
			if (variableName.matches("%.*%")) {
				addVar(variableName, JsonPath.read(responsebodies.get(key), jsonpath).toString());
				Report.updateTestLog(Action, "JSON element value stored", Status.DONE);
			} else {
				Report.updateTestLog(Action, "Variable format is not correct", Status.DEBUG);
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, ex);
			Report.updateTestLog(Action, "Error Storing JSON element :" + "\n" + ex.getMessage(), Status.DEBUG);
		}
	}

	@Action(object = ObjectType.WEBSERVICE, desc = "Store XML Element", input = InputType.YES, condition = InputType.YES)
	public void storeXMLelement() {
		try {
			String variableName = Condition;
			String expression = Data;
			if (variableName.matches("%.*%")) {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder;
				InputSource inputSource = new InputSource();
				inputSource.setCharacterStream(new StringReader(responsebodies.get(key)));
				dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(inputSource);
				doc.getDocumentElement().normalize();
				XPath xPath = XPathFactory.newInstance().newXPath();
				NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
				Node nNode = nodeList.item(0);
				String value = nNode.getNodeValue();
				addVar(variableName, value);
				Report.updateTestLog(Action, "XML element value stored", Status.DONE);
			} else {
				Report.updateTestLog(Action, "Variable format is not correct", Status.DEBUG);
			}
		} catch (IOException | ParserConfigurationException | XPathExpressionException | DOMException
				| SAXException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, ex);
			Report.updateTestLog(Action, "Error Storing XML element :" + "\n" + ex.getMessage(), Status.DEBUG);
		}
	}

	@Action(object = ObjectType.WEBSERVICE, desc = "Store Response Message In DataSheet ", input = InputType.YES)
	public void storeResponseBodyInDataSheet() {
		try {
			String strObj = Input;
			if (strObj.matches(".*:.*")) {
				try {
					System.out.println("Updating value in SubIteration " + userData.getSubIteration());
					String sheetName = strObj.split(":", 2)[0];
					String columnName = strObj.split(":", 2)[1];
					userData.putData(sheetName, columnName, responsebodies.get(key));
					Report.updateTestLog(Action, "Response body is stored in " + strObj, Status.DONE);
				} catch (Exception ex) {
					Logger.getLogger(this.getClass().getName()).log(Level.OFF, ex.getMessage(), ex);
					Report.updateTestLog(Action, "Error Storing text in datasheet :" + ex.getMessage(), Status.DEBUG);
				}
			} else {
				Report.updateTestLog(Action,
						"Given input [" + Input + "] format is invalid. It should be [sheetName:ColumnName]",
						Status.DEBUG);
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, ex);
			Report.updateTestLog(Action, "Error Storing response body in datasheet :" + "\n" + ex.getMessage(),
					Status.DEBUG);
		}
	}

	@Action(object = ObjectType.WEBSERVICE, desc = "Assert XML Element Equals ", input = InputType.YES, condition = InputType.YES)
	public void assertXMLelementEquals() {

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			InputSource inputSource = new InputSource();
			inputSource.setCharacterStream(new StringReader(responsebodies.get(key)));
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputSource);
			doc.getDocumentElement().normalize();
			XPath xPath = XPathFactory.newInstance().newXPath();
			String expression = Condition;
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
			Node nNode = nodeList.item(0);
			String value = nNode.getNodeValue();
			if (value.equals(Data)) {
				Report.updateTestLog(Action, "Element text [" + value + "] is as expected", Status.PASSNS);
			} else {
				Report.updateTestLog(Action, "Element text [" + value + "] is not as expected", Status.FAILNS);
			}
		} catch (IOException | ParserConfigurationException | XPathExpressionException | DOMException
				| SAXException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, ex);
			Report.updateTestLog(Action, "Error validating XML element :" + "\n" + ex.getMessage(), Status.DEBUG);
		}
	}

	@Action(object = ObjectType.WEBSERVICE, desc = "Set End Point ", input = InputType.YES, condition = InputType.OPTIONAL)
	public void setEndPoint() {
		try {
			String resource = handlePayloadorEndpoint(Data);

			endPoints.put(key, resource);
			System.out.println(userData.getScenario());
			Report.updateTestLog(Action, "End point set : " + resource, Status.DONE);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, ex);
			Report.updateTestLog(Action, "Error setting the end point :" + "\n" + ex.getMessage(), Status.DEBUG);
		}
	}

	private void returnResponseDetails() throws IOException {
		System.out.println("Response");
		/*InputStreamReader reader = new InputStreamReader(httpConnections.get(key).getInputStream());
		StringBuilder buf = new StringBuilder();
		char[] cbuf = new char[2048];
		int num;
		while (-1 != (num = reader.read(cbuf))) {
			buf.append(cbuf, 0, num);
		}*/
		responsecodes.put(key, Integer.toString(httpConnections.get(key).getResponseCode()));
		System.out.println("RESPONSE CODE : "+httpConnections.get(key).getResponseCode());
		BufferedReader in = new BufferedReader(new InputStreamReader(httpConnections.get(key).getInputStream()));
		String  ret = "";
		String inputLine;
	       while ((inputLine = in.readLine()) != null) 
	       {
	            ret = ret + inputLine;
	       }
	       if(in!=null)
	         in.close();
	       if(httpConnections.get(key)!=null)
	    	   httpConnections.get(key).disconnect();
	       if(ret!=null && ret.length()>0){
	           //return ret.toString();
	           responsebodies.put(key, ret.toString());
	       }
	       
		//responsebodies.put(key, buf.toString());
		responsecodes.put(key, Integer.toString(httpConnections.get(key).getResponseCode()));
		endtime.put(key, System.currentTimeMillis());
		responsemessages.put(key, httpConnections.get(key).getResponseMessage());
	}

	@Action(object = ObjectType.WEBSERVICE, desc = "Store Header Element in Datasheet", input = InputType.YES, condition = InputType.YES)
	public void storeHeaderElementByName() {
		try {
			String variableName = Condition;
			String headerName = Data;
			if (variableName.matches("%.*%")) {
				addVar(variableName, httpConnections.get(key).getHeaderField(headerName));
				Report.updateTestLog(Action, "Header value stored", Status.DONE);
			} else {
				Report.updateTestLog(Action, "Variable format is not correct", Status.DEBUG);
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, ex);
			Report.updateTestLog(Action, "Error Storing Header Element element :" + "\n" + ex.getMessage(),
					Status.DEBUG);
		}
	}

	@Action(object = ObjectType.WEBSERVICE, desc = "Add Header ", input = InputType.YES)
	public void addHeader() {
		try {
			if (Webservice.headers.containsKey(key)) {
				Webservice.headers.get(key).add(Data);
			} else {
				ArrayList<String> toBeAdded = new ArrayList<String>();
				toBeAdded.add(Data);
				Webservice.headers.put(key, toBeAdded);
			}
			Report.updateTestLog(Action, "Header added " + Data, Status.DONE);
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, ex);
			Report.updateTestLog(Action, "Error adding Header :" + "\n" + ex.getMessage(), Status.DEBUG);
		}
	}

	@Action(object = ObjectType.WEBSERVICE, desc = "Set Basic Authorization Details ", input = InputType.YES)
	public void setBasicAuthorization() {
		try {
			String authorizationdetails = Data;
			if (Data.contains(" Enc")) {
				byte[] encoded = Base64.decodeBase64(Data.getBytes());
				authorizationdetails = new String(encoded);
			}
			basicAuthorization.put(key, authorizationdetails) ;
			Report.updateTestLog(Action, "Authorization Details added ", Status.DONE);
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, ex);
			Report.updateTestLog(Action, "Error in setting Basic Authorization :" + "\n" + ex.getMessage(),
					Status.DEBUG);
		}
	}

	@Action(object = ObjectType.WEBSERVICE, desc = "Close the connection ", input = InputType.NO)
	public void closeConnection() {
		try {
			headers.remove(key);
			responsebodies.remove(key);
			basicAuthorization.remove(key);
			responsecodes.remove(key);
			responsemessages.remove(key);
			endPoints.remove(key);
			Report.updateTestLog(Action, "Connection is closed", Status.DONE);
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, ex);
			Report.updateTestLog(Action, "Error closing connection :" + "\n" + ex.getMessage(), Status.DEBUG);
		}
	}

	private Proxy getProxyDetails() {
		if (Control.getCurrentProject().getProjectSettings().getDriverSettings().useProxy()) {
			String proxyhost = Control.getCurrentProject().getProjectSettings().getDriverSettings()
					.getProperty("proxyHost");
			String proxyport = Control.getCurrentProject().getProjectSettings().getDriverSettings()
					.getProperty("proxyPort");
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyhost, Integer.parseInt(proxyport)));
			return proxy;
		} else {
			return null;
		}
	}

	private String getHttpAgentDetails() {
		if (Control.getCurrentProject().getProjectSettings().getUserDefinedSettings().stringPropertyNames()
				.contains("http.agent")) {
			if (!getUserDefinedData("http.agent").isEmpty()) {
				httpagents.put(key, getUserDefinedData("http.agent"));
				return httpagents.get(key);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private String handlePayloadorEndpoint(String data) throws FileNotFoundException {
		String payloadstring = data;
		File file = new File(Data);
		if (file.isFile()) {
			Scanner sc = new Scanner(file);
			payloadstring = "";
			while (sc.hasNext()) {
				payloadstring += sc.nextLine() + "\n";
			}
			sc.close();
		}
		payloadstring = handleDataSheetVariables(payloadstring);
		payloadstring = handleuserDefinedVariables(payloadstring);
		System.out.println("Payload :" + payloadstring);
		return payloadstring;
	}

	private String handleDataSheetVariables(String payloadstring) {
		List<String> sheetlist = Control.getCurrentProject().getTestData().getTestDataFor(Control.exe.runEnv())
				.getTestDataNames();
		for (int sheet = 0; sheet < sheetlist.size(); sheet++) {
			if (payloadstring.contains("{" + sheetlist.get(sheet) + ":")) {
				com.cognizant.cognizantits.datalib.testdata.model.TestDataModel tdModel = Control.getCurrentProject()
						.getTestData().getTestDataByName(sheetlist.get(sheet));
				List<String> columns = tdModel.getColumns();
				for (int col = 0; col < columns.size(); col++) {
					if (payloadstring.contains("{" + sheetlist.get(sheet) + ":" + columns.get(col) + "}")) {
						payloadstring = payloadstring.replace("{" + sheetlist.get(sheet) + ":" + columns.get(col) + "}",
								userData.getData(sheetlist.get(sheet), columns.get(col)));
					}
				}
			}
		}
		return payloadstring;
	}

	private String handleuserDefinedVariables(String payloadstring) {
		Collection<Object> valuelist = Control.getCurrentProject().getProjectSettings().getUserDefinedSettings()
				.values();
		for (Object prop : valuelist) {
			if (payloadstring.contains("{" + prop + "}")) {
				payloadstring = payloadstring.replace("{" + prop + "}", prop.toString());
			}
		}
		return payloadstring;
	}

	private void OpenURLconnection() {
		try {
			URL url = new URL(endPoints.get(key));
			if (getProxyDetails() != null) {
				httpConnections.put(key, (HttpURLConnection) url.openConnection(getProxyDetails()));
			} else {
				httpConnections.put(key, (HttpURLConnection) url.openConnection());
			}
		} catch (IOException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, ex.getMessage(), ex);
		}
	}

	private void setheaders() {
		try {
			if (Webservice.headers.containsKey(key)) {
				ArrayList<String> headers = Webservice.headers.get(key);
				if (headers.size() > 0) {
					headers.forEach((header) -> {
						httpConnections.get(key).setRequestProperty(header.split("=")[0], header.split("=")[1]);
					});
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, ex.getMessage(), ex);
		}
	}

	private void writeoutRequestBody(String data) {
		try {
			try (OutputStreamWriter out = new OutputStreamWriter(httpConnections.get(key).getOutputStream())) {
				out.write(data);
				out.flush();
				out.close();
			}
		} catch (IOException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, ex.getMessage(), ex);
		}
	}

	private void BasicAuthorizationCheck() {
		try {
			if (basicAuthorization.get(key) != null) {
				httpConnections.get(key).setRequestProperty("Authorization",
						"Basic " + new String(Base64.encodeBase64(basicAuthorization.get(key).getBytes())));
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, ex.getMessage(), ex);
		}
	}

	private void httpAgentCheck() {
		try {
			if (getHttpAgentDetails() != null) {
				System.setProperty("http.agent", getHttpAgentDetails());
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, ex.getMessage(), ex);
		}
	}

	private void setRequestMethod(String method) {
		try {
			switch (method) {
			case "POST":
			case "PUT": {
				httpConnections.get(key).setDoOutput(true);
				httpConnections.get(key).setRequestMethod(method);
				break;
			}
			case "GET":
			case "DELETE": {
				httpConnections.get(key).setRequestMethod(method);
				break;
			}

			}
		} catch (ProtocolException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, ex.getMessage(), ex);
		}
	}

	private void createhttpRequest(RequestMethod requestmethod) {
		try {
			httpAgentCheck();
			OpenURLconnection();
			setRequestMethod(requestmethod.toString());
			setheaders();
			BasicAuthorizationCheck();
			starttime.put(key, System.currentTimeMillis());
			if (requestmethod.toString().equals("PUT") || requestmethod.toString().equals("POST")) {
				writeoutRequestBody(handlePayloadorEndpoint(Data));
			}
			returnResponseDetails();
			
			long responsetime = endtime.get(key) - starttime.get(key);
			String responsetimeFormatted  = String.format("%,.2f", responsetime / 1000.0);
			
			Report.updateTestLog(Action, "Status code is : " + responsecodes.get(key), Status.DONE);
			Report.updateTestLog(Action, "Response received in : " + responsetimeFormatted +" seconds", Status.DONE);
		} catch (IOException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.OFF, ex.getMessage(), ex);
			Report.updateTestLog(Action,
					"Error in executing " + requestmethod.toString() + " request : " + "\n" + ex.getMessage(),
					Status.DEBUG);
		} finally {
			httpConnections.get(key).disconnect();
		}
	}
}

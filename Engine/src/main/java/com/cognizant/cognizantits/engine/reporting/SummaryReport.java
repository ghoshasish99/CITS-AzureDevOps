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
package com.cognizant.cognizantits.engine.reporting;

import com.cognizant.cognizantits.engine.constants.AppResourcePath;
import com.cognizant.cognizantits.engine.constants.FilePath;
import com.cognizant.cognizantits.engine.core.Control;
import com.cognizant.cognizantits.engine.core.RunContext;
import com.cognizant.cognizantits.engine.core.RunManager;
import com.cognizant.cognizantits.engine.reporting.impl.html.bdd.Report;
import com.cognizant.cognizantits.engine.reporting.impl.html.bdd.Report.Execution;
import com.cognizant.cognizantits.engine.reporting.impl.html.bdd.Report.Step;
import com.cognizant.cognizantits.engine.core.TMIntegration;
import com.cognizant.cognizantits.engine.reporting.impl.handlers.PrimaryHandler;
import com.cognizant.cognizantits.engine.reporting.impl.handlers.SummaryHandler;
import com.cognizant.cognizantits.engine.reporting.impl.html.HtmlSummaryHandler;
import com.cognizant.cognizantits.engine.reporting.impl.sync.SAPISummaryHandler;
import com.cognizant.cognizantits.engine.reporting.intf.OverviewReport;
import com.cognizant.cognizantits.engine.reporting.performance.har.Har;
import com.cognizant.cognizantits.engine.reporting.sync.Sync;
import com.cognizant.cognizantits.engine.reporting.util.DateTimeUtils;
import com.cognizant.cognizantits.engine.reporting.util.TestInfo;
import com.cognizant.cognizantits.engine.support.Status;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import net.sf.jett.transform.ExcelTransformer;

import org.apache.poi.hssf.util.HSSFColor;

//import net.sf.jett.transform.ExcelTransformer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

public final class SummaryReport implements OverviewReport {

	private static final Logger LOG = Logger.getLogger(SummaryReport.class.getName());

	public boolean RunComplete = false;

	DateTimeUtils RunTime;

	public Sync sync;

	private static final List<SummaryHandler> REPORT_HANDLERS = new ArrayList<>();

	private static final String False = null;

	private static final Execution NULL = null;

	private static final boolean True = false;
	public PrimaryHandler pHandler;

	public SummaryReport() {
		register(new HtmlSummaryHandler(this), true);
		register(new SAPISummaryHandler(this));
	}

	@SuppressWarnings("rawtypes")
	public void addHar(Har<String, Har.Log> h, TestCaseReport report, String pageName) {
		for (SummaryHandler handler : REPORT_HANDLERS) {
			handler.addHar(h, report, pageName);
		}
	}

	/**
	 * initialize the report data file.
	 *
	 * @param runTime
	 * @param size
	 */
	@Override
	public synchronized void createReport(String runTime, int size) {
		for (SummaryHandler handler : REPORT_HANDLERS) {
			handler.createReport(runTime, size);
		}
	}

	/**
	 * update the result of each test case result
	 *
	 * @param runContext
	 * @param report
	 * @param state
	 * @param executionTime
	 */
	@Override
	public synchronized void updateTestCaseResults(RunContext runContext, TestCaseReport report, Status state,
			String executionTime) {
		for (SummaryHandler handler : REPORT_HANDLERS) {
			handler.updateTestCaseResults(runContext, report, state, executionTime);
		}
		if (TMIntegration.isEnabled()) {
			updateTMResults(runContext, report, executionTime, state);
		}
	}

	private void updateTMResults(RunContext runContext, TestCaseReport report, String executionTime, Status state) {
		if (sync != null && sync.isConnected()) {
			System.out.println("[UPLOADING RESULTS TO TEST MANAGEMENT MODULE!!!]");
			TestInfo tc = new TestInfo(runContext.Scenario, runContext.TestCase, runContext.Description,
					runContext.Iteration, executionTime, FilePath.getDate(), FilePath.getTime(), runContext.BrowserName,
					runContext.BrowserVersion, runContext.PlatformValue);
			List<File> attach = new ArrayList<>();
			attach.add(new File(FilePath.getCurrentResultsPath(), report.getFile().getName()));
			/*
			 * create temp. console to avoid error from jira server on sending a open stream
			 */
			// File tmpConsole = createTmpConsole(new File(FilePath.getCurrentResultsPath(),
			// "console.txt"));
			// Optional.ofNullable(tmpConsole).ifPresent(attach::add);
			String prefix = tc.testScenario + "_" + tc.testCase + "_Step-";
			File imgFolder = new File(FilePath.getCurrentResultsPath() + File.separator + "img");
			if (imgFolder.exists()) {
				for (File image : imgFolder.listFiles()) {
					if (image.getName().startsWith(prefix)) {
						attach.add(image);
					}
				}
			}
			String status = state.equals(Status.PASS) ? "Passed" : "Failed";
			if (!sync.updateResults(tc, status, attach)) {
				report.updateTestLog(sync.getModule(), "Unable to Update Results to " + sync.getModule(), Status.DEBUG);
			}
			// Optional.ofNullable(tmpConsole).ifPresent(File::delete);
		} else {
			System.out.println("[ERROR:UNABLE TO REACH TEST MANAGEMENT MODULE!!!]");
			report.updateTestLog("Error", "Unable to Connect to TM Module", Status.DEBUG);
		}
	}

	public File createTmpConsole(File console) {
		try {
			File tmpConsole = File.createTempFile("console", ".txt");
			Files.copy(console.toPath(), tmpConsole.toPath(), StandardCopyOption.REPLACE_EXISTING);
			tmpConsole.deleteOnExit();
			return tmpConsole;
		} catch (IOException ex) {
			return null;
		}
	}

	/**
	 * finalize the summary report creation
	 * 
	 * @throws Exception
	 */
	@Override
	public synchronized void finalizeReport() throws Exception {
		RunComplete = true;
		for (SummaryHandler handler : REPORT_HANDLERS) {
			handler.finalizeReport();
		}
		afterReportComplete();
	}

	/**
	 * update the result when any error in execution
	 *
	 * @param testScenario
	 * @param testCase
	 * @param Iteration
	 * @param testDescription
	 * @param executionTime
	 * @param fileName
	 * @param state
	 * @param Browser
	 */
	@Override
	public void updateTestCaseResults(String testScenario, String testCase, String Iteration, String testDescription,
			String executionTime, String fileName, Status state, String Browser) {
		System.out.println("--------------->[UPDATING SUMMARY]");
		for (SummaryHandler handler : REPORT_HANDLERS) {
			handler.updateTestCaseResults(testScenario, testCase, Iteration, testDescription, executionTime, fileName,
					state, Browser);
		}

	}

	private static Gson gson() {
		return new com.google.gson.GsonBuilder().setPrettyPrinting().create();
	}

	private static Report parseReport(String report) throws Exception {
		return gson().fromJson(report, Report.class);
	}

	public void afterReportComplete() throws Exception {
		
		createjunitReport(FilePath.getLatestResultsLocation() + "/data.js");

		String current_release = RunManager.getGlobalSettings().getRelease();

		String current_testset = RunManager.getGlobalSettings().getTestSet();

		String case_check = Control.getCurrentProject().getProjectSettings()
				.getExecSettings(current_release, current_testset).getRunSettings().getProperty("excelReport");

		if (!RunManager.getGlobalSettings().isTestRun()) {
			if (case_check.equalsIgnoreCase("true")) {

				String openexcel = "cmd /c start excel";
				System.out.println("Latest report path" + FilePath.getLatestResultsLocation());

				String datajspath = FilePath.getLatestResultsLocation() + "\\data.js";

				try {

					File file = new File(datajspath);// Give the location of your data.js file
					String jstr = FileUtils.readFileToString(file).replaceFirst("var DATA=", "");
					String jsonString = jstr.substring(0, jstr.length() - 1);

					Report r = parseReport(jsonString);

					// System.out.println("the release name is " + r.releaseName);

					// System.out.println("the testset name is " + r.testsetName);

					// Write to excel file

					String template = AppResourcePath.getConfigurationPath()
							+ "\\ReportTemplate\\excel\\excelreporttemplate.xlsx";

					String excelreport = FilePath.getLatestResultsLocation() + "\\excelreport.xlsx";
					String excelreport_tm = FilePath.getCurrentResultsPath() + "\\excelreport.xlsx";
					Map<String, Object> beans = new HashMap<String, Object>();
					String releasename = r.releaseName + "-";
					beans.put("releaseName", releasename);
					// System.out.println("saved releasename");
					beans.put("Testsetname", r.testsetName);
					// System.out.println("check this" + r.testsetName);

					ExcelTransformer transformer = new ExcelTransformer();
					/*
					 * FileInputStream excelFile = new FileInputStream(new File(excelreport));
					 * XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
					 */

					// template file name, destination file name, beans
					// System.out.println("entering copy file");
					transformer.transform(template, excelreport, beans);

					FileInputStream excelFile = new FileInputStream(new File(excelreport));
					XSSFWorkbook workbook = new XSSFWorkbook(excelFile);

					ArrayList<ArrayList<String>> listOLists = new ArrayList<ArrayList<String>>();

					int rowIndex = 2;
					r.EXECUTIONS.forEach((Report.Execution e) -> {
						/*
						 * System.out.println(e.getScenarioName()); System.out.println(e.testcaseName);
						 * System.out.println(e.browser); System.out.println(e.getExeTime());
						 * System.out.println(e.getStatus()); System.out.println(e.platform);
						 * System.out.println(e.bversion);
						 */
						ArrayList<String> singleList = new ArrayList<String>();
						// System.out.println("hi no of iteration is");
						// System.out.println(e.STEPS.size());
						XSSFSheet sheet = workbook.createSheet(e.testcaseName);

						XSSFRow row_header = sheet.createRow(1);
						row_header.createCell(1).setCellValue("STEP NO");
						row_header.createCell(2).setCellValue("STEP NAME");
						row_header.createCell(3).setCellValue("ACTION");
						row_header.createCell(4).setCellValue("DESCRIPTION");
						row_header.createCell(5).setCellValue("STATUS");
						row_header.createCell(6).setCellValue("TSTAMP");

						AtomicInteger atomicInteger = new AtomicInteger(2);
						e.STEPS.forEach((Report.IterData i) -> {

							ArrayList<String> step = new ArrayList<String>();
							/*
							 * System.out.println("hi within execution and inside steps");
							 * System.out.println(i.name); System.out.println(i.type);
							 * System.out.println(i.startTime); System.out.println(i.endTime);
							 * System.out.println(i.description);
							 */

							XSSFRow roww = sheet.createRow(atomicInteger.getAndIncrement());
							roww.createCell(1).setCellValue(i.name);

							i.data.forEach((Report.Step s) -> {

								int index = atomicInteger.getAndIncrement();

								// System.out.println("hi within step");

								XSSFRow roww1 = sheet.createRow(index);

								if (s.data instanceof LinkedTreeMap) {

									LinkedTreeMap map = (LinkedTreeMap) s.data;

									/*
									 * System.out.println(map.get("stepno"));
									 * System.out.println(map.get("stepName"));
									 * System.out.println(map.get("action"));
									 * System.out.println(map.get("description"));
									 * System.out.println(map.get("status")); System.out.println(map.get("tStamp"));
									 */

									step.add(Double.toString((Double) map.get("stepno")));
									step.add((String) map.get("stepName"));
									step.add((String) map.get("action"));
									step.add((String) map.get("description"));
									step.add((String) map.get("status"));
									step.add((String) map.get("tStamp"));

									roww1.createCell(1).setCellValue(Double.toString((Double) map.get("stepno")));
									roww1.createCell(2).setCellValue((String) map.get("stepName"));
									roww1.createCell(3).setCellValue((String) map.get("action"));
									roww1.createCell(4).setCellValue((String) map.get("description"));
									roww1.createCell(5).setCellValue((String) map.get("status"));
									roww1.createCell(6).setCellValue((String) map.get("tStamp"));

								}

							});

						});

						singleList.add(e.getScenarioName());
						singleList.add(e.testcaseName);
						singleList.add(e.browser);
						singleList.add(e.getExeTime());
						singleList.add(e.getStatus());
						singleList.add(e.platform);
						singleList.add(e.iterationType);
						singleList.add(e.bversion);
						listOLists.add(singleList);

					});

					// System.out.println("The size is " + listOLists.size());

					// FileInputStream excelFile = new FileInputStream(new File(excelreport));
					// XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
					XSSFSheet sheet = workbook.getSheetAt(0);

					Iterator<ArrayList<String>> iterator = listOLists.iterator();
					int row = 2;
					while (iterator.hasNext()) {
						ArrayList singleList = iterator.next();
						Iterator<String> childiter = singleList.iterator();
						int i = 1;
						XSSFRow roww = sheet.createRow(row);
						while (childiter.hasNext()) {
							String s = childiter.next();
							// System.out.println(s + "value of r and i is" + row + i);

							roww.createCell(i).setCellValue(s);
							i++;
						}
						row++;

					}

					FileOutputStream outputStream = new FileOutputStream(excelreport);
					workbook.write(outputStream);
					FileOutputStream outputStreamrp = new FileOutputStream(excelreport_tm);
					workbook.write(outputStreamrp);

					launchexcel();
				}

				catch (IOException e) {
					System.err.println("IOException caught: " + e.getMessage());
				}
				
			}
		}
	}

	public void launchexcel() throws IOException {
		String excelreport = FilePath.getLatestResultsLocation() + "\\excelreport.xlsx";
		try {
			Runtime.getRuntime().exec("cmd /c start excel \"" + excelreport + "\"");
		} catch (Exception E) {
			System.out.println("Make sure Excel location is added to system path" + E.getMessage());
		}
	}

	public Boolean isPassed() {
		return !pHandler.getCurrentStatus().equals(Status.FAIL);
	}

	public static void register(SummaryHandler summaryHandler) {
		if (!REPORT_HANDLERS.contains(summaryHandler)) {
			REPORT_HANDLERS.add(summaryHandler);
		}
	}

	public static void reset() {
		REPORT_HANDLERS.clear();
	}

	private void register(SummaryHandler summaryHandler, boolean primaryHandler) {
		register(summaryHandler);
		if (primaryHandler) {
			pHandler = (PrimaryHandler) summaryHandler;
		}
	}
	
	static void createjunitReport(String datajspath) throws IOException, ParseException, java.text.ParseException {
		File datajs = new File(datajspath);
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n");
		sb.append("<testsuites name=\"" + getsuitesName(datajs) + "\" tests=\"" + getTotalTests(datajs)
				+ "\" failures=\"" + getfailed(datajs) + "\" time=\"" + getTotalexetime(datajs) + "\">" + "\n");
		getTestCases(datajs, sb);
		sb.append("</testsuites>");
		
		File file = new File(FilePath.getLatestResultsLocation() + "/junit.xml");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
		    writer.write(sb.toString());
		}

	}

	static String getfailed(File datajs) throws IOException, ParseException {
		String str = "";
		if (datajs.exists()) {
			String jstr = FileUtils.readFileToString(datajs).replaceFirst("var DATA=", "");
			str = jstr.substring(0, jstr.length() - 1);
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(str);
			Object failed = json.get("nofailTests").toString();
			str = (String) failed;
		}
		return str;
	}

	static String getTotalexetime(File datajs) throws IOException, ParseException, java.text.ParseException {
		String str = "";
		if (datajs.exists()) {
			String jstr = FileUtils.readFileToString(datajs).replaceFirst("var DATA=", "");
			str = jstr.substring(0, jstr.length() - 1);
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(str);
			Object start = json.get("startTime").toString();
			Object end = json.get("endTime").toString();
			SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS");

			Date date1 = format.parse((String) start);
			Date date2 = format.parse((String) end);
			long diff = (date2.getTime() - date1.getTime()) / 1000;
			str = Long.toString(diff);
		}
		return str;
	}

	static String getTotalTests(File datajs) throws IOException, ParseException {
		String str = "";
		if (datajs.exists()) {
			String jstr = FileUtils.readFileToString(datajs).replaceFirst("var DATA=", "");
			str = jstr.substring(0, jstr.length() - 1);
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(str);
			Object tests = json.get("noTests").toString();
			str = (String) tests;
		}
		return str;
	}

	static String getsuitesName(File datajs) throws IOException, ParseException {
		String str = "";
		if (datajs.exists()) {
			String jstr = FileUtils.readFileToString(datajs).replaceFirst("var DATA=", "");
			str = jstr.substring(0, jstr.length() - 1);
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(str);
			Object relName = json.get("releaseName");
			str = (String) relName;
		}
		return str;
	}

	static void getTestCases(File datajs, StringBuilder sb)
			throws IOException, ParseException, java.text.ParseException {
		String str = "";
		if (datajs.exists()) {
			String jstr = FileUtils.readFileToString(datajs).replaceFirst("var DATA=", "");
			str = jstr.substring(0, jstr.length() - 1);
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(str);
			JSONArray exec = (JSONArray) json.get("EXECUTIONS");
			SimpleDateFormat targetformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat sourceformat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS");

			for (int i = 0; i < exec.size(); i++) {
				JSONObject objects = (JSONObject) exec.get(i);
				Date date1 = sourceformat.parse((String) objects.get("startTime"));
				Date date2 = sourceformat.parse((String) objects.get("endTime"));

				long diff = (date2.getTime() - date1.getTime()) / 1000;
				String exetime = Long.toString(diff);
				sb.append("<testsuite name=\"" + "Scenario : " + objects.get("scenarioName") + ", Test Case : "
						+ objects.get("testcaseName") + "\" id=\"" + UUID.randomUUID() + "\" timestamp=\""
						+ targetformat.format(date1) + "\" tests=\"" + objects.get("noTests") + "\" failures=\""
						+ objects.get("nofailTests") + "\" errors=\"0\" time=\"" + exetime + "\">" + "\n");
				sb = getTestSteps(datajs, sb, objects.get("scenarioName").toString(),
						objects.get("testcaseName").toString());
				sb.append("</testsuite>" + "\n");
			}

		}
	}

	static StringBuilder getTestSteps(File datajs, StringBuilder sb, String scenario, String testcase)
			throws IOException, ParseException {
		String str = "";
		if (datajs.exists()) {
			String jstr = FileUtils.readFileToString(datajs).replaceFirst("var DATA=", "");
			str = jstr.substring(0, jstr.length() - 1);
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(str);
			JSONArray exec = (JSONArray) json.get("EXECUTIONS");

			for (int i = 0; i < exec.size(); i++) {
				JSONObject objects = (JSONObject) exec.get(i);
				if (objects.get("scenarioName").equals(scenario) && objects.get("testcaseName").equals(testcase)) {
					JSONArray steps = (JSONArray) objects.get("STEPS");
					JSONObject stepdata1 = (JSONObject) steps.get(0);
					JSONArray stepdata = (JSONArray) stepdata1.get("data");
					for (int j = 0; j < stepdata.size(); j++) {
						JSONObject object = (JSONObject) stepdata.get(j);
						if (object.get("type").equals("reusable")) {
							JSONArray reusabledetails = (JSONArray) object.get("data");
							JSONObject reusabledetails1 = (JSONObject) reusabledetails.get(0);
							JSONObject reusabledetails2 = (JSONObject) reusabledetails1.get("data");
							if (reusabledetails2.get("status").toString().equals("PASS")
									|| reusabledetails2.get("status").toString().equals("DONE"))
								sb.append("<testcase name=\"" + reusabledetails2.get("stepName") + " : "
										+ reusabledetails2.get("description").toString().replace("<", "&lt;")
												.replace(">", "&gt;")
										+ "\" time=\"" + reusabledetails2.get("tStamp") + "\" classname=\"Scenario : "
										+ scenario + ", Test Case : " + testcase + "\"/>" + "\n");
							else {
								sb.append("<testcase name=\"" + reusabledetails2.get("stepName") + "\" time=\""
										+ reusabledetails2.get("tStamp") + "\" classname=\"Scenario : " + scenario
										+ ", Test Case : " + testcase + "\">" + "\n");
								sb.append("<failure type=\"Step Level Failure\" message=\"" + reusabledetails2
										.get("description").toString().replace("<", "&lt;").replace(">", "&gt;")
										+ "\">");
								sb.append("</failure>" + "\n");
								sb.append("</testcase>" + "\n");
							}
						} else if (object.get("type").equals("step")) {
							JSONObject stepdetails = (JSONObject) object.get("data");
							if (stepdetails.get("status").toString().equals("PASS")
									|| stepdetails.get("status").toString().equals("DONE"))
								sb.append("<testcase name=\"" + stepdetails.get("stepName") + " : "
										+ stepdetails.get("description").toString().replace("<", "&lt;").replace(">",
												"&gt;")
										+ "\" time=\"" + stepdetails.get("tStamp") + "\" classname=\"Scenario : "
										+ scenario + ", Test Case : " + testcase + "\"/>" + "\n");
							else {
								sb.append("<testcase name=\"" + stepdetails.get("stepName") + "\" time=\""
										+ stepdetails.get("tStamp") + "\" classname=\"Scenario : " + scenario
										+ ", Test Case : " + testcase + "\">" + "\n");
								sb.append("<failure type=\"Step Level Failure\" message=\"" + stepdetails
										.get("description").toString().replace("<", "&lt;").replace(">", "&gt;")
										+ "\">");
								sb.append("</failure>" + "\n");
								sb.append("</testcase>" + "\n");
							}
						}
					}
					break;
				}
			}

		}
		return sb;
	}
	

}

package com.example.tutorhelpplugin.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.example.tutorhelpplugin.launching.TutorPluginLogTracker;
import com.example.tutorhelpplugin.splashHandlers.InteractiveSplashHandler;

public class AssignmentQuestionsViewClient {

	/**
	 * Get Assignments using API 
	 * @throws IOException
	 * @throws ParseException 
	 */
	public void getAssignmentsClient() throws IOException, ParseException {
		
		String directoryPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + 
									File.separator + "AssignmentList_Cosmo_Client";
		
		// Local machine URL
		URL url = new URL("http://localhost:8080/assignments");
		// Manohar AWS URL
		//URL url = new URL("http://34.224.41.66:8080/assignments");
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String output;
		JSONArray jObjList = new JSONArray();
		int jsonCount = 0;
		
		while ((output = br.readLine()) != null) {
			jObjList = (JSONArray) new JSONTokener(output).nextValue();
		}
		
		if (jObjList != null && jObjList.length() > 0) {
			while (jsonCount < jObjList.length()) {
				JSONObject jObj = jObjList.getJSONObject(jsonCount);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				
				String assignmentName = jObj.getString("assignmentName");
				String courseName = jObj.getString("courseName");
				String tempStartDate = jObj.getString("startDate");
				Date startDate = sdf.parse(tempStartDate);
				String tempEndDate = jObj.getString("endDate");
				Date endDate = sdf.parse(tempEndDate);
				String fileType = jObj.getString("fileType");
				JSONArray codeFile = jObj.getJSONArray("codeFile");
				
				Date currentDate = new Date();
				
				String fileExtension = ".java";
				if (fileType.equals("JAVA")) {
					fileExtension = ".java";
				}
				else if (fileType.equals("TEXT")) {
					fileExtension = ".txt";
				}
				
				if (!assignmentName.equals("Assignment00") && (currentDate.equals(startDate) || currentDate.after(startDate)) 
						&& (currentDate.before(endDate) || currentDate.equals(endDate))) {
					File temp1 = new File(directoryPath + File.separator + assignmentName + fileExtension);
					if (!temp1.exists() || temp1.isDirectory()) {
						try (FileWriter file = new FileWriter(directoryPath + File.separator + assignmentName + fileExtension)) {
							String str = "";
							for (int count = 0; count < codeFile.length(); count++) {
								str += (codeFile.getString(count));
								str += System.lineSeparator();
							}
							file.write(str);
							file.close();
							//System.out.println("Successfully Copied JSON Object to File...");
							//System.out.println("\nJSON Object: " + jObj);
						}
					}
					else {
						System.out.println("File "+ assignmentName + fileExtension +" Already Exists. So did not replace.");
					}
					
					File temp2 = new File(directoryPath + File.separator + assignmentName + "_" + courseName + "_" + "metadata" + ".txt");
					if (!temp2.exists() || temp2.isDirectory()) {
						try (FileWriter file = new FileWriter(directoryPath + File.separator + 
																assignmentName + "_" + courseName + "_" + "metadata" + ".txt")) {
							String str = "AssignmentName--"+assignmentName+System.lineSeparator();
							str += "CourseName--"+courseName+System.lineSeparator();
							str += "StartDate--"+tempStartDate+System.lineSeparator();
							str += "EndDate--"+tempEndDate+System.lineSeparator();
							file.write(str);
							file.close();
							//System.out.println("Successfully Copied JSON Object to File...");
							//System.out.println("\nJSON Object: " + jObj);
						}
					}
					else {
						System.out.println("File "+ assignmentName + "_" + courseName + "_" + "metadata" + ".txt" +" Already Exists. So did not replace.");
					}
				}
				
				jsonCount++;
			}
		}
		
		conn.disconnect();
	}
	
	/**
	 * Send log using API
	 * @throws IOException
	 * @throws ParseException 
	 */
	public void sendLogClient(List<String> lines) throws IOException, ParseException {
		String directoryPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + 
				File.separator + "AssignmentList_Cosmo_Client";

		String studentId = InteractiveSplashHandler.login_userName;
		String courseName = InteractiveSplashHandler.login_courseName;
		String assignmentName = TutorPluginLogTracker.assignmentName;
		List<String> outputFile = lines;
		
		// For local test
	    studentId = "1111111119";
	    courseName = "CSE360";
		
		File metaDataFile = new File(directoryPath + File.separator + assignmentName + "_" + courseName + "_" + "metadata" + ".txt");
		if (metaDataFile.exists() && !metaDataFile.isDirectory()) {
			List<String> metaData = Files.readAllLines(metaDataFile.toPath());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date startDate = sdf.parse(metaData.get(2).split("--")[1]);
			Date endDate = sdf.parse(metaData.get(3).split("--")[1]);
			Date currentDate = new Date();
			
			if ((currentDate.equals(startDate) || currentDate.after(startDate)) &&
					(currentDate.equals(endDate) || currentDate.before(endDate))) 
			{
				JSONObject jObj = new JSONObject();
				jObj.put("studentId", studentId);
				jObj.put("courseName", courseName);
				jObj.put("assignmentName", assignmentName);
				jObj.put("outputFile", outputFile);
				
				// Local machine URL
				URL url = new URL("http://localhost:8080/assignmentResults");
				// Manohar AWS URL
				//URL url = new URL("http://34.224.41.66:8080/assignmentResults");
				
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");

				String input = jObj.toString();
				
				OutputStream os = conn.getOutputStream();
				os.write(input.getBytes());
				os.flush();

				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					throw new RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode());
				}
				conn.disconnect();
			}
		}
		else {
			System.out.println("File "+ assignmentName + "_" + courseName + "_" + "metadata" + ".txt" +" does not Exists.");
			System.out.println("Cannot send info to server.");
		}
	}
	
}

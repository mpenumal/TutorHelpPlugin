package com.example.tutorhelpplugin.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
	 */
	public void getAssignmentsClient() throws IOException {
		
		String directoryPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + 
									File.separator + "AssignmentList_Cosmo_Client";
		
		// Local machine URL
		//URL url = new URL("http://localhost:8080/assignments");
		// Manohar AWS URL
		URL url = new URL("http://34.224.41.66:8080/assignments");
		
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
				String name = jObj.getString("name");
				boolean show = jObj.getBoolean("show");
				String language = jObj.getString("language");
				JSONArray codeFile = jObj.getJSONArray("codeFile");
				
				String fileExtension = ".java";
				if (language.equals("JAVA")) {
					fileExtension = ".java";
				}
				else if (language.equals("TEXT")) {
					fileExtension = ".txt";
				}
				
				File temp = new File(directoryPath + File.separator + name + fileExtension);
				
				if (!name.equals("Assignment00") && show) {
					if (!temp.exists() || temp.isDirectory()) {
						try (FileWriter file = new FileWriter(directoryPath + File.separator + name + fileExtension)) {
							String str = "";
							for (int count = 0; count < codeFile.length(); count++) {
								str += (codeFile.getString(count));
								str += System.lineSeparator();
							}
							file.write(str);
							//System.out.println("Successfully Copied JSON Object to File...");
							//System.out.println("\nJSON Object: " + jObj);
						}
					}
					else {
						System.out.println("File Already Exists. So did not replace.");
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
	 */
	public void sendLogClient(List<String> lines) throws IOException {
		String studentId = InteractiveSplashHandler.login_userName;
		String courseName = InteractiveSplashHandler.login_courseName;
		String assignmentName = TutorPluginLogTracker.assignmentName;
		
		List<String> outputFile = lines;
		
	    // For local test
	    //studentId = "1234567890";
	    //courseName = "CSE360";
	    
		JSONObject jObj = new JSONObject();
		jObj.put("studentId", studentId);
		jObj.put("courseName", courseName);
		jObj.put("assignmentName", assignmentName);
		jObj.put("outputFile", outputFile);
		
		// Local machine URL
		//URL url = new URL("http://localhost:8080/assignmentResults");
		// Manohar AWS URL
		URL url = new URL("http://34.224.41.66:8080/assignmentResults");
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");

		String input = jObj.toString();
		
		OutputStream os = conn.getOutputStream();
		os.write(input.getBytes());
		os.flush();

		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new RuntimeException("Failed : HTTP error code : "
				+ conn.getResponseCode());
		}

		conn.disconnect();
	}
	
}

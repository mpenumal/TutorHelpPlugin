package com.example.tutorhelpplugin.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.ResourcesPlugin;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.example.tutorhelpplugin.splashHandlers.InteractiveSplashHandler;

public class SampleViewClient {
	// Get Assignments using Socket
	/*
	public void getAssignmentsClient() throws Exception {  
		String address = null;
		address = "192.168.0.114";  // home
		// address = "10.143.13.106";  // ASU
		String directoryPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + File.separator + "AssignmentList_Client";
		
		//create the socket on port 5000
		Socket s=new Socket(address,5000);  
		
		BufferedOutputStream bos2 = new BufferedOutputStream(s.getOutputStream());
		DataOutputStream dout2 = new DataOutputStream(bos2);
		
		dout2.writeUTF("Assignment");
		dout2.flush();
		
		BufferedInputStream bis = new BufferedInputStream(s.getInputStream());
		DataInputStream din = new DataInputStream(bis);
		
		try {
			int assignmentCount = din.readInt();
			File[] assignmentList = new File[assignmentCount];
			
			for(int i = 0; i < assignmentCount; i++)
			{
				long sz = din.readLong();
				String filename = din.readUTF();
				
				System.out.println("Receving file: "+filename);
				
				File temp = new File(directoryPath + File.separator + filename);
				
				if (temp.exists() && !temp.isDirectory()) {
					dout2.writeUTF("present");
					dout2.flush();
				}
				else {
					dout2.writeUTF("absent");
					dout2.flush();
					
					assignmentList[i] = new File(directoryPath + "/" + filename);
					System.out.println("Saving as file: "+assignmentList[i]);
					
					FileOutputStream fos = new FileOutputStream(assignmentList[i]);
					BufferedOutputStream bos = new BufferedOutputStream(fos);

					for(int j = 0; j < sz; j++) bos.write(bis.read());
					bos.close();
				}
				
				System.out.println("Completed");
			}
			din.close();
			s.close();
		}
		catch(EOFException e)
		{
			e.printStackTrace();
			System.out.println("An error occured in the SampleViewClient class.");
		}
	}
	*/
	
	// Get Assignments using API 
	public void getAssignmentsClient() throws IOException {
		
		String directoryPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + 
									File.separator + "AssignmentList_Client";
		
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
							System.out.println("Successfully Copied JSON Object to File...");
							System.out.println("\nJSON Object: " + jObj);
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
	
	// Send Log using Socket
	/*
	public void sendLogClient() throws Exception {
		String address = null;
		address = "192.168.0.114";  // home
		//address = "10.143.13.106";  // ASU
		String directoryPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + File.separator + "ASULog_Client";
		File[] logList = new File(directoryPath).listFiles();
		String filename = logList[logList.length-1].getName();
		
		File log = new File(directoryPath + File.separator + filename);
		
		//create the socket on port 5000
		Socket s=new Socket(address,5000);
		
		BufferedOutputStream bos = new BufferedOutputStream(s.getOutputStream());
		DataOutputStream dout = new DataOutputStream(bos);
		
		dout.writeUTF("Log");
		dout.flush();
		
		try {
			long sz = log.length();
			dout.writeLong(sz); 
			dout.flush();
			
			System.out.println("Sending File: "+log.getName());
			
			dout.writeUTF(log.getName());
			dout.flush();
			
			FileInputStream fis = new FileInputStream(log);
			BufferedInputStream bis = new BufferedInputStream(fis);
			int theByte = 0;
			while((theByte = bis.read()) != -1) bos.write(theByte);

			bis.close();
			System.out.println("..ok"); 				 
				
			System.out.println("Send Complete");
			dout.flush();
			dout.close();
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("An error occured in the ASULog function in SampleViewClient.");
		}
		s.close();	
	}
	*/
	
	// Send log using API
	public void sendLogClient(boolean isAttempt) throws IOException {
		String directoryPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + 
								File.separator + "ASULog_Client";
		File[] logList = new File(directoryPath).listFiles();
		String filename = logList[logList.length-1].getName();
		// File log = new File(directoryPath + File.separator + filename);
		
		String studentId = InteractiveSplashHandler.login_username;
		String assignmentName = filename.replace(".txt", "");
	    int attemptCount = isAttempt ? 1 : 0;
		List<String> outputFile = null;
	    
	    Path s = Paths.get(directoryPath + File.separator + filename);
	    
	    try (Stream<String> lines = Files.lines(s)) {
	    	outputFile = lines.collect(Collectors.toList());
	    } catch (IOException e) {
	        System.out.println("Failed to load file. " + e);
	    }
		
		JSONObject jObj = new JSONObject();
		jObj.put("studentId", studentId);
		jObj.put("assignmentName", assignmentName);
		jObj.put("attemptCount", attemptCount);
		jObj.put("outputFile", outputFile);
		
		// Local machine URL
		//URL url = new URL("http://localhost:8080/assignmentResults");
		//Manohar AWS URL
		URL url = new URL("http://34.224.41.66:8080/assignmentResults");
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");

		String input = jObj.toString();
		
		System.out.println(input);
		
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

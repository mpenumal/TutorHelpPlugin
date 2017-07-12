package com.example.tutorhelpplugin.views;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;

import org.eclipse.core.resources.ResourcesPlugin;

public class SampleViewClient {  
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
}

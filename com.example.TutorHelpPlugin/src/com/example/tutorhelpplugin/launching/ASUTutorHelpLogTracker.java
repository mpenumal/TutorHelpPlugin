package com.example.tutorhelpplugin.launching;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.example.tutorhelpplugin.views.SampleViewClient;

public class ASUTutorHelpLogTracker implements IConsoleLineTracker {

	private IConsole m_console;
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IConsole console) {
		m_console = console;
		
	}

	@Override
	public void lineAppended(IRegion region) {
            try {
            	String line = m_console.getDocument().get(region.getOffset(), region.getLength());
            	long currentTime = System.currentTimeMillis() / 1000;
            	String separator = System.getProperty("line.separator");
            	line += separator+separator+"Run_Action_Status: Time = "+currentTime+separator+separator;
            	writeToLogFile(line);
			    
			    SampleViewClient svc = new SampleViewClient();
			    svc.sendLogClient();
				
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public static void writeToLogFile(String line) throws BadLocationException, IOException {
		String directoryPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + File.separator + "ASULog_Client";
		String fileName = "ASULog.txt";
		
		String projectPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		File projectParent = new File(projectPath);
		File[] listOfFolders = projectParent.listFiles();
		ArrayList<String> folderNames = new ArrayList<>();
		
		for (int i = 0; i < listOfFolders.length; i++) {
			if (listOfFolders[i].isDirectory()) {
				folderNames.add(listOfFolders[i].getName());
			}
		}
		
		for (String name : folderNames) {
			if (name.contains("Assignment") && !name.contains("Client")) {
				fileName = name.replace("Assignment", "") + ".txt";
			}
		}
		
		String separator = System.getProperty("line.separator");
		
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdir();
		}
		
		File file = new File(directoryPath + File.separator + fileName);
						
		FileWriter fw = new FileWriter(file, true); //the true will append the new data.
		fw.write(line+separator+separator);//appends the string to the file
		fw.close();
	}
}

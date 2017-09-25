package com.example.tutorhelpplugin.launching;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;

import com.example.tutorhelpplugin.views.AssignmentQuestionsViewClient;

public class TutorPluginLogTracker implements IConsoleLineTracker {

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
            	writeToLogFile(line);
			    
            	AssignmentQuestionsViewClient svc = new AssignmentQuestionsViewClient();
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
						
		FileWriter fw = new FileWriter(file, false); //the false will not append but replace the data.
		fw.write(line+separator);//appends the string to the file
		fw.close();
	}
}

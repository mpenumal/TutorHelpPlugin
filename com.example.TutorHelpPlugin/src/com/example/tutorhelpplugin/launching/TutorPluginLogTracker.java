package com.example.tutorhelpplugin.launching;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;

import com.example.tutorhelpplugin.views.AssignmentQuestionsViewClient;

public class TutorPluginLogTracker implements IConsoleLineTracker {

	private IConsole m_console;
	public static String assignmentName;
	
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
            	line = line.trim();
            	
            	if (!line.equals("")) {
            		List<String> lines = Collections.singletonList(line.trim()); 
                	
                	AssignmentQuestionsViewClient svc = new AssignmentQuestionsViewClient();
    			    svc.sendLogClient(lines);
            	}
				
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
}

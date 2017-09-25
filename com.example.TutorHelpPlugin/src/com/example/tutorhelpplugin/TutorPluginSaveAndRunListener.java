package com.example.tutorhelpplugin;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.example.tutorhelpplugin.launching.TutorPluginLogTracker;
import com.example.tutorhelpplugin.views.AssignmentQuestionsViewClient;

/**
 * 
 * @source https://github.com/wakatime/eclipse-wakatime
 *
 */
public class TutorPluginSaveAndRunListener implements IExecutionListener {
	
	 @Override
	 public void notHandled(String commandId, NotHandledException exception) {
	        // TODO Auto-generated method stub
	 }
	 @Override
	 public void postExecuteFailure(String commandId,
	            ExecutionException exception) {
	        // TODO Auto-generated method stub
	 }
	 @Override
	 public void postExecuteSuccess(String commandId, Object returnValue) {
		 String actionPerformed = "";
		 if (commandId.equals("org.eclipse.ui.file.save") ||
				 commandId.equals("org.eclipse.jdt.debug.ui.localJavaShortcut.run") ||
				 commandId.equals("org.eclipse.debug.ui.commands.RunLast")) {
	         IWorkbench workbench = PlatformUI.getWorkbench();
	         if (workbench == null) return;
	         IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	         if (window == null) return;
	         if (window.getPartService() == null) return;
	         if (window.getPartService().getActivePart() == null) return;
	         if (window.getPartService().getActivePart().getSite() == null) return;
	         if (window.getPartService().getActivePart().getSite().getPage() == null) return;
	         

	         String separator = System.getProperty("line.separator");
	         DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	         DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	         Date date = new Date();
	         
	         if (commandId.equals("org.eclipse.ui.file.save")) {
		    		actionPerformed = "Save_Action";
		    		if (window.getPartService().getActivePart().getSite().getPage().getActiveEditor() == null) return;
			        if (window.getPartService().getActivePart().getSite().getPage().getActiveEditor().getEditorInput() == null) return;
		    		// log file save operation
		            IEditorInput input = window.getPartService().getActivePart().getSite().getPage().getActiveEditor().getEditorInput();
		            if (input instanceof IURIEditorInput) {
		                URI uri = ((IURIEditorInput)input).getURI();
		                if (uri != null && uri.getPath() != null) {
		                    String currentFile = uri.getPath();
		                    try {
		                    	TutorPluginLogTracker.writeToLogFile(separator+separator+actionPerformed+separator+"FileName: "+
		                    			currentFile+separator+"Date: "+dateFormat.format(date)+separator+
		                    			"Time: "+timeFormat.format(date)+separator+separator);
		        			    
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
		            }
	         }
		     else if (commandId.equals("org.eclipse.jdt.debug.ui.localJavaShortcut.run") ||
		    		 commandId.equals("org.eclipse.debug.ui.commands.RunLast")) {
		    	 actionPerformed = "Run_Action";
		    	 // log file run operation
		    	 try {
		    		 TutorPluginLogTracker.writeToLogFile(separator+separator+actionPerformed+separator+
		    				 "Date: "+dateFormat.format(date)+separator+"Time: "+
		    				 timeFormat.format(date)+separator+separator);
			    
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
        }
	 }
	 
	 @Override
	 public void preExecute(String commandId, ExecutionEvent event) {
		 // TODO Auto-generated method stub
	 }
}

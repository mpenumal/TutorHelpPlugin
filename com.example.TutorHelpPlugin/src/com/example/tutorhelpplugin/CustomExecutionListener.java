package com.example.tutorhelpplugin;

import java.io.IOException;
import java.net.URI;

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

import com.example.tutorhelpplugin.launching.ASUTutorHelpLogTracker;
import com.example.tutorhelpplugin.views.SampleViewClient;

/**
 * 
 * @author manoh
 * @source https://github.com/wakatime/eclipse-wakatime
 *
 */
public class CustomExecutionListener implements IExecutionListener {
	
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
	    	System.out.println(commandId);
	        if (commandId.equals("org.eclipse.ui.file.save") ||
	        		commandId.equals("org.eclipse.jdt.debug.ui.localJavaShortcut.run")) {
	            IWorkbench workbench = PlatformUI.getWorkbench();
	            if (workbench == null) return;
	            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	            if (window == null) return;

	            if (window.getPartService() == null) return;
	            if (window.getPartService().getActivePart() == null) return;
	            if (window.getPartService().getActivePart().getSite() == null) return;
	            if (window.getPartService().getActivePart().getSite().getPage() == null) return;
	            if (window.getPartService().getActivePart().getSite().getPage().getActiveEditor() == null) return;
	            if (window.getPartService().getActivePart().getSite().getPage().getActiveEditor().getEditorInput() == null) return;

	            // log file save event
	            IEditorInput input = window.getPartService().getActivePart().getSite().getPage().getActiveEditor().getEditorInput();
	            if (input instanceof IURIEditorInput) {
	                URI uri = ((IURIEditorInput)input).getURI();
	                if (uri != null && uri.getPath() != null) {
	                    String currentFile = uri.getPath();
	                    long currentTime = System.currentTimeMillis() / 1000;
	                    
	                    try {
	                    	String separator = System.getProperty("line.separator");
	                    	ASUTutorHelpLogTracker.writeToLogFile("Execution_Action_Status: Filename = "+currentFile+", Time = "+currentTime+separator+separator);
	        			    
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
	            }
	        }
	    }

	    @Override
	    public void preExecute(String commandId, ExecutionEvent event) {
	        // TODO Auto-generated method stub

	    }
	
}
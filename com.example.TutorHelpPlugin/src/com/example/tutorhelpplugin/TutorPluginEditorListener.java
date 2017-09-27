package com.example.tutorhelpplugin;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Control;

import com.example.tutorhelpplugin.launching.TutorPluginLogTracker;
import com.example.tutorhelpplugin.views.AssignmentQuestionsViewClient;

/**
 * 
 * @source https://github.com/wakatime/eclipse-wakatime
 *
 */
public class TutorPluginEditorListener implements IPartListener2 {

	@Override
	public void partActivated(IWorkbenchPartReference partReference) {
		IEditorPart part = partReference.getPage().getActiveEditor();
        if (!(part instanceof AbstractTextEditor))
            return;
        
        // log new active file
        IEditorInput input = part.getEditorInput();
        if (input instanceof IURIEditorInput) {
            URI uri = ((IURIEditorInput)input).getURI();
            if (uri != null && uri.getPath() != null) {
                String currentFile = uri.getPath();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            	DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            	Date date = new Date();
                
                try {
                	List<String> lines = Arrays.asList("", "", "Editor_Action", "FileName: "+currentFile, 
                						"Date: "+dateFormat.format(date), "Time: "+timeFormat.format(date), "", "");
                	
                	String temp = currentFile.split("/src")[0];
                	String[] folderNamesInWorkspace = temp.split("/");
                	String assignmentName = folderNamesInWorkspace[folderNamesInWorkspace.length-1];
                	
                	TutorPluginLogTracker.assignmentName = assignmentName;
                	
    			    AssignmentQuestionsViewClient svc = new AssignmentQuestionsViewClient();
    			    svc.sendLogClient(lines);
    				
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
	public void partBroughtToTop(IWorkbenchPartReference arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void partClosed(IWorkbenchPartReference arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void partHidden(IWorkbenchPartReference arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void partOpened(IWorkbenchPartReference partReference) {
		// listen for caret movement
        try {
            AbstractTextEditor editor = (AbstractTextEditor)((IEditorReference) partReference).getEditor(false);
            
            // Verify Listener
            ((StyledText)editor.getAdapter(Control.class)).addVerifyListener(new VerifyListener() {
        		public void verifyText(VerifyEvent event) {
        			
        			System.out.println(event.text.trim());
        			if (event.text != null && event.text.trim() != "" && event.text.trim().length() > 200) {
        				IEditorPart part = partReference.getPage().getActiveEditor();
            	        if (!(part instanceof AbstractTextEditor))
            	            return;
            	        
            	        // log new active file
            	        IEditorInput input = part.getEditorInput();
            	        if (input instanceof IURIEditorInput) {
            	            URI uri = ((IURIEditorInput)input).getURI();
            	            if (uri != null && uri.getPath() != null) {
            	                String currentFile = uri.getPath();
            	                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            	            	DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            	            	Date date = new Date();
            	                
            	                try {
            	                	List<String> lines = Arrays.asList("", "", "Paste_Action", "FileName: "+currentFile, 
                    						"Date: "+dateFormat.format(date), "Time: "+timeFormat.format(date), "", "");
            	                	
            	                	String temp = currentFile.split("/src")[0];
            	                	String[] folderNamesInWorkspace = temp.split("/");
            	                	String assignmentName = folderNamesInWorkspace[folderNamesInWorkspace.length-1];
            	                	
            	                	TutorPluginLogTracker.assignmentName = assignmentName;
            	    			    
            	    			    AssignmentQuestionsViewClient svc = new AssignmentQuestionsViewClient();
            	    			    svc.sendLogClient(lines);
            	    				
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
        	});
        } 
        catch (Exception e) {
        }
	}

	@Override
	public void partVisible(IWorkbenchPartReference arg0) {
		// TODO Auto-generated method stub
	}
	
}

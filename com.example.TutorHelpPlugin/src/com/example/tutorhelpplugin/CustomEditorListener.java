package com.example.tutorhelpplugin;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.example.tutorhelpplugin.launching.ASUTutorHelpLogTracker;
import com.example.tutorhelpplugin.views.SampleViewClient;

/**
 * 
 * @author manoh
 * @source https://github.com/wakatime/eclipse-wakatime
 *
 */
public class CustomEditorListener implements IPartListener2 {

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		IEditorPart part = partRef.getPage().getActiveEditor();
        if (!(part instanceof AbstractTextEditor))
            return;

        // log new active file
        IEditorInput input = part.getEditorInput();
        if (input instanceof IURIEditorInput) {
            URI uri = ((IURIEditorInput)input).getURI();
            if (uri != null && uri.getPath() != null) {
                String currentFile = uri.getPath();
                //long currentTime = System.currentTimeMillis() / 1000;
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            	DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            	Date date = new Date();
                
                try {
                	String separator = System.getProperty("line.separator");
                	ASUTutorHelpLogTracker.writeToLogFile(separator+separator+"Editor_Action"+separator+"FileName: "+
                			currentFile+separator+"Date: "+dateFormat.format(date)+separator+
                			"Time: "+timeFormat.format(date)+separator+separator);
    			    
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
	public void partOpened(IWorkbenchPartReference arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void partVisible(IWorkbenchPartReference arg0) {
		// TODO Auto-generated method stub
		
	}
	
}

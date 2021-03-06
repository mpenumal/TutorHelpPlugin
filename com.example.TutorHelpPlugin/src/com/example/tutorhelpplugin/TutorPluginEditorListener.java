package com.example.tutorhelpplugin;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
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
	
	public static ArrayList<String> existingEditorList = new ArrayList<>();

	@Override
	public void partActivated(IWorkbenchPartReference partReference) {
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partReference) {
	}

	@Override
	public void partClosed(IWorkbenchPartReference partReference) {
		IEditorPart part = partReference.getPage().getActiveEditor();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Date date = new Date();
		
		if (part == null) {
			if (existingEditorList != null && !existingEditorList.isEmpty() && existingEditorList.size() > 0) {
				for (String editor : existingEditorList) {
					// pop everything and send as closed
					try {
		            	List<String> lines = Arrays.asList("", "", "Editor_Closed_Action", "FileName|"+editor, 
		            						"DateTime|"+dateFormat.format(date), "", "");
		            	
		            	String temp = editor.split("/src")[0];
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
				existingEditorList = new ArrayList<>();
			}
		}
		else {
			if (!(part instanceof AbstractTextEditor))
	            return;
			IEditorReference[] newEditorRef = partReference.getPage().getEditorReferences();
			ArrayList<String> newEditorList = new ArrayList<>();
			for (IEditorReference editorRef : newEditorRef) {
				try {
					URI uri = ((IURIEditorInput)editorRef.getEditorInput()).getURI();
					if (uri != null && uri.getPath() != null) {
		                newEditorList.add(uri.getPath());
		            }
				} catch (PartInitException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			String closedFile = "";
			for (String editor : existingEditorList) {
				if (!newEditorList.contains(editor)) {
					closedFile = editor;
					existingEditorList.remove(closedFile);
					break;
				}
			}
			
			if (closedFile != null && closedFile != "") {
				try {
	            	List<String> lines = Arrays.asList("", "", "Editor_Closed_Action", "FileName|"+closedFile, 
	            						"DateTime|"+dateFormat.format(date), "", "");
	            	
	            	String temp = closedFile.split("/src")[0];
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
	public void partDeactivated(IWorkbenchPartReference partReference) {
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
            	                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            	            	Date date = new Date();
            	                
            	                try {
            	                	Matcher m = Pattern.compile("(\r\n)|(\r)|(\n)").matcher(event.text.trim());
            	                	int lineCount = 1;
            	                	while (m.find())
            	                	{
            	                	    lineCount ++;
            	                	}
            	                	
            	                	List<String> lines = Arrays.asList("", "", "Paste_Action", "FileName|"+currentFile, 
                    						"PastedLines|"+lineCount, "DateTime|"+dateFormat.format(date), "", "");
            	                	
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
	public void partVisible(IWorkbenchPartReference partReference) {
        IEditorPart part = partReference.getPage().getActiveEditor();
        if (!(part instanceof AbstractTextEditor))
            return;
        
        // log Visible file
        IEditorInput input = part.getEditorInput();
        if (input instanceof IURIEditorInput) {
            URI uri = ((IURIEditorInput)input).getURI();
            if (uri != null && uri.getPath() != null) {
                String currentFile = uri.getPath();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            	Date date = new Date();
                
            	if (existingEditorList != null && !existingEditorList.contains(currentFile)) {
                	existingEditorList.add(currentFile);            		
            	}
            	
                try {
                	List<String> lines = Arrays.asList("", "", "Editor_Visible_Action", "FileName|"+currentFile, 
                						"DateTime|"+dateFormat.format(date), "", "");
                	
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

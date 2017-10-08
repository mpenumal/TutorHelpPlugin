package com.example.tutorhelpplugin;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.example.tutorhelpplugin.launching.TutorPluginLogTracker;
import com.example.tutorhelpplugin.views.AssignmentQuestionsViewClient;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.example.TutorHelpPlugin"; //$NON-NLS-1$
	private static IExecutionListener executionListener;
	private static TutorPluginEditorListener editorListener;
	
	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		editorListener = new TutorPluginEditorListener();
	}
	
	@Override
    public void earlyStartup() {
        final IWorkbench workbench = PlatformUI.getWorkbench();

        // listen for save file events
        ICommandService commandService = (ICommandService) workbench.getService(ICommandService.class);
        executionListener = new TutorPluginRunListener();
        commandService.addExecutionListener(executionListener);
        
        workbench.getDisplay().asyncExec(new Runnable() {
            public void run() {
            	IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                if (window == null) return;
                if (window.getPartService() == null) return;
                
            	// listen for change of active file
                window.getPartService().addPartListener(editorListener);
                
                if (window.getPartService().getActivePart() == null) return;
                if (window.getPartService().getActivePart().getSite() == null) return;
                if (window.getPartService().getActivePart().getSite().getPage() == null) return;
                if (window.getPartService().getActivePart().getSite().getPage().getActiveEditor() == null) return;
                if (window.getPartService().getActivePart().getSite().getPage().getActiveEditor().getEditorInput() == null) return;

                // log file if one is opened by default
                IEditorInput input = window.getPartService().getActivePart().getSite().getPage().getActiveEditor().getEditorInput();
                if (input instanceof IURIEditorInput) {
                    URI uri = ((IURIEditorInput)input).getURI();
                    if (uri != null && uri.getPath() != null) {
                        String currentFile = uri.getPath();
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
           	         	Date date = new Date();
                        
           	         	List<String> lines = Arrays.asList("", "", "Open_By_Default", "FileName|"+currentFile, 
           	         										"DateTime|"+dateFormat.format(date), "", "");
     	
           	         	String temp = currentFile.split("/src")[0];
           	         	String[] folderNamesInWorkspace = temp.split("/");
           	         	String assignmentName = folderNamesInWorkspace[folderNamesInWorkspace.length-1];
           	         	
           	         	TutorPluginLogTracker.assignmentName = assignmentName;
           	         	
           	         	AssignmentQuestionsViewClient svc = new AssignmentQuestionsViewClient();
           	         	try {
							svc.sendLogClient(lines);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    }
                }
            }
        });
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		
		if (TutorPluginEditorListener.existingEditorList != null && !TutorPluginEditorListener.existingEditorList.isEmpty()) {
			for (String editor : TutorPluginEditorListener.existingEditorList) {
		        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		        Date date = new Date();
		        
	        	List<String> lines = Arrays.asList("", "", "Eclipse_Closed", "FileName|"+editor, 
	        										"DateTime|"+dateFormat.format(date), "", "");

	        	String temp = editor.split("/src")[0];
	        	String[] folderNamesInWorkspace = temp.split("/");
	        	String assignmentName = folderNamesInWorkspace[folderNamesInWorkspace.length-1];
	        	
	        	TutorPluginLogTracker.assignmentName = assignmentName;
	        	
	        	AssignmentQuestionsViewClient svc = new AssignmentQuestionsViewClient();
		        try {
					svc.sendLogClient(lines);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		super.stop(context);
		
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null && window.getPartService() != null)
            window.getPartService().removePartListener(editorListener);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}

package com.example.tutorhelpplugin;

import java.io.IOException;

import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.example.tutorhelpplugin.launching.ASUTutorHelpLogTracker;
import com.example.tutorhelpplugin.views.SampleViewClient;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.example.TutorHelpPlugin"; //$NON-NLS-1$
	private static IExecutionListener executionListener;
	private static CustomEditorListener editorListener;

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
		editorListener = new CustomEditorListener();
	}
	
	@Override
    public void earlyStartup() {
        final IWorkbench workbench = PlatformUI.getWorkbench();

        // listen for save file events
        ICommandService commandService = (ICommandService) workbench.getService(ICommandService.class);
        executionListener = new CustomExecutionListener();
        commandService.addExecutionListener(executionListener);
        
        workbench.getDisplay().asyncExec(new Runnable() {
            public void run() {
            	IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                if (window == null) return;
                
            	// listen for change of active file
                window.getPartService().addPartListener(editorListener);
            }
        });
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
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

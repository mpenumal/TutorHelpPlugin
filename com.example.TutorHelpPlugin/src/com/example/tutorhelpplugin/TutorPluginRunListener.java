package com.example.tutorhelpplugin;

import java.io.IOException;
//import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
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
public class TutorPluginRunListener implements IExecutionListener {
	
	public static String currentProject = "";
	
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
		 if (commandId.equals("org.eclipse.jdt.debug.ui.localJavaShortcut.run") ||
				 commandId.equals("org.eclipse.debug.ui.commands.RunLast")) {
	         IWorkbench workbench = PlatformUI.getWorkbench();
	         if (workbench == null) return;
	         IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	         if (window == null) return;
	         if (window.getPartService() == null) return;
	         if (window.getPartService().getActivePart() == null) return;
	         if (window.getPartService().getActivePart().getSite() == null) return;
	         if (window.getPartService().getActivePart().getSite().getPage() == null) return;

	         DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	         DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	         Date date = new Date();
	         
		     if (commandId.equals("org.eclipse.jdt.debug.ui.localJavaShortcut.run") ||
		    		 commandId.equals("org.eclipse.debug.ui.commands.RunLast")) {
		    	 // log file run operation
		    	 try {
		    		 List<String> lines = Arrays.asList("", "", "Run_Action", "Date: "+dateFormat.format(date), 
		    				 							"Time: "+timeFormat.format(date), "", "");
		    		 
		    		 String assignmentName = getCurrentSelectedProject();
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
	 
	 /**
	  * Get the project name from the selectionService
	  * @return projectName
	  */
	 public static String getCurrentSelectedProject() {
		 IProject project = null;
		 ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		 ISelection selection = selectionService.getSelection();
		 
		 if(selection instanceof IStructuredSelection) {
			 Object element = ((IStructuredSelection)selection).getFirstElement();
			 
			 if (element instanceof IResource) {
				 project= ((IResource)element).getProject();
			 } 
			 else if (element instanceof IPackageFragmentRoot) {
				 IJavaProject jProject = ((IPackageFragmentRoot)element).getJavaProject();
				 project = jProject.getProject();
		     } else if (element instanceof IJavaElement) {
		    	 IJavaProject jProject= ((IJavaElement)element).getJavaProject();
		    	 project = jProject.getProject();
		     }
		 }
		 return project.getName();
	 }
	 
	 @Override
	 public void preExecute(String commandId, ExecutionEvent event) {
		 // TODO Auto-generated method stub
	 }
}

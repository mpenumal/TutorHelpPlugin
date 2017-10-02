package com.example.tutorhelpplugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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

	         DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	         Date date = new Date();
	         
	         // log file run operation
	    	 try {
	    		 int loc = getTotalLinesOfCode();
	    		 List<String> lines = Arrays.asList("", "", "Run_Action", "LOC|"+loc, "DateTime|"+dateFormat.format(date), "", "");
	    		 IProject project = getCurrentSelectedProject();
	    		 TutorPluginLogTracker.assignmentName = project.getName();
	    		 AssignmentQuestionsViewClient svc = new AssignmentQuestionsViewClient();
	    		 svc.sendLogClient(lines);
	    		 getCompilationErrorsFromProblemsView();
	    		 
	    	 } catch (IOException e) {
	    		 // TODO Auto-generated catch block
	    		 e.printStackTrace();
	    	 } catch (Exception e) {
	    		 // TODO Auto-generated catch block
	    		 e.printStackTrace();
	    	 }
        }
		else if (commandId.equals("org.eclipse.jdt.debug.ui.localJavaShortcut.debug") ||
				 commandId.equals("org.eclipse.debug.ui.commands.DebugLast")) {
			 IWorkbench workbench = PlatformUI.getWorkbench();
	         if (workbench == null) return;
	         IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	         if (window == null) return;
	         if (window.getPartService() == null) return;
	         if (window.getPartService().getActivePart() == null) return;
	         if (window.getPartService().getActivePart().getSite() == null) return;
	         if (window.getPartService().getActivePart().getSite().getPage() == null) return;

	         DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	         Date date = new Date();
	         
	         // log file Debug operation
	    	 try {
	    		 List<String> lines = Arrays.asList("", "", "Debug_Action", "DateTime|"+dateFormat.format(date), "", "");
	    		 IProject project = getCurrentSelectedProject();
	    		 TutorPluginLogTracker.assignmentName = project.getName();
	    		 AssignmentQuestionsViewClient svc = new AssignmentQuestionsViewClient();
	    		 svc.sendLogClient(lines);
	    		 getCompilationErrorsFromProblemsView();
	    		 
	    	 } catch (IOException e) {
	    		 // TODO Auto-generated catch block
	    		 e.printStackTrace();
	    	 } catch (Exception e) {
	    		 // TODO Auto-generated catch block
	    		 e.printStackTrace();
	    	 }
		}
	 }
	 
	 /**
	  * Get the total lines of java code in the project
	 * @throws CoreException 
	 * @throws IOException 
	  */
	 private int getTotalLinesOfCode() throws CoreException, IOException {
		 IProject project = getCurrentSelectedProject();
		 return processContainer(project);
	 }
	 
	 
	 private int processContainer(IContainer container) throws CoreException, IOException {
		 int loc = 0;
		 IResource [] members = container.members();
		 for (IResource member : members) {
			 if (member instanceof IContainer) {
				 loc += processContainer((IContainer)member);
			 }
			 else if (member instanceof IFile) {
				 if (member.getFullPath().toString().endsWith(".java")) {
					 File temp = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString()+member.getFullPath().toString());
					 LineNumberReader reader = new LineNumberReader(new FileReader(temp));
					 reader.skip(Long.MAX_VALUE);
					 loc += reader.getLineNumber();
					 reader.close();
				 }
			 }
	     }
		 return loc;
	 }
	 
	
	/**
	 * Get the compilation errors from the Problems View
	 * @throws CoreException
	 * @throws ParseException 
	 */
	private void getCompilationErrorsFromProblemsView() throws CoreException, ParseException {
		IMarker[] markers = ResourcesPlugin.getWorkspace().getRoot().findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		if (markers != null && markers.length > 0) {
			List<String> lines = new ArrayList<String>(); // s.asList(new String[10*markers.length]);
			for (IMarker m : markers) {
				lines.add("");
				lines.add("");
				lines.add("Compilation_Error");
				lines.add("Message|"+m.getAttribute(IMarker.MESSAGE));
				lines.add("FileName|"+m.getResource());
				lines.add("LineNumber|"+m.getAttribute(IMarker.LINE_NUMBER));
				lines.add("Priority|"+m.getAttribute(IMarker.PRIORITY));
				lines.add("Severity|"+m.getAttribute(IMarker.SEVERITY));
				lines.add("");
				lines.add("");
			}
			
			if (lines != null && lines.size() > 0 && lines.get(0) == "") {
				AssignmentQuestionsViewClient svc = new AssignmentQuestionsViewClient();
				try {
					svc.sendLogClient(lines);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	 
	 /**
	  * Get the current project from the selectionService
	  * @return projectName
	  */
	private static IProject getCurrentSelectedProject() {
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
		return project;
	}
	 
	 @Override
	 public void preExecute(String commandId, ExecutionEvent event) {
		 // TODO Auto-generated method stub
	 }
}

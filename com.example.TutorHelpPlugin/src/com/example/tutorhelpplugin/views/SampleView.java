package com.example.tutorhelpplugin.views;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class SampleView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.example.tutorhelpplugin.views.SampleView";

	private TableViewer viewer;
	private Action refreshAction;
	private Action deleteAction;
	private Action doubleClickAction;
	 

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

/**
	 * The constructor.
	 */
	public SampleView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		
		ArrayList<String> arrList = getFilesInFolder();
		
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		if (arrList == null || arrList.isEmpty()) {
			viewer.setInput(new String[] { "List is Empty." });
		}
		else {
			viewer.setInput(arrList);
		}
		viewer.setLabelProvider(new ViewLabelProvider());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "com.example.TutorHelpPlugin.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private ArrayList<String> getFilesInFolder() {
		String directoryPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + File.separator + "AssignmentList_Client";
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdir();
		}
		
		File folder = new File(directoryPath);
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> arrList = new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				arrList.add("Assignment" + (arrList.size()+1) + ": " + listOfFiles[i].getName());
			}
		}
		return arrList;
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				SampleView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(refreshAction);
		manager.add(new Separator());
		manager.add(deleteAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(refreshAction);
		manager.add(deleteAction);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(refreshAction);
		manager.add(deleteAction);
	}

	private void makeActions() {
		refreshAction = new Action() {
			public void run() {
				if (viewer != null) {
					SampleViewClient svc = new SampleViewClient();
					try {
						svc.getAssignmentsClient();
						//System.out.println("Workspace: "+ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ArrayList<String> arrList = getFilesInFolder();
					
					viewer.setContentProvider(ArrayContentProvider.getInstance());
					viewer.setInput(arrList);
					viewer.setLabelProvider(new ViewLabelProvider());
				}
				else {
					showMessage("Refresh Action executed");
				}
			}
		};
		refreshAction.setText("Refresh Action");
		refreshAction.setToolTipText("Refresh Action tooltip");
		refreshAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
		
		deleteAction = new Action() {
			public void run() {
				String directoryPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + File.separator + "AssignmentList_Client";
				if (viewer != null) {
					File[] assignmentList = new File(directoryPath).listFiles();
					for(File file: assignmentList) 
					    if (!file.isDirectory()) 
					        file.delete();
					
					viewer.setContentProvider(ArrayContentProvider.getInstance());
					viewer.setInput(new String[] { "List is Empty." });
					viewer.setLabelProvider(new ViewLabelProvider());
				}
				else {
					showMessage("Delete Action executed");
				}
			}
		};
		deleteAction.setText("Delete Action");
		deleteAction.setToolTipText("Delete Action tooltip");
		deleteAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_ETOOL_DELETE));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				File fileToOpen = null;
				String packageName, className;
				String fileName = obj.toString().split(":")[1].trim();
				String projectName = obj.toString().split(":")[0];
				projectName = projectName.substring(0, projectName.length() - 1) + fileName.replace(".java", "");
				System.out.println(fileName);
				
				try {
					className = fileName;
					fileName = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + File.separator + "AssignmentList_Client" + File.separator + fileName;
					packageName = "ASUCourse";
					createJavaProject(projectName, fileName, packageName, className);
					fileToOpen = new File(fileName);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}		
		};
	}

	/**
	 * @return
	 * @throws CoreException 
	 * @throws IOException 
	 */
	private void createJavaProject(String projectName, String fileName, String packageName, String className) throws CoreException, IOException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		if (!project.exists() ) {
			project.create(null);
			project.open(null);
			
			IProjectDescription description = project.getDescription();
			description.setNatureIds(new String[] { JavaCore.NATURE_ID });
			project.setDescription(description, null);
			
			IJavaProject javaProject = JavaCore.create(project);
			
			IFolder binFolder = project.getFolder("bin");
			binFolder.create(false, true, null);
			javaProject.setOutputLocation(binFolder.getFullPath(), null);
			
			List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
			IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
			LibraryLocation[] locations = JavaRuntime.getLibraryLocations(vmInstall);
			for (LibraryLocation element : locations) {
			 entries.add(JavaCore.newLibraryEntry(element.getSystemLibraryPath(), null, null));
			}
			//add libs to project class path
			javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);
			
			IFolder sourceFolder = project.getFolder("src");
			sourceFolder.create(false, true, null);
			
			IPackageFragmentRoot fragmentRoot = javaProject.getPackageFragmentRoot(sourceFolder);
			IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
			IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
			System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
			newEntries[oldEntries.length] = JavaCore.newSourceEntry(fragmentRoot.getPath());
			javaProject.setRawClasspath(newEntries, null);
			
			IPackageFragment pack = javaProject.getPackageFragmentRoot(sourceFolder).createPackageFragment(packageName, false, null);
			
			String source;
			source = readFile(fileName);
			
			StringBuffer buffer = new StringBuffer();
			buffer.append("package " + pack.getElementName() + ";\n");
			buffer.append("\n");
			buffer.append(source);
			
			ICompilationUnit cu = pack.createCompilationUnit(className , buffer.toString(), false, null);
		}
		else {
			if (!project.isOpen()) {
				project.open(null);
			}
		}
	}
	
	String readFile(String fileName) throws IOException {
		String file = FileSystems.getDefault().getPath(fileName).normalize().toAbsolutePath().toString();
	    BufferedReader br = new BufferedReader(new FileReader(file));
	    
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}
	
	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Sample View",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}

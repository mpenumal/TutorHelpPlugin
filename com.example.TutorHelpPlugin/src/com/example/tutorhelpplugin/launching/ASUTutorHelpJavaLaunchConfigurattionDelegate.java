package com.example.tutorhelpplugin.launching;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.JavaLaunchDelegate;

public class ASUTutorHelpJavaLaunchConfigurattionDelegate extends JavaLaunchDelegate {
	
	private static Map fgLaunchToFileMap = new HashMap();
	
	//Used to map temp file to launch object.
	private ILaunch fLaunch;
	
	public synchronized void launch(ILaunchConfiguration configuration, 
            String mode, 
            ILaunch launch, 
            IProgressMonitor monitor) throws CoreException {
		
		try {
			fLaunch = launch;
			super.launch(configuration, mode, launch, monitor);
		} catch (CoreException e) {
			cleanup(launch);
			throw e;
		}
		fLaunch = null;
	}
	
	private void cleanup(ILaunch launch) {
		File temp = (File) fgLaunchToFileMap.get(launch);
		if (temp != null) {
			try {
				fgLaunchToFileMap.remove(launch);
				temp.delete();
			} finally {
				if (fgLaunchToFileMap.isEmpty()) {
					DebugPlugin.getDefault().removeDebugEventListener(this);
				}
			}
		}		
	}
	
	
}

/**************************************80**************************************/
/*	Use tab width = 5, indent level width = 5, and a fixed-font in a >= 80 
 *	character wide window to view this file.
 */

/*	This is a GUI frontend to the unrar commandline tool.
 *	unrar is Copyright (c) 1993-99 Eugene Roshal
 */

/*
 *	Unrar.java
 *	Project: Unrar X
 *
 *	Created by Daniel Aarno on Tue Apr 09 2002.
 *	Copyright (c) 2002 Daniel Aarno.
 *
 *	This program is free software; you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation; either version 2 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program; if not, write to the Free Software
 *	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/*	See the README file on how to install this program
 */

import com.apple.cocoa.foundation.*;
import com.apple.cocoa.application.*;
import java.io.*;

/**
 * The Unrar class is responsible for interaction with the Cocoa GUI. It can set
 * the different GUI elements status, and allso gets called when the user 
 * invokes some action.
 * @version v0.0.2
 * @author Daniel Aarno
 */
public class Unrar extends Thread implements Cloneable {

	//GUI controlls
	NSTextField status;
	NSProgressIndicator pBar;
	NSTextField path;
	NSTextField destPath;
	NSTextField extractPath;
	NSButton cancel;
	NSButton unrar;
	NSPanel question;
	//End GUI controlls
	
	GlobalObjects g = new GlobalObjects();

	public Unrar() {
		NSBundle app = NSBundle.mainBundle();
		String str;
		g.theError = new CocoaErrorHandler();
		g.theError.debug = true;
		
		g.theError.ErrorMsg("This software is PRE ALPHA release! This means " +
			"the software is for evaluation and is NOT tested. It contains " +
			"known bugs and may behave unexpectedly. Please use at your " +
			"OWN RISK! See the README.txt and Licence.txt files before " +
			"using this software. If unsure Quit now!");
		
		try {	//Get initial settings
			g.theSettings = new SettingsHandler(app.resourcePath() + 
								File.separator + "default.settings");
			String sfPath = g.theSettings.LoadSetting("preferenceFile");
			String pathType = g.theSettings.LoadSetting("pathType");
				
			//Get or create personal settings file
			if(pathType == null || sfPath == null)
				g.theError.ErrorFatal("Could no parse settings file");
			else if(pathType.equalsIgnoreCase("home")) {
				sfPath = System.getProperty("user.home") + 
										File.separator + sfPath;
			}
			else if(pathType.equalsIgnoreCase("absolute"))
				sfPath = File.separator + sfPath;
			else if(!pathType.equalsIgnoreCase("relative"))
				g.theError.ErrorFatal("Could no parse settings file");
			if(!(new File(sfPath)).exists())
				g.theSettings.CopySettings(sfPath);
			g.theSettings = new SettingsHandler(sfPath);
			//Settings file loaded OK
			str = g.theSettings.LoadSetting("unrarPath");
			if(str.equals("builtIn"))
				str = app.resourcePath() + File.separator + 
										"unrar-2.71_pre_alpha-0.1";
			g.theRunner = new UnrarRunner(str, g.theError);
					
		} catch (Exception e) {
			g.theError.ErrorFatal(e);
		}
	}

/**
 * Starts the extraction in a new thread.
 */	
	public void run() {
		double perc = 0D;
		//Update the GUI to reflect the state of the application
		unrar.setEnabled(false);
		cancel.setEnabled(true);
		pBar.setDoubleValue(perc);
	
		g.theRunner.path =SettingsHandler.ConvertHomePath(path.stringValue());
		g.theRunner.destPath = SettingsHandler.ConvertHomePath(
										destPath.stringValue());
		UnrarInterpreter ui = new UnrarInterpreter(g.theError, g.theRunner);
		ui.RunUnrar();
		setPriority(MIN_PRIORITY);
				
		pBar.setIndeterminate(true);
		
		while(!ui.IsDone()) {
			perc = ui.GetPercent();
			if(perc < 0D)
				pBar.animate(null);
			else {
				pBar.setIndeterminate(false);
				pBar.setDoubleValue(perc);
			}
				
			try { sleep(500); }
			catch(Exception e) { };
		}
		pBar.setIndeterminate(false);
		perc = 0D;
		pBar.setDoubleValue(perc);
		
		unrar.setEnabled(true);
		cancel.setEnabled(false);
		g.theError.ErrorFatal("This is because of unimplemented " + 
			"features in the PRE ALPHA release. Please visit " +
			"http://unrarx.sourceforge.net to see what is going on with " + 
			"Unrar X. You can send you experinces, bad or good to: " +
			"macbishop@users.sourceforge.net.");
	}
/**
 * Gets called as a result of the user clicking on the begin-to-extract button.
 * @param sender The NSButton that invoked the method.
 */
	public void RunUnrar(Object sender) {
		this.start();
	}
/**
 * Cancels the extraction in progress.
 * @param sender The NSButton that invoked the method.
 */
	public void Cancel(Object sender) {
		g.theError.DebugMsg("Call to unfinished method occurred!!", 
										new NullPointerException());
		((NSButton)sender).setEnabled(false);
		unrar.setEnabled(true);
	}
	
	public void SelectFile(Object sender) {
		int i;
		NSOpenPanel thePanel = NSOpenPanel.openPanel();
		NSArray fileTypes = new NSArray("rar");

		i = thePanel.runModalInDirectory(System.getProperty("user.home"),
								"*.rar", fileTypes);
		if(i == thePanel.OKButton)
			path.setTitleWithMnemonic(
						(String)thePanel.filenames().objectAtIndex(0));
	}
	
	public void SelectDest(Object sender) {
		int i;
		NSOpenPanel thePanel = NSOpenPanel.openPanel();
		
		thePanel.setCanChooseFiles(false);
		thePanel.setCanChooseDirectories(true);
		i = thePanel.runModalInDirectory(System.getProperty("user.home"),
								(String)null, (NSArray)null);
		if(i == thePanel.OKButton)
			destPath.setTitleWithMnemonic(
						(String)thePanel.filenames().objectAtIndex(0));
	}
}
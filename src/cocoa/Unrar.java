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
 * @version v0.0.1
 * @author Daniel Aarno
 */
public class Unrar extends Thread {

	//GUI controlls
	NSTextField status;
	NSProgressIndicator pBar;
	NSTextField path;
	NSButton cancel;
	NSButton unrar;
	NSPanel question;
	//End GUI controlls
	
	GlobalObjects g;

	public Unrar() {
		g.theError = new ErrorHandler();
		g.theError.debug = true;
		
		try {	//Get initial settings
			g.theSettings = new SettingsHandler("default.settings");
			String sfPath = g.theSettings.LoadSetting("preferenceFile");
			String pathType = g.theSettings.LoadSetting("pathType");
			
			//Get or create personal settings file
			if(pathType == null)
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
			g.theRunner = new UnrarRunner(
					g.theSettings.LoadSetting("unrarPath"), g.theError);
					
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
		
		g.theRunner.path = path.stringValue();
		UnrarInterpreter ui = new UnrarInterpreter(g.theError, g.theRunner);
		ui.RunUnrar();

		pBar.setIndeterminate(true);
		
		while(!ui.IsDone()) {
			perc = ui.GetPercent();
			if(perc < 0D)
				pBar.animate(null);
			try { sleep(100); }
			catch(Exception e) { };
		}
		pBar.setIndeterminate(false);
		perc = 0D;
		pBar.setDoubleValue(perc);
		
		unrar.setEnabled(true);
		cancel.setEnabled(false);
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
}

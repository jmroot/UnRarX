/**************************************80**************************************/
/*	Use tab width = 5, indent level width = 5, and a fixed-font in a >= 80 
 *	character wide window to view this file.
 */

/*	This class presents the user with error messages.
 */

/*
 *	CocoaErrorHandler.java
 *	Project: Unrar X
 *
 *	Created by Daniel Aarno on Thu May 30 2002.
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
 * Use an alert dialogue in Cocoa to display error and debug messages.
 * @version v0.0.0
 * @author Daniel Aarno
 */

public class CocoaErrorHandler extends ErrorHandler {

public boolean debug = false;
public NSWindow theWindow = null;
/**
 * Prints message and e to stderr and returns, if debug is enabled.
 * Otherwise it returnes without action.
 * @param message The debug message to display
 * @param e The Exception to display
 */
	public void DebugMsg(String message, Exception e) {
		super.DebugMsg(message, e);
	}
/**
 * Prints message and e to stderr and exits with status 1, if debug is enabled.
 * Otherwise it returnes without action.
 * @param message The debug message to display
 * @param e The Exception to display
 */	
	public void DebugFatal(String message, Exception e) {
		super.DebugFatal(message, e);
	}
/**
 * Prints message to stderr and returns, if debug is enabled. Otherwise it 
 * returnes without action.
 * @param message The debug message to display
 */	
	public void DebugMsg(String message) {
		super.DebugMsg(message);
	}
/**
 * Prints message to stderr and exits with status 1, if debug is enabled. 
 * Otherwise it returnes without action.
 * @param message The debug message to display
 */	
	public void DebugFatal(String message) {
		super.DebugFatal(message);
	}
/**
 * Prints message to an alert dialogue and returns.
 * @param message The error message to display
 */	
	public void ErrorMsg(String message) {
		int i;
		System.err.println(message);
		i = NSAlertPanel.runAlert(null, message,
										"Dismiss", "Quit", null);
		if(i != NSAlertPanel.DefaultReturn)
			ErrorFatal("This could be because the user choose to quit or" + 
					" because of an unknown error.");
	}
/**
 * Prints message to stderr, andan alert dialogue, exits with status 1 
 * when dialogue is dissmised.
 * @param message The debug message to display
 */		
	public void ErrorFatal(String message) {
		System.err.println(message);
		NSAlertPanel.runAlert("Unrar X will now exit!", 
									message, "Quit", null, null);
		System.exit(1);
	}
/**
 * Formats and prints e to stderr and an alert dialogue and returns
 * when the dialogue is dissmissed.
 * @param e The Exception to display
 */		
	public void ErrorMsg(Exception e) {
		int i;
		super.ErrorMsg("Exception: " + e);
		i = NSAlertPanel.runAlert("An unrecoverable exception occured!" +
			"Unrar X will now exit.",
			"This is a bad thing and probably means that a " + 
			"programmer has done something bad. It is recomended that" +
			"you restart the application.\n The exception is:\n" + 
								e, "Dismiss", "Quit", null);

		if(i != NSAlertPanel.DefaultReturn)
			ErrorFatal("This could be because the user choose to quit or" + 
					" because of an unknown error.");
	}
/**
 * Formats and prints e to stderr and an alert dialogue. Exits with status 1
 * when the dialogue is dissmissed.
 * @param e The Exception to display
 */	
	public void ErrorFatal(Exception e) {
		super.ErrorMsg("Exception: " + e);
		NSAlertPanel.runAlert("An uncaught exception occured!",
			"This is a bad thing and probably means that a " + 
			"programmer has done something bad.\n The exception is:\n" + 
								e, "Dismiss", "Quit", null);
		System.exit(1);
	}
}
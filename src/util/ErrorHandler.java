/**************************************80**************************************/
/*	Use tab width = 5, indent level width = 5, and a fixed-font in a >= 80 
 *	character wide window to view this file.
 */

/*	This class is a wrapper to collect and assemble error messages. This class
 *	can then be sub-classed to present error messages in a more natural way,
 *	for instance using a GUI. The ErrorHandler base-class simply writes them 
 *	to stderr.
 */

/*
 *	ErrorHandler.java
 *	Project: N/A
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
 
 import java.io.*;

/**
 * This class is a wrapper to collect and assemble error messages. This class
 * can then be sub-classed to present error messages in a more natural way,
 * for instance using a GUI. The ErrorHandler base-class simply writes them to
 * stderr.
 * @version v0.0.0
 * @author Daniel Aarno
 */

public class ErrorHandler {

public boolean debug = false;
/**
 * Prints message and e to stderr and returns, if debug is enabled.
 * Otherwise it returnes without action.
 * @param message The debug message to display
 * @param e The Exception to display
 */
	public void DebugMsg(String message, Exception e) {
		String str = message + "\n\n" + e;
		DebugMsg(str); 
	}
/**
 * Prints message and e to stderr and exits with status 1, if debug is enabled.
 * Otherwise it returnes without action.
 * @param message The debug message to display
 * @param e The Exception to display
 */	
	public void DebugFatal(String message, Exception e) {
		String str = message + "\n\n" + e;
		DebugFatal(str); 
	}
/**
 * Prints message to stderr and returns, if debug is enabled. Otherwise it 
 * returnes without action.
 * @param message The debug message to display
 */	
	public void DebugMsg(String message) {
		if(debug)
			System.err.println(message);
	}
/**
 * Prints message to stderr and exits with status 1, if debug is enabled. 
 * Otherwise it returnes without action.
 * @param message The debug message to display
 */	
	public void DebugFatal(String message) {
		if(!debug)
			return;
		System.err.println(message);
		System.exit(1);
	}
/**
 * Prints message to stderr and returns, if debug is enabled. Otherwise it 
 * returnes without action.
 * @param message The debug message to display
 */	
	public void ErrorMsg(String message) {
		System.err.println(message);
	}
/**
 * Prints message to stderr and exits with status 1, if debug is enabled.
 * Otherwise it returnes without action.
 * @param message The debug message to display
 */		
	public void ErrorFatal(String message) {
		System.err.println(message);
		System.exit(1);
	}
/**
 * Formats and prints e to stderr and returns, if debug is enabled.
 * Otherwise it returnes without action.
 * @param e The Exception to display
 */		
	public void ErrorMsg(Exception e) {
		ErrorMsg("Exception: " + e);
	}
/**
 * Formats and prints e to stderr and exits with status 1, if debug is enabled.
 * Otherwise it returnes without action.
 * @param e The Exception to display
 */	
	public void ErrorFatal(Exception e) {
		ErrorFatal("Exception: " + e);
	}
}

/********************************* History ************************************/
/*
2002-04-09	First included in the Unrar X project.

*/
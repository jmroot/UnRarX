/**************************************80**************************************/
/*	Use tab width = 5, indent level width = 5, and a fixed-font in a >= 80 
 *	character wide window to view this file.
 */

/*	This is a GUI frontend to the unrar commandline tool.
 *	unrar is Copyright (c) 1993-99 Eugene Roshal
 */

/*
 *	UnrarRunner.java
 *	Project: Unrar X
 *
 *	Created by Daniel Aarno on Mon Apr 08 2002.
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
 * The UnrarRunner stores information needed to run the UNIX unrar utility. 
 * That inludes options, switches, paths etc. It can allso execute the unrar
 * utility (assuming it knows its path) with these options and provide a
 * BufferedReader connected to the stdout of the unrar utility. Unrarunner may
 * also be used to collect some information about a rar-archive.
 * @version v0.0.0
 * @author Daniel Aarno
 */
 
public class UnrarRunner {
	//Options
	public int action;
	public UnrarSwitches us = new UnrarSwitches();

	private String unrarPath;
	private ErrorHandler eh = null;
	
	public UnrarRunner() {
	}
	
	public UnrarRunner(String unrarPath, ErrorHandler eh) {
		Init(unrarPath, eh);
	}
	
	public void Init(String unrarPath, ErrorHandler eh) {
		if(eh == null)
			eh = new ErrorHandler();
		this.eh = eh;
		this.unrarPath = unrarPath;
	}
	
	public BufferedReader RunUnrar(String filePath) {
		String str;
		str = unrarPath;
		
		switch(action) {	//Parse the action. Get option to pass to unrar
			case 0: str += " x"; break;
			case 1: str += " e"; break;
			case 2: str += " t"; break;
		}
		
		str += us.GetOptions();
		str += " " + filePath;
		return StartUnrar(str);
	}
	
	private BufferedReader StartUnrar(String options) {
		String str = null;
		Runtime theRuntime = null;
		Process child = null;
		//OutputStream childOP = null;
		InputStream childIP = null;
	
		theRuntime = Runtime.getRuntime();
	
		try
		{
			child = theRuntime.exec(options);
			//childOP = child.getOutputStream();
			childIP = child.getInputStream();
		} catch (Exception e)
		{
			System.err.println("Could not create child process.");
			return null;
		}
	
		try
		{
			BufferedReader r = new BufferedReader(
									new InputStreamReader(childIP));
			return r;
		}
		catch(Exception e)
		{
			System.err.println("Could not pipe to child process: " + e);
			return null;
		}
	}
}

class UnrarSwitches {
	public boolean freshen = false;
	public boolean overwrite = false;
	public boolean recurse = true;
	public boolean update = true;
	public boolean yesOnAll = true;
	
	public String GetOptions() {
		String str = "";
		
		if(freshen)
			str += " -f";
		if(overwrite)
			str += " -o+";
		else
			str += " -o-";
		if(recurse)
			str += " -r";
		if(update)
			str += " -u";
		if(yesOnAll)
			str += " -y";
			
		return str;
	}
}
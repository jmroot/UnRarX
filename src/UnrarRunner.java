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
 * @version v0.0.2
 * @author Daniel Aarno
 */
 
public class UnrarRunner implements Cloneable{
	//Options
	public int action;
	public UnrarSwitches us = new UnrarSwitches();
	public String path;	//the file to do "stuff" on.
	
	public OutputStream childOP= null;
	
	private String unrarPath;	//Where the unrar utility is located.
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
	
	public InputStream RunUnrar() { return RunUnrar(path); }
	
	public InputStream RunUnrar(String filePath) {
		String str;
		str = unrarPath;
		
		switch(action) {	//Parse the action. Get option to pass to unrar
			case 0: str += " x"; break; //Extract with full path
			case 1: str += " e"; break; //Extract to current directory
			case 2: str += " t"; break; //Test archive files
			case 3: str += " p"; break; //Print file to stdout
			case 4: str += " l"; break; //List contents of archive
			case 5: str += " v"; break; //Verbosely list contents of archive
		}
		
		str += us.GetOptions();
		str += " " + filePath;
		return StartUnrar(str);
	}
	
	private InputStream StartUnrar(String options) {
		String[] envp = new String [1];
		Process child = null;
		InputStream childIP = null;
		Runtime theRuntime = Runtime.getRuntime();
		
		envp[0] = "HOME=" + System.getProperty("user.home");
		
		try
		{
			child = theRuntime.exec(options, envp);
			childIP = child.getInputStream();
			childOP = child.getOutputStream();
			return childIP;
		} catch (Exception e)
		{
			System.err.println("Could not create child process.");
			return null;
		}
	}
	
	public Object clone() {
		try {
			return super.clone();
		} catch(CloneNotSupportedException e) {
			eh.ErrorFatal("Could not clone object! Exception: " + e);
		}
		return null;
	}
}

class UnrarSwitches {
	public boolean freshen = false;
	public boolean overwrite = false;
	public boolean recurse = true;
	public boolean update = true;
	public boolean yesOnAll = false;
	
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
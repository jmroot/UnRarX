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
import java.util.*;

/**
 * The UnrarRunner stores information needed to run the UNIX unrar utility. 
 * That inludes options, switches, paths etc. It can allso execute the unrar
 * utility (assuming it knows its path) with these options and provide a
 * BufferedReader connected to the stdout of the unrar utility. Unrarunner may
 * also be used to collect some information about a rar-archive.
 * @version v0.0.3
 * @author Daniel Aarno
 */
 
public class UnrarRunner implements Cloneable{
	//Options
	public int action;
	public UnrarSwitches us = new UnrarSwitches();
	public String path;	//the file to do "stuff" on.
	public String destPath; //Where the files will go.
	
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
		String[] str;
		StringTokenizer tk = new StringTokenizer(us.GetOptions());
		str = new String[3 + tk.countTokens()];
		int i;
		
		str[0] = unrarPath;
		
		switch(action) {	//Parse the action. Get option to pass to unrar
			case 0: str[1] = "x"; break; //Extract with full path
			case 1: str[1] = "e"; break; //Extract to current directory
			case 2: str[1] = "t"; break; //Test archive files
			case 3: str[1] = "p"; break; //Print file to stdout
			case 4: str[1] = "l"; break; //List contents of archive
			case 5: str[1] = "v"; break; //Verbosely list contents of archive
		}
		
		for(i = 0; i < tk.countTokens(); i++)
			str[2 + i] = tk.nextToken();
		str[2 + i] = filePath;
		if(!destPath.endsWith("/"))
			destPath += "/";
		str[3 + i] = destPath;
		return StartUnrar(str);
	}
	
	private InputStream StartUnrar(String[] cmd) {
		String[] envp = new String [1];
		Process child = null;
		InputStream childIP = null;
		Runtime theRuntime = Runtime.getRuntime();
				
		try
		{
			child = theRuntime.exec(cmd);
			childIP = child.getInputStream();
			childOP = child.getOutputStream();
			return childIP;
		} catch (Exception e)
		{
			eh.ErrorMsg("Could not create child process. " + e);
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
/**************************************80**************************************/
/*	Use tab width = 5, indent level width = 5, and a fixed-font in a >= 80 
 *	character wide window to view this file.
 */

/*	This is a GUI frontend to the unrar commandline tool.
 *	unrar is Copyright (c) 1993-99 Eugene Roshal
 */

/*
 *	UnrarInterpreter.java
 *	Project: Unrar X
 *
 *	Created by Daniel Aarno on Tue Apr 16 2002.
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
 * The UnrarInterpreter class can be used to launch the unrar utility via a 
 * UnrarRunner object. It can also gather information on a particular rar
 * file and provide feedback on the extraction process.
 * @version v0.0.0
 * @author Daniel Aarno
 */


public class UnrarInterpreter extends Thread{
	private boolean isDone = true;
	private UnrarRunner ur;
	private ErrorHandler eh;
	private InputStream unrarStdOut = null;
	private String path;
	private int noFiles = -1;
	private Vector fileList = new Vector();

/**
 * Creates a new empty UnrarInterpreter. Currently there is no use for this
 * function.
 */	
	public UnrarInterpreter() {
	}

/**
 * Creates a new UnrarRunner and attaches it to an UnrarRunner object.
 * @param eh An ErrorHandler object to handle any errors that might occur.
 * @param runner An UnrarRunner object connected "connected" to the rar-file
 */
	public UnrarInterpreter(ErrorHandler eh, UnrarRunner runner) {
		this.eh = eh;
		ur = runner;
	}
	
	public void RunUnrar() {
		isDone = false; //There might be a problem here otherwise
		start();
	}
/**
 * Begin the extraction process. Call the RunUnrar function of the UnrarRunner
 * object associated with this instance of the UnrarInterpreter class. Then begin
 * to gather statistics about the extraction process.
 */
	public void run() {
		//The implementation here might change. It is probably better to start
		//unrar and simply pass the InputStream to here. Have to consider this.
		char[] buf = new char[0x400];
		isDone = false;
		BufferedReader r = new BufferedReader(new InputStreamReader(ur.RunUnrar()));
		try {
			while(r.read(buf, 0, 0x400) >= 0)
				sleep(100);
		} catch(Exception e) { };
				
		isDone = true;
	}
/**
 * Retrieve an InputStream object associated with stdout of unrar.
 * @return The InputStream returned from the unrar process.
 */	
	public InputStream GetStdOut() {
		return unrarStdOut;
	}
/**
 * Get the number of percent completed for the extraction process.
 * This number is an estimate and can not be guaranteed to be accurate.
 * @return The percentage completed in the extraction process. If returnvalue
 * is less then zero (< 0) this means indeterminate.
 */
	public double GetPercent() {
		if(isDone)
			return 0D;
		else
			return -1D;
	}
/**
 * Get a listing of filenames, sizes, compressed size and compression ratio.
 * @return A Vector of Strings arrays containing filenames, sizes, compressed size and 
 * compression ratio.
 */ 
	public Vector GetFileList() {
		return fileList;
	}
/**
 * Get the name of the file currently decompressing. Due to delays and 
 * scheduling this might not be the file that is actually being decompressed.
 * @return The name of the file currently being decompressed.
 */
	public String GetCurrentFile() {
		return "";
	}
/**
 * Retrieves status about the decompression phase. This could be the last line
 * written to stdout by the unrar helper application. But it is not 
 * necessarily soo.	
 * @return A String containing the status of the expansion process. This should
 * be in a human-readable format.
 */
	public String GetStatus() {
		return "";
	}
/**
 * Check if the extraction process is completed, ie if the UnrarInterpreter is
 * idle.
 * @return boolean value indicating the state of the extraction process.
 */	
	public boolean IsDone() { return isDone; }
	
/**
 * Assemble statistics about the rar-archive.
 */
	protected void GenerateStats() {
		UnrarRunner tmpRunner = (UnrarRunner)ur.clone();
		
		tmpRunner.us = new UnrarSwitches();
		tmpRunner.action = 4;	//List contents of archive
		path = new String(tmpRunner.path);
		
		fileList = CreateFileList(tmpRunner.RunUnrar(path));
	}
	
	protected Vector CreateFileList(InputStream inStream) {
		//This works, but needs to be cleaned.
		char[] buf = new char[0x400];
		int i=0, n=0, sum;
		String str = "", leftOver="";
		String newLine = System.getProperty("line.separator");
		StringTokenizer tk;
		Vector lines = new Vector();
		
		BufferedReader r = new BufferedReader(new InputStreamReader(inStream));
		try {
			do {
				sum = 0;
				str = leftOver;
				while(i >= 0 && sum < 0x400) {
					i  = r.read(buf, 0, 0x400 - sum);
					sum += i;
					if(i > 0)
						str += String.valueOf(buf, 0, i);
					if(sum < 0x400)
						sleep(50);
				}
								
				tk = new StringTokenizer(str, "-"); 
				if(n == 0)	//Remove garbage in the begining
					str = RemoveGarbage(tk);
				
				leftOver = "";
				boolean merge = true;
				String tmp="";
				
				while(tk.hasMoreTokens()) {					
					if(merge)
						str = tk.nextToken(newLine);
					else
						str = tmp;
					if(!tk.hasMoreTokens()) {
						int size = lines.size();
						leftOver = ((String)lines.get(size-1)) + str;
						lines.remove(size-1);
						break;
					}
					tmp = tk.nextToken();
					merge = false;
					if(tmp.startsWith("                ")) {//broken line
						merge = true;
						str = str + tmp;
						tmp = "";
						if(!tk.hasMoreTokens()) {
							leftOver = str;
							break;
						}
					}
					str = str.trim();
					if((str.startsWith("-----") && str.endsWith("-----")))
						break;
					n++;
					lines.add(str);
					if(!tk.hasMoreTokens()) {
						leftOver = tmp;
						break;
					}
				}				
				
			} while(i > 0);
		} catch(Exception e) { System.out.println(e); System.exit(-5); }
		return lines;
	}
	
	private String RemoveGarbage(StringTokenizer tk) {	
	//This is very dirty an bug prone
	//If the path to the .rar file contains '-' this will
	//cause unexpected results.
		String str;
		eh.DebugMsg("Call to buggy code");
		str = tk.nextToken("-");
		str = tk.nextToken("-");
		str = tk.nextToken(System.getProperty("line.separator"));
		str = str.trim();
		if(!(str.startsWith("-----") && str.endsWith("-----"))) {
			eh.ErrorFatal("Could not understand what the unrar utility trying to tell me");
		}
		return str;
	}
/**
 * Check if the current stats is up to date.
 * @return true if the stats is up to date, otherwise false.
 */	
	protected boolean StatsUpToDate() { 
		if(path == null)
			return false;
		else
			return (path.equals(ur.path) && fileList.size() > 0);
	}
	
	public int GetFileCount() {
		if(!StatsUpToDate())
			GenerateStats();
		return fileList.size();
	}
}

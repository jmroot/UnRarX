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
 * @version v0.0.2
 * @author Daniel Aarno
 */


public class UnrarInterpreter extends Thread{
	private boolean isDone = true;
	private double percent = 0;
	private UnrarRunner ur;
	private ErrorHandler eh;
	private InputStream unrarStdOut = null;
	private String path;
	private int noFiles = -1;
	private Vector fileList = new Vector();
	private Vector listInfo = new Vector();
	private String status = "idle", currentFile = "";

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
		status = "Extracting files";
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
		int i, file = 0;
		String str="",tmp;
		String endl = System.getProperty("line.separator");
		StringTokenizer tk;

		isDone = false;
		BufferedReader r = new BufferedReader(new InputStreamReader(ur.RunUnrar()));
		
		try {
			str = RemoveGarbage(r,2);
			while(true) {
				sleep(200);
				if((i = r.read(buf, 0, 0x400)) > 0)
					str += String.valueOf(buf, 0, i);
				else
					break;
				tk = new StringTokenizer(str, endl);
				i = tk.countTokens();
				if(i > 1 || str.endsWith(endl)) {
					if(str.startsWith("All OK"))	//Done
						break;
				/*
					if(Check for query here)
						DoQuery();
				*/
					for(i = 1; i < tk.countTokens(); i++) {
						str = tk.nextToken();
						file++;
					}
					if(!str.endsWith("Ok"))
						eh.ErrorMsg("Error: " + str);
					for(i = 0; str.charAt(i) != ' ';i++);
					str = str.substring(i, str.length() - 2); //Not valid if not Ok
					currentFile = str.trim();
					str = tk.nextToken();
					if(GetFileCount() > 10)
						percent = (file / (double)GetFileCount())*100.0;
					else
						percent = -1.0;
				}
			}
		} catch(Exception e) { };
				
		status = "idle";
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
			return percent;
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
		return currentFile;
	}
/**
 * Retrieves status about the decompression phase. This could be the last line
 * written to stdout by the unrar helper application. But it is not 
 * necessarily soo.	
 * @return A String containing the status of the expansion process. This should
 * be in a human-readable format.
 */
	public String GetStatus() {
		return status;
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
		
		CreateFileList(tmpRunner.RunUnrar(path));
	}
	
	protected Vector CreateFileList(InputStream inStream) {
		//This works, but needs to be cleaned and is error-prone.
		char[] buf = new char[0x400];
		int i=0;
		String str = "";
		String endl = System.getProperty("line.separator");
		StringTokenizer tk;
		Vector lines = new Vector();
		
		BufferedReader r = new BufferedReader(new InputStreamReader(inStream));
		try {
			//Strip out the first 8 lines (double \n gives < 4 with tokenizer)
			str = RemoveGarbage(r,4);
			str = AddToFileList(str);
			while(true) {
				sleep(200);
				if((i = r.read(buf, 0, 0x400)) > 0)
					str += String.valueOf(buf, 0, i);
				else
					break;
				tk = new StringTokenizer(str, endl);
				i = tk.countTokens();
				if(i > 1 || str.endsWith(endl)) {
					if(str.startsWith("-----------------------------------------------------------------------------"))
						break;
					str = AddToFileList(str);
				}
			}
		} catch(Exception e) { System.out.println(e); System.exit(-5); }
		
		
		
/*		for(i = 0; i < fileList.size(); i++)
			System.out.println((String)fileList.get(i));
*/		
		return null;
	}

/**
 * Strips the "garbage" ie the header with copyright and more from the
 * beginning of unrar output.
 * @param r a BufferedReader wrapped around unrar's stdout.
 * @return The remainder of what was read from the buffer.
 */
	private String RemoveGarbage(BufferedReader r, int size) {
		char[] buf = new char[0x400];
		String str="";
		String endl = System.getProperty("line.separator");
		int lines = 0, i;
		StringTokenizer tk = null;
		boolean removeEndl = true;
		
		try {
			while(lines < size + 1) {
				sleep(500);
				if((i = r.read(buf, 0, 0x400)) > 0)
					str += String.valueOf(buf, 0, i);
				else
					eh.ErrorFatal("Could not understand what unrar is telling me");
				tk = new StringTokenizer(str, endl);
				lines = tk.countTokens();
			} 
		} catch(Exception e) { System.out.println(e); System.exit(-5); }
		removeEndl = !str.endsWith(endl);

		str = new String("");
		for(i = 0; i < size; i++)
			tk.nextToken();
		while(tk.hasMoreTokens())
			str += (tk.nextToken() + endl);
		if(removeEndl)
			str = new String(str.toCharArray(), 0, str.length() - endl.length());
		return str;
	}
	
	private String AddToFileList(String str) {
		String endl = System.getProperty("line.separator");
		boolean endlAtEnd = str.endsWith(endl);
		int i;
		StringTokenizer lineToken = new StringTokenizer(str, endl);
		StringTokenizer spaceToken = new StringTokenizer(" ");
		String name = "", info, tmp;

		while(lineToken.hasMoreTokens()) {
			if(name.equals(""))
				name = lineToken.nextToken();
			if(!lineToken.hasMoreTokens())
				break;
			info = lineToken.nextToken();
			
			//sizof string check here
			if(!lineToken.hasMoreTokens())
				return name + endl + info + (endlAtEnd ? endl : "");
			
			if(info.startsWith("        ")) {	//Broken line
				fileList.add(name);
				listInfo.add(info);
				name = info = "";
			}
			else {	//on one line
				for(i = 21; i > 0 && name.charAt(i) != ' '; i--);
				tmp = new String(name.toCharArray(),0,i);
				fileList.add(tmp.trim());
				tmp = new String(name.toCharArray(),i + 1,name.length() - i - 1);
				listInfo.add(tmp.trim());
				name = info;
				info = "";
			}
		}
		if(endlAtEnd)	//What hapends if name=null, endlAtEnd = true?
				return name + endl;
			else
				return name;
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


/**
 * Returns the number of files in the archive connected to the UnrarRunner.
 * @return Number of files in the archive.
 */	
	public int GetFileCount() {
		if(!StatsUpToDate())
			GenerateStats();
		return fileList.size();
	}
}

/**************************************80**************************************/
/*	Use tab width = 5, indent level width = 5, and a fixed-font in a >= 80 
 *	character wide window to view this file.
 */

/*	This class provides settings-file management to java applications. It can
 *	be used to read and store settings matching key-words from a file.
 */

/*
 *	SettingsHandler.java
 *	Project: N/A
 *
 *	Created by Daniel Aarno on Wed Jan 09 2002.
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
 * This class manages settings in a file. You may use it to retrieve or store settings
 * in a file. The settings are stored in the following way: Each line starts with a keyphrase
 * associated with the setting then comes a '=' and then the setting itself. Example (three lines): 
 * host=123.200.200.200<BR>
 * port=4711<BR>
 * keyphrase=whatever
 * @version v0.2.0
 * @author Daniel Aarno
 */
public class SettingsHandler
{
private File sf = null;
/**
 * Creates a new empty SettingsHandler.
 */
public SettingsHandler() { }
/**
 * Creates a SettingsHandler and binds it to a settings file.
 * @param path path denoting the settings file.
 */
public SettingsHandler(String path) { sf = new File(path); }
/**
 * Creates a SettingsHandler and binds it to a settings file.
 * @param settingsFile a File object bound to a settings file.
 */
public SettingsHandler(File settingsFile) { ChooseFile(settingsFile); }
/**
 * Binds this SettingsHandler to a settings file.
 * @param settingsFile a File object bound to a settings file.
 */
public void ChooseFile(File settingsFile) { sf = settingsFile; }
/**
 * Retrieve a setting corresponding a keyphrase.
 * @param settingsString the keyphrase of the setting we want to retrieve.
 * @return The settings corresponding to the keyphrase.
 */
public String LoadSetting(String settingsString) throws Exception
{
	int n;
	char[] buf = new char[0x401];
	String setting;
	
	if(sf == null)
		return null;	//Throw fnf
	if(!sf.exists())
		return null;
	
	FileReader in = new FileReader(sf);
	do
	{
		n = in.read(buf,0,0x400);	//Read 1kB (1024 characters)
		if(n == -1)
			return null;
		String str = new String(buf);
		StringTokenizer st = new StringTokenizer(str, "\n");
		StringTokenizer st2;
		while(st.hasMoreTokens())
		{
			setting = st.nextToken();
			st2 = new StringTokenizer(setting, "=");
			if(st2.hasMoreTokens())
				setting = st2.nextToken();
			if(settingsString.equals(setting) && st2.hasMoreTokens())
			{
				setting = st2.nextToken();
				in.close();
				return setting;
			}
		}
	}while(n == 0x400);
	in.close();
	return null;
}
/**
 * Store settings in the settings file.
 * @param setting the string to store in the file. Formatted in the following way: keyphrase=setting
 * @return The setting previously associated with the keyphrase.
 */
public String StoreSetting(String setting) throws Exception
{
	StringTokenizer st = new StringTokenizer(setting, "=");
	String str = st.nextToken();
	String key;
	int n;
	char[] buf = new char[0x401];
	
	str = LoadSetting(str);
	if(str == null)
	{
		FileWriter out = new FileWriter(sf.toString(), true);
		key = setting + "\n";
		out.write(key, 0, key.length());
		out.flush();
		out.close();
		return str;
	}
	
	FileReader in = new FileReader(sf);
	
	FileWriter out = new FileWriter(sf);
	do
	{
		n = in.read(buf,0,0x400);	//Read 1kB (1024 characters)
		if(n == -1)
			throw new EOFException();
		in.close();
		key = new String(buf);
		st = new StringTokenizer(str, "\n");
		while(st.hasMoreTokens())
		{
			key = st.nextToken();
			if(!setting.equals(key))
			{
				out.write(key,0,key.length());
			}
		}
	}while(false);
	
	out.flush();
	out.close();
	
	return str;
}

/**
 * Copys the current settings file to another location.
 * @param path The path to the new file.
 * @throws Everything
 */
	public void CopySettings(String to) throws Exception {
		InputStream in = null;
		OutputStream out = null;
		String from = sf.getPath(); 
		try {
			in = new FileInputStream(from);
			out = new FileOutputStream(to);
			int length = in.available(); // danger, use only for "small" files!
			byte[] bytes = new byte[length];
			in.read(bytes);
			out.write(bytes);         
		} finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}
	}
/**
 * Converts a home-relative path to an absolute path. 
 * It first checks to see if the path actually is a home relative path
 * by examining the start of path checking for '~'. This method currently
 * only works if home is denoted by '~' (as *all* UNIX like systems).
 * @param path The path to convert to an absolute path.
 * @return The absolute path. If the path does is not in the expected format
 * path it self is returned.
 */	
	static public String ConvertHomePath(String path) {
		String homeID = "~";
		
		homeID += File.separator;
		if(!path.startsWith(homeID))
			return path;
			
		return System.getProperty("user.home") + path.substring(1);
	}

}

/********************************* History ************************************/
//2002-05-02 -- Added ConvertHomePath()

/**************************************80**************************************/
/*	Use tab width = 5, indent level width = 5, and a fixed-font in a >= 80 
 *	character wide window to view this file.
 */

/*	This is a GUI frontend to the unrar commandline tool.
 *	unrar is Copyright (c) 1993-99 Eugene Roshal
 */

/*
 *	GlobalObjects.java
 *	Project: Unrar X
 *
 *	Created by Daniel Aarno on Wed Apr 10 2002.
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


public class GlobalObjects {
	public static ErrorHandler theError;
	public static SettingsHandler theSettings;
	public static UnrarRunner theRunner;
}
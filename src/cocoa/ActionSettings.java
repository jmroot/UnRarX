/**************************************80**************************************/
/*	Use tab width = 5, indent level width = 5, and a fixed-font in a >= 80 
 *	character wide window to view this file.
 */

/*	This is a GUI frontend to the unrar commandline tool.
 *	unrar is Copyright (c) 1993-99 Eugene Roshal
 */

/*
 *	ActionSettings.java
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

/**
 * The ActionSettings class is responsible for retrieving and setting the 
 * action to perform in the GUI.
 * @version v0.0.0
 * @author Daniel Aarno
 */
public class ActionSettings {

	NSPopUpButton action;
	GlobalObjects g = new GlobalObjects();
/**
 * Gets called when the user invokes the Popup-button that selects the action
 * to perform.
 * @param sender The NSPopUpButton that caused the event.
 */
	public void SetAction(Object sender) {
		int i = ((NSPopUpButton)sender).indexOfSelectedItem();
		g.theRunner.action = i;
	}
}

/*
 UnRarX, Mac OS X GUI for rar and par file extraction.
 Copyright © 2003  Peter Noriega

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 RAR and unRAR are copyright Eugene Roshal.

 THE UNRAR UTILITY ARE DISTRIBUTED "AS IS".  NO WARRANTY OF ANY KIND IS EXPRESSED OR IMPLIED.  YOU USE AT YOUR OWN RISK.  THE AUTHORS WILL NOT BE LIABLE FOR DATA LOSS, DAMAGES, LOSS OF PROFITS OR ANY OTHER KIND OF LOSS WHILE USING OR MISUSING THIS SOFTWARE.

 Thanks for using UnRarX for Mac OS X.
 ________________________________________
 */

#import "Controller.h"
#import "DropView.h"

@implementation DropView

- (unsigned int)draggingEntered:(id <NSDraggingInfo>)sender {
    NSPasteboard *pboard;
    pboard = [sender draggingPasteboard];
    //NSLog(@"Drag Entered");
    if ([[pboard types] indexOfObject:NSFilenamesPboardType] != NSNotFound) {
        return NSDragOperationCopy;
    }
    return NSDragOperationNone;
}

- (unsigned int)draggingUpdated:(id <NSDraggingInfo>)sender {
    NSPasteboard *pboard;
    pboard = [sender draggingPasteboard];
    //NSLog(@"Drag Updated");
    if ([[pboard types] indexOfObject:NSFilenamesPboardType] != NSNotFound) {
        return NSDragOperationCopy;
    }
    return NSDragOperationNone;
}

- (void)draggingExited:(id <NSDraggingInfo>)sender {
    //NSLog(@"Drag Exited");
    return;
}

- (BOOL)prepareForDragOperation:(id <NSDraggingInfo>)sender {
    //NSLog(@"Prepare for Drag");
    return YES;
}

- (BOOL)performDragOperation:(id <NSDraggingInfo>)sender {
    //NSLog(@"Perform Drag Operation");
    return YES;
}

- (void)concludeDragOperation:(id <NSDraggingInfo>)sender {
    NSPasteboard *pboard = [sender draggingPasteboard];
    NSArray *filenames = [pboard propertyListForType:NSFilenamesPboardType];
    //NSLog(@"Conclude Drag");
    [[NSApp delegate] examineFilenames:filenames];
    //[NSThread detachNewThreadSelector:@selector(examineFilenames:) toTarget:[NSApp delegate] withObject:filenames];
}


@end

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

#import <Cocoa/Cocoa.h>

#define EXTRACT_PROBLEM NSLocalizedStringFromTable(@"Extraction Problem", @"Localized", "Title for Extraction Problem Alert")
#define VERIFY_PROBLEM NSLocalizedStringFromTable(@"Verification Problem", @"Localized", "Title for Verification Problem Alert")
#define OK NSLocalizedStringFromTable(@"OK", @"Localized", "Confirmation Text")
#define EXTRACT_NO_LOCATION_ERROR NSLocalizedStringFromTable(@"Please select a location to extract file!", @"Localized", "Extraction Problem - No location selected error")
#define EXTRACT_NO_FILE_ERROR NSLocalizedStringFromTable(@"Please select a file to extract!", @"Localized", "Extraction Problem - No file selected error")
#define TEST_NO_FILE_ERROR NSLocalizedStringFromTable(@"Please select a file to test!", @"Localized", "Test Problem - No file selected error")
#define INFO_NO_FILE_ERROR NSLocalizedStringFromTable(@"Please select a file to list contents!", @"Localized", "Info Problem - No file selected error")




@interface Controller : NSObject
{
    IBOutlet id fileField;
    IBOutlet id progressBar;
    IBOutlet id window;
    IBOutlet id prefsWindow;
    IBOutlet id locationWindow;
    IBOutlet id passwordWindow;
    IBOutlet id taskTextField;
    IBOutlet id textScrollView;
    IBOutlet id pathDisplay;
    IBOutlet id passwordField;
    IBOutlet id extractionView;
    IBOutlet id dropView;
    IBOutlet id fileView;
    IBOutlet id locationPullDown;
    IBOutlet id overwriteCB;
    IBOutlet id recurseCB;
    IBOutlet id assumeCB;
    IBOutlet id keepCB;
    IBOutlet id partorarCB;
    IBOutlet id tableView;
    //IBOutlet id textView;
    

    NSTask *unrar;
    NSTask *par;
    NSString *filePath;
    NSString *extractPath;
    NSString *extractPathDisplay;
    NSString *filename;
    NSToolbar *toolbar;
    NSMutableDictionary *items;
    NSMutableArray *locations;
    NSUserDefaults *prefs;
    
}

- (void)awakeFromNib;
- (void)dealloc;
- (void)checkPrefs;
- (IBAction)browse:(id)sender;
- (IBAction)extract:(id)sender;
- (IBAction)test:(id)sender;
- (IBAction)list:(id)sender;
- (IBAction)clearLog:(id)sender;
- (void)processFile:(NSString *)path toPath:(NSString *)extractPath;
- (void)testFile:(NSString *)path;
- (void)listFile:(NSString *)path;
- (void)recoverFile:(NSString *)path;
- (IBAction)setLocation:(id)sender;
- (void)appendOutput:(NSString *)output;
- (void)scrollToVisible:(id)ignore;
- (void)addToTextViewContentOfString:(NSString *)theString;
- (IBAction)stop:(id)sender;
- (void)examineFilenames:(NSArray *)  filenames;
- (IBAction)action:(id)sender;
- (IBAction)customize:(id)sender;
- (IBAction)showhide:(id)sender;
- (IBAction)openPrefsSheet:(id)sender;
- (IBAction)closePrefsSheet:(id)sender;
- (IBAction)setPrefs:(id)sender;
- (void)saveData;
- (NSToolbarItem *)toolbar:(NSToolbar *)toolbar itemForItemIdentifier:(NSString *)itemIdentifier willBeInsertedIntoToolbar:(BOOL)flag;
- (NSArray *)toolbarDefaultItemIdentifiers:(NSToolbar*)toolbar;
- (NSArray *)toolbarAllowedItemIdentifiers:(NSToolbar*)toolbar;
- (BOOL)application:(NSApplication *)sender openFile:(NSString *)name;
- (IBAction)showAboutBox:(id)sender;
- (IBAction)openLocationsSheet:(id)sender;
- (IBAction)closeLocationsSheet:(id)sender;
- (NSDictionary *)createRecord;
- (IBAction)addRecord:(id)sender;
- (IBAction)deleteRecord:(id)sender;
- (IBAction)openPasswordSheet:(id)sender;
- (IBAction)closePasswordSheet:(id)sender;


// NSTableDataSource methods
- (int)numberOfRowsInTableView:(NSTableView *)aTableView;
- (id)tableView:(NSTableView *)aTableView
objectValueForTableColumn:(NSTableColumn *)aTableColumn
            row:(int)rowIndex;
- (void)tableView:(NSTableView *)aTableView
   setObjectValue:(id)anObject
   forTableColumn:(NSTableColumn *)aTableColumn
              row:(int)rowIndex;







@end

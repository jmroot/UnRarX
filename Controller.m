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
#import "AboutBox.h"

@implementation Controller
 
//Loading Extractions Locations from Prefs
- (void)_loadLocations
{
    int count, total = [locations count];
    [locationPullDown removeAllItems];
    [locationPullDown addItemWithTitle:NSLocalizedStringFromTable(@"File Directory", @"Localized", @"Label for File Directory Option in Pulldown")];        
    [locationPullDown addItemWithTitle:NSLocalizedStringFromTable(@"Desktop", @"Localized", @"Label for Desktop Option in Pulldown")];        
    [locationPullDown addItemWithTitle:NSLocalizedStringFromTable(@"Other...", @"Localized", @"Label for Other Option in Pulldown")];        
    for(count = 0; count < total; count++)
    {
        [locationPullDown insertItemWithTitle:[[locations objectAtIndex:count] objectForKey:@"Name"] atIndex:count+2];        
    }
    [[locationPullDown menu] insertItem:[NSMenuItem separatorItem] atIndex:total+2];
    [locationPullDown addItemWithTitle:NSLocalizedStringFromTable(@"Manage...", @"Localized", @"Label for Manage Option in Pulldown")];        
}

- (void) awakeFromNib 
{
    NSToolbarItem *item;
    NSRect rect =  [window frame];
    NSRect eRect = [extractionView frame];
    NSRect tRect = [textScrollView frame];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(finishedTask:)name:NSTaskDidTerminateNotification object:nil];
    filePath = nil;
    extractPath = nil;
    extractPathDisplay = nil;
    filename = nil;
    items = nil;
    unrar = nil;
    par = nil;    
    
    items=[[NSMutableDictionary alloc] init];

    item=[[NSToolbarItem alloc] initWithItemIdentifier:@"Browse"];
    [item setPaletteLabel:NSLocalizedStringFromTable(@"Browse", @"Localized", @"Toolbar Palette Label for Browse for Files")]; 
    [item setLabel:NSLocalizedStringFromTable(@"Browse Files", @"Localized", @"Toolbar Label for Browse for Files")]; 
    [item setToolTip:NSLocalizedStringFromTable(@"Browse for Files", @"Localized", @"Toolbar Tooltip for Browse for Files")]; 
    [item setImage:[NSImage imageNamed:@"browse"]]; 
    [item setTarget:self]; 
    [item setAction:@selector(browse:)];
    [items setObject:item forKey:@"Browse"]; 
    
    item=[[NSToolbarItem alloc] initWithItemIdentifier:@"Extract"];
    [item setPaletteLabel:NSLocalizedStringFromTable(@"Extract", @"Localized", @"Toolbar Palette Label for Extract Files")]; 
    [item setLabel:NSLocalizedStringFromTable(@"Extract Files", @"Localized", @"Toolbar Label for Extract Files")]; 
    //[item setEnabled:YES];
    [item setToolTip:NSLocalizedStringFromTable(@"Extract Files to Disk", @"Localized", @"Toolbar Tooltip for Extract Files")]; 
    [item setImage:[NSImage imageNamed:@"extract"]]; 
    [item setTarget:self]; 
    [item setAction:@selector(action:)];
    [items setObject:item forKey:@"Extract"]; 

    item=[[NSToolbarItem alloc] initWithItemIdentifier:@"Test"];
    [item setPaletteLabel:NSLocalizedStringFromTable(@"Test", @"Localized", @"Toolbar Palette Label for Test Files")]; 
    [item setLabel:NSLocalizedStringFromTable(@"Test Files", @"Localized", @"Toolbar Label for Test Files")]; 
    [item setToolTip:NSLocalizedStringFromTable(@"Test RAR Files", @"Localized", @"Toolbar Tooltip for Test Files")]; 
    [item setImage:[NSImage imageNamed:@"test"]]; 
    [item setTarget:self]; 
    [item setAction:@selector(test:)];
    [items setObject:item forKey:@"Test"]; 

    item=[[NSToolbarItem alloc] initWithItemIdentifier:@"Info"];
    [item setPaletteLabel:NSLocalizedStringFromTable(@"Info", @"Localized", @"Toolbar Palette Label for Info on RAR Archive")]; 
    [item setLabel:NSLocalizedStringFromTable(@"Get Info", @"Localized", @"Toolbar Label for Info on RAR Archive")]; 
    [item setToolTip:NSLocalizedStringFromTable(@"List Contents of Archive", @"Localized", @"Toolbar Tooltip for Info on RAR Archive")];
    [item setImage:[NSImage imageNamed:@"info"]]; 
    [item setTarget:self]; 
    [item setAction:@selector(list:)];
    [items setObject:item forKey:@"Info"]; 

    item=[[NSToolbarItem alloc] initWithItemIdentifier:@"Password"];
    [item setPaletteLabel:NSLocalizedStringFromTable(@"Password", @"Localized", @"Toolbar Palette Label for Password Option")]; 
    [item setLabel:NSLocalizedStringFromTable(@" Use Password", @"Localized", @"Toolbar Label for Password Option")]; 
    [item setToolTip:NSLocalizedStringFromTable(@"Use Password for RAR", @"Localized", @"Toolbar Tooltip for Password Option")]; 
    [item setImage:[NSImage imageNamed:@"password"]]; 
    [item setTarget:self]; 
    [item setAction:@selector(openPasswordSheet:)];
    [items setObject:item forKey:@"Password"];     

    item=[[NSToolbarItem alloc] initWithItemIdentifier:@"Filename"];
    [item setPaletteLabel:NSLocalizedStringFromTable(@"Filename", @"Localized", @"Toolbar Palette Label for Filename")]; 
    [item setLabel:NSLocalizedStringFromTable(@"RAR Filename", @"Localized", @"Toolbar Label for Filename")]; 
    [item setToolTip:NSLocalizedStringFromTable(@"RAR Filename Display", @"Localized", @"Toolbar Tooltip for Filename")];
    [item setView: fileView];
    [item setMinSize:NSMakeSize(30, NSHeight([fileView frame]))];
    [item setMaxSize:NSMakeSize(600, NSHeight([fileView frame]))];
    [item setTarget:self];
    [items setObject:item forKey:@"Filename"];

    [item release];

    toolbar=[[NSToolbar alloc] initWithIdentifier:@"toolbar"];
    [toolbar setDelegate:self];
    [toolbar setAllowsUserCustomization:YES];
    [toolbar setDisplayMode:NSToolbarDisplayModeLabelOnly];
    [toolbar setAutosavesConfiguration:YES];
    [dropView registerForDraggedTypes:[NSArray arrayWithObject:NSFilenamesPboardType]];
    [[extractionView retain] removeFromSuperview];
    rect.origin.y += eRect.size.height;
    rect.size.height -= eRect.size.height;
    tRect = [textScrollView frame];
    tRect.origin.y = 0.0;
    [window setFrame:rect display:NO animate:NO];
    [textScrollView setFrame:tRect];
    [textScrollView display];
    
    [window setToolbar:toolbar];
    [window makeKeyAndOrderFront:nil];
}

- (void)applicationDidFinishLaunching:(NSNotification *)aNotification
{
    [self checkPrefs];
}

- (void)dealloc
{
    [self saveData];
    [toolbar release];
    [items release];
    [filePath release];
    [extractPath release];
    [extractPathDisplay release];
    [filename release];
    [unrar release];
    [par release];
    [locations release];
    [prefs release];
}

- (void)checkPrefs
{
    prefs = [[NSUserDefaults standardUserDefaults] retain];
    if([prefs boolForKey:@"Overwrite"] == nil)
    {
        [prefs setObject:@"YES" forKey:@"Overwrite"];
    }
    if([prefs boolForKey:@"Overwrite"] == YES)
    {
        [overwriteCB setState:NSOnState];
    }
    if([prefs boolForKey:@"Recurse"] == nil)
    {
        [prefs setObject:@"NO" forKey:@"Recurse"];
    }
    if([prefs boolForKey:@"Recurse"] == YES)
    {
        [recurseCB setState:NSOnState];
    }
    if([prefs boolForKey:@"Assume Yes"] == nil)
    {
        [prefs setObject:@"NO" forKey:@"Assume Yes"];
    }
    if([prefs boolForKey:@"Assume Yes"] == YES)
    {
        [assumeCB setState:NSOnState];
    }
    if([prefs boolForKey:@"Keep Broken"] == nil)
    {
        [prefs setObject:@"NO" forKey:@"Keep Broken"];
    }
    if([prefs boolForKey:@"Keep Broken"] == YES)
    {
        [keepCB setState:NSOnState];
    }
    if([prefs boolForKey:@"Partorar"] == nil)
    {
        [prefs setObject:@"NO" forKey:@"Partorar"];
    }
    if([prefs boolForKey:@"Partorar"] == YES)
    {
        [partorarCB setState:NSOnState];
    }
    
    
    if ([prefs arrayForKey:@"Locations"] != nil )
    {
        locations = [[NSMutableArray alloc] initWithArray:[prefs arrayForKey:@"Locations"]];
    }
    else
    {
        locations = [[NSMutableArray alloc] init];
    }
    [self _loadLocations];
}

- (void)_doextract:(id)op
{
    NSFileManager *manager = [NSFileManager defaultManager];
    if([[op filenames] count] == 0)
            return;
    filePath = [[op filenames] lastObject];
    [filePath retain];
    filename = [manager displayNameAtPath:filePath];
    [filename retain];
    [fileField setStringValue:filename];
    [[items objectForKey:@"Filename"] setLabel:filename];
    [[items objectForKey:@"Extract"] setLabel:@"Extract"];
    [[items objectForKey:@"Extract"] setImage:[NSImage imageNamed:@"extract"]]; // image
    [[items objectForKey:@"Extract"] setEnabled:YES];
    //if(extractPath == nil)
        //[self setLocation:nil];
}

- (void)_setpath:(id)op
{
    NSString *extension;
    if([[op filenames] count] == 0)
    {
        return;
    }
    extractPath = [[op filenames] lastObject];
    extension = [extractPath pathExtension];
    if(![[extractPath pathExtension] isEqualTo:@""])
    {
        //NSLog(@"This is not a Directory");
        extractPath = [extractPath stringByDeletingLastPathComponent];
        [extractPath retain];
        return;
    }
    [extractPath retain];
    [self addRecord:nil];
    //NSLog(@"Extract Path is %@", extractPath);
}


- (IBAction)browse:(id)sender
{        
    NSOpenPanel *op = [NSOpenPanel openPanel];
    [op beginSheetForDirectory:NSHomeDirectory() file:nil types:[NSArray arrayWithObject:@"rar"] modalForWindow:window modalDelegate:self didEndSelector:@selector(_doextract:) contextInfo:op];

}

- (IBAction)action:(id)sender;
{
    if([[[items objectForKey:@"Extract"] label] isEqualTo:@"Extract"] == true)
    {
        [self extract:nil];
        return;
    }
    if([[[items objectForKey:@"Extract"] label] isEqualTo:@"Stop"] == true)
    {
        [self stop:nil];
        return;
    }
    else
    {
        [[items objectForKey:@"Extract"] setEnabled:NO];
        return;
    }
}

- (IBAction)extract:(id)sender
{
    if(![[fileField stringValue] isEqualTo:@""])
    {
        [self setLocation:nil];
        //NSLog(@"Extract Path is %@", extractPath);
        if(extractPath == nil)
        {
            NSBeginAlertSheet(EXTRACT_PROBLEM, OK, nil, nil, window, self, nil, nil, nil, EXTRACT_NO_LOCATION_ERROR, nil);
            return;
        }
        [pathDisplay setStringValue:@" "];
        [pathDisplay setStringValue:extractPath];
        [[items objectForKey:@"Extract"] setLabel:@"Stop"];
        [[items objectForKey:@"Extract"] setImage:[NSImage imageNamed:@"stop"]]; // image
        [[items objectForKey:@"Extract"] setToolTip:[NSString stringWithFormat:@"Stop Current Task"]];
        [[items objectForKey:@"Extract"] setEnabled:YES];
        [self processFile:filePath toPath:extractPath];
    }
    else
    {
        NSBeginAlertSheet(EXTRACT_PROBLEM, OK, nil, nil, window, self, nil, nil, nil, EXTRACT_NO_FILE_ERROR, nil);
    }
}

- (IBAction)test:(id)sender
{
    if(![[fileField stringValue] isEqualTo:@""])
    {
        [self testFile:filePath];
    }
    else
    {
        NSBeginAlertSheet(VERIFY_PROBLEM, OK, nil, nil, window, self, nil, nil, nil, TEST_NO_FILE_ERROR, nil);
    }
}

- (IBAction)list:(id)sender
{
    if(![[fileField stringValue] isEqualTo:@""])
    {
        [self listFile:filePath];
    }
    else
    {
        NSBeginAlertSheet(VERIFY_PROBLEM, OK, nil, nil, window, self, nil, nil, nil, INFO_NO_FILE_ERROR, nil);
    }
}

- (IBAction)clearLog:(id)sender
{
    
}


- (IBAction)stop:(id)sender;
{
    //NSLog(@"Stopping");
    [unrar terminate];
}

- (void)examineFilenames:(NSArray *)  filenames
{
    NSString *name;
    NSString *path;
    NSString *extension;
    NSString *subextension;
    NSFileManager *manager = [NSFileManager defaultManager];
    //NSLog(@"Examining Filenames");
    if([filenames count] > 0)
    {
        path = [filenames objectAtIndex:0];
        name = [manager displayNameAtPath:path];
        extension  = [name pathExtension];
        filePath = [filenames objectAtIndex:0];
        [filePath retain];
        //NSLog(@"FilePath is %@", filePath);
        filename = [manager displayNameAtPath:filePath];
        [filename retain];
        subextension = [extension substringToIndex:1];
        //NSLog(@"FileName is %@", filename);
        [fileField setStringValue:filename];
        [fileField setTextColor:[NSColor blueColor]];        
        if([extension compare:@"rar" options:NSCaseInsensitiveSearch] == NSOrderedSame)
        {
            //NSLog(@"Valid RAR File");
            [[items objectForKey:@"Filename"] setLabel:filename];
            [[items objectForKey:@"Extract"] setLabel:@"Extract"];
            [[items objectForKey:@"Extract"] setImage:[NSImage imageNamed:@"extract"]]; // image
            [[items objectForKey:@"Extract"] setEnabled:YES];
            //if(extractPath == nil)
                //[self setLocation:nil];
            [self extract:nil];
            return;
        }
        if([extension compare:@"ace" options:NSCaseInsensitiveSearch] == NSOrderedSame)
        {
            //NSLog(@"Valid ACE File");
            //[self extractAce:filePath];
            //return;
        }        
        if([extension compare:@"par" options:NSCaseInsensitiveSearch] == NSOrderedSame)
        {
            //NSLog(@"Valid PAR File");
            [self recoverFile:filePath];
            return;
        }
        if([subextension compare:@"p" options:NSCaseInsensitiveSearch] == NSOrderedSame)
        {
            //NSLog(@"Valid PAR File");
            [self recoverFile:filePath];
            return;
        }        
        else
        {
            //NSLog(@"Still Attempt to Extract");
            [[items objectForKey:@"Filename"] setLabel:filename];
            [[items objectForKey:@"Extract"] setLabel:@"Extract"];
            [[items objectForKey:@"Extract"] setImage:[NSImage imageNamed:@"extract"]]; // image
            [[items objectForKey:@"Extract"] setEnabled:YES];
            [self extract:nil];
            return;
        }
    }
    else
    {
        return;    
    }
}


- (void)processFile:(NSString *)path toPath:(NSString *)location
{
    NSMutableArray *args = [NSMutableArray array];
    NSPipe *taskPipe = [NSPipe pipe];
    NSString *command = @"x";
    NSString *overwrite = @"-o+";
    NSString *recurse = @"-r";
    NSString *assumeYes = @"-y";
    NSString *keepBroken = @"-kb";
    NSString *password = @"-p";
    NSRect rect =  [window frame];
    NSRect eRect = [extractionView frame];
    NSRect tRect = [textScrollView frame];
    int emask = [textScrollView autoresizingMask];
    
    unrar = [[NSTask alloc] init];

    rect.origin.y -= eRect.size.height;
    rect.size.height += eRect.size.height;
    emask = [extractionView autoresizingMask];
    [textScrollView setAutoresizingMask:NSViewMinYMargin];
    tRect = [textScrollView frame];
    [window setFrame:rect display:YES animate:YES];
    [textScrollView setAutoresizingMask:emask];
    [[window contentView] addSubview:extractionView];
    tRect.origin.y = eRect.size.height;
    [textScrollView setFrame:tRect];
    [extractionView display];
    [textScrollView display];
    
    /* set standard I/O, here to a NSPipe */
    [unrar setStandardOutput:taskPipe];
    [unrar setStandardError:taskPipe];
    
    /* set arguments */
    [args addObject:command];
    if([prefs boolForKey:@"Overwrite"] == YES)
    {
        //NSLog(@"Overwrite Files");
        [args addObject:overwrite];
    }
    if([prefs boolForKey:@"Recurse"] == YES)
    {
        [args addObject:recurse];
    }
    if([prefs boolForKey:@"Assume YES"] == YES)
    {
        [args addObject:assumeYes];
    }
    if([prefs boolForKey:@"Keep Broken"] == YES)
    {
        //NSLog(@"Keep Broken Extracted Files");
        [args addObject:keepBroken];
    }

    //NSLog(@"Checking Password");
    if(![[passwordField stringValue] isEqualTo:@""])
    {
        //NSLog(@"Password is not null");
        password = [password stringByAppendingString:[passwordField stringValue]];
        //NSLog(@"Password is %@", password);
        [args addObject:password];
        [passwordField setStringValue:@""];
    }
    //NSLog(@"Path is %@", path);
    //NSLog(@"Location is %@", location);
    [args addObject:path];
    [args addObject:location];
    [unrar setArguments:args];
    
    /* set the path of the executable */
    [unrar setLaunchPath:[[NSBundle mainBundle] pathForResource:@"unrar" ofType:nil]];
    
    /* we want taskPipe to send notifications to be able to grab the output */
    [[taskPipe fileHandleForReading] readInBackgroundAndNotify];

    [progressBar startAnimation:self];

    [unrar launch];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(printTaskOutput:) name:NSFileHandleReadCompletionNotification object:[[unrar standardOutput] fileHandleForReading]];

    //[unrar waitUntilExit];
}

- (void)testFile:(NSString *)path
{
    NSMutableArray *args = [NSMutableArray array];
    NSPipe *taskPipe = [NSPipe pipe];
    NSString *command = @"t";
    NSRect rect =  [window frame];
    NSRect eRect = [extractionView frame];
    NSRect tRect = [textScrollView frame];
    int emask = [textScrollView autoresizingMask];

    unrar = [[NSTask alloc] init];

    rect.origin.y -= eRect.size.height;
    rect.size.height += eRect.size.height;
    emask = [extractionView autoresizingMask];
    [textScrollView setAutoresizingMask:NSViewMinYMargin];
    tRect = [textScrollView frame];
    [window setFrame:rect display:YES animate:YES];
    [textScrollView setAutoresizingMask:emask];
    [[window contentView] addSubview:extractionView];
    tRect.origin.y = eRect.size.height;
    [textScrollView setFrame:tRect];
    [extractionView display];
    [textScrollView display];

    /* set standard I/O, here to a NSPipe */
    [unrar setStandardOutput:taskPipe];
    [unrar setStandardError:taskPipe];

    /* set arguments */
    [args addObject:command];
    [args addObject:path];
    [unrar setArguments:args];

    /* set the path of the executable */
    [unrar setLaunchPath:[[NSBundle mainBundle] pathForResource:@"unrar" ofType:nil]];

    /* we want taskPipe to send notifications to be able to grab the output */
    [[taskPipe fileHandleForReading] readInBackgroundAndNotify];

    [progressBar startAnimation:self];

    [unrar launch];

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(printTaskOutput:) name:NSFileHandleReadCompletionNotification object:[[unrar standardOutput] fileHandleForReading]];

    //[unrar waitUntilExit];
}

- (void)listFile:(NSString *)path
{
    NSMutableArray *args = [NSMutableArray array];
    NSPipe *taskPipe = [NSPipe pipe];
    NSString *command = @"v";
    NSRect rect =  [window frame];
    NSRect eRect = [extractionView frame];
    NSRect tRect = [textScrollView frame];
    int emask = [textScrollView autoresizingMask];

    unrar = [[NSTask alloc] init];

    rect.origin.y -= eRect.size.height;
    rect.size.height += eRect.size.height;
    emask = [extractionView autoresizingMask];
    [textScrollView setAutoresizingMask:NSViewMinYMargin];
    tRect = [textScrollView frame];
    [window setFrame:rect display:YES animate:YES];
    [textScrollView setAutoresizingMask:emask];
    [[window contentView] addSubview:extractionView];
    tRect.origin.y = eRect.size.height;
    [textScrollView setFrame:tRect];
    [extractionView display];
    [textScrollView display];

    /* set standard I/O, here to a NSPipe */
    [unrar setStandardOutput:taskPipe];
    [unrar setStandardError:taskPipe];

    /* set arguments */
    [args addObject:command];
    [args addObject:path];
    [unrar setArguments:args];

    /* set the path of the executable */
    [unrar setLaunchPath:[[NSBundle mainBundle] pathForResource:@"unrar" ofType:nil]];

    /* we want taskPipe to send notifications to be able to grab the output */
    [[taskPipe fileHandleForReading] readInBackgroundAndNotify];

    [progressBar startAnimation:self];

    [unrar launch];

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(printTaskOutput:) name:NSFileHandleReadCompletionNotification object:[[unrar standardOutput] fileHandleForReading]];

    //[unrar waitUntilExit];
}

- (void)recoverFile:(NSString *)path
{
    NSMutableArray *args = [NSMutableArray array];
    NSPipe *taskPipe = [NSPipe pipe];
    NSString *command = @"r";
    //NSString *move = @"-m";
    //NSString *fix = @"-f";
    //NSString *ignorecase = @"+C";
    NSRect rect =  [window frame];
    NSRect eRect = [extractionView frame];
    NSRect tRect = [textScrollView frame];
    int emask = [textScrollView autoresizingMask];
    
    par = [[NSTask alloc] init];

    rect.origin.y -= eRect.size.height;
    rect.size.height += eRect.size.height;
    emask = [extractionView autoresizingMask];
    [textScrollView setAutoresizingMask:NSViewMinYMargin];
    tRect = [textScrollView frame];
    [window setFrame:rect display:YES animate:YES];
    [textScrollView setAutoresizingMask:emask];
    [[window contentView] addSubview:extractionView];
    tRect.origin.y = eRect.size.height;
    [textScrollView setFrame:tRect];
    [extractionView display];
    [textScrollView display];
    
    /* set standard I/O, here to a NSPipe */
    [par setStandardOutput:taskPipe];
    [par setStandardError:taskPipe];

    /* set arguments */
    [args addObject:command];
    //[args addObject:move];
    //[args addObject:fix];
    //[args addObject:ignorecase];
    [args addObject:path];
    //[args addObject:location];
    [par setArguments:args];

    /* set the path of the executable */
    [par setLaunchPath:[[NSBundle mainBundle] pathForResource:@"par2" ofType:nil]];
    [par setCurrentDirectoryPath:[path stringByDeletingLastPathComponent]];
    /* we want taskPipe to send notifications to be able to grab the output */
    [[taskPipe fileHandleForReading] readInBackgroundAndNotify];
    
    [progressBar startAnimation:self];
    
    [par launch];

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(printTaskOutput:) name:NSFileHandleReadCompletionNotification object:[[par standardOutput] fileHandleForReading]];

    //[par waitUntilExit];
}

- (IBAction)setLocation:(id)sender
{
    if([locationPullDown indexOfSelectedItem] == 0)
    {
        //NSLog(@"Location is File Directory");
        extractPath = [filePath stringByDeletingLastPathComponent];
        //NSLog(@"Extract Path is %@", extractPath);
        return;
    }
    if([locationPullDown indexOfSelectedItem] == 1)
    {
        //NSLog(@"Location is Desktop");
        extractPath = [NSHomeDirectory() stringByAppendingPathComponent:@"Desktop"];
        //NSLog(@"Extract Path is %@", extractPath);
        return;
    }    
    if([[[locationPullDown selectedItem] title] isEqualTo:@"Other..."])
    {
        NSOpenPanel *op = [NSOpenPanel openPanel];
        [op setCanChooseFiles:NO];
        [op setAllowsMultipleSelection:NO];
        [op setCanChooseDirectories:YES];
        [op beginSheetForDirectory:NSHomeDirectory() file:nil types:nil modalForWindow:window modalDelegate:self didEndSelector:@selector(_setpath:) contextInfo:op];
        return;
    }
    if([[[locationPullDown selectedItem] title] isEqualTo:@"Manage..."])
    {
        //NSLog(@"Managing");
        [locationPullDown selectItemWithTitle:@"File Directory"];
        [self openLocationsSheet:self];
        return;
    }
    else
    {
        extractPath = [[locations objectAtIndex:[locationPullDown indexOfSelectedItem] - 2] objectForKey:@"Path"];
    }
}

- (void)printTaskOutput:(NSNotification *)aNotification
{
    // there is Data from the task output or error file to print in the Sheet Window
    NSString *outputString;
    NSData *data = [[aNotification userInfo] objectForKey:NSFileHandleNotificationDataItem];
    
    if (data && [data length])
    {
        outputString = [NSString stringWithCString:[data bytes] length:[data length]];
        [self appendOutput:outputString];
        //[self addToTextViewContentOfString:outputString];
        [[aNotification object] readInBackgroundAndNotify];
    }
}

- (void)appendOutput:(NSString *)output
{
    // add the string (a chunk of the results from locate) to the NSTextView's
    // backing store, in the form of an attributed string
    [[taskTextField textStorage] appendAttributedString: [[[NSAttributedString alloc]
                             initWithString: output] autorelease]];
    // setup a selector to be called the next time through the event loop to scroll
    // the view to the just pasted text.  We don't want to scroll right now,
    // because of a bug in Mac OS X version 10.1 that causes scrolling in the context
    // of a text storage update to starve the app of events
    [self performSelector:@selector(scrollToVisible:) withObject:nil afterDelay:0.0];
}

// This routine is called after adding new results to the text view's backing store.
// We now need to scroll the NSScrollView in which the NSTextView sits to the part
// that we just added at the end

- (void)scrollToVisible:(id)ignore 
{
    [taskTextField scrollRangeToVisible:NSMakeRange([[taskTextField string] length], 0)];
}

- (void)addToTextViewContentOfString:(NSString *)theString
{
    NSRange theRange;

    theRange.length = 0;
    theRange.location = [[taskTextField string] length];
    [taskTextField setEditable:YES];
    [taskTextField setSelectedRange:theRange];
    [taskTextField insertText:theString];
    [taskTextField setEditable:NO];
}

- (NSString*)_lookForCompanionRar
{
    int loop;
    NSString *path = [filePath stringByDeletingLastPathComponent];
    NSArray *array = [[NSFileManager defaultManager] directoryContentsAtPath:path];
    NSEnumerator *enumerator = [array objectEnumerator];
    //NSArray *pathComponents = [path pathComponents];
    NSString *localFile;
    NSString *aFile;
    //NSLog(@"Filename is %@", filename);
    //NSLog(@"File path is %@", path);
    //NSLog(@"Filename w/o Extension is %@", [filename stringByDeletingPathExtension]);
    //NSLog(@"Extension is %@", [filename pathExtension]);
    localFile = [filename stringByDeletingPathExtension];

    if([[filename pathExtension] isEqualToString:@"PAR2"] == true)
    {
        for(loop = 1; loop <= 1; loop++)
        {
            if(([localFile rangeOfString:@"."]).location != NSNotFound)
            {
                localFile = [localFile stringByDeletingPathExtension];
                //NSLog(@"Local File is %@", localFile);
            }            
        }
        //localFile = [localFile stringByAppendingString:@".part01"] ;
        //NSLog(@"Local File is %@", localFile);
    }
    
    while(aFile = [enumerator nextObject])
    {
        //NSLog(@"Entering While Loop");
        if([[aFile pathExtension] rangeOfString:@"rar" options:NSCaseInsensitiveSearch|NSBackwardsSearch].location != NSNotFound && [[aFile lastPathComponent] rangeOfString:localFile options:NSCaseInsensitiveSearch|NSBackwardsSearch].location != NSNotFound)
        {
            //return [path stringByAppendingString:aFile];
            //NSLog(@"Found Matching RAR File");
            return [NSString stringWithFormat:@"%@/%@", path, aFile];            
        }
    }
    return nil;
}

- (void)finishedTask:(NSNotification *)aNotification {
    NSString *confirmation = @" Task Complete.";
    NSString *finalConfirmation = [filename stringByAppendingString:confirmation];
    NSRect rect =  [window frame];
    NSRect eRect = [extractionView frame];
    NSRect tRect = [textScrollView frame];
    NSTask *task = [aNotification object];
    
    [pathDisplay setStringValue:finalConfirmation];
    [progressBar stopAnimation:self];
    [[items objectForKey:@"Extract"] setLabel:@"Extract"];
    [[items objectForKey:@"Extract"] setImage:[NSImage imageNamed:@"extract"]]; // image
    [[items objectForKey:@"Extract"] setToolTip:[NSString stringWithFormat:@"Extract"]]; // tooltip
    [[items objectForKey:@"Extract"] setEnabled:YES];    

    [[extractionView retain] removeFromSuperview];
    rect.origin.y += eRect.size.height;
    rect.size.height -= eRect.size.height;
    tRect = [textScrollView frame];
    tRect.origin.y = 0.0;
    [window setFrame:rect display:NO animate:NO];
    [textScrollView setFrame:tRect];
    [textScrollView display];

    if([prefs boolForKey:@"Partorar"] == YES)
    {
        //NSLog(@"Using Par to rar");
        if([[[task launchPath] lastPathComponent] isEqualTo:@"par"])
        {
            NSString *rarFile = [self _lookForCompanionRar];
            //NSLog(@"RAR File is %@", rarFile);
            if(rarFile)
            {
                [self examineFilenames:[NSArray arrayWithObject:rarFile]];
                //[self processFile:rarFile toPath:[filePath stringByDeletingLastPathComponent]];
            }
        }
        
        if([[[task launchPath] lastPathComponent] isEqualTo:@"par2"])
        {
            //NSLog(@"Hello");
            NSString *rarFile = [self _lookForCompanionRar];
            //NSLog(@"RAR File is %@", rarFile);
            if(rarFile)
            {
                [self examineFilenames:[NSArray arrayWithObject:rarFile]];
                //[self processFile:rarFile toPath:[filePath stringByDeletingLastPathComponent]];
            }
        }        
    }
    else
    {
        //[progressBar stopAnimation:self];
        //NSLog(@"Not using Par to rar");
    }
}

- (void)windowWillClose:(NSNotification *)aNotification
{
    [NSApp terminate:self];
}

- (BOOL)applicationShouldTerminateAfterLastWindowClosed:(NSApplication *)theApplication
{
    return YES;
}

- (NSToolbarItem *)toolbar:(NSToolbar *)toolbar itemForItemIdentifier:(NSString *)itemIdentifier willBeInsertedIntoToolbar:(BOOL)flag {
    return [items objectForKey:itemIdentifier];
}

- (NSArray *)toolbarDefaultItemIdentifiers:(NSToolbar*)toolbar {
    NSMutableArray* identifiers = [NSMutableArray new];
    
    [identifiers addObject:@"Filename"];
    [identifiers addObject:NSToolbarFlexibleSpaceItemIdentifier];
    [identifiers addObject:@"Password"];
    [identifiers addObject:@"Test"];
    [identifiers addObject:@"Browse"];
    [identifiers addObject:@"Extract"];

    //NSLog(@"Default Items");
    return identifiers;
}

- (NSArray *)toolbarAllowedItemIdentifiers:(NSToolbar*)toolbar {
    NSMutableArray* allowedItems = [NSMutableArray new];
    [allowedItems addObject:@"Browse"];
    [allowedItems addObject:@"Test"];
    [allowedItems addObject:@"Info"];
    [allowedItems addObject:@"Password"];
    [allowedItems addObject:@"Extract"];
    [allowedItems addObject:@"Filename"];
    [allowedItems addObject:NSToolbarCustomizeToolbarItemIdentifier];
    [allowedItems addObject:NSToolbarSeparatorItemIdentifier];
    [allowedItems addObject:NSToolbarSpaceItemIdentifier];
    [allowedItems addObject:NSToolbarFlexibleSpaceItemIdentifier];
    
    //NSLog(@"Allowed Items");
    return allowedItems;
}

- (int)count {
    return [items count];
}

- (IBAction)customize:(id)sender {
    [toolbar runCustomizationPalette:sender];
}

- (IBAction)showhide:(id)sender {
    [toolbar setVisible:![toolbar isVisible]];
}

- (IBAction)openPrefsSheet:(id)sender
{
    [NSApp beginSheet:prefsWindow
    modalForWindow:window
    modalDelegate:nil
    didEndSelector:nil
    contextInfo:nil];
    [NSApp runModalForWindow:prefsWindow];
    [NSApp endSheet:prefsWindow];
    [prefsWindow orderOut:self];
}

- (IBAction)closePrefsSheet:(id)sender
{
    //[self setPrefs:nil];
    [NSApp stopModal];
}

- (IBAction)setPrefs:(id)sender
{
    if([overwriteCB state] == NSOnState)
    {
        [prefs setObject:@"YES" forKey:@"Overwrite"];
    }
    else if([overwriteCB state] == NSOffState)
    {
        [prefs setObject:@"NO" forKey:@"Overwrite"];
    }
    if([recurseCB state] == NSOnState)
    {
        [prefs setObject:@"YES" forKey:@"Recurse"];
    }
    else if([recurseCB state] == NSOffState)
    {
        [prefs setObject:@"NO" forKey:@"Recurse"];
    }
    if([assumeCB state] == NSOnState)
    {
        [prefs setObject:@"YES" forKey:@"Assume Yes"];
    }
    else if([assumeCB state] == NSOffState)
    {
        [prefs setObject:@"NO" forKey:@"Assume Yes"];
    }
    if([keepCB state] == NSOnState)
    {
        [prefs setObject:@"YES" forKey:@"Keep Broken"];
    }
    else if([keepCB state] == NSOffState)
    {
        [prefs setObject:@"NO" forKey:@"Keep Broken"];
    }
    if([partorarCB state] == NSOnState)
    {
        [prefs setObject:@"YES" forKey:@"Partorar"];
    }
    else if([partorarCB state] == NSOffState)
    {
        [prefs setObject:@"NO" forKey:@"Partorar"];
    }
    
}

- (void)saveData
{
    //NSLog(@"Saving Data");
    [prefs setObject:locations forKey:@"Locations"];
    [prefs synchronize];
}


- (BOOL)application:(NSApplication *)sender openFile:(NSString *)name;
{
    [self checkPrefs];
    [self examineFilenames:[NSArray arrayWithObject:name]];
    return YES;
}

- (IBAction)showAboutBox:(id)sender
{
    [[AboutBox sharedInstance] showPanel:sender];
}

- (IBAction)openLocationsSheet:(id)sender
{
    [NSApp beginSheet:locationWindow
       modalForWindow:window
        modalDelegate:nil
       didEndSelector:nil
          contextInfo:nil];
    [NSApp runModalForWindow:locationWindow];
    [NSApp endSheet:locationWindow];
    [locationWindow orderOut:self];
}

- (IBAction)closeLocationsSheet:(id)sender
{
    [NSApp stopModal];
}

- (NSDictionary *)createRecord
{
    NSMutableDictionary *location = [[NSMutableDictionary alloc] init];

    [location setObject:[extractPath lastPathComponent] forKey:@"Name"];
    [location setObject:extractPath forKey:@"Path"];

    [location autorelease];
    return location;
}

- (IBAction)addRecord:(id)sender
{
    //NSLog(@"Adding Record");
    [locations addObject:[self createRecord]];
    [tableView reloadData];
    [self _loadLocations];
    [locationPullDown selectItemWithTitle:[[locations lastObject] objectForKey:@"Name"]];
    [self saveData];
}

- (IBAction)deleteRecord:(id)sender
{
    NSEnumerator *enumerator;
    NSNumber *index;
    NSMutableArray *tempArray;
    id tempObject;

    if ( [tableView numberOfSelectedRows] == 0 )
        return;

    NSBeep();

    enumerator = [tableView selectedRowEnumerator];
    tempArray = [NSMutableArray array];

    while ( (index = [enumerator nextObject]) ) {
        tempObject = [locations objectAtIndex:[index intValue]];
        [tempArray addObject:tempObject];
    }

    [locations removeObjectsInArray:tempArray];
    [self _loadLocations];
    [tableView reloadData];
    [self saveData];
}

- (int)numberOfRowsInTableView:(NSTableView *)aTableView
{
    return [locations count];
}

- (id)tableView:(NSTableView *)aTableView
objectValueForTableColumn:(NSTableColumn *)aTableColumn
            row:(int)rowIndex
{
    id theRecord, theValue;

    theRecord = [locations objectAtIndex:rowIndex];
    theValue = [theRecord objectForKey:[aTableColumn identifier]];

    return theValue;
}

- (void)tableView:(NSTableView *)aTableView
   setObjectValue:(id)anObject
   forTableColumn:(NSTableColumn *)aTableColumn
              row:(int)rowIndex
{
    id theRecord;

    theRecord = [locations objectAtIndex:rowIndex];
    [theRecord setObject:anObject forKey:[aTableColumn identifier]];

    [self _loadLocations];
    [self saveData];
}

- (IBAction)openPasswordSheet:(id)sender
{
    [NSApp beginSheet:passwordWindow
       modalForWindow:window
        modalDelegate:nil
       didEndSelector:nil
          contextInfo:nil];
    [NSApp runModalForWindow:passwordWindow];
    [NSApp endSheet:passwordWindow];
    [passwordWindow orderOut:self];
}

- (IBAction)closePasswordSheet:(id)sender
{
    //[self setPrefs:nil];
    [NSApp stopModal];
}


@end

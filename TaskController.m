#import "TaskController.h"

@implementation TaskController

- (NSTask *)processFile:(NSString *)filePath toPath:(NSString *)extractPath;
{
    NSTask *aTask = [[NSTask alloc] init];
    NSMutableArray *args = [NSMutableArray array];
    NSPipe *taskPipe = [NSPipe pipe];
    NSString *command = @"x";
    
    /* set standard I/O, here to a NSPipe */
    [aTask setStandardOutput:taskPipe];
    [aTask setStandardError:taskPipe];
    
    /* set arguments */
    [args addObject:command];
    [args addObject:filePath];
    [args addObject:extractPath];
    [aTask setArguments:args];
    
    /* set the path of the executable */
    [aTask setLaunchPath:[[NSBundle mainBundle] pathForResource:@"unrar" ofType:nil]];
    
    /* we want taskPipe to send notifications to be able to grab the output */
    [[taskPipe fileHandleForReading] readInBackgroundAndNotify];
    
    return aTask;
}

@end

/* TaskController */

#import <Cocoa/Cocoa.h>

@interface TaskController : NSObject
{
}

- (NSTask *)processFile:(NSString *)filePath toPath:(NSString *)extractPath;

@end

//
//  UAImageDownloader.m
//  pelis
//
//  Created by Master Móviles on 20/1/16.
//  Copyright © 2016 DLSI. All rights reserved.
//

#import "UAImageDownloader.h"

@implementation UAImageDownloader

- (id)initWithUrl: (NSString *) url
        indexPath: (NSIndexPath *)indexPath
          session: (NSURLSession *)session
         delegate: (id<UAImageDownloaderDelegate>) delegate
{
    self = [super init];
    
    if (self)
    {
        self.url = url;
        self.delegate = delegate;
        
        NSString *encodedUrl = [url stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]];
        NSURL *imageURL = [NSURL URLWithString:encodedUrl];
        
        NSURLSessionDownloadTask *downloadPhotoTask =
        [session downloadTaskWithURL:imageURL
                   completionHandler:^(NSURL *location, NSURLResponse *response, NSError *error)
         {
             NSHTTPURLResponse *httpResponse=(NSHTTPURLResponse *) response;
             if (httpResponse.statusCode == 200)
             {
                 if (!error)
                 {
                     UIImage *downloadedImage = [UIImage imageWithData:[NSData dataWithContentsOfURL:location]];
                     
                     dispatch_async(dispatch_get_main_queue(), ^{
                         [self.delegate imageDownloader:self didFinishDownloadingImage:downloadedImage forIndexPath:indexPath];
                     });
                 }
                 else
                 {
                     NSLog(@"Image %@ not found", imageURL);
                     [self.delegate imageDownloader:self didFailDownloadingImage:url forIndexPath:indexPath];
                 }
             }
             else
             {
                 NSLog(@"Received code %ld for url %@",(long)httpResponse.statusCode,imageURL);
                 [self.delegate imageDownloader:self didFailDownloadingImage:url forIndexPath:indexPath];
             }
         }];
        
        [downloadPhotoTask resume];
    }
    
    return self;
}

@end

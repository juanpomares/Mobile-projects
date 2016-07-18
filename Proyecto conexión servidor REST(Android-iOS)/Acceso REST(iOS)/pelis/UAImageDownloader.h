//
//  UAImageDownloader.h
//  pelis
//
//  Created by Master Móviles on 20/1/16.
//  Copyright © 2016 DLSI. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@class UAImageDownloader;

@protocol UAImageDownloaderDelegate

- (void) imageDownloader: (UAImageDownloader *) downloader
didFinishDownloadingImage: (UIImage *) image
            forIndexPath: (NSIndexPath *) indexPath;

-(void) imageDownloader:(UAImageDownloader *)downloader
didFailDownloadingImage: (NSString *)url
          forIndexPath : (NSIndexPath *)indexPath;

@end

@interface UAImageDownloader : NSObject <NSURLSessionDelegate>

- (id)initWithUrl: (NSString *) url
        indexPath: (NSIndexPath *)indexPath
          session: (NSURLSession *)session
         delegate: (id<UAImageDownloaderDelegate>) delegate;

@property(nonatomic,strong) NSString *url;
@property(nonatomic,strong) UIImage *image;

@property(nonatomic,unsafe_unretained) id<UAImageDownloaderDelegate> delegate;

@end
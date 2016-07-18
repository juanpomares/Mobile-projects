//
//  MasterViewController.h
//  pelis
//
//  Created by Antonio Pertusa on 31/1/15.
//  Copyright (c) 2015 DLSI. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "UAImageDownloader.h"


@class DetailViewController;

@interface MasterViewController : UITableViewController<UAImageDownloaderDelegate, UIScrollViewDelegate>

@property (strong, nonatomic) DetailViewController *detailViewController;
@property (nonatomic,strong) NSMutableDictionary *downloadingImages;

@property NSURLSession *session;
@end


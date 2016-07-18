//
//  DetailViewController.h
//  pelis
//
//  Created by Antonio Pertusa on 31/1/15.
//  Copyright (c) 2015 DLSI. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Peli.h"

@interface DetailViewController : UIViewController

@property (strong, nonatomic) Peli *detailItem;
@property (weak, nonatomic) IBOutlet UIImageView *imageView;
@property (weak, nonatomic) IBOutlet UITextView *textView;


@end


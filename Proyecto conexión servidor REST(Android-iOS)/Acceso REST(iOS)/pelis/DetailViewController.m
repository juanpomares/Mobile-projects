//
//  DetailViewController.m
//  pelis
//
//  Created by Antonio Pertusa on 31/1/15.
//  Copyright (c) 2015 DLSI. All rights reserved.
//

#import "DetailViewController.h"

@interface DetailViewController ()

@end

@implementation DetailViewController

#pragma mark - Managing the detail item

- (void)setDetailItem:(id)newDetailItem {
    if (_detailItem != newDetailItem) {
        _detailItem = newDetailItem;
            
        // Update the view.
        [self configureView];
    }
}

- (void)configureView {
    // Update the user interface for the detail item.
    if (self.detailItem)
    {
        Peli *peli=self.detailItem;
        
        self.textView.text=peli.synopsis;
        self.navigationItem.title=[NSString stringWithFormat:@"%@ (%d)",peli.title, peli.year];
        self.imageView.image=peli.image;
    }
}

// Para que el texto del scrollview no este desplazado
-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.textView scrollRangeToVisible:NSMakeRange(0, 0)];
    });
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    [self configureView];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

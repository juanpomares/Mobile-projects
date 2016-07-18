//
//  Pelis.h
//  LazyPelis
//
//  Created by Antonio Pertusa on 27/1/15.
//  Copyright (c) 2015 DLSI. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface Peli : NSObject

@property (nonatomic,strong) NSString *title;
@property int year;
@property (nonatomic,strong) NSString *director;
@property (nonatomic,strong) NSString *urlPoster;
@property BOOL rented;
@property (nonatomic,strong) NSString *synopsis;
@property (nonatomic,strong) UIImage *image;

-(id)initWithTitle:(NSString *)title withYear:(int)year withDirector:(NSString *)director withPoster:(NSString *)urlPoster rented:(BOOL)rented withSynopsis:(NSString *)synopsis;

@end

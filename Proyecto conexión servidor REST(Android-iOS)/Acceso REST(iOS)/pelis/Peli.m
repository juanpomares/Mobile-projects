//
//  Pelis.m
//  LazyPelis
//
//  Created by Antonio Pertusa on 27/1/15.
//  Copyright (c) 2015 DLSI. All rights reserved.
//

#import "Peli.h"

@implementation Peli

-(id)initWithTitle:(NSString *)title withYear:(int)year withDirector:(NSString *)director withPoster:(NSString *)urlPoster rented:(BOOL)rented withSynopsis:(NSString *)synopsis
{
    self=[super init];
    if (self)
    {
        self.title=title;
        self.year=year;
        self.director=director;
        self.urlPoster=urlPoster;
        self.synopsis=synopsis;
        self.rented=rented;
        self.image=nil;
    }
    return self;
}

@end



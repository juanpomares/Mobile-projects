//
//  Touch.h
//  BackToTheJump
//
//  Created by Master Móviles on 21/3/16.
//
//

#ifndef Touch_h
#define Touch_h

#include <stdio.h>
#include "cocos2d.h"

class Touch : public cocos2d::Layer{
    
public:
    
    bool init();
    bool onTouchBegan( cocos2d::Touch *touch, cocos2d::Event *event );
    
private:
    
};

#endif /* Touch_h */

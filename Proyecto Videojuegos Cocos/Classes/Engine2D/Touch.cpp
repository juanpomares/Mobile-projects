//
//  Touch.cpp
//  BackToTheJump
//
//  Created by Master Móviles on 21/3/16.
//
//

#include "Touch.h"

bool Touch::init()
{
    auto touchListener = cocos2d::EventListenerTouchOneByOne::create();
    
    touchListener->onTouchBegan = CC_CALLBACK_2(Touch::onTouchBegan, this);
    
    _eventDispatcher->addEventListenerWithSceneGraphPriority( touchListener, this );
    
    return true;
}

bool Touch::onTouchBegan(cocos2d::Touch *touch, cocos2d::Event *event)
{
    return true;
}
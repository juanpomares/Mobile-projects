//
//  Engine2D.h
//  Test01
//
//  Created by Fidel Aznar on 13/1/15.
//
//

#pragma once

#include "cocos2d.h"
USING_NS_CC;

class Engine2D{
    
    public:
    
    void createOpenGLContext();
    void createEngine(const char *windowName);
    void setScene(Scene *scene);
    void pause();
    void resume();
    
};



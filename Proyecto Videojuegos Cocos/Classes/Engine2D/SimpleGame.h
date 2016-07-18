//
//  EScene.h
//  Test01
//
//  Created by Fidel Aznar on 13/1/15.
//
//

#pragma once

#include "Engine2D.h"
#include "GameEntity.h"
#include "cocos2d.h"

USING_NS_CC;

class SimpleGame: public Scene {
    
public:
    virtual bool init();
    
    ~SimpleGame();
    
    virtual void preloadResources()=0;
    virtual void run();
    
    virtual void updateEachFrame(float delta)=0;
    virtual void updateIA(float delta)=0;
    
    void addGameEntity(GameEntity *ge);
    void removeGameEntity(GameEntity *ge);
    
    void updateEachGameEntityWithDelta(float delta);
    void preloadEachGameEntity();
    void addEachGameEntityNodeTo(Node *node);
    
    virtual void onKeyPressed(EventKeyboard::KeyCode keyCode, cocos2d::Event *event);
    virtual void onKeyReleased(EventKeyboard::KeyCode keyCode, cocos2d::Event *event);
    
protected:

    Vector<GameEntity*> m_gameEntities;
    
};



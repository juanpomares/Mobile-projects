//
//  MenuLevel.h
//  IBackground
//
//  Created by JorgeH on 11/4/16.
//
//

#ifndef __IBackground__MenuLevel__
#define __IBackground__MenuLevel__

#include <stdio.h>
#include "cocos2d.h"
#include "Sonido.h"

USING_NS_CC;

class MenuLevel : public cocos2d::CCScene
{
private:
    MenuLevel();
    cocos2d::CCMenu* _menu;
    bool _sliding;
    void PageLeft();
    void PageRight();
    void SlidingDone();
    void preload();
    
    cocos2d::Sprite* medallaBase;
    
    void GoToMenuScene(cocos2d::Ref* sender);
    
    float Escalado(float tam);
	void onKeyPressed(EventKeyboard::KeyCode keyCode, cocos2d::Event *event);
    
protected:
    bool init();
    
private:
    void CreateMenu();
    
    Sonido sonido;
    
public:
    
    static MenuLevel* create();
    
    ~MenuLevel();
    
    virtual void onEnter();
    virtual void onExit();
    virtual void onEnterTransitionDidFinish();
    virtual void onExitTransitionDidStart();
    
    void MenuCallback(cocos2d::CCObject* sender);
    static cocos2d::Scene* createScene();
    
};

#endif /* defined(__IBackground__MenuLevel__) */

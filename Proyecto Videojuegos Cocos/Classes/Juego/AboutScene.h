//
//  MenuLevel.h
//  IBackground
//
//  Created by JorgeH on 11/4/16.
//
//

#ifndef __AboutScene__
#define __AboutScene__

#include <stdio.h>
#include "cocos2d.h"
#include "Sonido.h"

USING_NS_CC;

class AboutScene : public cocos2d::CCScene
{
private:
	AboutScene();
    cocos2d::CCMenu* _menu;  
	void onKeyPressed(EventKeyboard::KeyCode keyCode, cocos2d::Event *event);
    
protected:
    bool init();
    
private:
    void CreateMenu();
    
    Sonido sonido;
    
public:
    
    static AboutScene* create();
    
    ~AboutScene();
    
    virtual void onEnter();
    virtual void onExit();
    virtual void onEnterTransitionDidFinish();
    virtual void onExitTransitionDidStart();
    
    void MenuCallback(cocos2d::CCObject* sender);
    static cocos2d::Scene* createScene();
    
};

#endif /* defined(__IBackground__MenuLevel__) */

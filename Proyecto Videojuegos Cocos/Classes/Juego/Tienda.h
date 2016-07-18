//
//  Tienda.h
//  BackToTheJump
//
//  Created by JorgeH on 11/5/16.
//
//

#ifndef Tienda_h
#define Tienda_h

#include <stdio.h>
#include "cocos2d.h"
#include "Sonido.h"

USING_NS_CC;

class Tienda : public cocos2d::CCScene
{
private:
    Tienda();
    cocos2d::CCMenu* _menu;
    bool _sliding;
    void PageLeft();
    void PageRight();
    void SlidingDone();
    
    void GoToMenuScene(cocos2d::Ref* sender);
	void onKeyPressed(EventKeyboard::KeyCode keyCode, cocos2d::Event *event);
    float Escalado(float tam);
    
protected:
    bool init();
    
private:
    void CreateMenu();
    
    Sonido sonido;
    
public:
    
    static Tienda* create();
    
    ~Tienda();
    
    virtual void onEnter();
    virtual void onExit();
    virtual void onEnterTransitionDidFinish();
    virtual void onExitTransitionDidStart();
    
    void MenuCallback(cocos2d::CCObject* sender);
    static cocos2d::Scene* createScene();
    
};

#endif /* defined(__IBackground__Tienda__) */

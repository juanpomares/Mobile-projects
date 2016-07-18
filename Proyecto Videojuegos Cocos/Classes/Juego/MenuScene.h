//
//  Menu.h
//  BackToTheJump
//
//  Created by Master Móviles on 21/3/16.
//
//

#ifndef MenuScene_h
#define MenuScene_h

#include <stdio.h>
#include "cocos2d.h"
#include "Sonido.h"
#include "Decorado.h"

class MenuScene : public cocos2d::Layer{
    
public:
    static cocos2d::Scene* createScene();
    
    virtual bool init();
    
    CREATE_FUNC(MenuScene);
    
    private :
    void GoToGameScene(cocos2d::Ref *sender);
	void GoToAboutScene(cocos2d::Ref *sender);
    void GoToTienda(cocos2d::Ref *sender);
    
    Sonido sonido;
    
};

#endif /* Menu_h */

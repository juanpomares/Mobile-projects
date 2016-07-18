//
//  Menu.h
//  BackToTheJump
//
//  Created by Master Móviles on 21/3/16.
//
//

#ifndef CargandoSceneH
#define CargandoSceneH

#include <stdio.h>
#include "cocos2d.h"


class CargandoScene : public cocos2d::Layer{
    
public:
    static cocos2d::Scene* crearEscena();
    virtual bool init();    
    CREATE_FUNC(CargandoScene);
    
private :
    void GoToMenuScene();    
};

#endif /* CargandoSceneH */


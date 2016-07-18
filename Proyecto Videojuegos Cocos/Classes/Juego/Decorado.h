//
//  Decorado.h
//  VideojuegosCocos
//
//  Created by Fidel Aznar on 16/1/15.
//
//

#pragma once

#include "../Engine2D/GameEntity.h"
#include "cocos2d.h"

class Decorado: public GameEntity{
    
public:
    
    bool init();
    
    void preloadResources(){};
    Node* getNode();
    
    void setTipo(int _tipo);

    CREATE_FUNC(Decorado);
    
private :
    
    cocos2d::Sprite *bc1, *bc2, *bc3;
    cocos2d::Vec2 pos1, pos2, pos3;
    cocos2d::Layer* layer;
    
    int tiempo, tipo;
    cocos2d::Size visibleSize;
    cocos2d::Vec2 origin;
    
	RepeatForever* getActionRepeat();
};

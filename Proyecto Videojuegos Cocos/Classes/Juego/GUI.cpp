//
//  GUI.cpp
//  VideojuegosCocos
//
//  Created by Fidel Aznar on 16/1/15.
//
//

#include "GUI.h"

bool GUI::init(){
    GameEntity::init();
    return true;
}

void GUI::preloadResources(){
}


Node* GUI::getNode(){
    if(m_node==NULL) {
        
        Size visibleSize = Director::getInstance()->getVisibleSize();
        Vec2 origin = Director::getInstance()->getVisibleOrigin();
        
        m_node= Node::create();
        
    }
    
    return m_node;
}






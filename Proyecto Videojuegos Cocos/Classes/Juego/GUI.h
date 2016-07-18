//
//  GUI.h
//  VideojuegosCocos
//
//  Created by Fidel Aznar on 16/1/15.
//
//

#pragma once

#include "../Engine2D/GameEntity.h"

class GUI: public GameEntity{
    
public:
    
    bool init();
    
    void preloadResources();
    Node* getNode();
    
    CREATE_FUNC(GUI);
    

    
};

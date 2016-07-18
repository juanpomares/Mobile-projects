//
//  Portal.hpp
//  BackToTheJump
//
//  Created by Master Móviles on 25/5/16.
//
//

#ifndef Portal_hpp
#define Portal_hpp

#include "Npc.h"


class Portal: public NPC
{
    
public:
    
    void preloadResources();
    Node* getNode();
    ~Portal();
    
    
    void destruction();
    
    CREATE_FUNC(Portal);
    
    void update();
    
    
    void update(float delta);
    void cambiaEscala(Node* n);

    
    Node* personaje=NULL;
    int escalax=1, escalay=1;
private:
    double height;
    
};
#endif /* Portal_hpp */

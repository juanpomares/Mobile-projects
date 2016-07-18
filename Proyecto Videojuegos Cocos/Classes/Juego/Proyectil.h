
//
//  Proyectil.h
//  BackToTheJump
//
//  Created by Master Móviles on 25/4/16.
//
//

#ifndef Proyectil_h
#define Proyectil_h

#include "Npc.h"
#include "Mundo.h"


class Proyectil: public NPC{
    
public:
    
    void preloadResources();
    Node* getNode();
    void update(float delta);
    ~Proyectil();
    
    CREATE_FUNC(Proyectil);
    
private:
    int primeravez=2;
    
    
};


#endif /* Proyectil_h */

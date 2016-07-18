//
//  Pinchos.h
//  BackToTheJump
//
//  Created by Master Móviles on 11/4/16.
//
//

#ifndef Pinchos_h
#define Pinchos_h

#include "Npc.h"
#include "Mundo.h"

class Pinchos: public NPC{
    
public:
    ~Pinchos();
    void preloadResources();
    Node* getNode();
    
    void update(float delta);
    CREATE_FUNC(Pinchos);
    
    void SetColor(int colorC);
    int colorPincho=0;

    
private:
    bool ocultable=false;
    int primeravez=2;
};



#endif /* Pinchos_h */

//
//  mina.h
//  BackToTheJump
//
//  Created by Master Móviles on 18/4/16.
//
//

#ifndef mina_h
#define mina_h

#include "Npc.h"
#include "Mundo.h"
#include "Sonido.h"


class Mina: public NPC{
    
public:
    
    void preloadResources();
    Node* getNode();
    ~Mina();
    
    void destruction();
    
    CREATE_FUNC(Mina);
    
private:
    Animation *m_animDestruction;
	Sonido sonido;
};


#endif /* mina_h */

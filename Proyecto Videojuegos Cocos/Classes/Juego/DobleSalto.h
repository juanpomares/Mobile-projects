//
//  DobleSalto.h
//  BackToTheJump
//
//  Created by Master Móviles on 9/5/16.
//
//

#ifndef DobleSalto_h
#define DobleSalto_h

#include "Npc.h"
#include "Mundo.h"
#include "Sonido.h"
#include <Box2D/Box2D.h>

class DobleSalto: public NPC{
    
public:
    
    void preloadResources();
    Node* getNode();
    ~DobleSalto();
    
    void destruction();
    
    CREATE_FUNC(DobleSalto);
    
private:
    Animation *m_animDestruction;
	Sonido sonido;

};


#endif /* DobleSalto_h */

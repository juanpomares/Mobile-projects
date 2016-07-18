//
//  Monedas.h
//  BackToTheJump
//
//  Created by Master Móviles on 11/4/16.
//
//

#ifndef Monedas_h
#define Monedas_h

#include "Npc.h"
#include "Mundo.h"
#include "Sonido.h"

class Moneda: public NPC{
    
public:
    
    void preloadResources();
    Node* getNode();
    void destroy();
    int TipoMedalla=-1;
	Sonido sonido;

    
    ~Moneda();
    
    CREATE_FUNC(Moneda);
};



#endif /* Monedas_h */

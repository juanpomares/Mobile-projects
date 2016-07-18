//
//  Npc.h
//  VideojuegosCocos
//
//  Created by Fidel Aznar on 16/1/15.
//
//

#pragma once

#define ACTION_GRAVITY  005

#include "../Engine2D/PhysicsGameEntity.h"
#include "PhysicsPlayer.h"


enum npcType{TMina = 0, TBala, TLlama, TMoneda, TMonedaFalsa, TMedalla, TMedallaCogida, TBolaFuego, TSalto, TFinNivel, TCambioEscala, TRueda};


class NPC: public PhysicsGameEntity{
    
public:
    
    bool init();
    void update(float delta);
    
    virtual void preloadResources(){};
    virtual Node* getNode(){return NULL;};
    ~NPC();
    
    int getDestruction(){return m_destruction;}
    
    
    CREATE_FUNC(NPC);
    
    double x, y;
    npcType tipo;
    
    virtual void destroy(){}
protected:
    int m_destruction=0;
    Sprite *m_sprite;
    Animation *m_anim;
    CombinedData* mCB=NULL;
    
};

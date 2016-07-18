//
//  Rueda.h
//  BackToTheJump
//
//  Created by Master Móviles on 25/5/16.
//
//

#ifndef Rueda_h
#define Rueda_h

#include "Npc.h"
#include "Mundo.h"

class Rueda: public NPC{
    
public:    
    void preloadResources();
    Node* getNode();
    void destroy();
    void update(float delta);
    
    ~Rueda();
    
    CREATE_FUNC(Rueda);    
private:
    int m_angle = 0;
	static const int m_vel_rotation = 5;
};

#endif /* Rueda_h */

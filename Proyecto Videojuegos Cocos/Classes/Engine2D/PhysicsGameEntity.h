//
//  PhysicsGameEntity.h
//  Fisica
//
//  Created by Miguel Angel Lozano Ortega on 28/3/15.
//
//

#pragma once

#include <Box2D/Box2D.h>
#include "GameEntity.h"
#include "../Util/CombinedData.h"

USING_NS_CC;

#define PTM_RATIO 256

class PhysicsGameEntity: public GameEntity {
    
public:
    
    virtual void update(float delta);

    void removePhysicsBody();
    
    b2Body* getBody(){return m_body;};
    void setTransform(Vec2 pos, float angle);
    
protected:
    b2Body *m_body=NULL;
    
};


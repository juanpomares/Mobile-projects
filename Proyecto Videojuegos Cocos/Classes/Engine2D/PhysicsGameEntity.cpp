//
//  PhysicsGameEntity.cpp
//  Fisica
//
//  Created by Miguel Angel Lozano Ortega on 28/3/15.
//
//

#include "PhysicsGameEntity.h"

void PhysicsGameEntity::update(float delta) {
    if(m_node && m_body) {
        b2Vec2 pos = m_body->GetPosition();
        float angle = m_body->GetAngle();
        
        m_node->setPosition(Vec2(pos.x * PTM_RATIO, pos.y * PTM_RATIO));
        m_node->setRotation(-CC_RADIANS_TO_DEGREES(angle));
    }
}

void PhysicsGameEntity::setTransform(cocos2d::Vec2 pos, float angle) {
    Node *node = getNode();
    b2Body *body = getBody();
    
    if(node) {
        m_node->setRotation(angle);
        m_node->setPosition(pos);
    }

    if(body) {
        m_body->SetTransform(b2Vec2(pos.x/PTM_RATIO, pos.y/PTM_RATIO), -CC_DEGREES_TO_RADIANS(angle));
    }
}


void PhysicsGameEntity::removePhysicsBody()
{
    if(m_body!=NULL)
    {
        m_body->GetWorld()->DestroyBody(m_body);
        m_body=NULL;
    }
}


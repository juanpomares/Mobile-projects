//
//  Rueda.cpp
//  BackToTheJump
//
//  Created by Master Móviles on 25/5/16.
//
//

#include "Rueda.h"
//#include "Game.h"

void Rueda::preloadResources()
{    
	auto spriteFrameCache = SpriteFrameCache::getInstance();
	if (!spriteFrameCache->getSpriteFrameByName("Saw.png")) {
		spriteFrameCache->addSpriteFramesWithFile(OBJETOS_PLIST);
	}   
}


Node* Rueda::getNode(){
    
    if(m_node==NULL) 
	{
		m_sprite = Sprite::createWithSpriteFrameName("Saw.png");

        m_sprite->setPosition(x, y);
        m_sprite->retain();
        
        m_node = m_sprite;
        m_node->setLocalZOrder(100);
        m_node->setAnchorPoint(Vec2(0.5,0.5));
        
        mCB=new CombinedData(this, CombinedDataType::TCDObjetoLetal);        
        
        b2World *world = Mundo::getActual()->getPhysicsWorld();
        
        b2BodyDef bodyDef;
        bodyDef.type = b2BodyType::b2_kinematicBody;
        
		b2CircleShape shapeBoundingBox;
		shapeBoundingBox.m_radius = m_sprite->getContentSize().width * 0.4 / PTM_RATIO;
		
        m_body = world->CreateBody(&bodyDef);
        auto fixture = m_body->CreateFixture(&shapeBoundingBox, 1.0);
        fixture->SetSensor(true);
        fixture->SetUserData(mCB);
        
        setTransform(cocos2d::Vec2(x+m_sprite->getContentSize().width/2,y+(1.15)*m_sprite->getContentSize().height), 0);
    }
    
    return m_node;    
}

void Rueda::update(float delta)
{
    if(m_angle>=360)
        m_angle -= 360;
    
    m_angle+=m_vel_rotation;    
    setTransform(m_sprite->getPosition(), m_angle);
}

void Rueda::destroy()
{
    if(!m_destruction)
    {
        m_destruction=true;
        if(m_sprite->getParent())
            m_sprite->removeFromParentAndCleanup(true);
    }
}

Rueda::~Rueda()
{
    if(m_sprite!=NULL)
    {
        m_sprite->release();
        m_sprite=NULL;
    }
}


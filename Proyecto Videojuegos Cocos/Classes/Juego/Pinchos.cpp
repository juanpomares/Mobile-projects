//
//  Monedas.cpp
//  BackToTheJump
//
//  Created by Master Móviles on 11/4/16.
//
//


#include "Pinchos.h"
#include "MiGame.h"

void Pinchos::preloadResources(){

	auto spriteFrameCache = SpriteFrameCache::getInstance();
	if (!spriteFrameCache->getSpriteFrameByName("Pinchos.png")) {
		spriteFrameCache->addSpriteFramesWithFile(OBJETOS_PLIST);
	}

}



void Pinchos::update(float delta)
{
    NPC::update(delta);
    
    
    if(m_sprite->isVisible() && ocultable)
    {
        if(primeravez==2)
            primeravez--;
        else
        {
            if(primeravez==1)
            {
                CCLOG("Ocultar");
                //auto mov = CCMoveBy::create(0.5, Point(0,-256));
                auto mov = MoveBy::create(3, Vec2(0, 0));
                mov->setTag(2);
                
                
                m_sprite->runAction(mov);
                primeravez=0;
            }
            
            if(m_sprite->getActionByTag(2))
            {
                setTransform(m_sprite->getPosition() + Vec2(0.0, -1.5f), 0);
            }
        }
        
        
    }
}

Pinchos::~Pinchos()
{
    m_sprite->release();
}

Node* Pinchos::getNode(){
    
    if(m_node==NULL)
    {

		m_sprite = Sprite::createWithSpriteFrameName("Pinchos.png");

        
        m_sprite->setPosition(x, y);
        m_node=m_sprite;
        
        m_node->retain();
        
        m_node->setAnchorPoint(Vec2(0.5,0.5));
        
        mCB=new CombinedData(this, CombinedDataType::TCDObjetoLetal);
        
        
        b2World *world = Mundo::getActual()->getPhysicsWorld();
        
        b2BodyDef bodyDef;
        bodyDef.type = b2BodyType::b2_staticBody;
        
        
        b2PolygonShape shapeBoundingBox;
        shapeBoundingBox.SetAsBox(m_sprite->getContentSize().width * 0.45 / PTM_RATIO,
                                  m_sprite->getContentSize().height * 0.35 / PTM_RATIO);
        
        m_body = world->CreateBody(&bodyDef);
        auto fixture = m_body->CreateFixture(&shapeBoundingBox, 1.0);
        fixture->SetSensor(true);
        fixture->SetUserData(mCB);
        
        setTransform(cocos2d::Vec2(x+m_sprite->getContentSize().width/2,y+(0.5+1.25)*m_sprite->getContentSize().height), 0);

        
        switch(colorPincho)
        {
            case 2:
                m_sprite->setColor(Color3B::RED);
                break;
            case 1:
                m_sprite->setColor(Color3B::GREEN);
                break;
            case 0:
				m_sprite->setColor(Color3B::WHITE);// (255.0f, 255.0f, 255.0f));
            default:
                break;
        }

    }
    
    
    return m_node;
    
}

void Pinchos::SetColor(int colorC)
{

    if(colorPincho==colorC && colorC!=0)
    {
        ocultable=true;
    }else
    {
        ocultable = false;
    }
}



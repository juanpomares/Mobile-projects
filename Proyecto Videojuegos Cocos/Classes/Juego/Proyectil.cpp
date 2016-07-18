//
//  Proyectil.cpp
//  BackToTheJump
//
//  Created by Master Móviles on 25/4/16.
//
//

#include "Proyectil.h"
#include "Mundo.h"

void Proyectil::preloadResources()
{
    //Cache de sprites
    auto spriteFrameCache = SpriteFrameCache::getInstance();
    //Cache de animaciones
    auto animCache = AnimationCache::getInstance();
    
    m_anim = Animation::create();
    
    if(tipo==npcType::TBolaFuego)
    {
        if(!spriteFrameCache->getSpriteFrameByName("fireball_1.png")) 
            spriteFrameCache->addSpriteFramesWithFile(OBJETOS_PLIST);
        
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("fireball_1.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("fireball_2.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("fireball_3.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("fireball_4.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("fireball_5.png"));
        
        m_anim->setDelayPerUnit(.1f);
        m_anim->retain();
        
        animCache->addAnimation(m_anim, "proyectilLlama");
    }
    
    else if (tipo==npcType::TBala)
    {
        if(!spriteFrameCache->getSpriteFrameByName("Bullet_000.png"))
            spriteFrameCache->addSpriteFramesWithFile(OBJETOS_PLIST);
       
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Bullet_000.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Bullet_001.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Bullet_002.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Bullet_003.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Bullet_004.png"));
        
        m_anim->setDelayPerUnit(.1f);
        m_anim->retain();
        
        animCache->addAnimation(m_anim, "proyectilBala");
    }    
}


Node* Proyectil::getNode(){
    
    if(m_node==NULL) {
        
        if(tipo==npcType::TBolaFuego)
        {
            m_sprite = Sprite::createWithSpriteFrameName("fireball_1.png");
        }else /*if (tipo==npcType::TBala)*/
        {
            m_sprite = Sprite::createWithSpriteFrameName("Bullet_000.png");
        }
        
        Action *move = RepeatForever::create(Animate::create(m_anim));
        m_sprite->runAction(move);
        
        m_node = m_sprite;        
        m_node->setLocalZOrder(0);        
        mCB=new CombinedData(this, CombinedDataType::TCDObjetoLetal);        
        
        b2World *world = Mundo::getActual()->getPhysicsWorld();
        
        b2BodyDef bodyDef;
        bodyDef.type = b2BodyType::b2_kinematicBody;        
        
		b2PolygonShape shapeBoundingBox;
		if (tipo == npcType::TBolaFuego)
		{
			shapeBoundingBox.SetAsBox(m_sprite->getContentSize().width * 0.4 / PTM_RATIO,
									m_sprite->getContentSize().height * 0.25 / PTM_RATIO);
		}else /*if (tipo == npcType::TBala)*/
		{
			shapeBoundingBox.SetAsBox(m_sprite->getContentSize().width * 0.3 / PTM_RATIO,
									m_sprite->getContentSize().height * 0.25 / PTM_RATIO);
		}

        m_body = world->CreateBody(&bodyDef);        
        m_body->SetTransform(b2Vec2(x/PTM_RATIO, y/PTM_RATIO), 0);
        
        auto fixture = m_body->CreateFixture(&shapeBoundingBox, 1.0);
        fixture->SetSensor(true);
        fixture->SetUserData(mCB);        
        
        m_sprite->setPosition(x,y);        
        setTransform(cocos2d::Vec2(x+m_sprite->getContentSize().width/2,y+(0.5+1.25)*m_sprite->getContentSize().height), 0);    
        m_sprite->retain();        
    }    
    
    return m_node;    
}

void Proyectil::update(float delta)
{
    NPC::update(delta);    
    
    if(m_sprite->isVisible())
    {
        if(primeravez==2)
            primeravez--;
        else
        {
            if(primeravez==1)
            {
                auto mov = MoveBy::create(10, Vec2(0, 0));
                mov->setTag(2);
                
				m_sprite->runAction(mov);
                primeravez=0;
            }

            if(m_sprite->getActionByTag(2))
            {
                setTransform(m_sprite->getPosition() + Vec2(-256/20, 0), 0);
            }
        }      
    }
}

Proyectil::~Proyectil()
{
    if(m_anim!=NULL)
    {
        m_anim->release();
        m_anim=NULL;
    }
    
    if(m_sprite!=NULL)
    {
        m_sprite->release();
        m_sprite=NULL;
    }
}

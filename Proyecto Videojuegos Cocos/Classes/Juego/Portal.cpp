//
//  Portal.cpp
//  BackToTheJump
//
//  Created by Master Móviles on 25/5/16.
//
//

#include "Portal.hpp"
#include "MiGame.h"
#include "PhysicsPlayer.h"

void Portal::preloadResources()
{
    //Cache de sprites
    auto spriteFrameCache = SpriteFrameCache::getInstance();
    //Cache de animaciones
    auto animCache = AnimationCache::getInstance();
    
    //Si no estaba el spritesheet en la caché lo cargo
    
    if(!spriteFrameCache->getSpriteFrameByName("portal_0.png")) {
        spriteFrameCache->addSpriteFramesWithFile(OBJETOS_PLIST);
    }
    
    m_anim = Animation::create();
    
    
    
    m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("portal_0.png"));
    m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("portal_1.png"));
    m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("portal_2.png"));
    m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("portal_3.png"));
    m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("portal_4.png"));
    m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("portal_5.png"));
    m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("portal_6.png"));
    m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("portal_7.png"));
    m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("portal_8.png"));
    
    m_anim->setDelayPerUnit(.2f);
    m_anim->retain();
    
    
    animCache->addAnimation(m_anim, "portaldestruction");
    
    
}


Node* Portal::getNode(){
    
    if(m_node==NULL) {
        
        m_sprite = Sprite::createWithSpriteFrameName("portal_0.png");
        m_sprite->setPosition(x,y);
        
        m_node = m_sprite;
        m_sprite->retain();
        m_node->setAnchorPoint(Vec2(0.5,0.5));
        
        height=m_sprite->getContentSize().height;
        

		auto scale = ScaleBy::create(0.5f, 1.25f, 1.5f);
		auto mySpawn = Sequence::createWithTwoActions(scale, scale->reverse());		
		auto actionRepeat = RepeatForever::create(mySpawn);
		m_sprite->runAction(actionRepeat);

		auto tint  = TintTo::create(0.2f, (tipo == npcType::TCambioEscala) ? Color3B(175, 255, 255): Color3B(255, 255, 64));
		auto tint2 = TintTo::create(0.2f, (tipo == npcType::TCambioEscala) ? Color3B(64, 175, 255) : Color3B(175, 175, 0));


		auto mySpawn2 = Sequence::createWithTwoActions(tint, tint2);
			auto actionRepeat2 = RepeatForever::create(mySpawn2);
			actionRepeat2->setTag(1);
			m_sprite->runAction(actionRepeat2);
		


		if (tipo == npcType::TCambioEscala)
		{
			m_sprite->setColor(Color3B::MAGENTA);
		}
		else
		{
			m_sprite->setColor(Color3B::YELLOW);
		}

        
        if(tipo==npcType::TCambioEscala)
            mCB=new CombinedData(this, CombinedDataType::TCDCambioEscala);
        else            
            mCB=new CombinedData(this, CombinedDataType::TCDFinNivel);
        
        
        b2World *world = Mundo::getActual()->getPhysicsWorld();
        
        b2BodyDef bodyDef;
        bodyDef.type = b2BodyType::b2_staticBody;        
        
        b2PolygonShape shapeBoundingBox;
        shapeBoundingBox.SetAsBox(m_sprite->getContentSize().width * 0.25 / PTM_RATIO,
                                                             height * 0.25 / PTM_RATIO);        
        m_body = world->CreateBody(&bodyDef);
        auto fixture = m_body->CreateFixture(&shapeBoundingBox, 1.0);
        fixture->SetSensor(true);
        fixture->SetUserData(mCB);
        
        setTransform(cocos2d::Vec2(x+m_sprite->getContentSize().width/2,y+(0.5+1.25)*height), 0);
        
        height/=2;    
    }    
    
    return m_node;
    
}


Portal::~Portal()
{


    m_anim->release();
    m_sprite->release();
}



void Portal::destruction()
{
	if (!m_sprite->getActionByTag(DESTRUCTION_TAG))
	{
		m_sprite->stopAllActions();

		auto callbackescala = CallFunc::create([=]() {
			m_destruction = true;
		});
		auto callbackfinlev = CallFunc::create([=]() {

			MiGame* m_game = (MiGame*)m_node->getParent()->getParent()->getParent();
			m_game->finNivel(this, true); });

		Animate *AnimDestruct = Animate::create(m_anim);
		auto seqDes = Sequence::create(AnimDestruct, tipo == npcType::TCambioEscala ? callbackescala : callbackfinlev, NULL);
		m_sprite->runAction(seqDes);
		seqDes->setTag(DESTRUCTION_TAG);
	}
}

void Portal::update(float delta)
{       
    if(m_sprite->isVisible() && personaje!=NULL)
    {        
		if (npcType::TFinNivel == tipo || !m_sprite->getActionByTag(DESTRUCTION_TAG))
		{
			setTransform(Vec2(m_node->getPosition().x, personaje->getPosition().y + height - personaje->getContentSize().height / 2.25f), 0);
		}
    }    


    NPC::update(delta);
}

void Portal::cambiaEscala(Node* n)
{
    if(tipo==npcType::TCambioEscala && escalax!=0)
    {
        Vec2 escalaVec2=Vec2(n->getScaleX(), n->getScaleY());
        
        //n->setScale(escalax*fabs(escalaVec2.x), escalay*fabs(escalaVec2.y));
		n->runAction(ScaleTo::create(0.55f, escalax*fabs(escalaVec2.x), escalay*fabs(escalaVec2.y)));
        escalax=0;
    }
}

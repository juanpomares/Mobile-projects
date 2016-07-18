//
//  Monedas.cpp
//  BackToTheJump
//
//  Created by Master Móviles on 11/4/16.
//
//


#include "Monedas.h"
#include "MiGame.h"

void Moneda::preloadResources()
{ 
    //Cache de sprites
    auto spriteFrameCache = SpriteFrameCache::getInstance();
    //Cache de animaciones
    auto animCache = AnimationCache::getInstance();
 
    //Si no estaba el spritesheet en la caché lo cargo
    
    m_anim = Animation::create();

	auto isMedalla = (tipo != npcType::TMoneda && tipo != npcType::TMonedaFalsa);

    if(isMedalla)
    {
        if(!spriteFrameCache->getSpriteFrameByName("medalla0-0.png"))
            spriteFrameCache->addSpriteFramesWithFile(PLIST_MEDALLA);
        
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("medalla0-0.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("medalla0-1.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("medalla0-2.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("medalla0-3.png"));
        
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("medalla1-0.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("medalla1-1.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("medalla1-2.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("medalla1-3.png"));
        
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("medalla2-0.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("medalla2-1.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("medalla2-2.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("medalla2-3.png"));
        
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("medalla3-0.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("medalla3-1.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("medalla3-2.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("medalla3-3.png"));
        
        m_anim->setDelayPerUnit(.1f);
        m_anim->retain();
        
        animCache->addAnimation(m_anim, "medallaGirar");
    }
    else /*if (tipo==npcType::TMoneda || tipo==npcType::TMonedaFalsa)*/
    {
        if(!spriteFrameCache->getSpriteFrameByName("Coin1.png")) {
            spriteFrameCache->addSpriteFramesWithFile(OBJETOS_PLIST);
        }
        
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Coin1.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Coin2.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Coin3.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Coin4.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Coin5.png"));
        m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Coin6.png"));
        
        m_anim->setDelayPerUnit(.1f);
        m_anim->retain();
        
        animCache->addAnimation(m_anim, "monedaGirar");
    }
    
 }


Node* Moneda::getNode(){
 
	if (m_node == NULL) 
	{
		auto isMedalla = (tipo != npcType::TMoneda && tipo != npcType::TMonedaFalsa);

		if (isMedalla) //tipo==npcType::TMedalla || tipo == npcType::TMedallaCogida)
		{
			m_sprite = Sprite::createWithSpriteFrameName("medalla0-0.png");

			if (tipo == npcType::TMedallaCogida)
			{
				m_sprite->setColor(Color3B::GRAY);
				//m_sprite->setOpacity(255 * 0.75f);
			}
		}
		else /*if (tipo==npcType::TMoneda || tipo==npcType::TMonedaFalsa)*/
		{
			m_sprite = Sprite::createWithSpriteFrameName("Coin1.png");
			//m_sprite->setScale(5.0,5.0);

			if (tipo == npcType::TMonedaFalsa)
			{
				auto tintTo = TintTo::create(0.f, 155.0f, 155.0f, 155.0f);
				m_sprite->runAction(tintTo);
			}
		}


		m_sprite->setPosition(x, y);
		m_sprite->retain();
		Action *money = RepeatForever::create(Animate::create(m_anim));
		m_sprite->runAction(money);

		m_node = m_sprite;
		m_node->setLocalZOrder(100);
		m_node->setAnchorPoint(Vec2(0.5f, 0.5f));

		mCB = new CombinedData(this, CombinedDataType::TCDRecogible);
		b2World *world = Mundo::getActual()->getPhysicsWorld();

		b2BodyDef bodyDef;
		bodyDef.type = b2BodyType::b2_staticBody;

		b2Fixture *fixture;

		m_body = world->CreateBody(&bodyDef);

		if (!isMedalla)
		{
			b2PolygonShape shapeBoundingBox;
			shapeBoundingBox.SetAsBox(m_sprite->getContentSize().width * 0.25 / PTM_RATIO,
									m_sprite->getContentSize().height * 0.25 / PTM_RATIO);

			fixture = m_body->CreateFixture(&shapeBoundingBox, 1.0);
		}
		else
		{
			b2CircleShape shapeBoundingCircle;
			shapeBoundingCircle.m_radius = m_sprite->getContentSize().width * 0.4 / PTM_RATIO;
			fixture = m_body->CreateFixture(&shapeBoundingCircle, 1.0);
		}




     fixture->SetSensor(true);
     fixture->SetUserData(mCB);
     
     setTransform(cocos2d::Vec2(x+m_sprite->getContentSize().width/2,y+(0.5+1.25)*m_sprite->getContentSize().height), 0);
 }
 
 
 return m_node;
 
 }



void Moneda::destroy()
{

	if (!m_sprite->getActionByTag(DESTRUCTION_TAG))
	{

		auto callback = CallFunc::create([=]() {
			m_destruction = true;
		});


		auto action = FadeOut::create(0.25f);

		auto seqDes = Sequence::create(action, callback, NULL);
		m_sprite->runAction(seqDes);
		seqDes->setTag(DESTRUCTION_TAG);
		sonido.reproducir(WAV_MONEDA);

		MiGame* m_game = (MiGame*)m_node->getParent()->getParent()->getParent();
		m_game->updateMonedas(this);


	}
    //this->~Moneda();
        
}

Moneda::~Moneda()
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

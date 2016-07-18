//
//  DobleSalto.cpp
//  BackToTheJump
//
//  Created by Master Móviles on 9/5/16.
//
//

#include "DobleSalto.h"


void DobleSalto::preloadResources(){
    
    //Cache de sprites
    auto spriteFrameCache = SpriteFrameCache::getInstance();
    //Cache de animaciones
    auto animCache = AnimationCache::getInstance();
    
    //Si no estaba el spritesheet en la caché lo cargo
    
    if(!spriteFrameCache->getSpriteFrameByName("Muzzle_000.png")) 
        spriteFrameCache->addSpriteFramesWithFile(OBJETOS_PLIST);
    
    m_anim = Animation::create();
    m_animDestruction = Animation::create();
    
    m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Muzzle_000.png"));
    
    m_animDestruction->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Muzzle_001.png"));
    m_animDestruction->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Muzzle_002.png"));
    m_animDestruction->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Muzzle_003.png"));
    m_animDestruction->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Muzzle_004.png"));
    
    m_anim->setDelayPerUnit(.2f);
    m_anim->retain();
    
    m_animDestruction->setDelayPerUnit(.15f);
    m_animDestruction->retain();
    
    animCache->addAnimation(m_anim, "dobleSaltoActivado");
    animCache->addAnimation(m_animDestruction, "dobleSaltoDesactivado");
}


Node* DobleSalto::getNode(){
    
    if(m_node==NULL)
    {
        mCB =  new CombinedData((void*)this, CombinedDataType::TCDDobleSalto);        
        m_sprite = Sprite::createWithSpriteFrameName("Muzzle_000.png");
        
        
        b2World *world = Mundo::getActual()->getPhysicsWorld();
        b2BodyDef bodyDef;
        bodyDef.type = b2BodyType::b2_staticBody;
        
        b2PolygonShape shapeBoundingBox;
        shapeBoundingBox.SetAsBox(m_sprite->getContentSize().width * 0.25/ PTM_RATIO,
                                  m_sprite->getContentSize().height * 0.25 / PTM_RATIO);
        m_body = world->CreateBody(&bodyDef);
        
        x+=128;
        m_body->SetTransform(b2Vec2(x/PTM_RATIO, y/PTM_RATIO), 0);
        
        auto fixtureJump = m_body->CreateFixture(&shapeBoundingBox, 1.0);
        
        fixtureJump->SetSensor(true);
        fixtureJump->SetUserData(mCB);


        Action *activate = RepeatForever::create(Animate::create(m_anim));
        activate->setTag(ACTIVATE_TAG);
        
        
        m_sprite->runAction(activate);
        m_node = m_sprite;
        
        m_sprite->setPosition(x,y);
        setTransform(cocos2d::Vec2(x,y+(0.5+1.25)*m_sprite->getContentSize().height), 0);
        m_sprite->retain();
        
        
        //m_node->setLocalZOrder(0);
    }
    
    
    return m_node;
    
}

void DobleSalto::destruction()
{
    m_sprite->stopActionByTag(ACTIVATE_TAG);
    
    auto callbackResume = CallFunc::create([=](){
        
        //m_sprite->removeFromParentAndCleanup(true);
        Mundo::getActual()->deleteNPC(this);
        //m_body->GetWorld()->DestroyBody(m_body);
        //NPC::destruction=true;
        //this->~DobleSalto();
        
    });
    
    Animate *AnimDestruct = Animate::create(m_animDestruction);
    auto seqDes = Sequence::create(AnimDestruct, callbackResume, NULL);
    m_sprite->runAction(seqDes);
    seqDes->setTag(DESTRUCTION_TAG);

	sonido.reproducir(WAV_SALTO);

}

DobleSalto::~DobleSalto()
{
    m_anim->release();
    m_sprite->release();
}

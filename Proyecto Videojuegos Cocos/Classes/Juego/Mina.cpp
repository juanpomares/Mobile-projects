#include "Mina.h"


void Mina::preloadResources(){
    
    //Cache de sprites
    auto spriteFrameCache = SpriteFrameCache::getInstance();
    //Cache de animaciones
    auto animCache = AnimationCache::getInstance();
    
    //Si no estaba el spritesheet en la caché lo cargo
    
    if(!spriteFrameCache->getSpriteFrameByName("mina.png")) {
        spriteFrameCache->addSpriteFramesWithFile(OBJETOS_PLIST);
    }
    
    m_anim = Animation::create();
    m_animDestruction = Animation::create();

    
    m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("mina.png"));
    m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("mina2.png"));
        
    m_animDestruction->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("explosion_2.png"));
    m_animDestruction->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("explosion_3.png"));
    m_animDestruction->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("explosion_4.png"));
    m_animDestruction->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("explosion_5.png"));
    m_animDestruction->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("explosion_6.png"));
    m_animDestruction->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("explosion_7.png"));
    
    m_anim->setDelayPerUnit(.2f);
    m_anim->retain();
    
    m_animDestruction->setDelayPerUnit(.15f);
    m_animDestruction->retain();
    
    animCache->addAnimation(m_anim, "minaActivada");
    animCache->addAnimation(m_animDestruction, "minaExplosion");

    
}


Node* Mina::getNode(){
    
    if(m_node==NULL) {
        
        m_sprite = Sprite::createWithSpriteFrameName("mina.png");
        m_sprite->setPosition(x,y);
        m_sprite->retain();
        
        Action *activate = RepeatForever::create(Animate::create(m_anim));
        activate->setTag(ACTIVATE_TAG);

        m_sprite->runAction(activate);
        
        m_node = m_sprite;
        
        m_node->setLocalZOrder(0);
        
        
        m_node->setAnchorPoint(Vec2(0.5f,0.2f));
        
        mCB=new CombinedData(this, CombinedDataType::TCDBombaNuclear);
        
        
        b2World *world = Mundo::getActual()->getPhysicsWorld();
        
        b2BodyDef bodyDef;
        bodyDef.type = b2BodyType::b2_staticBody;
        
        
        b2PolygonShape shapeBoundingBox;
        shapeBoundingBox.SetAsBox(m_sprite->getContentSize().width * 0.4 / PTM_RATIO,
                                  m_sprite->getContentSize().height * 0.1 / PTM_RATIO);
        
        m_body = world->CreateBody(&bodyDef);
        auto fixture = m_body->CreateFixture(&shapeBoundingBox, 1.0);
        fixture->SetSensor(true);
        fixture->SetUserData(mCB);
        
        setTransform(cocos2d::Vec2(x+m_sprite->getContentSize().width/2,y+(0.8)*m_sprite->getContentSize().height), 0);
    }
    
    
    return m_node;
    
}

void Mina::destruction()
{
    m_sprite->stopActionByTag(ACTIVATE_TAG);
    
    auto callbackResume = CallFunc::create([=](){
        /*m_sprite->removeFromParentAndCleanup(true);
        this->~Mina();
        Mundo::getActual()->deleteNPC(this);*/
        m_destruction=true;
        
    });
    
    Animate *AnimDestruct = Animate::create(m_animDestruction);
    auto seqDes = Sequence::create(AnimDestruct, callbackResume, NULL);
    m_sprite->runAction(seqDes);
    seqDes->setTag(DESTRUCTION_TAG);
	sonido.reproducir(WAV_EXPLOSION);

}

Mina::~Mina()
{   
    if(m_animDestruction!=NULL)
    {
        m_animDestruction->release();
        m_animDestruction=NULL;
    }
    
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

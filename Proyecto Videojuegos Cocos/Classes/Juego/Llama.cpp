#include "Llama.h"
#include "Mundo.h"

void Llama::preloadResources(){
    
    //Cache de sprites
    auto spriteFrameCache = SpriteFrameCache::getInstance();
    //Cache de animaciones
    auto animCache = AnimationCache::getInstance();
    
    //Si no estaba el spritesheet en la caché lo cargo
    
    if(!spriteFrameCache->getSpriteFrameByName("fuego1.png")) {
        spriteFrameCache->addSpriteFramesWithFile(OBJETOS_PLIST);
    }
    
    m_anim = Animation::create();
    
    m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("fuego1.png"));
    m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("fuego2.png"));
    
    m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("fuego3.png"));
    m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("fuego4.png"));
    m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("fuego5.png"));
    m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("fuego6.png"));
    m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("fuego7.png"));
    m_anim->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("fuego8.png"));
    
    m_anim->setDelayPerUnit(.15f);
    m_anim->retain();
    
    animCache->addAnimation(m_anim, "Llama");
    
}


Node* Llama::getNode(){
    
    if(m_node==NULL) {
        
        m_sprite = Sprite::createWithSpriteFrameName("fuego1.png");
        m_sprite->setPosition(x,y);
        m_sprite->retain();
        
        Action *move = RepeatForever::create(Animate::create(m_anim));
        m_sprite->runAction(move);
        
        m_node = m_sprite;
        
        m_node->setAnchorPoint(Vec2(0.5, 0.25));
        
        m_node->setLocalZOrder(0);
        
        mCB=new CombinedData(this, CombinedDataType::TCDObjetoLetal);
        
        
        b2World *world = Mundo::getActual()->getPhysicsWorld();
        
        b2BodyDef bodyDef;
        bodyDef.type = b2BodyType::b2_staticBody;
        
        
        double widthptm=m_sprite->getContentSize().width/PTM_RATIO;
        double heightptm=m_sprite->getContentSize().height/PTM_RATIO;
        
        b2PolygonShape shapeBoundingBox;
        shapeBoundingBox.SetAsBox(widthptm*0.35f,  heightptm*0.3f);
        
        m_body = world->CreateBody(&bodyDef);
        auto fixture = m_body->CreateFixture(&shapeBoundingBox, 1.0);
        fixture->SetSensor(true);
        fixture->SetUserData(mCB);
        
        setTransform(cocos2d::Vec2(75+x+m_sprite->getContentSize().width/2,y+(0.75)*m_sprite->getContentSize().height), 0);
        
    }
    
    
    return m_node;
    
}

Llama::~Llama()
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

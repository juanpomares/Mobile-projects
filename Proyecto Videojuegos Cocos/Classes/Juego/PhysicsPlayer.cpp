//
//  PhysicsPlayer.cpp
//  Mario
//
//  Created by Miguel Angel Lozano Ortega on 19/1/16.
//
//

#include "PhysicsPlayer.h"
#include "Mundo.h"
#include "MiGame.h"


bool PhysicsPlayer::init(){
    GameEntity::init();
    m_playerSprite = Sprite::create();
    contactos_suelo=0;
    //contactosDB=0;
    return true;
}

PhysicsPlayer::~PhysicsPlayer()
{
    //CCLOG("Destructor del Player");
    animAndar->release();
    animJump->release();
    animSlide->release();
    animFall->release();
    animDead->release();
    
    if(mCB!=NULL)
    {
        delete mCB;
        mCB=NULL;
    }
}

void PhysicsPlayer::preloadResources(){
    
    //Cache de sprites
    auto spriteFrameCache = SpriteFrameCache::getInstance();
    //Cache de animaciones
    auto animCache = AnimationCache::getInstance();
    
    //Si no estaba el spritesheet en la caché lo cargo
    /*if(!spriteFrameCache->getSpriteFrameByName("mario_1.png")) {
     spriteFrameCache->addSpriteFramesWithFile("marioss.plist");
     }*/
    
    if(!spriteFrameCache->getSpriteFrameByName("Run (1).png"))
    {
        spriteFrameCache->addSpriteFramesWithFile(PLIST_PERSONAJE);
        //spriteFrameCache->addSpriteFramesWithFile("mario/Plist.plist");
    }
    
    /*Animation *animAndar = Animation::create();
     animAndar->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("mario_1.png"));
     animAndar->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("mario_2.png"));
     animAndar->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("mario_3.png"));
     animAndar->setDelayPerUnit(0.1);
     */
    
    animAndar = Animation::create();
    animAndar->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Run (1).png"));
    animAndar->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Run (2).png"));
    animAndar->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Run (3).png"));
    animAndar->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Run (4).png"));
    animAndar->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Run (5).png"));
    animAndar->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Run (6).png"));
    animAndar->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Run (7).png"));
    animAndar->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Run (8).png"));
    
    animAndar->setDelayPerUnit(0.1f);
    animAndar->retain();
    
    auto animIddle = Animation::create();
    animIddle->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Run (1).png"));
    animCache->addAnimation(animIddle, "animIddle");
    
    animJump = Animation::create();
    animJump->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Jump (01).png"));
    animJump->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Jump (02).png"));
    animJump->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Jump (03).png"));
    animJump->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Jump (04).png"));
    animJump->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Jump (05).png"));
    animJump->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Jump (06).png"));
    animJump->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Jump (07).png"));
    animJump->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Jump (08).png"));
    animJump->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Jump (09).png"));
    animJump->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Jump (10).png"));
    
    animJump->setDelayPerUnit(0.1f);
    animJump->retain();
    
    animSlide = Animation::create();
    animSlide->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Slide (01).png"));
    animSlide->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Slide (02).png"));
    animSlide->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Slide (03).png"));
    animSlide->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Slide (04).png"));
    animSlide->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Slide (05).png"));
    animSlide->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Slide (06).png"));
    animSlide->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Slide (07).png"));
    animSlide->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Slide (08).png"));
    animSlide->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Slide (09).png"));
    animSlide->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Slide (10).png"));
    
    animSlide->setDelayPerUnit(0.1f);
    animSlide->retain();
    
    animFall = Animation::create();
    // animFall->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Jump (01).png"));
    animFall->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Jump (02).png"));
    animFall->setDelayPerUnit(0.1f);
    animFall->retain();
    
    animDead = Animation::create();
    animDead->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Dead (01).png"));
    animDead->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Dead (02).png"));
    animDead->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Dead (03).png"));
    animDead->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Dead (04).png"));
    animDead->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Dead (05).png"));
    animDead->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Dead (06).png"));
    animDead->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Dead (07).png"));
    animDead->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Dead (08).png"));
    animDead->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Dead (09).png"));
    animDead->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Dead (10).png"));
    
    animDead->setDelayPerUnit(0.1f);
    animDead->retain();
    
    
    /*animCache->addAnimation(animAndar, "animAndar");
     
     m_animAndar = animAndar;*/
}

Node* PhysicsPlayer::getNode(){
    
    if(m_node==NULL)
    {
        Size visibleSize = Director::getInstance()->getVisibleSize();
        Vec2 origin = Director::getInstance()->getVisibleOrigin();
        
        mCB=new CombinedData(this, CombinedDataType::TCDPersonaje);
        
        //m_playerSprite = Sprite::createWithSpriteFrameName("mario_1.png");
        m_playerSprite = Sprite::createWithSpriteFrameName("Run (1).png");
        
        m_playerSprite->setAnchorPoint(cocos2d::Vec2(0.5, 0.5));
        
        m_node = m_playerSprite;
        m_node->setLocalZOrder(100);
        
        m_node->setScale(0.7f);
        
        // Crea cuerpo físico
        b2World *world = Mundo::getActual()->getPhysicsWorld();      
        
        b2BodyDef bodyDef;
        bodyDef.type = b2BodyType::b2_dynamicBody;
        bodyDef.fixedRotation = true;
        
        b2PolygonShape shapeBoundingBox;
        
        double widthptm=m_playerSprite->getContentSize().width*0.7/PTM_RATIO;
        double heightptm=m_playerSprite->getContentSize().height*0.7/PTM_RATIO;
        
        
        shapeBoundingBox.SetAsBox( widthptm* 0.4, heightptm*0.4);
        
        m_body = world->CreateBody(&bodyDef);
        
        
        b2PolygonShape shapeSensorDown;
        shapeSensorDown.SetAsBox(widthptm * 0.25, heightptm * 0.05,
                                 b2Vec2(-widthptm * 0.1, heightptm* (0.1-0.5)), 0);
        
        m_groundTest = m_body->CreateFixture(&shapeSensorDown, 1.0);
        m_groundTest->SetSensor(false);
        m_groundTest->SetUserData(mCB);


        b2PolygonShape shapeSensorLateral;
        shapeSensorLateral.SetAsBox(widthptm* 0.07, heightptm* 0.275,
                                    b2Vec2(widthptm* 0.1, heightptm * (0.35-0.5)), 0);
        
        m_lateralTest = m_body->CreateFixture(&shapeSensorLateral, 1.0);
        m_lateralTest->SetSensor(true);
        m_lateralTest->SetUserData(mCB);
        
        b2PolygonShape shapeSensorUp[2];
        
        for(int i=0; i<2; i++)
        {
            shapeSensorUp[i].SetAsBox(widthptm * 0.25, heightptm * 0.1 ,
                                      b2Vec2(-widthptm * 0.1, heightptm * (-0.5+((i==0)?0.8:0.55))), 0);
            
            m_upTest[i] = m_body->CreateFixture(&shapeSensorUp[i], 1.0);
            m_upTest[i]->SetSensor(true);
            m_upTest[i]->SetUserData(mCB);
            
            contactos_up[i]=0;
        }

        this->setTransform(Vec2(origin.x, visibleSize.height/2 + origin.y+500), 0.0f);
        
        auto m_world=Mundo::getActual()->getPhysicsWorld();
        m_world->SetContactListener(new PlayerContactListener(this));

		m_playerSprite->setOpacity(0);
		auto action = FadeIn::create(0.75f);
		m_playerSprite->runAction(action);

		float ant = m_playerSprite->getScale();
		m_playerSprite->setScale(0.15f);
		m_playerSprite->runAction(ScaleTo::create(0.75f, ant));
        
        Mundo::getActual()->setPlayer(m_node);
    }


    if(DEBUG)
    {
        DrawNode *drawNode = DrawNode::create();
        drawNode->drawDot(Point(9,m_playerSprite->getBoundingBox().size.height/2.0), 2, Color4F(0, 0, 1, 1));
        m_node->addChild(drawNode);
    }
    
    
    return m_node;
}

bool PhysicsPlayer::checkGrounded() { return m_groundCollision;}

bool PhysicsPlayer::checkLateralCollision()
{
    return m_lateralCollision;
}

void PhysicsPlayer::Slide()
{
    if (!m_playerSprite->getActionByTag(ACTION_ANIM_DIE) && !m_playerSprite->getActionByTag(ACTION_ANIM_SLIDE) && !m_playerSprite->getActionByTag(ACTION_ANIM_SLIDE) && !m_playerSprite->getActionByTag(ACTION_ANIM_JUMP) && !m_playerSprite->getActionByTag(ACTION_ANIM_FALL))
    {
       // CCLOG("slide");
        agachado=true;
        m_playerSprite->stopActionByTag(ACTION_ANIM_MOVE);
        auto actionAnimate = Animate::create(animSlide);
        //auto callback = CallFunc::create([=](){this->EndSlide(); });
        //auto action = Sequence::create(actionAnimate, callback, NULL);
        auto action = RepeatForever::create(actionAnimate);
        action->setTag(ACTION_ANIM_SLIDE);
        m_playerSprite->runAction(action);
    }
}

void PhysicsPlayer::EndSlide()
{
    if(m_playerSprite->getActionByTag(ACTION_ANIM_SLIDE))
    {
        m_playerSprite->stopActionByTag(ACTION_ANIM_SLIDE);
        TouchGround();
        agachado=false;
    }
    //m_playerSprite->setDisplayFrameWithAnimationName("animIddle", 0);
}

void PhysicsPlayer::TouchGround()
{
    if (!m_playerSprite->getActionByTag(ACTION_ANIM_DIE) && !m_playerSprite->getActionByTag(ACTION_ANIM_MOVE) && !m_playerSprite->getActionByTag(ACTION_ANIM_SLIDE))
    {
        
       // CCLOG("TouchGround");
        m_playerSprite->stopActionByTag(ACTION_ANIM_FALL);
        m_playerSprite->stopActionByTag(ACTION_ANIM_JUMP);
        
        Animate* actionAnimate = Animate::create(animAndar);
        
        
        RepeatForever* actionRepeat = RepeatForever::create(actionAnimate);
        actionRepeat->setTag(ACTION_ANIM_MOVE);
        m_playerSprite->runAction(actionRepeat);
    }
    /*Añadir cosas*/
}


void PhysicsPlayer::Fall()
{
    if (!m_playerSprite->getActionByTag(ACTION_ANIM_DIE) && !m_playerSprite->getActionByTag(ACTION_ANIM_FALL) && !m_playerSprite->getActionByTag(ACTION_ANIM_JUMP))
    {
        m_playerSprite->stopActionByTag(ACTION_ANIM_MOVE);
        m_playerSprite->stopActionByTag(ACTION_ANIM_SLIDE);   
        
        auto actionAnimate = Animate::create(animFall);
        RepeatForever* actionRepeat = RepeatForever::create(actionAnimate);        
        
        actionRepeat->setTag(ACTION_ANIM_FALL);
        m_playerSprite->runAction(actionRepeat);
        //CCLOG("Fall");
    }
}


void PhysicsPlayer::Jump()
{
    if ((checkGrounded() || contactosDobleSalto>0) && !m_playerSprite->getActionByTag(ACTION_ANIM_DIE) && !m_playerSprite->getActionByTag(ACTION_ANIM_SLIDE)  && (!m_playerSprite->getActionByTag(ACTION_ANIM_FALL) || m_DobleSalto!=NULL))
    {
        if(m_DobleSalto!=NULL && contactosDobleSalto>0)
        {
            ((DobleSalto*)m_DobleSalto)->destruction();
            m_DobleSalto=NULL;
        }        
        
        
        m_playerSprite->stopAllActions();
        /*(ACTION_ANIM_MOVE);
         m_playerSprite->stopActionByTag(ACTION_ANIM_JUMP);
         */auto actionAnimate = Animate::create(animJump);
        auto callback = CallFunc::create([=]()
                                         {
                                             m_playerSprite->stopActionByTag(ACTION_ANIM_JUMP);
                                             this->Fall();
                                         });
        auto action = Sequence::create(actionAnimate, callback, NULL);
        action->setTag(ACTION_ANIM_JUMP);
        m_playerSprite->runAction(action);
        
        m_body->SetLinearVelocity(b2Vec2(m_body->GetLinearVelocity().x, m_jump));
    }
}

void PhysicsPlayer::update(float delta)
{
    PhysicsGameEntity::update(delta);
    //checkCollisions();
    
    //CCLOG("Lateral: %d, Suelo: %d, Arriba: %d, DoubleJumpContacts: %d", m_lateralCollision*1, m_groundCollision*1, contactos_up[agachado], contactosDB);
    
    
    m_body->SetLinearVelocity(b2Vec2(m_vel, m_body->GetLinearVelocity().y));
    updateCamera();
    
    if(contactos_up[agachado*1])
        Die();
}


void PhysicsPlayer::EndDie()
{
    //Size visibleSize = Director::getInstance()->getVisibleSize();
    //Vec2 origin = Director::getInstance()->getVisibleOrigin();
    
    //this->setTransform(Vec2(visibleSize.width/2 + origin.x+20, visibleSize.height/2 + origin.y + 300), 0.0f);
    
    //m_node->stopAllActions();
    //TouchGround();
    
	MiGame* m_game = (MiGame*)m_node->getParent()->getParent();
    m_game->finNivel(this, false);
}

void PhysicsPlayer::Die()
{
    m_vel=0;
    
    //Si no se esta parpadenado, parpadeo y quito vida
    if(!m_playerSprite->getActionByTag(ACTION_ANIM_DIE)){
        m_vidas--;
        auto actionAnimate = Animate::create(animDead);
        
        auto callback = CallFunc::create([=](){this->EndDie(); });
        
        auto action = Sequence::create(actionAnimate, callback,NULL);
        action->setTag(ACTION_ANIM_DIE);
        
        m_playerSprite->stopAllActions();
        m_playerSprite->runAction(action);
        
        auto vely=m_body->GetLinearVelocity().y;
        
        m_body->SetLinearVelocity(b2Vec2(0, vely>0?0:vely));
    }
}




void PhysicsPlayer::updateCamera()
{
    if(m_scrollable!=NULL)
    {
        auto pos = getPosition();
        
        Mundo *m_mundo = Mundo::getActual();
        auto winSize = Director::getInstance()->getWinSize();
        
        auto tilesize = m_mundo->getTileSize();
        auto tamtiles = m_mundo->getTamTiles();
        
        float scalex = m_scrollable->getScaleX(), scaley = m_scrollable->getScaleY();
        
        
        float x = MAX(pos.x+winSize.width/(4*fabs(scalex)), winSize.width/(fabs(scalex)*2));
        float y = scaley<0?(-winSize.height/scaley):0;    // MAX(pos.y, winSize.height/2);
        
        
        x = MIN(x, tilesize.width*tamtiles.width-winSize.width/(fabs(scalex)*(2.0)));
        
        
        Point actualPosition = Point(x*scalex, y*scaley);
        
        
        Point centerOfView =  Point(winSize.width/2, -(scaley)*winSize.height/2);
        //CCPoint viewPoint = ccpSub(centerOfView, actualPosition);
        
        
        m_scrollable->setPosition(centerOfView-actualPosition);
    }
}

void PhysicsPlayer::BeginContact(b2Contact* contact)
{
    
    b2Fixture *FixA=contact->GetFixtureA();
    b2Fixture *FixB=contact->GetFixtureB();
    
    /*if(FixA->GetUserData()==FixB->GetUserData())
    {
        CCLOG("Son iguales");
    }*/
    
    bool personajeA=((CombinedData*)FixA->GetUserData())->isPersonaje();
    
    b2Fixture* FixUsuario=(personajeA)?FixA:FixB;
    
    CombinedData* cbColision=(CombinedData*)((personajeA==true)?FixB:FixA)->GetUserData();
    
    if(cbColision->isFinNivel())
    {        
        if(!m_playerSprite->getActionByTag(ACTION_FIN_LEVEL))
        {
            Portal* p=(Portal*) cbColision->mPointer;
            p->destruction();
            
            m_playerSprite->stopAllActions();
            
            auto action=FadeOut::create(1.5);
            action->setTag(ACTION_FIN_LEVEL);
            
            m_playerSprite->runAction(action);
			m_playerSprite->runAction(ScaleTo::create(1.5, 0.15f));
            m_vel=0.5;
        }
    }else if(cbColision->isRecogible())
    {

		if (!(FixUsuario != m_lateralTest && FixUsuario != m_upTest[agachado * 1] && FixUsuario != m_groundTest))
		{
			Moneda* m = (Moneda*)cbColision->mPointer;

			if (!m->getDestruction())
			{
				
				m->destroy();
			}
		}
    }else if(cbColision->isBombaNuclear())
    {
        Mina* m=(Mina*)cbColision->mPointer;
        
        if(!m->getDestruction())
        {
            m->destruction();            
            Die();
        }
    }else if(cbColision->isCambioEscala())
    {
        Portal* p=(Portal*) cbColision->mPointer;
        
        if(!p->getDestruction())
        {
            p->destruction();
            p->cambiaEscala(m_scrollable);
        }       
    }else
    {
        if(cbColision->isDobleSalto())
        {
            if(FixUsuario==m_groundTest)
            {    contactosDobleSalto++;
                m_DobleSalto=cbColision->mPointer;
            }
        }else
            if(FixUsuario==m_groundTest)
            {
                if(cbColision->isMundo())
                {
                    if(contactos_suelo==0)
                    {
                        m_groundCollision=true;
                        TouchGround();
                    }
                    contactos_suelo++;
                }else
                    if(cbColision->isObjetoLetal())
                    {
                        Die();
                    }
            }else
                if(FixUsuario==m_lateralTest)
                {
                    Die();
                    m_lateralCollision=true;
                }else
					if (FixUsuario == m_upTest[0])
					{
						if (cbColision->isMundo() || cbColision->isObjetoLetal())
							contactos_up[0]++;
					}
					else
						if (FixUsuario == m_upTest[1])
						{
							if (cbColision->isMundo() || cbColision->isObjetoLetal())
								contactos_up[1]++;
						}
    }
}


bool PhysicsPlayer::isDying()
{
	if (m_playerSprite)
		return m_playerSprite->getActionByTag(ACTION_ANIM_DIE);
	else
		return false;
}

void PhysicsPlayer::EndContact(b2Contact* contact)
{
    b2Fixture *FixA=contact->GetFixtureA();
    b2Fixture *FixB=contact->GetFixtureB();
    
    bool personajeA=((CombinedData*)FixA->GetUserData())->isPersonaje();
    
    b2Fixture* FixUsuario=(personajeA)?FixA:FixB;
    
    CombinedData* cbColision=(CombinedData*)((personajeA==true)?FixB:FixA)->GetUserData();
    
    if(FixUsuario==m_groundTest)
    {
        if(cbColision->isMundo())
        {
            contactos_suelo--;
            if(contactos_suelo==0)
            {
                m_groundCollision=false;
                Fall();
            }
        }else
        {
            if(cbColision->isDobleSalto())
            {
                contactosDobleSalto--;
                if(contactosDobleSalto<1)
                    m_DobleSalto=NULL;
            }
        }
    }else
        if(FixUsuario==m_lateralTest)
        {
            if(cbColision->isDobleSalto())
            {
                //contactosDB--;
            }else
                m_lateralCollision=false;
        }else
			if (FixUsuario == m_upTest[0])
			{
				if (cbColision->isMundo() || cbColision->isObjetoLetal())
					contactos_up[0]--;
			}
			else
				if (FixUsuario == m_upTest[1])
				{
					if (cbColision->isMundo() || cbColision->isObjetoLetal())
						contactos_up[1]--;
				}
}

PlayerContactListener::PlayerContactListener(PhysicsPlayer *player) {
    m_player=player;
}

void PlayerContactListener::BeginContact(b2Contact *contact)
{
    m_player->BeginContact(contact);
}

void PlayerContactListener::EndContact(b2Contact *contact)
{
    m_player->EndContact(contact);
}


void PhysicsPlayer::SetColor(int colorC)
{
    switch(colorC)
    {
        case 1:
            m_playerSprite->setColor(Color3B::GREEN);
            break;
        case 2:
            m_playerSprite->setColor(Color3B::RED);
            break;
        case 0:
            m_playerSprite->setColor(Color3B(255.0f, 255.0f, 255.0f));
        default:
            break;
    }
    
    color=colorC;
    
    
}

int PhysicsPlayer::getColor()
{
    return color;
}

//
//  Menu.cpp
//  BackToTheJump
//
//  Created by Master Móviles on 21/3/16.
//
//


#include "CargandoScene.h"
#include "MenuScene.h"
#include "Definiciones.h"

USING_NS_CC;

Scene* CargandoScene::crearEscena()
{	
    auto scene = Scene::create();    
    auto layer = CargandoScene::create();    
    scene->addChild(layer);
    
    return scene;
}

bool CargandoScene::init()
{
    if ( !Layer::init() )
    {
        return false;
    }
    
  
	auto sprite = Sprite::create(IMG_SPLASH_SCREEN);

	double heightTotal = sprite->getContentSize().height;
	double tamh = Director::getInstance()->getVisibleSize().height;

	double scaleh = tamh / heightTotal;

	double widthTotal = sprite->getContentSize().width;
	double tamw = Director::getInstance()->getVisibleSize().width;

	double scalew = tamw / widthTotal;

	sprite->setScale(scalew, scaleh);
	sprite->setAnchorPoint(Vec2(0, 0));
	sprite->setPosition(Point::ZERO);


	this->addChild(sprite);


	auto actionAnimate = MoveBy::create(0.25f, Point::ZERO);
	auto callback = CallFunc::create([=]()
	{
		auto spriteFrameCache = SpriteFrameCache::getInstance();
		if (!spriteFrameCache->getSpriteFrameByName("Pinchos.png"))
		{
			spriteFrameCache->addSpriteFramesWithFile(OBJETOS_PLIST);
		}

		if (!spriteFrameCache->getSpriteFrameByName("Run (1).png"))
		{
			spriteFrameCache->addSpriteFramesWithFile(PLIST_PERSONAJE);
		}

		if (!spriteFrameCache->getSpriteFrameByName("medalla0-0.png"))
		{
			spriteFrameCache->addSpriteFramesWithFile(PLIST_MEDALLA);
		}

		auto sp = Sprite::create();
		sp->initWithFile(IMG_SPRITESHEET); 

		GoToMenuScene();
	});
	auto action = Sequence::create(actionAnimate, callback, NULL);

	this->runAction(action);
    
    return true;
}

void CargandoScene::GoToMenuScene()
{
		auto scene = MenuScene::createScene();

		Director::getInstance()->replaceScene(TransitionFade::create(TRANSITION_TIME, scene));
}
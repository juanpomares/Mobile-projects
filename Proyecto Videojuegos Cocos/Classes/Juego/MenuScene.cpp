//
//  Menu.cpp
//  BackToTheJump
//
//  Created by Master Móviles on 21/3/16.
//
//

#include "MenuScene.h"
#include "MiGame.h"
#include "MenuLevel.h"
#include "AboutScene.h"
#include "Tienda.h"
#include "Almacenamiento.h"

USING_NS_CC;

Scene* MenuScene::createScene()
{	
    auto scene = Scene::create();    
    auto layer = MenuScene::create();    
    scene->addChild(layer);
    
    return scene;
}

bool MenuScene::init()
{
    if ( !Layer::init() )
    {
        return false;
    }
    
    auto dec = new Decorado();
    this->addChild(dec->getNode());
    
	Almacenamiento alm;
	alm.comprobar();

    sonido.reproducirFondo(WAV_MUSICA1);
    
    Size visibleSize = Director::getInstance()->getVisibleSize();
    Vec2 origin = Director::getInstance()->getVisibleOrigin();
    
    auto bPlay = MenuItemImage::create( IMG_JUGAR, IMG_JUGAR, CC_CALLBACK_1( MenuScene::GoToGameScene, this ) );
    bPlay->setPosition( Point( visibleSize.width / 2 + origin.x, visibleSize.height / 1.5 + origin.y ) );
    
    auto bTienda = MenuItemImage::create( IMG_TIENDA, IMG_TIENDA, CC_CALLBACK_1( MenuScene::GoToTienda, this ) );
    bTienda->setPosition( Point( visibleSize.width / 2 + origin.x, visibleSize.height / 2 + origin.y ) );
    
	auto bAcerca = MenuItemImage::create(IMG_ACERCA, IMG_ACERCA, CC_CALLBACK_1(MenuScene::GoToAboutScene, this));
	bAcerca->setPosition(Point(visibleSize.width / 2 + origin.x, visibleSize.height / 3 + origin.y));

    bPlay->setScale(2);
    bTienda->setScale(2);
	bAcerca->setScale(2);
    
    auto menu = Menu::create( bPlay, NULL );
    menu->addChild(bTienda);
	menu->addChild(bAcerca);
    menu->setPosition( Point::ZERO );
    
    this->addChild( menu );

    return true;
}

void MenuScene::GoToTienda(Ref *sender)
{
    auto scene = Tienda::create();
    
    sonido.reproducir(MP3_BOTON);
    
    Director::getInstance()->replaceScene(CCTransitionSlideInT::create(TRANSITION_TIME, scene));
}

void MenuScene::GoToAboutScene(Ref *sender)
{
	auto scene = AboutScene::create();

	sonido.reproducir(MP3_BOTON);

	Director::getInstance()->replaceScene(CCTransitionSlideInB::create(TRANSITION_TIME, scene));
}

void MenuScene::GoToGameScene(Ref *sender)
{
    auto scene = MenuLevel::create();
    
	sonido.reproducir(MP3_BOTON);
    
    Director::getInstance()->replaceScene(CCTransitionProgressInOut::create(TRANSITION_TIME, scene));
}



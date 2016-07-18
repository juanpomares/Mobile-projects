//
//  MenuLevel.cpp
//  IBackground
//
//  Created by JorgeH on 11/4/16.
//
//

#include "AboutScene.h"
#include "Definiciones.h"

#include "MenuScene.h"

USING_NS_CC;

//Almacenamiento alm;

AboutScene::AboutScene() :
_menu(NULL)
{
}

AboutScene::~AboutScene()
{
    
}

Scene* AboutScene::createScene()
{
    auto scene = Scene::create();    
    auto layer = AboutScene::create();    
    scene->addChild(layer);
    
    return scene;
}

void AboutScene::CreateMenu()
{
    if(_menu == NULL)
    {
        Size scrSize = CCDirector::getInstance()->getWinSize();
        
        _menu = Menu::create();
        _menu->setPosition(Vec2(0,0));
        addChild(_menu);

		auto pItem2 = MenuItemImage::create(IMG_BACK, IMG_BACK, this, CC_MENU_SELECTOR(AboutScene::MenuCallback));

		pItem2->setPosition(Vec2(scrSize.width * 0.1, scrSize.height*0.93));
		pItem2->setScale(0.1);
		_menu->addChild(pItem2);

		auto creadopor = MenuItemLabel::create(Label::create(" Juego creado por: ", FONTS_ARIAL, 28));
		creadopor->setDisabledColor(Color3B::WHITE);
		creadopor->setEnabled(false);
		creadopor->setPosition(Vec2(scrSize.width * 0.5, scrSize.height*0.7));

		_menu->addChild(creadopor);


		auto jhp = MenuItemLabel::create(Label::create(" Jorge Hernández Párraga ", FONTS_ARIAL, 30));
		jhp->setDisabledColor(Color3B::WHITE);
		jhp->setEnabled(false);
		jhp->setPosition(Vec2(scrSize.width * 0.5, scrSize.height*0.55));

		_menu->addChild(jhp);

		auto eve = MenuItemLabel::create(Label::create(" Evarist Pérez Esteve ", FONTS_ARIAL, 30));
		eve->setDisabledColor(Color3B::WHITE);
		eve->setEnabled(false);
		eve->setPosition(Vec2(scrSize.width * 0.5, scrSize.height*0.45));

		_menu->addChild(eve);

		auto jpb = MenuItemLabel::create(Label::create(" Juan Pomares Bernabeu ", FONTS_ARIAL, 30));
		jpb->setDisabledColor(Color3B::WHITE);
		jpb->setEnabled(false);
		jpb->setPosition(Vec2(scrSize.width * 0.5, scrSize.height*0.35));

		_menu->addChild(jpb);


       /*
        
        MenuItemFont* pItem;
        Point position;
        Size visibleSize = Director::getInstance()->getVisibleSize();
        
        pItem = MenuItemFont::create("Atrás", this, menu_selector(MenuLevel::GoToMenuScene));
		auto pItem2 = MenuItemImage::create(IMG_BACK, IMG_BACK, this, menu_selector(MenuLevel::GoToMenuScene));
		pItem2->setTag(ARROW_LEFT);
        position = Vec2(visibleSize.width * 0.1,visibleSize.height*0.93);
		pItem2->setPosition(position);
		pItem2->setScale(0.1);
        _menu->addChild(pItem2);
        
        for(int idx = 0; idx < MENU_ITEMS_TOTAL; idx++)
        {
			std::string sNivel = alm.IntToString(idx);
			
			std::string sBloqueado = "nivel" + sNivel;
            position = CalculatePosition(idx);
            if(alm.cargar(sBloqueado) == "desbloqueado" || idx==0)
            {
                char buffer[256];
                sprintf(buffer,"Nivel %d",idx);
                auto textNivel = MenuItemLabel::create(Label::create(buffer, FONTS_ARIAL, 35));
                textNivel->setPosition(position.x, position.y + (visibleSize.width / MENU_ITEMS_ACROSS)/6);
				textNivel->setDisabledColor(Color3B::WHITE);
				textNivel->setEnabled(false);
                auto fondo_nivel = MenuItemImage::create(IMG_B_NIVEL, IMG_B_NIVEL_PRESIONADO, this, menu_selector(MenuLevel::MenuCallback));
                float escalar = Escalado(fondo_nivel->getContentSize().width);
                fondo_nivel->setScale(escalar, escalar);
                fondo_nivel->setPosition(position);
            
                _menu->addChild(fondo_nivel);
                fondo_nivel->setTag(idx);
                _menu->addChild(textNivel);
                
                for(int i = -1; i < 2; i++)
                {
					auto m = Sprite::createWithSpriteFrameName("medalla0-0.png");
					auto moneda = MenuItemSprite::create(m, m, m);
                    std::string nivel = alm.IntToString(idx);
                    std::string monedaAlmacenada = alm.IntToString(i+1);
                    std::string valorAlmacenado = "medalla"+nivel+monedaAlmacenada;
                    cocos2d::log("%s", valorAlmacenado.c_str());
                    if(alm.cargar(valorAlmacenado) != "conseguida")
                    {
                        moneda->setColor(Color3B::BLACK);
                    }
                    moneda->setPosition(position.x + (i*((fondo_nivel->getContentSize().width * escalar) / 3)),     position.y - 30);
                    moneda->setScale(escalar/2, escalar / 2);
                    _menu->addChild(moneda);
                }
            }else{
                auto fondo_nivel_base = Sprite::create(IMG_B_NIVEL_BLOQUEADO);
				auto fondo_nivel = MenuItemSprite::create(fondo_nivel_base, fondo_nivel_base, fondo_nivel_base);
                float escalar = Escalado(fondo_nivel->getContentSize().width);
                fondo_nivel->setScale(escalar, escalar);
                fondo_nivel->setPosition(position);
                
                _menu->addChild(fondo_nivel);
            }
        }*/
    }
}



bool AboutScene::init()
{
    auto dec = new Decorado();
    this->addChild(dec->getNode());
    
	auto listener = cocos2d::EventListenerKeyboard::create();
	listener->onKeyPressed = CC_CALLBACK_2(AboutScene::onKeyPressed, this);
	Director::getInstance()->getEventDispatcher()->addEventListenerWithSceneGraphPriority(listener, this);

    
    return true;
}


void AboutScene::onKeyPressed(EventKeyboard::KeyCode keyCode, cocos2d::Event *event)
{
	if (keyCode == EventKeyboard::KeyCode::KEY_ESCAPE || keyCode == EventKeyboard::KeyCode::KEY_BACK)
		MenuCallback(this);
}


AboutScene* AboutScene::create()
{
	AboutScene *pRet = new AboutScene();
    if (pRet && pRet->init())
    {
        pRet->autorelease();
        return pRet;
    }
    else
    {
        CC_SAFE_DELETE(pRet);
        return NULL;
    }
}

void AboutScene::onEnter()
{
    CCScene::onEnter();
    CreateMenu();
}

void AboutScene::onExit()
{
    CCScene::onExit();
}

void AboutScene::onEnterTransitionDidFinish()
{
    CCScene::onEnterTransitionDidFinish();
}

void AboutScene::onExitTransitionDidStart()
{
    CCScene::onExitTransitionDidStart();    
}


void AboutScene::MenuCallback(Object* sender)
{
    // This is a very contrived example
    // for handling the menu items.
    // -1 ==> Left Arrow
    // -2 ==> Right Arrow
    // Anything else is a selection

    sonido.reproducir(MP3_BOTON);
    
    //Director::getInstance()->replaceScene(TransitionFade::create(TRANSITION_TIME, scene));
	auto scene = MenuScene::createScene();

	Director::getInstance()->replaceScene(CCTransitionSlideInT::create(TRANSITION_TIME, scene));
}


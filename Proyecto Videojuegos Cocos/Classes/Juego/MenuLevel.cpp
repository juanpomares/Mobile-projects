//
//  MenuLevel.cpp
//  IBackground
//
//  Created by JorgeH on 11/4/16.
//
//

#include "MenuLevel.h"
#include "Almacenamiento.h"
#include "MiGame.h"
#include "Definiciones.h"
#include "MenuScene.h"
#include "Monedas.h"

#define ARROW_LEFT (-1)
#define ARROW_RIGHT (-2)

#define MENU_ITEMS_ACROSS 3
#define MENU_ITEMS_DOWN 1
#define MENU_ITEMS_PAGE (MENU_ITEMS_ACROSS*MENU_ITEMS_DOWN)
#define MENU_ITEMS_TOTAL 3

#define MENU_PAGES ((MENU_ITEMS_TOTAL/MENU_ITEMS_PAGE))
#define MENU_FRACTION (Vec2(0.8,0.8))
#define MENU_ANCHOR (Vec2(0.5,0.5))

#define SLIDE_DURATION 1.0

USING_NS_CC;

//Almacenamiento alm;

MenuLevel::MenuLevel() :
_menu(NULL), 
_sliding(false)
{
}

MenuLevel::~MenuLevel()
{
    
}

Scene* MenuLevel::createScene()
{
    auto scene = Scene::create();
    
    auto layer = MenuLevel::create();
    
    scene->addChild(layer);
    
    return scene;
}

static Point CalculatePosition(int itemNum)
{
    /*Size visibleSize = Director::getInstance()->getVisibleSize();
    Vec2 origin = Director::getInstance()->getVisibleOrigin();*/
    Size scrSize = Director::getInstance()->getVisibleSize();
    float Xs = scrSize.width;
    float Ys = scrSize.height;
    int gRows = MENU_ITEMS_DOWN;
    int gCols = MENU_ITEMS_ACROSS;
    int gBins = gRows*gCols;
    float Xb = MENU_FRACTION.x*Xs/gCols;
    float Yb = MENU_FRACTION.y*Ys/gRows;
    float Xa = MENU_ANCHOR.x * Xs;
    float Ya = MENU_ANCHOR.y * Ys;
    int page = itemNum / gBins;
    
    int binCol = itemNum % gCols;
    int binRow = (itemNum-page*gBins) / gCols;
    
    float xPos = binCol * Xb + Xb/2 + Xa - MENU_FRACTION.x*Xs/2 + page * Xs;
    float yPos = Ya - binRow*Yb - Yb/2 + MENU_FRACTION.y * Ys/2;
    
    Point pos = Vec2(xPos,yPos);
    
    return pos;
}

void MenuLevel::CreateMenu()
{
    if(_menu == NULL)
    {
        preload();
        Size scrSize = CCDirector::getInstance()->getWinSize();
        
        _menu = Menu::create();
        _menu->setPosition(Vec2(0,0));
        addChild(_menu);
        
        /*BORRAR*/
        
        Almacenamiento alm;
        
        cocos2d::log("Medallas conseguidas : %s", alm.cargar("medallasconseguidas").c_str());
        
        /*FIN BORRAR*/
        
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
        }
    }
}

float MenuLevel::Escalado(float tam)
{
    Size visibleSize = Director::getInstance()->getVisibleSize();
    float Aescalar = (visibleSize.width) / (MENU_ITEMS_ACROSS+2
                                            );
    float escalado = Aescalar / tam;
    //(visibleSize.width - 10 / MENU_ITEMS_ACROSS) / fondo_nivel->getContentSize().width
    return escalado;
}


bool MenuLevel::init()
{
    auto dec = new Decorado();
    this->addChild(dec->getNode());
    
	auto listener = cocos2d::EventListenerKeyboard::create();
	listener->onKeyPressed = CC_CALLBACK_2(MenuLevel::onKeyPressed, this);
	Director::getInstance()->getEventDispatcher()->addEventListenerWithSceneGraphPriority(listener, this);

    
    return true;
}


void MenuLevel::onKeyPressed(EventKeyboard::KeyCode keyCode, cocos2d::Event *event)
{
	if (keyCode == EventKeyboard::KeyCode::KEY_ESCAPE || keyCode == EventKeyboard::KeyCode::KEY_BACK)

		GoToMenuScene(this);
}


MenuLevel* MenuLevel::create()
{
    MenuLevel *pRet = new MenuLevel();
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

void MenuLevel::onEnter()
{
    CCScene::onEnter();
    CreateMenu();
}

void MenuLevel::onExit()
{
    CCScene::onExit();
}

void MenuLevel::onEnterTransitionDidFinish()
{
    CCScene::onEnterTransitionDidFinish();
}

void MenuLevel::onExitTransitionDidStart()
{
    CCScene::onExitTransitionDidStart();    
}

void MenuLevel::SlidingDone()
{
    _sliding = false;
}

void MenuLevel::PageLeft()
{
    if(_sliding)
        return;
    _sliding = true;
    Size scrSize = CCDirector::getInstance()->getWinSize();
    FiniteTimeAction* act1 = CCMoveBy::create(SLIDE_DURATION, Vec2(scrSize.width,0));
    FiniteTimeAction* act2 = CCCallFunc::create(this, callfunc_selector(MenuLevel::SlidingDone));
    _menu->runAction(CCSequence::create(act1,act2,NULL));
}

void MenuLevel::PageRight()
{
    if(_sliding)
        return;
    _sliding = true;
    Size scrSize = CCDirector::getInstance()->getWinSize();
    FiniteTimeAction* act1 = MoveBy::create(SLIDE_DURATION, Vec2(-scrSize.width,0));
    FiniteTimeAction* act2 = CallFunc::create(this, callfunc_selector(MenuLevel::SlidingDone));
    _menu->runAction(CCSequence::create(act1,act2,NULL));
}

void MenuLevel::MenuCallback(Object* sender)
{
    // This is a very contrived example
    // for handling the menu items.
    // -1 ==> Left Arrow
    // -2 ==> Right Arrow
    // Anything else is a selection
    Sprite* pMenuItem = (Sprite*)sender;
    CCLOG("Got Item %d Pressed",pMenuItem->getTag());
    
    switch(pMenuItem->getTag())
    {
        case 0:
            //PageLeft();
            Almacenamiento alm;
            alm.guardar("nivelseleccionado", "1");
            break;
        case 1:
            //PageRight();
            alm.guardar("nivelseleccionado", "2");
            break;
        case 2:
            alm.guardar("nivelseleccionado", "3");
            break;
        default:
            break;
    }
    //auto scene = Game::createScene();
    
    sonido.reproducir(MP3_BOTON);
    
    //Director::getInstance()->replaceScene(TransitionFade::create(TRANSITION_TIME, scene));
    auto scene = MiGame::create();
    scene->preloadResources();
    scene->start();
    Director::getInstance()->replaceScene(TransitionFade::create(TRANSITION_TIME, scene));
}

void MenuLevel::preload()
{
    //Cache de sprites
    auto spriteFrameCache = SpriteFrameCache::getInstance();
    //Cache de animaciones
    auto animCache = AnimationCache::getInstance();
    
        if(!spriteFrameCache->getSpriteFrameByName("medalla0-0.png")) {
            spriteFrameCache->addSpriteFramesWithFile(PLIST_MEDALLA);
        }
}

void MenuLevel::GoToMenuScene(Ref* sender)
{
    auto scene = MenuScene::createScene();
	sonido.reproducir(MP3_BOTON);
    Director::getInstance()->replaceScene(CCTransitionProgressOutIn::create(TRANSITION_TIME, scene));
}
//
//  Tienda.cpp
//  BackToTheJump
//
//  Created by JorgeH on 11/5/16.
//
//

#include <stdio.h>
#include "Tienda.h"
#include "Almacenamiento.h"
#include "MenuScene.h"
#include "Definiciones.h"

#define ARROW_LEFT (-1)
#define ARROW_RIGHT (-2)

#define MENU_ITEMS_ACROSS 3
#define MENU_ITEMS_DOWN 1
#define MENU_ITEMS_PAGE (MENU_ITEMS_ACROSS*MENU_ITEMS_DOWN)
#define MENU_ITEMS_TOTAL 4

#define MENU_PAGES ((MENU_ITEMS_TOTAL/MENU_ITEMS_PAGE))
#define MENU_FRACTION (Vec2(0.8,0.8))
#define MENU_ANCHOR (Vec2(0.5,0.5))

#define SLIDE_DURATION 1.0

#define ACTION_ANIM_MOVE    006
#define PLAYER_TAG 007

USING_NS_CC;

//Almacenamiento alm;

Tienda::Tienda() :
_menu(NULL),
_sliding(false)
{
}

Tienda::~Tienda()
{
    
}

Scene* Tienda::createScene()
{
    auto scene = Scene::create();
    
    auto layer = Tienda::create();
    
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
    int gRows = MENU_ITEMS_ACROSS;
    int gCols = MENU_ITEMS_DOWN;
    int gBins = gRows*gCols;
    float Xb = MENU_FRACTION.x*Xs/4;
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

void Tienda::CreateMenu()
{
    if(_menu == NULL)
    {
        Size scrSize = CCDirector::getInstance()->getWinSize();
        
        _menu = Menu::create();
        _menu->setPosition(Vec2(0,0));
        addChild(_menu);
        
        /*BORRAR*/
        
        Almacenamiento alm;
        
        /*FIN BORRAR*/
    
        Size visibleSize = Director::getInstance()->getVisibleSize();
		auto spriteFrameCache = SpriteFrameCache::getInstance();

		if (!spriteFrameCache->getSpriteFrameByName("Coin1.png"))
			spriteFrameCache->addSpriteFramesWithFile(OBJETOS_PLIST);

		auto spriteMoneda = Sprite::createWithSpriteFrameName("Coin1.png");
		auto monedita = MenuItemSprite::create(spriteMoneda, spriteMoneda);
		monedita->setEnabled(false);
		monedita->setScale(0.3);
		auto textMon = MenuItemLabel::create(Label::create(" x " + alm.cargar("monedas"), FONTS_ARIAL, 25));
		textMon->setDisabledColor(Color3B::WHITE);
		textMon->setEnabled(false);
		textMon->setPosition(Vec2(visibleSize.width * 0.9, visibleSize.height*0.9));
		textMon->setName("monedas");
		monedita->setPosition(Vec2(visibleSize.width * 0.9 - textMon->getContentSize().width, visibleSize.height*0.9));
		_menu->addChild(textMon);
		_menu->addChild(monedita);
        
        MenuItemFont* pItem;
        Point position;
        pItem = MenuItemFont::create("Atrás", this, menu_selector(Tienda::GoToMenuScene));
        position = Vec2(visibleSize.width * 0.1,visibleSize.height*0.93);
		auto pItem2 = MenuItemImage::create(IMG_BACK, IMG_BACK, this, menu_selector(Tienda::GoToMenuScene));
		pItem2->setTag(ARROW_LEFT);
		position = Vec2(visibleSize.width * 0.1, visibleSize.height*0.93);
		pItem2->setPosition(position);
		pItem2->setScale(0.1);
        _menu->addChild(pItem2);
        // Create the next/back menu items.
        for(int page = 0; page < MENU_PAGES; page++)
        {
            // Create the Back/Forward buttons for the page.
            // Back arrow if there is a previous page.
            if(page > 0)
            {
                pItem = MenuItemFont::create("Atras", this, menu_selector(Tienda::MenuCallback));
                pItem->setTag(ARROW_LEFT);
                position = Vec2(page*scrSize.width + scrSize.width*0.1,scrSize.height*0.1);
                pItem->setPosition(position);
                pItem->setFontSize(35);
                pItem->setFontName("Arial");
                _menu->addChild(pItem);
            }
            if(page < (MENU_PAGES-1))
            {
                pItem = MenuItemFont::create("Siguiente", this, menu_selector(Tienda::MenuCallback));
                pItem->setTag(ARROW_RIGHT);
                position = Vec2(page*scrSize.width + scrSize.width*0.9,scrSize.height*0.1);
                pItem->setPosition(position);
                pItem->setFontSize(35);
                pItem->setFontName("Arial");
                _menu->addChild(pItem);
            }
        }

        if(!spriteFrameCache->getSpriteFrameByName("Idle (1).png"))
        {
            spriteFrameCache->addSpriteFramesWithFile(PLIST_PERSONAJE);
        }
		
		int iNivel = alm.StringToInt(alm.cargar("medallasconseguidas"));
		
		

        
        for(int idx = 0; idx < MENU_ITEMS_TOTAL; idx++)
        {
			std::string sNivel = alm.IntToString(idx);
			
			std::string sBloqueado = "armadura" + sNivel;
            position = CalculatePosition(idx);
            if(idx < MENU_ITEMS_TOTAL - 1)
            {
            if(iNivel>=idx*3 || idx==0)
            {
                alm.guardar(sBloqueado, "desbloqueado");
                auto item = Sprite::create();
                item->setSpriteFrame(spriteFrameCache->getSpriteFrameByName("Idle (1).png"));
                //item->setScale(0.2, 0.2);
                
                switch (idx) {
                    case 1:
                        item->setColor(Color3B::GREEN);
                        break;
                    case 2:
                        item->setColor(Color3B::RED);
                        break;
                        
                    default:
                        break;
                }
                auto fondo_nivel = MenuItemImage::create(IMG_B_NIVEL, IMG_B_NIVEL_PRESIONADO, this, menu_selector(Tienda::MenuCallback));
                Sprite* b = Sprite::create(IMG_B_NIVEL_PRESIONADO);
                fondo_nivel->setDisabledImage(b);
                float escalar = Escalado(fondo_nivel->getContentSize().width);
                fondo_nivel->setScale(escalar*2 , escalar);
                fondo_nivel->setPosition(position);
                
                _menu->addChild(fondo_nivel);
                fondo_nivel->setTag(idx);
				auto menuitemsprite = MenuItemSprite::create(item, item, item);
				menuitemsprite->setEnabled(false);
				menuitemsprite->setScale(0.2f);
				menuitemsprite->setPosition(position.x - fondo_nivel->getContentSize().width / 6, position.y);
				_menu->addChild(menuitemsprite);
				MenuItemLabel* textItem;
				char buffer[256];
                sprintf(buffer,"%d Monedas",idx * 25);
				if (alm.armaduraComprada(idx))
				{
					sprintf(buffer, "Comprada");
				}
                textItem = MenuItemLabel::create(Label::create(buffer, FONTS_ARIAL, 25));
				textItem->setEnabled(false);
				textItem->setColor(Color3B::WHITE);
				textItem->setDisabledColor(Color3B::WHITE);
                _menu->addChild(textItem);
                textItem->setTag(idx+100);
                textItem->setPosition(position);
                textItem->setPositionX(textItem->getPositionX() + item->getContentSize().width / 12);
                std::string a = alm.cargar("selArmadura" + sNivel);
                if(alm.cargar("selArmadura" + sNivel) == "true")
                {
                    fondo_nivel->setEnabled(false);
                }
            }else{
				auto fsprite = Sprite::create(IMG_B_NIVEL_BLOQUEADO);
                auto fondo_nivel = MenuItemSprite::create(fsprite, fsprite, fsprite);
                float escalar = Escalado(fondo_nivel->getContentSize().width);
                fondo_nivel->setScale(escalar * 2, escalar);
                fondo_nivel->setPosition(position);
                
                _menu->addChild(fondo_nivel);
                alm.guardar("selArmadura" + sNivel, "false");
            }
        }else
        {
            auto animCache = AnimationCache::getInstance();
            
            auto animAndar = Animation::create();
            animAndar->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Idle (1).png"));
            animAndar->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Idle (2).png"));
            animAndar->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Idle (3).png"));
            animAndar->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Idle (4).png"));
            animAndar->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Idle (5).png"));
            animAndar->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Idle (6).png"));
            animAndar->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Idle (7).png"));
            animAndar->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Idle (8).png"));
			animAndar->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Idle (9).png"));
			animAndar->addSpriteFrame(spriteFrameCache->getSpriteFrameByName("Idle (10).png"));
            
            animAndar->setDelayPerUnit(0.1f);
            animAndar->retain();

            Animate* actionAnimate = Animate::create(animAndar);
        
            auto item = Sprite::create();
			item->setScale(-0.7f, 0.7f);

            item->setSpriteFrame(spriteFrameCache->getSpriteFrameByName("Idle (1).png"));
            position = CalculatePosition(1);
            item->setPosition(position.x, position.y);
            item->setPositionX(item->getPositionX() + item->getContentSize().width-50);
            item->setTag(PLAYER_TAG);
            
            this->addChild(item);
            RepeatForever* actionRepeat = RepeatForever::create(actionAnimate);
            actionRepeat->setTag(ACTION_ANIM_MOVE);
            item->runAction(actionRepeat);

			/*auto mySpawn = Sequence::createWithTwoActions(MoveBy::create(1, Vec2(0, 50)), 
													   MoveBy::create(1, Vec2(0, -50)));


			auto actionRepeat2 = RepeatForever::create(mySpawn);
			item->runAction(actionRepeat2);*/


            for(int i = 0; i<MENU_ITEMS_TOTAL-1; i++)
            {
				std::string num = alm.IntToString(i);
				
				if(alm.cargar("selArmadura" + num) == "true")
                {
                    switch(i)
                    {
                        case 1:
                            item->setColor(Color3B::GREEN);
                            break;
                        case 2:
                            //PageRight();
                            item->setColor(Color3B::RED);
                            break;
                        case 0:
                            item->setColor(Color3B(255.0f, 255.0f, 255.0f));
                        default:
                            break;
                    }
                }
            }
        }
        }
    }
}

float Tienda::Escalado(float tam)
{
    Size visibleSize = Director::getInstance()->getVisibleSize();
    float Aescalar = (visibleSize.height) / (MENU_ITEMS_ACROSS+2);
    float escalado = Aescalar / tam;
    //(visibleSize.width - 10 / MENU_ITEMS_ACROSS) / fondo_nivel->getContentSize().width
    return escalado;
}


bool Tienda::init()
{
    auto dec = new Decorado();
	dec->setTipo(1);
    this->addChild(dec->getNode());

	auto listener = cocos2d::EventListenerKeyboard::create();
	listener->onKeyPressed = CC_CALLBACK_2(Tienda::onKeyPressed, this);
	Director::getInstance()->getEventDispatcher()->addEventListenerWithSceneGraphPriority(listener, this);
    
    return true;
}

void Tienda::onKeyPressed(EventKeyboard::KeyCode keyCode, cocos2d::Event *event)
{
	if (keyCode == EventKeyboard::KeyCode::KEY_ESCAPE || keyCode == EventKeyboard::KeyCode::KEY_BACK)

		GoToMenuScene(this);
}

Tienda* Tienda::create()
{
    Tienda *pRet = new Tienda();
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

void Tienda::onEnter()
{
    CCScene::onEnter();
    CreateMenu();
}

void Tienda::onExit()
{
    CCScene::onExit();
}

void Tienda::onEnterTransitionDidFinish()
{
    CCScene::onEnterTransitionDidFinish();
}

void Tienda::onExitTransitionDidStart()
{
    CCScene::onExitTransitionDidStart();
    
}

void Tienda::SlidingDone()
{
    _sliding = false;
}

void Tienda::PageLeft()
{
    if(_sliding)
        return;
    _sliding = true;
    Size scrSize = CCDirector::getInstance()->getWinSize();
    FiniteTimeAction* act1 = CCMoveBy::create(SLIDE_DURATION, Vec2(scrSize.width,0));
    FiniteTimeAction* act2 = CCCallFunc::create(this, callfunc_selector(Tienda::SlidingDone));
    _menu->runAction(CCSequence::create(act1,act2,NULL));
}

void Tienda::PageRight()
{
    if(_sliding)
        return;
    _sliding = true;
    Size scrSize = CCDirector::getInstance()->getWinSize();
    FiniteTimeAction* act1 = CCMoveBy::create(SLIDE_DURATION, Vec2(-scrSize.width,0));
    FiniteTimeAction* act2 = CCCallFunc::create(this, callfunc_selector(Tienda::SlidingDone));
    _menu->runAction(CCSequence::create(act1,act2,NULL));
}

void Tienda::GoToMenuScene(Ref* sender)
{
    auto scene = MenuScene::createScene();
	sonido.reproducir(MP3_BOTON);
    Director::getInstance()->replaceScene(CCTransitionSlideInB::create(TRANSITION_TIME, scene));
}

void Tienda::MenuCallback(Object* sender)
{
    // This is a very contrived example
    // for handling the menu items.
    // -1 ==> Left Arrow
    // -2 ==> Right Arrow
    // Anything else is a selection
    Sprite* pMenuItem = (Sprite*)sender;
    CCLOG("Got Item %d Pressed",pMenuItem->getTag());
    Sprite *player = (Sprite*) this->getChildByTag(PLAYER_TAG);
    auto tintTo = TintTo::create(0.f, 0.0f, 220.0f, 255.0f);
    MenuItemImage* pm = (MenuItemImage*)sender;
    Almacenamiento alm;
    //alm.gua+
    /*float escalar = Escalado(pm->getContentSize().width);
    const Rect rect = Rect(pm->rect().getMinX() * escalar, pm->rect().getMaxY() * escalar, pm->rect().getMinX() * escalar, pm->rect().getMaxY() * escalar);*/
    pm->setEnabled(false);
    for(int i = 0  ; i < 3 ; i++)
    {
        Node* tag = _menu->getChildByTag(i);
		std::string sNivel = alm.IntToString(i);
		
		std::string sBloqueado = "armadura" + sNivel;
		if(i!=pMenuItem->getTag())
        {
            if(alm.cargar(sBloqueado) == "desbloqueado")
            {
                MenuItemImage* pmT = (MenuItemImage*) tag;

				if(pmT)
					pmT->setEnabled(true);
                alm.guardar("selArmadura" + sNivel, "false");
            }
        }else
        {
            alm.guardar("selArmadura" + sNivel, "true");
        }
    }
    //SpriteFrame* imagen = SpriteFrame::create("b_nivel_pressed.png", rect);
    //pm->setNormalSpriteFrame(imagen);
    
    switch(pMenuItem->getTag())
    {
        case 1:
            
            player->setColor(Color3B::GREEN);
            break;
        case 2:
            //PageRight();
            player->setColor(Color3B::RED);
            break;
        case 0:
            player->setColor(Color3B(255.0f, 255.0f, 255.0f));
        default:
            break;
    }
    //auto scene = Game::createScene();
    
    if(!alm.armaduraComprada(pMenuItem->getTag()))
    {
        Label* textMon = (Label*) _menu->getChildByName("monedas");
        String mon = alm.cargar("monedas");
        char buffer[256];
        int contenido = mon.intValue() - pMenuItem->getTag() * 25;
        alm.guardar("monedas", alm.IntToString(contenido));
        sprintf(buffer,"Monedas : %d ",contenido);
        textMon->setString(buffer);
        alm.guardarArmadura(pMenuItem->getTag());
    }
    sonido.reproducir(MP3_BOTON);
}

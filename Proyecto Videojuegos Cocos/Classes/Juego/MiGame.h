//
//  MarioSc.h
//  Test01
//
//  Created by Fidel Aznar on 13/1/15.
//
//

#pragma once

#include "../Engine2D/SimpleGame.h"

#include "Npc.h"
#include "Decorado.h"
#include "GUI.h"
#include "Mundo.h"
#include "PhysicsPlayer.h"
#include "Sonido.h"


class MiGame: public SimpleGame{
    
public:
    
    
    bool init();
        ~MiGame();
    
    void preloadResources();
    void start();
    void reset();
    
    void pausa(cocos2d::Ref *sender);
    void continuar(Ref *sender);
    void finNivel(Ref *sender, bool pasado);
    void restart(Ref *sender);
    void siguiente(Ref *sender);
    void niveles(Ref *sender);
    
    MenuItemImage *bPausa;
    MenuItemImage *bPlay;
    MenuItemImage *bSalir;
    MenuItemImage *bLevels;
    MenuItemImage *bRestart;
    MenuItemImage *bSiguiente;
    Sprite *capa;
    MenuItemSprite *fondoSprite;
    MenuItemLabel *TextoNivel;
	MenuItemLabel *TextoMonedas;
    Menu *menu;
    Menu *menuDie;
    
    void updateEachFrame(float delta);
    void updateIA(float delta);
    float Escalado(float tam);

    void updateMonedas(Moneda *m);
    
    bool onTouchBegan( cocos2d::Touch *touch, cocos2d::Event *event );
    bool onTouchEnded( cocos2d::Touch *touch, cocos2d::Event *event );
    
    void onKeyPressed(EventKeyboard::KeyCode keyCode, cocos2d::Event *event);
    void onKeyReleased(EventKeyboard::KeyCode keyCode, cocos2d::Event *event);
    
    Node* getScrollable();
    
    // implement the "static create()" method manually
    CREATE_FUNC(MiGame);
    
private:
    PhysicsPlayer *m_player;
    GUI *m_gui;
    Mundo *m_mundo;

	bool m_pausa = false;
	bool m_salto = false;

	Sonido sonido;

    int monedasConseguidas = 0;
    
    bool medallas[3];
    
    Label* textMon;
    
    cocos2d::Size visibleSize;
    cocos2d::Vec2 origin;
    
    Node *scrollable;
};



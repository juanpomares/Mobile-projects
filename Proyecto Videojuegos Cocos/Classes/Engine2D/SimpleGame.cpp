//
//  EScene.cpp
//  Test01
//
//  Created by Fidel Aznar on 13/1/15.
//
//

#include "SimpleGame.h"

#define IAFrequency 1/15.0
 
bool SimpleGame::init(){
    
    //Super
    Scene::init();

    return true;
}

void SimpleGame::run(){
    
    //Update
    this->schedule(schedule_selector(SimpleGame::updateEachFrame));
    this->schedule(schedule_selector(SimpleGame::updateIA),IAFrequency);
    
    //Creo listeners del teclado
    auto listener = cocos2d::EventListenerKeyboard::create();
    listener->onKeyPressed = CC_CALLBACK_2(SimpleGame::onKeyPressed,this);
    Director::getInstance()->getEventDispatcher()->addEventListenerWithSceneGraphPriority(listener, this);
    
    listener = cocos2d::EventListenerKeyboard::create();
    listener->onKeyReleased = CC_CALLBACK_2(SimpleGame::onKeyReleased,this);
    Director::getInstance()->getEventDispatcher()->addEventListenerWithSceneGraphPriority(listener, this);
    
}


void SimpleGame::addGameEntity(GameEntity *ge){
    m_gameEntities.pushBack(ge);
}


void SimpleGame::removeGameEntity(GameEntity *ge){
    m_gameEntities.eraseObject(ge);
}

void SimpleGame::updateEachGameEntityWithDelta(float delta){
    for (auto e: this->m_gameEntities){
        e->update(delta);
    }
}
void SimpleGame::preloadEachGameEntity(){
    for (auto e: this->m_gameEntities){
        e->preloadResources();
    }
}
void SimpleGame::addEachGameEntityNodeTo(Node *node){
    for (auto e: this->m_gameEntities){
        node->addChild(e->getNode(), e->getNode()->getLocalZOrder());
    }
}


SimpleGame::~SimpleGame()
{
    this->m_gameEntities.clear();
}

void SimpleGame::onKeyPressed(EventKeyboard::KeyCode keyCode, Event *event){};
void SimpleGame::onKeyReleased(EventKeyboard::KeyCode keyCode, Event *event){};

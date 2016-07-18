//
//  PhysicsPlayer.hpp
//  Mario
//
//  Created by Miguel Angel Lozano Ortega on 19/1/16.
//
//

#pragma once

#define DEBUG false

#define ACTION_ANIM_MOVE    001
#define ACTION_ANIM_FALL    002
#define ACTION_ANIM_JUMP    003
#define ACTION_ANIM_DIE     004
#define ACTION_ANIM_SLIDE   005
#define ACTION_FIN_LEVEL   006

#include "../Engine2D/PhysicsGameEntity.h"

enum colorCharacter{Amarillo=0, Verde, Rojo};

class PhysicsPlayer: public PhysicsGameEntity {
public:
    
    bool init();
    ~PhysicsPlayer();
    void preloadResources();
    Node* getNode();
    
    const Vec2& getPosition(){
        return m_playerSprite->getPosition();
    };
    
    void setPosition(const Vec2& pos) {
        this->setTransform(pos, 0.0f);
    }
    
    virtual void update(float dt);
    
    int getNumVidas(){return m_vidas; };

    void Jump();
    void Fall();
    void Die();
    void EndDie();
    
    void Slide();
    void EndSlide();
    void TouchGround();
    
    bool checkGrounded();
    bool checkLateralCollision();
    
    
    Node* m_scrollable=NULL;
    

    void BeginContact(b2Contact* contact);
    void EndContact(b2Contact* contact);
    
    void SetColor(int color);
    int  getColor();

	bool isDying();

    CREATE_FUNC(PhysicsPlayer);
private:
    CombinedData* mCB=NULL;
    
    int contactos_suelo=0, contactos_up[2], contactosDobleSalto=0;
    
    Sprite *m_playerSprite;
    Animation *animAndar;
    Animation *animJump;
    Animation *animSlide;
    Animation *animFall;
    Animation *animDead;
    
    
    b2Fixture *m_groundTest;
    b2Fixture *m_lateralTest;
    
    b2Fixture *m_upTest[2];
    
    
    
    Speed *m_actionAndar;
    Animation *m_animAndar;
    
    int m_vidas=3;
    int color=0;
    
    bool m_lateralCollision=false, m_groundCollision=false, m_upCollision=false;
    
    bool agachado=false;
    
    float m_vel=3;
    const float m_jump=10;
    
    
    void updateCamera();
    
    void* m_DobleSalto=NULL;
    
};

class PlayerContactListener : public b2ContactListener {
    
public:
    
    PlayerContactListener(PhysicsPlayer *player);
    
    // Se produce un contacto entre dos cuerpos
    virtual void BeginContact(b2Contact* contact);
    
    // El contacto entre los cuerpos ha finalizado
    virtual void EndContact(b2Contact* contact);
    
private:
    PhysicsPlayer *m_player;
};



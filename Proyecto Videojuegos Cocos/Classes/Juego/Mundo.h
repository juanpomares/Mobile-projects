//
//  Mundo.h
//  VideojuegosCocos
//
//  Created by Fidel Aznar on 16/1/15.
//
//

#pragma once

#include "../Engine2D/GameEntity.h"
#include "../Engine2D/TiledMapHelper.h"
#include "../Engine2D/PhysicsGameEntity.h"
#include "../Engine2D/CocosDebugDraw.h"
#include "Definiciones.h"
#include "Npc.h"
#include "Monedas.h"
#include "mina.h"
#include "DobleSalto.h"
#include "Llama.h"
#include "Proyectil.h"
#include "Pinchos.h"
#include "Portal.hpp"
#include "Rueda.h"

class Mundo: public PhysicsGameEntity{
    
public:
    bool init();
    ~Mundo();
    static Mundo* getActual();
    
    virtual void preloadResources();
    virtual Node* getNode();
    void update(float delta);
    
    Rect getVisibleRect();
    void setVisibleRect(float x, float y, float width, float height);
    void setVisibleRectOrigin(float x, float y);
    
    /*int getTileGIDAtPosition(const Point &tileCoords);
    Point tileCoordForPosition(const Vec2& position);
    void deleteTileAt(const Point &tileCoords);
	*/
    Size getTileSize();
    
    cocos2d::Size getTamTiles();
    
    Vector<NPC*> getNPCs();
    void deleteNPC(NPC *npcDeleted);
    
    b2World *getPhysicsWorld(){return m_world;}
    
    Node* scrollable_camera;
    
    CREATE_FUNC(Mundo);
    void setPlayer(Node* n){m_player=n;}
    
private:
    
    static Mundo *m_instance_actual;
    Rect m_visibleRect;
    
    TiledMapHelper *m_tiledMapHelper;
    
    Vector<NPC*> m_npcs;
	Vector<NPC*> m_npcs_borrados;
    
	void clear_NPCS();

    // Mundo fisico
    b2World *m_world;
    CocosDebugDraw *m_debugDraw;
    
    CombinedData* cb=NULL;
    CombinedData* cbVerde=NULL;
    Node* m_node;
    Node* m_player;
    
    int armadura = 0;
    
    void initPhysics();
    
};



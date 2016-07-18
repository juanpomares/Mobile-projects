//
//  TiledMapHelper.h
//  VideojuegosCocos
//
//  Created by Fidel Aznar on 18/1/15.
//
//

#pragma once

#include "cocos2d.h"
USING_NS_CC;


class TiledMapHelper: public Ref {
    
public:
    
    bool init();
    
    void loadTileMap(const char *url, const char *withCollisionLayer);
    
    //Point tileCoordForPosition(const Vec2& position);
   // Rect rectForTileAt(const Point &tileCoords);
    //int getTileGIDAt(const Point &tileCoords);
    //int getTileGIDAtPosition(const Vec2& position);
    //void deleteTileAt(const Point &tileCoords);
    
    TMXTiledMap *getTiledMap();
    TMXObjectGroup *getObjectGroup();
    ValueVector getObjects();
    
    CREATE_FUNC(TiledMapHelper);

    
private:
    
    TMXTiledMap *m_tiledmap;
    TMXObjectGroup *m_objectGroup;
	TMXLayer *m_collision;
    
};


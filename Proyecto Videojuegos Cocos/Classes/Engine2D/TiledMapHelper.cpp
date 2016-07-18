//
//  TiledMapHelper.cpp
//  VideojuegosCocos
//
//  Created by Fidel Aznar on 18/1/15.
//
//

#include "TiledMapHelper.h"

bool TiledMapHelper::init(){
    
    return true;

}


void TiledMapHelper::loadTileMap(const char *url, const char *withCollisionLayer){
    Size visibleSize = Director::getInstance()->getVisibleSize();
    
    m_tiledmap = TMXTiledMap::create(url);
    m_tiledmap->setLocalZOrder(-500);
    m_tiledmap->setPositionY(visibleSize.height/2);
    m_collision = m_tiledmap->getLayer(withCollisionLayer);

	if(m_collision)
	{
		m_collision->setLocalZOrder(500);
	}
	else
	{
		CCLOG("Error con M_Collision :0");
	}

	m_objectGroup = m_tiledmap->getObjectGroup("Objetos");
}


/*
Point TiledMapHelper::tileCoordForPosition(const Point& position)
{
    Size tileSize = m_tiledmap->getTileSize();
    auto tpos = m_tiledmap->getPosition();
    
    float totalHeight = m_tiledmap->getMapSize().height * tileSize.height;
    float x = floor((position.x-tpos.x) / tileSize.width);
    float y = floor((totalHeight - (position.y-tpos.y)) / tileSize.height);
    return Point(x, y);
}

Rect TiledMapHelper::rectForTileAt(const Point &tileCoords) {
    Size tileSize = m_tiledmap->getTileSize();
    auto tpos = m_tiledmap->getPosition();
    
    float totalHeight = m_tiledmap->getMapSize().height * tileSize.height;
    Point origin = Point((tileCoords.x-tpos.x) * tileSize.width, totalHeight -
                         (((tileCoords.y-tpos.y) + 1) * tileSize.height));
    return Rect(origin.x, origin.y,
                tileSize.width, tileSize.height);
}


int TiledMapHelper::getTileGIDAt(const Point &tileCoords){
    Size s = m_collision->getLayerSize();
    
    if(tileCoords.x>=0 && tileCoords.x<s.width && tileCoords.y>=0 && tileCoords.y<s.height)
        return m_collision->getTileGIDAt(tileCoords);
    else {
        CCLOG("Warning: Accesing pos %f,%f in tilemap (out of range)", tileCoords.x, tileCoords.y);
        return 0;
    }
    
    return 0;
}


void TiledMapHelper::deleteTileAt(const Point &tileCoords){
    m_collision->setTileGID(0, tileCoords);
}


int TiledMapHelper::getTileGIDAtPosition(const Point &pos){
    return getTileGIDAt(tileCoordForPosition(pos));
}*/

ValueVector TiledMapHelper::getObjects()
{
    if(m_objectGroup != NULL)
    {
        ValueVector objects = m_objectGroup->getObjects();
        
        return objects;
        
    }
    
    return ValueVector();
}

TMXTiledMap *TiledMapHelper::getTiledMap(){

    return m_tiledmap;
}


TMXObjectGroup *TiledMapHelper::getObjectGroup(){
    return m_objectGroup;
}


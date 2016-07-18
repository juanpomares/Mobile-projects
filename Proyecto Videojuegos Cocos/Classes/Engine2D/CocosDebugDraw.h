//
//  CocosDebugDrawNode.hpp
//  Mario
//
//  Created by Miguel Angel Lozano Ortega on 28/2/16.
//
//

#ifndef CocosDebugDrawNode_hpp
#define CocosDebugDrawNode_hpp

#include <stdio.h>

#include "Box2D/Box2D.h"
#include "cocos2d.h"

struct b2AABB;

class CocosDebugDraw : public b2Draw
{
    float32 mRatio;
    cocos2d::DrawNode *mNode;
    
public:
    CocosDebugDraw();
    CocosDebugDraw( float32 ratio );
    ~CocosDebugDraw();
    
    cocos2d::Node* GetNode();
    void Clear();
    
    virtual void DrawPolygon(const b2Vec2* vertices, int vertexCount, const b2Color& color);
    virtual void DrawSolidPolygon(const b2Vec2* vertices, int vertexCount, const b2Color& color);
    virtual void DrawCircle(const b2Vec2& center, float32 radius, const b2Color& color);
    virtual void DrawSolidCircle(const b2Vec2& center, float32 radius, const b2Vec2& axis, const b2Color& color);
    virtual void DrawSegment(const b2Vec2& p1, const b2Vec2& p2, const b2Color& color);
    virtual void DrawTransform(const b2Transform& xf);
    virtual void DrawPoint(const b2Vec2& p, float32 size, const b2Color& color);
    virtual void DrawString(int x, int y, const char* string, ...);
    virtual void DrawAABB(b2AABB* aabb, const b2Color& color);
};


#endif /* CocosDebugDrawNode_hpp */

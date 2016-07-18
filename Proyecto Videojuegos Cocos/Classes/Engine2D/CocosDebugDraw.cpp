//
//  CocosDebugDrawNode.cpp
//  Mario
//
//  Created by Miguel Angel Lozano Ortega on 28/2/16.
//
//

#include "CocosDebugDraw.h"


USING_NS_CC;

CocosDebugDraw::CocosDebugDraw()
: mRatio( 1.0f )
{
    mNode = DrawNode::create();
    mNode->retain();
}

CocosDebugDraw::CocosDebugDraw( float32 ratio )
: mRatio( ratio )
{
    mNode = DrawNode::create();
    mNode->retain();
}

CocosDebugDraw::~CocosDebugDraw()
{
    mNode->release();
}

void CocosDebugDraw::Clear()
{
    mNode->clear();
}

Node* CocosDebugDraw::GetNode() {
    return mNode;
}


void CocosDebugDraw::DrawPolygon(const b2Vec2* old_vertices, int vertexCount, const b2Color& color)
{
    
    Vec2 *vertices = new Vec2[vertexCount];
    for( int i=0;i<vertexCount;i++)
    {
        vertices[i] = Vec2(old_vertices[i].x * mRatio, old_vertices[i].y * mRatio);
    }

    //mNode->drawPolygon(vertices, vertexCount, Color4F(0, 0, 0, 0), 1.0f, Color4F(color.r, color.g, color.b, 1.0f));
    mNode->drawPoly(vertices, vertexCount, false, Color4F(color.r, color.g, color.b, 1.0f));
    
    delete[] vertices;
}

void CocosDebugDraw::DrawSolidPolygon(const b2Vec2* old_vertices, int vertexCount, const b2Color& color)
{

    Vec2 *vertices = new Vec2[vertexCount];
    for( int i=0;i<vertexCount;i++)
    {
        vertices[i] = Vec2(old_vertices[i].x * mRatio, old_vertices[i].y * mRatio);
    }
    
    //mNode->drawPolygon(vertices, vertexCount, Color4F(color.r, color.g, color.b, 1.0f), 1.0f, Color4F(color.r, color.g, color.b, 1.0f));
    mNode->drawSolidPoly(vertices, vertexCount, Color4F(color.r, color.g, color.b, 1.0f));
    
    delete[] vertices;
}

void CocosDebugDraw::DrawCircle(const b2Vec2& center, float32 radius, const b2Color& color)
{
    mNode->drawCircle(Vec2(center.x * mRatio, center.y * mRatio), radius * mRatio, 0.0f, 16.0f, false, 1.0f, 1.0f, Color4F(color.r, color.g, color.b, 1.0f));
}

void CocosDebugDraw::DrawSolidCircle(const b2Vec2& center, float32 radius, const b2Vec2& axis, const b2Color& color)
{
    mNode->drawSolidCircle(Vec2(center.x * mRatio, center.y * mRatio), radius * mRatio, 0.0f, 16.0f, 1.0f, 1.0f, Color4F(color.r, color.g, color.b, 1.0f));
}

void CocosDebugDraw::DrawSegment(const b2Vec2& p1, const b2Vec2& p2, const b2Color& color)
{
    mNode->drawSegment(Vec2(p1.x * mRatio, p1.y * mRatio), Vec2(p2.x * mRatio , p2.y * mRatio), 1.0f, Color4F(color.r, color.g, color.b, 1.0f));
}

void CocosDebugDraw::DrawTransform(const b2Transform& xf)
{
    b2Vec2 p1 = xf.p, p2;
    const float32 k_axisScale = 0.4f;
    p2 = p1 + k_axisScale * xf.q.GetXAxis();
    DrawSegment(p1, p2, b2Color(1,0,0));
    
    p2 = p1 + k_axisScale * xf.q.GetYAxis();
    DrawSegment(p1,p2,b2Color(0,1,0));
}

void CocosDebugDraw::DrawPoint(const b2Vec2& p, float32 size, const b2Color& color)
{
    mNode->drawPoint(Vec2(p.x * mRatio, p.y * mRatio), size * mRatio, Color4F(color.r, color.g, color.b, 1.0f));
}

void CocosDebugDraw::DrawString(int x, int y, const char *string, ...)
{
    //    NSLog(@"DrawString: unsupported: %s", string);
    //printf(string);
    /* Unsupported as yet. Could replace with bitmap font renderer at a later date */
}

void CocosDebugDraw::DrawAABB(b2AABB* aabb, const b2Color& color)
{
    mNode->drawRect(Vec2(aabb->lowerBound.x * mRatio, aabb->lowerBound.y * mRatio), Vec2(aabb->upperBound.x * mRatio, aabb->upperBound.y * mRatio), Color4F(color.r, color.g, color.b, 1.0f));
}

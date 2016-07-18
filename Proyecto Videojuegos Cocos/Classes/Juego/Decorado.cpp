//
//  Decorado.cpp
//  VideojuegosCocos
//
//  Created by Fidel Aznar on 16/1/15.
//
//

#include "Decorado.h"
#include "Definiciones.h"

#define VelFondo 8

bool Decorado::init(){
    GameEntity::init();
    tipo = 0;
    return true;
}


Node* Decorado::getNode(){
    if(m_node==NULL) {
        
        visibleSize = Director::getInstance()->getVisibleSize();
        origin = Director::getInstance()->getVisibleOrigin();
        
        std::string imagen;
        std::string imagen2;
        
        if(tipo == 1)
        {
            imagen = IMG_ESCENA3;
            imagen2 = IMG_ESCENA3;
        }
        
        else
        {
			imagen = IMG_VENTANA2;
            imagen2 = IMG_ESCENA2;
        }
        
        this->bc1 = Sprite::create(imagen);
        this->bc1->setPosition(Point::ZERO);
        this->bc1->setAnchorPoint(Vec2(0, 0));
        /*this->bc1->setPosition(Point( visibleSize.width / 2 + origin.x, visibleSize.height / 2 + origin.y ));*/
        
        this->bc2 = Sprite::create(imagen2);
        this->bc2->setPosition(Point::ZERO);
        this->bc2->setAnchorPoint(Vec2(0, 0));
        /*this->bc2->setPosition( Point( bc1->getPosition().x + bc1->boundingBox().size.width, bc1->getPosition().y));*/
        
        this->bc3 = Sprite::create(imagen2);
        this->bc3->setPosition(Point(visibleSize.width, 0));
        this->bc3->setAnchorPoint(Vec2(0, 0));
        /*this->bc3->setPosition( Point( bc2->getPosition().x + bc2->boundingBox().size.width, bc1->getPosition().y));*/
        
        auto callback = CallFunc::create([=]()
                                         {
                                             //CCLog("loquesea");
                                             this->bc2->setPosition(Point(visibleSize.width, 0));
											 this->bc2->runAction(getActionRepeat());
                                         });
        MoveTo* actionAnimate = MoveTo::create(VelFondo/2, Vec2(-visibleSize.width, 0));
        auto action = Sequence::create(actionAnimate, callback, NULL);
        
        double widthTotal = this->bc1->getContentSize().width;
        double tamWidth=Director::getInstance()->getVisibleSize().width;
        
        double heightTotal = this->bc1->getContentSize().height;
        double tamHeight=Director::getInstance()->getVisibleSize().height;
        
        double scalefinalH = tamHeight / heightTotal;
        double scalefinalW = tamWidth/widthTotal;
        
        this->bc1->setScale(scalefinalW, scalefinalH);
        this->bc2->setScale(scalefinalW, scalefinalH);
        this->bc3->setScale(scalefinalW, scalefinalH);
        
		this->bc3->runAction(getActionRepeat());
		this->bc2->runAction(action);
        
        m_node= Node::create();
        m_node->addChild(bc2);
        m_node->addChild(bc3);
        if(tipo != 1)
        {
            m_node->addChild(bc1);
        }

		//Para que no se vea la linea que a veces sale entre bc1 y bc2
		auto colorLayer = LayerColor::create(Color4B(12, 12, 12, 255));
		m_node->addChild(colorLayer, -5);

        m_node->setLocalZOrder(-50);
    }
    
    return m_node;
}

void Decorado::setTipo(int _tipo)
{
    tipo = _tipo;
}


RepeatForever* Decorado::getActionRepeat()
{
    visibleSize = Director::getInstance()->getVisibleSize();
    
    MoveTo* actionMove2 = MoveTo::create(0, Vec2(visibleSize.width, 0));
    MoveTo* actionMove = MoveTo::create(VelFondo, Vec2(-visibleSize.width, 0));
    Sequence* secuencia = Sequence::create(actionMove, actionMove2, NULL);
    
    return RepeatForever::create(secuencia);
}
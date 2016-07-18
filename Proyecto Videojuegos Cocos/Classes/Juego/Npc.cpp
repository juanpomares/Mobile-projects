//
//  Npc.cpp
//  VideojuegosCocos
//
//  Created by Fidel Aznar on 16/1/15.
//
//

#include "Npc.h"
#include "Mundo.h"


bool NPC::init(){
    PhysicsGameEntity::init();
	m_destruction = false;
    m_sprite = NULL;
    return true;
}

void NPC::update(float delta)
{
    if(m_destruction==1)
    {
        PhysicsGameEntity::removePhysicsBody();
		m_destruction =2;
        Mundo::getActual()->deleteNPC(this);
        //delete this;
    }else  if (m_destruction == 0)
    {
		if (!(tipo != npcType::TFinNivel && tipo != npcType::TCambioEscala))
			return;

		PhysicsGameEntity::update(delta);        
		auto mundillo=Mundo::getActual();
        
		if(mundillo!=NULL)
		{
			//Si es visible (no confundir con si se rederiza o no ,visible significa alpha>0)
			if(m_sprite->isVisible())
			{
				Rect r = mundillo->getVisibleRect();        
        
				//Como no se renderiza lo pauso
				if(!r.containsPoint(m_sprite->getPosition()))
				{
					m_sprite->pause(); m_sprite->setVisible(false);        
				}
			//Si es invisible y vuelve a ser visible lo restauro
			}
    
			if(!m_sprite->isVisible())
			{
				Rect r = mundillo->getVisibleRect();
				if(r.containsPoint(m_sprite->getPosition()))
				{
					m_sprite->resume(); m_sprite->setVisible(true);
				}
			}
		}
    }
}

NPC::~NPC()
{
    removePhysicsBody();
    //CCLOG("Destructor del tipo npc %d", this->tipo);
    if(mCB!=NULL)
    {
        delete mCB;
        mCB=NULL;
    }

}




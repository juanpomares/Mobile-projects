//
//  Mundo.cpp
//  VideojuegosCocos
//
//  Created by Fidel Aznar on 16/1/15.
//
//

#include "Mundo.h"
#include "iostream"
#include "Almacenamiento.h"


bool Mundo::init()
{
    Mundo::m_instance_actual=this;
    
    
    GameEntity::init();
    
    m_tiledMapHelper = TiledMapHelper::create();
    m_tiledMapHelper->retain();
    
    // Inicialización del mundo físico
    m_world = new b2World(b2Vec2(0,-20));
    
    m_debugDraw = new CocosDebugDraw(PTM_RATIO);
    m_world->SetDebugDraw(m_debugDraw);
    
    uint32 flags = 0;
    flags += b2Draw::e_shapeBit;
    //flags += b2Draw::e_jointBit;
    //flags += b2Draw::e_aabbBit;
    //flags += b2Draw::e_pairBit;
    //flags += b2Draw::e_centerOfMassBit;
    
    m_debugDraw->SetFlags(flags);
    // cambiosEscala.clear();
    
    return true;
}

Mundo* Mundo::getActual(){
    
    /*if(Mundo::m_instance==NULL) {
     Mundo::m_instance = Mundo::create();
     //Hago un retain hasta el final de los tiempos, soy un singleton
     m_instance->retain();
     }*/
    
    return Mundo::m_instance_actual;
}

Mundo::~Mundo(){
    CCLOG("Mundo es Singleton, no debería morir hasta el final. Cuidado...");
    m_tiledMapHelper->release();
    
    this->m_npcs.clear();
    
    if(Mundo::m_instance_actual==this)
        Mundo::m_instance_actual=NULL;
    
    if(m_world != NULL) {
        delete m_world;
        m_world = NULL;
    }
    if(m_debugDraw != NULL) {
        delete m_debugDraw;
        m_debugDraw = NULL;
    }
    
    if(cb != NULL) {
        delete cb;
        cb=NULL;
    }
    
    if(cbVerde != NULL) {
        delete cbVerde;
        cbVerde=NULL;
    }
}

void Mundo::preloadResources()
{    
    const char *url=NULL;
    
    Almacenamiento alm;
    
    std::string str=alm.cargar("nivelseleccionado");
    
	int strint = alm.StringToInt(str);
	

    switch (strint)
    {
        case 2:
            url=TMX_NIVEL2;
            break;
            
        case 3:
            url=TMX_NIVEL3;
            break;
            
        default:
            url=TMX_NIVEL1;
            break;
    }
    
    m_tiledMapHelper->loadTileMap(url, "Foreground");
    initPhysics();    
}

void Mundo::initPhysics()
{
    b2BodyDef bodyDef;
    bodyDef.position.Set(0, 0);
    bodyDef.type = b2BodyType::b2_staticBody;
    
    cb=new CombinedData(this, CombinedDataType::TCDMundo);
    cbVerde=new CombinedData(this, CombinedDataType::TCDObjetoLetal);
    //cbFinNivel=new CombinedData(this, CombinedDataType::TCDFinNivel);
    
    m_body = m_world->CreateBody(&bodyDef);
    
    b2FixtureDef fixtureDef;
    
    TMXObjectGroup *groupPhysics = m_tiledMapHelper->getTiledMap()->getObjectGroup("Fisica");
    
    auto objects = groupPhysics->getObjects();
    
    
    
    for(auto object : objects)
    {
        ValueVector polyline = object.asValueMap().at("polylinePoints").asValueVector();
        float x = object.asValueMap().at("x").asFloat() + m_tiledMapHelper->getTiledMap()->getPositionX();
        float y = object.asValueMap().at("y").asFloat() + m_tiledMapHelper->getTiledMap()->getPositionY();
        
        //Genera cadena en box2d
        int numPoints = polyline.size();
        b2Vec2 *chainPoints = new b2Vec2[numPoints];
        
        int i=0;
        for(auto point: polyline) {
            chainPoints[i++].Set((x+point.asValueMap().at("x").asFloat()) / PTM_RATIO,
                                 (y-point.asValueMap().at("y").asFloat()) / PTM_RATIO);
        }
        
        b2ChainShape chain;
        chain.CreateChain(chainPoints, numPoints);
        fixtureDef.shape = &chain;
        
        
        b2Fixture* fix=m_body->CreateFixture(&fixtureDef);
        auto properties=object.asValueMap();
        
        auto tipo=properties.at("type");
        if(!tipo.isNull())
        {
            /*if(tipo.asString() == "CambioEscala")
             {
             fix->SetSensor(true);
             auto tipox=object.asValueMap().at("CambioX");
             auto tipoy=object.asValueMap().at("CambioY");
             
             CombinedData *auxcb=nullptr;
             cambiosEscala.insert(cambiosEscala.end(), auxcb=new CombinedData(new Vec2(tipox.isNull()?0:tipox.asInt(), tipoy.isNull()?0:tipoy.asInt()), TCDCambioEscala));
             
             fix->SetUserData(auxcb);
             //fix->SetUserData(cb);
             }else
             */
            /*if(tipo.asString() == "DobleSalto")
             {
             fix->SetSensor(true);
             fix->SetUserData(cbDoubleJump);
             }else*/
            
            
            /*if (tipo.asString()=="FinNivel")
             {
             fix->SetUserData(cbFinNivel);
             }else*/
            fix->SetUserData(cbVerde);
        }
        else
            fix->SetUserData(cb);
    }


	//Añadir borde de bajo
	b2Vec2 *chainPoints = new b2Vec2[2];	

	Size visibleSize = Director::getInstance()->getVisibleSize();
	float height = (visibleSize.height / 2)/PTM_RATIO;
	chainPoints[0].Set(0, height);
	chainPoints[1].Set((getTamTiles().width-1)*m_tiledMapHelper->getTiledMap()->getTileSize().width, height);

	b2ChainShape chain;
	chain.CreateChain(chainPoints, 2);
	fixtureDef.shape = &chain;

	b2Fixture* fix = m_body->CreateFixture(&fixtureDef);
	fix->SetUserData(cbVerde);
}

Node* Mundo::getNode(){
    
    if(m_node==NULL)
    {
        m_node = Node::create();
        Size visibleSize = Director::getInstance()->getVisibleSize();
        
        m_tiledMapHelper->getTiledMap()->setPositionY(visibleSize.height/2);
        
        //Añado el tiledmap
        //m_tiledMapHelper->getTiledMap()->removeChild(m_tiledMapHelper->getCollisionLayer());
        m_node->addChild(m_tiledMapHelper->getTiledMap(), 500);

        
        Almacenamiento alm;
        
        for(int i = 0; i<3; i++)
        {
            if(alm.armaduraSeleccionada(i))
            {
                armadura = i;                
            }
        }

		int posmedallas[6] = { 0,0,0,0,0 };
		int num_medallas = 0;
        
        
       // m_node->addChild(m_debugDraw->GetNode(),-50);
        
        ValueVector objectsMap = m_tiledMapHelper->getObjects();
        
		bool finnivel = false;

        for (auto& object : objectsMap)
        {
            auto properties = object.asValueMap();
            auto tipo = properties.at("type");
            
            if (!tipo.isNull())
            {
                int x = (properties.at("x")).asInt();
                int y = (properties.at("y")).asInt();
                
                NPC *objetoCreado = NULL;
                
                if(tipo.asString() == "Moneda")
                {
                    objetoCreado =  Moneda::create();
                    objetoCreado->tipo= npcType::TMoneda;
                }else if(tipo.asString() == "MonedaFalsa")
                {
                    objetoCreado =  Moneda::create();
                    objetoCreado->tipo= npcType::TMonedaFalsa;
                }else if(tipo.asString() == "FinNivel")
                {
					finnivel = true;
                    objetoCreado =  Portal::create();
                    ((Portal*)objetoCreado)->personaje=m_player;
                    objetoCreado->tipo= npcType::TFinNivel;
                }else if(tipo.asString() == "CambioEscala")
                {
                    objetoCreado =  Portal::create();
                    ((Portal*)objetoCreado)->personaje=m_player;
                    
                    auto tipox=properties.at("CambioX");
                    auto tipoy=properties.at("CambioY");
                    
                    ((Portal*)objetoCreado)->escalax=tipox.isNull()?1:tipox.asInt();
                    ((Portal*)objetoCreado)->escalay=tipoy.isNull()?1:tipoy.asInt();
                    
                    objetoCreado->tipo= npcType::TCambioEscala;
				}
				else if (tipo.asString() == "Medalla")
				{

					if (num_medallas < 3)
					{
					
						posmedallas[num_medallas] = x;
						posmedallas[num_medallas+3] = y;
						num_medallas++;
					}
                    
                }else if(tipo.asString() == "Mina")
                {
                    objetoCreado =  Mina::create();
                    objetoCreado->tipo= npcType::TMina;
                }else if(tipo.asString() == "DobleSalto")
                {
                    objetoCreado =  DobleSalto::create();
                    objetoCreado->tipo= npcType::TSalto;
                }else if(tipo.asString() == "Llama")
                {
                    objetoCreado =  Llama::create();
                    objetoCreado->tipo= npcType::TLlama;
                }else if(tipo.asString() == "Fuego")
                {
                    objetoCreado =  Proyectil::create();
                    objetoCreado->tipo= npcType::TBolaFuego;
                }else if (tipo.asString() == "Rueda")
				{
					objetoCreado = Rueda::create();
					objetoCreado->tipo = npcType::TRueda;
				}else if(tipo.asString() == "Bala")
                {
                    objetoCreado =  Proyectil::create();
                    objetoCreado->tipo= npcType::TBala;
                }else if(tipo.asString() == "Pinchos")
                {
                    objetoCreado =  Pinchos::create();
                    ((Pinchos*)objetoCreado)->colorPincho = colorCharacter::Amarillo;
                    ((Pinchos*)objetoCreado)->SetColor(armadura);
                }
                
                else if(tipo.asString() == "PinchosRojos")
                {
                    objetoCreado =  Pinchos::create();
                    ((Pinchos*)objetoCreado)->colorPincho = colorCharacter::Rojo;
                    ((Pinchos*)objetoCreado)->SetColor(armadura);
                    
                }
                
                else if(tipo.asString() == "PinchosVerdes")
                {
                    objetoCreado =  Pinchos::create();
                    ((Pinchos*)objetoCreado)->colorPincho = colorCharacter::Verde;
                    ((Pinchos*)objetoCreado)->SetColor(armadura);
                    
                }
                
                if(objetoCreado!=NULL)
                {
                    objetoCreado->x = x;
                    objetoCreado->y = y;
                    objetoCreado->preloadResources();
                    m_node->addChild(objetoCreado->getNode());
                    
                    m_npcs.pushBack(objetoCreado);
                }                
            }
        }

		//Ordenar Medallas
		int i, j, tamano = 3, temp1;
		for (i = 0; i < (tamano - 1); i++)
		{
			for (j = i + 1; j < tamano; j++)
			{
				if (posmedallas[j] < posmedallas[i])
				{
					temp1 = posmedallas[j];
					posmedallas[j] = posmedallas[i];
					posmedallas[i] = temp1;

					temp1 = posmedallas[j+tamano];
					posmedallas[j + tamano] = posmedallas[i + tamano];
					posmedallas[i + tamano] = temp1;
				}
			}
		}


		//Crear Medallas
		for (int i = 0; i < 3; i++)
		{
			Almacenamiento alm;

			std::string nivel = alm.IntToString(alm.StringToInt(alm.cargar("nivelseleccionado")) - 1);

			std::string monedaAlmacenada = alm.IntToString(i);
			std::string valorAlmacenado = "medalla" + nivel + monedaAlmacenada;
			//CCLOG("%@", valorAlmacenado.c_str());

			Moneda* objetoCreado = Moneda::create();
			objetoCreado->TipoMedalla = i;

			if (alm.cargar(valorAlmacenado) != "conseguida")
				objetoCreado->tipo = npcType::TMedalla;
			else
				objetoCreado->tipo = npcType::TMedallaCogida;

			objetoCreado->x = posmedallas[i];
			objetoCreado->y = posmedallas[i+3];
			objetoCreado->preloadResources();
			m_node->addChild(objetoCreado->getNode());

			m_npcs.pushBack(objetoCreado);


		}

		

		if (!finnivel)
		{
			Portal* p=Portal::create();
			p->personaje = m_player;
			p->tipo=npcType::TFinNivel;
			p->y=0;
			p->x=getTileSize().width*(getTamTiles().width*1.f-1.5f);
			p->preloadResources();
			m_node->addChild(p->getNode());

			m_npcs.pushBack(p);
		}

		m_npcs_borrados.clear();
 }
    
    
    return m_node;
    
}


Size Mundo::getTileSize(){
    return m_tiledMapHelper->getTiledMap()->getTileSize();
}

Vector<NPC*> Mundo::getNPCs(){
    return m_npcs;
}

void Mundo::deleteNPC(NPC *npcDeleted)
{

	if (npcDeleted != NULL)
		m_npcs_borrados.pushBack(npcDeleted);
}

void Mundo::update(float delta)
{
	//clear_NPCS();

    m_debugDraw->Clear();
    m_world->DrawDebugData();
    
    m_world->ClearForces();
    m_world->Step(1.0f/60.0f, 6, 2);
    
    for (auto npc: this->m_npcs){ npc->update(delta);}  

	clear_NPCS();

}


void Mundo::clear_NPCS()
{
	for (auto npc : this->m_npcs_borrados)
	{
		Node *n = NULL;
		if ((n = npc->getNode()) != NULL)
			n->removeFromParent();
		m_npcs.eraseObject(npc);
	}

	m_npcs_borrados.clear();
}

cocos2d::Size Mundo::getTamTiles()
{

	return m_tiledMapHelper->getTiledMap()->getMapSize();
	//->getLayerSize();
}

Rect Mundo::getVisibleRect(){
    return m_visibleRect;
}

void Mundo::setVisibleRectOrigin(float x, float y){
    m_visibleRect.origin.x = x;
    m_visibleRect.origin.y = y;
}

void Mundo::setVisibleRect(float x, float y, float width, float height){
    
    //CCLOG("X: %f, Y: %f, W: %f, H: %f", x, y, width, height);
    
    m_visibleRect.origin.x = x;
    m_visibleRect.origin.y = y;
    m_visibleRect.size.width = width;
    m_visibleRect.size.height = height;
}

//
//  MarioSc.cpp
//  Test01
//
//  Created by Fidel Aznar on 13/1/15.
//
//

#include "MiGame.h"
#include "../Util/Singleton.h"
#include "Almacenamiento.h"
#include "Sonido.h"
#include "MenuLevel.h"
#include "MenuScene.h"

Mundo* Mundo::m_instance_actual = NULL;

bool MiGame::init()
{    
    SimpleGame::init();
    
    m_player = PhysicsPlayer::create();
    m_player->retain();
    addGameEntity(m_player);
    
    /*m_decorado = new Decorado();
    m_decorado->retain();
    addGameEntity(m_decorado);*/
    
    m_mundo = Mundo::create();
    m_mundo->retain();
    addGameEntity(m_mundo);
    
    m_gui = GUI::create();
    m_gui->retain();
    addGameEntity(m_gui);

	//this->setColor(Color3B::BLUE);// (12, 12, 12));
    
    return true;
}

void MiGame::pausa(Ref *sender)
{
	CCLOG("Entro al pausa");
	if (!m_pausa && !m_player->isDying())
	{
		CCLOG("Lo pauso todo :0");
		bPausa->setLocalZOrder(0);
		bPlay->setLocalZOrder(1);
		bSalir->setLocalZOrder(1);
		Director::getInstance()->pause();

		capa->setVisible(true);
		capa->setLocalZOrder(0);

		bPlay->setVisible(true);
		bSalir->setVisible(true);
		bPausa->setVisible(false);

		this->pause();
		m_pausa = true;
	}
}

void MiGame::continuar(Ref *sender)
{
	CCLOG("Entro al continuar");
	if (m_pausa)
	{
		//CONTINUAR ADD
		bPausa->setLocalZOrder(1);
		bPlay->setLocalZOrder(0);
		Director::getInstance()->resume();

		//aSprite.runAction(cc.FadeTo.create(1.0,50));

		capa->setVisible(false);
		capa->setLocalZOrder(0);

		bPlay->setVisible(false);
		bSalir->setVisible(false);
		bPausa->setVisible(true);

		this->resume();
		m_pausa = false;
	}
}


float MiGame::Escalado(float tam)
{
    Size visibleSize = Director::getInstance()->getVisibleSize();
    float Aescalar = (visibleSize.width) / (2);
    float escalado = Aescalar / tam;
    //(visibleSize.width - 10 / MENU_ITEMS_ACROSS) / fondo_nivel->getContentSize().width
    return escalado;
}


void MiGame::finNivel(Ref *sender, bool pasado)
{
    bPlay->setVisible(false);
    bPausa->setVisible(false);
    
    menuDie->setVisible(true);
    
    capa->setVisible(true);
    capa->setLocalZOrder(0);
    float escalar = Escalado(fondoSprite->getContentSize().width);
    Almacenamiento alm;
    

    int iNivel = alm.StringToInt(alm.cargar("nivelseleccionado"))+ -1;

    std::string sNivel = alm.IntToString(iNivel);
	

    for(int i = -1; i < 2; i++)
    {
		auto m = Sprite::createWithSpriteFrameName("medalla0-0.png");
		auto moneda = MenuItemSprite::create(m,m,m);
        
        std::string monedaAlmacenada = alm.IntToString(i+1);
        std::string valorAlmacenado = "medalla"+ sNivel+monedaAlmacenada;
        if(alm.cargar(valorAlmacenado) != "conseguida")
        {
            if(!medallas[i+1])
            {
                moneda->setColor(Color3B::BLACK);
            }
        }
        moneda->setPosition((i*((fondoSprite->getContentSize().width * escalar) / 3)), 15);
        moneda->setScale(escalar/2, escalar / 2);
        menuDie->addChild(moneda, 50);
    }
    
    if(pasado)
    {
        //menuDie->addChild(bSiguiente);
        String moneditas = alm.cargar("monedas");
        char buffer[256];
        monedasConseguidas += moneditas.intValue();
        alm.guardar("monedas", alm.IntToString(monedasConseguidas));
        
        int valor = alm.StringToInt(alm.cargar("nivelseleccionado"))+ 1;
        
        for(int i = 0 ; i < 3 ; i++)
        {
            if(medallas[i])
            {
                alm.guardarMedalla(alm.StringToInt(alm.cargar("nivelseleccionado"))-1  , i);
            }
        }
        
        std::string sNivel = alm.cargar("nivelseleccionado");
        std::string sBloqueado = "nivel" + sNivel;
        alm.guardar(sBloqueado, "desbloqueado");
        
        if(valor > 3)
        {
            bSiguiente->setVisible(false);
            valor = 3;
        }
        //alm.guardar("nivelseleccionado", Value::Value(valor).asString());
        
    }else
    {
		((Label*)TextoNivel->getLabel())->setString("¡Nivel no completado!");
        bSiguiente->setVisible(false);
    }

    
    Director::getInstance()->pause();

    this->pause();

}

//ADD

void MiGame::restart(cocos2d::Ref *sender)
{
    this->resume();
    
    /*Almacenamiento alm;
    int valor = Value::Value(alm.cargar("nivelseleccionado")).asInt() + 1;
    
    if(valor > 3)
    {
        bSiguiente->setVisible(false);
        valor = 3;
    }
    alm.guardar("nivelseleccionado", Value::Value(valor).asString());*/
    
    auto scene = MiGame::create();
    scene->preloadResources();
    scene->start();
    Director::getInstance()->resume();
    
    //Director::getInstance()->restart();
    Director::getInstance()->replaceScene(TransitionFade::create(TRANSITION_TIME, scene));
}

void MiGame::niveles(cocos2d::Ref *sender)
{
    Director::getInstance()->resume();
    auto scene = MenuScene::createScene();
    
    Sonido sonido;
    
    sonido.reproducir(MP3_BOTON);
    
    Director::getInstance()->replaceScene(TransitionFade::create(TRANSITION_TIME, scene));
    
}

void MiGame::siguiente(cocos2d::Ref *sender)
{
    this->resume();
    
    
    Almacenamiento alm;
    int valor = alm.StringToInt(alm.cargar("nivelseleccionado")) + 1;
    
    if(valor > 3)
    {
        valor = 3;
    }
    alm.guardar("nivelseleccionado", alm.IntToString(valor));

    
    auto scene = MiGame::create();
    scene->preloadResources();
    scene->start();
    Director::getInstance()->resume();
    
    //Director::getInstance()->restart();
    Director::getInstance()->replaceScene(TransitionFade::create(TRANSITION_TIME, scene));
}

void MiGame::reset()
{
    Sonido sonido;
    
    sonido.reproducir(MP3_BOTON);
    removeGameEntity(m_mundo);
    m_mundo = Mundo::getActual();
    addGameEntity(m_mundo);
}


MiGame::~MiGame()
{
    if(m_player!=NULL)
    {
        m_player->release();
        m_player=NULL;
    }
    
    
    if(m_gui!=NULL)
    {
        m_gui->release();
        m_gui=NULL;
    }
    
    if(m_mundo!=NULL)
    {
        m_mundo->release();
        m_mundo=NULL;
    }
}

void MiGame::preloadResources(){
    preloadEachGameEntity();
    
    Size visibleSize = Director::getInstance()->getVisibleSize();
    Vec2 origin = Director::getInstance()->getVisibleOrigin();
    setContentSize(Size(3392, visibleSize.height));
    
    m_mundo->setVisibleRect(origin.x, origin.y, visibleSize.width, visibleSize.height);
}

void MiGame::start(){
    
    scrollable = Node::create();
    
    addEachGameEntityNodeTo(scrollable);
    scrollable->removeChild(m_gui->getNode());
    //scrollable->removeChild(m_decorado->getNode());
    
    addChild(scrollable);
    addChild(m_gui->getNode());
    
    Decorado* dec = new Decorado();
    dec->setTipo(1);
    this->addChild(dec->getNode());
    
    auto touchListener = cocos2d::EventListenerTouchOneByOne::create();
    
    touchListener->onTouchBegan = CC_CALLBACK_2(MiGame::onTouchBegan, this);
    touchListener->onTouchEnded = CC_CALLBACK_2(MiGame::onTouchEnded, this);
    _eventDispatcher->addEventListenerWithSceneGraphPriority( touchListener, this );
    
    visibleSize = cocos2d::Director::getInstance()->getVisibleSize();
    origin = cocos2d::Director::getInstance()->getVisibleOrigin();
    //addChild(m_decorado->getNode());
    
    //****NUEVO
     
    //restart Icon made by http://www.flaticon.es/autores/freepik from www.flaticon.com
    auto a = Sprite::create(IMG_B_NIVEL);
	fondoSprite = MenuItemSprite::create(a, a, a);
    float escalar = Escalado(fondoSprite->getContentSize().width);
    fondoSprite->setScale( escalar, escalar);
    fondoSprite->setPosition(Point(0, 0));
    
    bLevels = MenuItemImage::create();
	auto sp = Sprite::create(IMG_LISTA);

	bLevels->setNormalImage(sp);
	bLevels->setSelectedImage(sp);

	bLevels->setCallback(CC_CALLBACK_1(MiGame::niveles, this));
    bLevels->setPosition((-1 * ((fondoSprite->getContentSize().width * escalar) / 3)),
                         0 - visibleSize.height / 4);
    bLevels->setScale(0.45f, 0.45f);
    

    bRestart = MenuItemImage::create( IMG_RESTART, IMG_RESTART, CC_CALLBACK_1(MiGame::restart, this ) );
    bRestart->setPosition((0 * ((fondoSprite->getContentSize().width * escalar) / 3)),
                          0 - visibleSize.height / 4);
    bRestart->setScale(0.4f, 0.4f);
    
    bSiguiente = MenuItemImage::create(IMG_NEXT, IMG_NEXT, CC_CALLBACK_1(MiGame::siguiente, this ) );
    bSiguiente->setPosition((1 * ((fondoSprite->getContentSize().width * escalar) / 3)),
                            0 - visibleSize.height / 4);
    bSiguiente->setScale(0.7f, 0.7f);
    
    TextoNivel = MenuItemLabel::create(Label::createWithTTF("¡Nivel completado!", FONTS_ARIAL, 35));
    
	float height = 150;//Sprite::create(IMG_MONEDA_PNG)->getContentSize().height;

    TextoNivel->setPosition(Point(0, height));
    
    TextoMonedas = MenuItemLabel::create(Label::createWithTTF("x 12", FONTS_ARIAL, 35));
    
    TextoMonedas->setPosition(Point(0, height*100));//Para que desaparezca de la pantalla jeje
    
    auto mon = Sprite::createWithSpriteFrameName("medalla0-0.png");
    mon->setScale(TextoMonedas->getContentSize().height / mon->getContentSize().width, TextoMonedas->getContentSize().height / mon->getContentSize().height);
    mon->setPosition(Point( - TextoMonedas->getContentSize().width / 2, TextoMonedas->getPosition().y));
    
    //****FIN
    
    bPausa = MenuItemImage::create( IMG_PAUSA, IMG_PAUSA, CC_CALLBACK_1(MiGame::pausa, this ) );
    bPausa->setPosition( Point( visibleSize.width / 2 - bPausa->getContentSize().width * 0.2, visibleSize.height / 2 - bPausa->getContentSize().height * 0.2 ) );
    bPausa->setScale(0.2f, 0.2f);
    bPausa->setName("botonpausa");
    
    bPlay = MenuItemImage::create( IMG_PLAY, IMG_PLAY, CC_CALLBACK_1(MiGame::continuar, this ) );
    bPlay->setPosition(Point(0, bPlay->getContentSize().height * 0.5));
    bPlay->setScale(0.5, 0.5);
    bPlay->setVisible(false);
    
    bSalir = MenuItemImage::create( IMG_EXIT, IMG_EXIT, CC_CALLBACK_1(MiGame::niveles, this ) );
    bSalir->setPosition(Point(bPlay->getPosition().x, - bPlay->getContentSize().height * 0.5));
    bSalir->setScale(1, 1);
    bSalir->setVisible(false);
    
    capa = Sprite::create(IMG_TRANSPARENTE);
    capa->setPosition(0, 0);
    capa->setScale(visibleSize.width, visibleSize.height);
    this->addChild(capa);
    capa->setVisible(false);
    
    bPausa->setLocalZOrder(1);
    bPlay->setLocalZOrder(0);
    
    menu = Menu::create( bPlay, NULL );
    menu->addChild(bPausa);
    menu->addChild(bSalir);
    
    menuDie = Menu::create();
    menuDie->addChild(fondoSprite);
    menuDie->addChild(TextoNivel);
    menuDie->addChild(TextoMonedas);
    menuDie->addChild(bLevels);
    menuDie->addChild(bSiguiente);
    menuDie->addChild(bRestart);
    
    this->addChild(menu);
    this->addChild(menuDie);
    
    menuDie->setVisible(false);
    
    SimpleGame::run();
    
    //La camara que siga al personaje (ver http://www.learn-cocos2d.com/2012/12/ways-scrolling-cocos2d-explained/)
    //No se puede agregar a la escena directamente, hay que crear un nodo para el scroll o da problemas con la cámara
    //scrollable->runAction(Follow::create(m_player->getNode()));
    
    double heightTotal = m_mundo->getTamTiles().height*m_mundo->getTileSize().height;
    double tam=Director::getInstance()->getVisibleSize().height;
    
    double scalefinal = tam/heightTotal;
    

    scrollable->setScale(scalefinal);
    
    m_mundo->scrollable_camera=scrollable;
    m_player->m_scrollable=scrollable;
    
    Almacenamiento alm;
    for(int i = 0; i<3; i++)
    {
        if(alm.armaduraSeleccionada(i))
        {
            m_player->SetColor(i);            
        }
    }

    textMon = Label::create(" x 0", FONTS_ARIAL, 25);
    textMon->setPosition(Vec2(visibleSize.width * 0.1,visibleSize.height*0.9));
    this->addChild(textMon);

	auto spriteFrameCache = SpriteFrameCache::getInstance();

	if (!spriteFrameCache->getSpriteFrameByName("Coin1.png"))
		spriteFrameCache->addSpriteFramesWithFile(OBJETOS_PLIST);
	auto spriteMoneda = Sprite::createWithSpriteFrameName("Coin1.png");
	auto monedita = MenuItemSprite::create(spriteMoneda, spriteMoneda);
	monedita->setEnabled(false);
	monedita->setScale(0.3);
	monedita->setPosition(Vec2(visibleSize.width * 0.1 - textMon->getContentSize().width, visibleSize.height*0.9));
	this->addChild(monedita);
    
}

void MiGame::updateEachFrame(float delta){

    //Lo pongo aqui para evitar que la interpolación de las acciones en updateIA haga pequeños saltos al subir el personaje
    //el consumo de recursos es minimo. Solo para player
    //m_player->testWorldGravity();
    
    updateEachGameEntityWithDelta(delta);
    
    
    /*for (auto npc: m_mundo->getNPCs()){
        npc->testWorldGravity();
    }*/
    
    //m_player->updateCamera(scrollable);


}

void MiGame::updateIA(float delta){
    
    updateEachGameEntityWithDelta(delta);
    
    
    static Size windowSize = Director::getInstance()->getVisibleSize();
    
    
    float scalex = scrollable->getScaleX(), fscalex=fabs(scalex), scaley = scrollable->getScaleY(), fscaley=fabs(scaley);

    
    float posxfinal = -scrollable->getPosition().x-windowSize.width*scalex/2;
    
    if(scalex<0)
    {
        posxfinal+=windowSize.width;
        posxfinal*=-1;
    }
    float posyfinal = -scrollable->getPosition().y-windowSize.height*scaley/2;
    if(scaley<0)
    {
        posyfinal+=windowSize.height;
        posyfinal*=-1;
    }
    m_mundo->setVisibleRect(posxfinal*((fscalex>1)?1:1/fscalex), posyfinal*((fscaley>1)?1:1/fscaley), windowSize.width*((fscalex>1)?fscalex:1/fscaley)*1.25, windowSize.height*((fscaley>1)?fscaley:1/fscaley)*1.25);
    
	if (m_salto)
		m_player->Jump();
}


bool MiGame::onTouchBegan(cocos2d::Touch *touch, cocos2d::Event *event)
{
    if(touch->getLocation().x < visibleSize.width / 2 + origin.x)
    {
        //CCLOG("TOQUE IZQUIERDA");
        this->m_player->Slide();
		return true;
    }else
    {
        //CCLOG("TOQUE Derecha");
		m_salto = true;
        this->m_player->Jump();

		return true;

       /* for (auto npcElement : this->m_mundo->getNPCs())
        {
            if(npcElement->tipo == npcType::TSalto)
            {
                ((DobleSalto*)npcElement)->destruction();
            }
        }*/
    }
    
    return false;
}

bool MiGame::onTouchEnded(cocos2d::Touch *touch, cocos2d::Event *event)
{
    this->m_player->EndSlide();
	m_salto = false;
    return true;
}


Node* MiGame::getScrollable() { return scrollable; }


void MiGame::onKeyPressed(EventKeyboard::KeyCode keyCode, cocos2d::Event *event){
    

	if (keyCode == EventKeyboard::KeyCode::KEY_ESCAPE || keyCode == EventKeyboard::KeyCode::KEY_BACK)
	{
		CCLOG(keyCode == EventKeyboard::KeyCode::KEY_ESCAPE?"Escape":"Back");
		if (m_pausa)
			continuar(this);
		else
			pausa(this);
	}
	/*
	if ()
	{
		CCLOG("Back");
		if (m_pausa)
			continuar(this);
		else
			pausa(this);
	}*/

   /* if (keyCode == EventKeyboard::KeyCode::KEY_LEFT_ARROW)
    {
        // (c) Cambiar modo de control del personaje (ahora usa move)
        //m_player->moveLeft();
        m_player->move(-1.0);
    }
    if (keyCode == EventKeyboard::KeyCode::KEY_RIGHT_ARROW){
        // (c) Cambiar modo de control del personaje (ahora usa move)
        //m_player->moveRight();
        m_player->move(1.0);
    }*/
    
    /*
     if (keyCode == EventKeyboard::KeyCode::KEY_DOWN_ARROW){
     // (c) Cambiar modo de control del personaje (ahora usa move)
     //m_player->moveRight();
     m_player->setAgachado(true);
     }*/

	if (keyCode == EventKeyboard::KeyCode::KEY_DOWN_ARROW) {
		m_player->Slide();
	}
    if(keyCode==EventKeyboard::KeyCode::KEY_SPACE || keyCode==EventKeyboard::KeyCode::KEY_LEFT_CTRL){
		m_salto = true;
		m_player->Jump();
    }
    
    if(keyCode==EventKeyboard::KeyCode::KEY_0)
    {
        Almacenamiento alm;
        alm.guardar("nivel0", "desbloqueado");
        alm.guardar("nivel1", "aa");
        alm.guardar("nivel2", "aa");
        alm.guardar("nivel3", "aa");
        
        for(int i=0; i<3; i++)
            for(int j=0; j<3; j++)
                alm.guardarMedalla(i, j, false);
        
        
    }
}

void MiGame::onKeyReleased(EventKeyboard::KeyCode keyCode, cocos2d::Event *event){
    
	if (keyCode == EventKeyboard::KeyCode::KEY_SPACE || keyCode == EventKeyboard::KeyCode::KEY_LEFT_CTRL) {
		m_salto = false;
		m_player->Jump();
	}

    /* if (keyCode == EventKeyboard::KeyCode::KEY_LEFT_ARROW)
     {
     // (c) Cambiar modo de control del personaje (ahora usa move)
     //m_player->stopLeft();
     m_player->move(0.0);
     }
     if (keyCode == EventKeyboard::KeyCode::KEY_RIGHT_ARROW){
     // (c) Cambiar modo de control del personaje (ahora usa move)
     //m_player->stopRight();
     m_player->move(0.0);
     }
     */
    
	if (keyCode == EventKeyboard::KeyCode::KEY_DOWN_ARROW) {
		m_player->EndSlide();
	}

    if (keyCode == EventKeyboard::KeyCode::KEY_W){
        scrollable->setScaleY(-fabs(scrollable->getScaleY()));
    }
    
    if (keyCode == EventKeyboard::KeyCode::KEY_S){
        scrollable->setScaleY(fabs(scrollable->getScaleY()));
    }
    
    if (keyCode == EventKeyboard::KeyCode::KEY_A){
        scrollable->setScaleX(-fabs(scrollable->getScaleX()));
    }
    
    if (keyCode == EventKeyboard::KeyCode::KEY_D){
        scrollable->setScaleX(fabs(scrollable->getScaleX()));
    }
}

void MiGame::updateMonedas(Moneda *m)
{
    npcType _tipo=m->tipo;
    
    
    char buffer[256];
    switch (_tipo) {
        case TMoneda:
            monedasConseguidas++;
            sprintf(buffer," x %d ",monedasConseguidas);
            textMon->setString(buffer);
            break;
            
        case TMonedaFalsa:
            monedasConseguidas--;
            sprintf(buffer," x %d ",monedasConseguidas);
            textMon->setString(buffer);
            break;
            
        case TMedalla:
            
            CCLOG("Cogida medalla: %d", m->TipoMedalla);
            medallas[m->TipoMedalla] = true;
			sonido.reproducir(WAV_MEDALLA);

            /*
            monedasConseguidas++;
            char buffer[256];
            sprintf(buffer,"Monedas : %d ",monedasConseguidas);
            textMon->setString(buffer);*/
            
            
            break;


		case TMedallaCogida:
			CCLOG("Medalla vuelta a conseguir");
			break;
            
        default:
            break;
    }
}




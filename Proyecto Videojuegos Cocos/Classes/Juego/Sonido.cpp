//
//  Sonido.cpp
//  IBackground
//
//  Created by JorgeH on 25/4/16.
//
//

#include <stdio.h>
#include "Sonido.h"

auto audio = CocosDenshion::SimpleAudioEngine::getInstance();

void Sonido::reproducir(std::string _id)
{
    audio->playEffect(_id.c_str());
    audio->setEffectsVolume(audio->getBackgroundMusicVolume() / 0.1);
}

void Sonido::preload(std::string _id)
{
    audio->preloadEffect( _id.c_str() );
}

void Sonido::reproducirFondo(std::string _id)
{
	if (!audio->isBackgroundMusicPlaying())
	{
		audio->playBackgroundMusic(_id.c_str(), true);
	}
}

void Sonido::pausar()
{
    audio->pauseBackgroundMusic();
    
    audio->pauseAllEffects();
}

void Sonido::continuar()
{
    audio->resumeBackgroundMusic();

    audio->resumeAllEffects();
}

void Sonido::parar()
{
    audio->stopBackgroundMusic();
    
    audio->stopAllEffects();
}
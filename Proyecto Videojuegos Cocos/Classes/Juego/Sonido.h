//
//  Sonido.h
//  IBackground
//
//  Created by JorgeH on 25/4/16.
//
//

#ifndef Sonido_h
#define Sonido_h

#include <stdio.h>
#include "cocos2d.h"
#include "SimpleAudioEngine.h"

class Sonido {
    
public:

    void preload(std::string _id);
    void reproducir(std::string _id);
    void reproducirFondo(std::string _id);
    void continuar();
    void parar();
    void pausar();
};


#endif /* Sonido_h */

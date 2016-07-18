//
//  Almacenamiento.h
//  IBackground
//
//  Created by JorgeH on 11/4/16.
//
//

#ifndef __IBackground__Almacenamiento__
#define __IBackground__Almacenamiento__

#include <stdio.h>
#include "cocos2d.h"

class Almacenamiento {
    
public:
    void guardar(std::string _variable, std::string _valor);
    void guardarMedalla(int _nivel, int _numero, bool guardado=true);
    void guardarArmadura(int _armadura);
    
    std::string cargar(std::string _variable);
    
    bool armaduraComprada(int _armadura);
    bool armaduraSeleccionada(int _num);

	static int StringToInt(std::string _variable);
	static std::string IntToString(int _variable);

	void comprobar();
};

#endif /* defined(__IBackground__Almacenamiento__) */

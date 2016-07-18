//
//  Almacenamiento.cpp
//  IBackground
//
//  Created by JorgeH on 11/4/16.
//
//

#include "Almacenamiento.h"

USING_NS_CC;

void Almacenamiento::guardar(std::string _variable, std::string _valor)
{
    const char* variable = _variable.c_str();
    const char* valor = _valor.c_str();
    
    UserDefault::getInstance()->setStringForKey(variable, valor);
}

std::string Almacenamiento::cargar(std::string _variable)
{
    const char* variable = _variable.c_str();
    return UserDefault::getInstance()->getStringForKey(variable);
}

void Almacenamiento::guardarMedalla(int _nivel, int _numero, bool guardado)
{
    std::string nivel = IntToString(_nivel);
    std::string monedaAlmacenada = IntToString(_numero);
    std::string valorAlmacenado = "medalla"+nivel+monedaAlmacenada;
    
    const char* variable = valorAlmacenado.c_str();

    UserDefault::getInstance()->setStringForKey(variable, guardado?"conseguida":"");
    
    std::string conseguidas = cargar("medallasconseguidas");
    int cons = StringToInt(conseguidas);
    
    if(cons < 0)
    {
        cons = 0;
    }
    
    if(guardado)
    {
        cons++;
        conseguidas = IntToString(cons);
        const char* variableConseguidas = conseguidas.c_str();
        
        guardar("medallasconseguidas", variableConseguidas);
    }else
    {
        cons--;
        conseguidas = IntToString(cons);
        const char* variableConseguidas = conseguidas.c_str();
        
        guardar("medallasconseguidas", variableConseguidas);
    }
}

bool Almacenamiento::armaduraComprada(int _armadura)
{
	std::string armadura = IntToString(_armadura);
    std::string valorAlmacenado = "armaduraComprada"+armadura;
    
    const char* variable = valorAlmacenado.c_str();
    return UserDefault::getInstance()->getStringForKey(variable, "false")=="true";
}

void Almacenamiento::guardarArmadura(int _armadura)
{
    std::string armadura = IntToString(_armadura);
    std::string valorAlmacenado = "armaduraComprada"+armadura;
    
    const char* variable = valorAlmacenado.c_str();
    
    UserDefault::getInstance()->setStringForKey(variable, "true");
}

bool Almacenamiento::armaduraSeleccionada(int _num)
{

	std::string num = IntToString(_num);
	std::string valorAlmacenado = "selArmadura"+ num;
    
    const char* variable = valorAlmacenado.c_str();
	
    return UserDefault::getInstance()->getStringForKey(variable, "false")=="true";
    
}

int Almacenamiento::StringToInt(std::string _variable)
{
	Value aux(_variable);
	return aux.asInt();
}

std::string Almacenamiento::IntToString(int _variable)
{
	Value aux(_variable);
	return aux.asString();
}

void Almacenamiento::comprobar()
{
	if (cargar("iniciado") == "iniciado")
	{
		for (int i = 0; i<3; i++)
		{
			std::string num = IntToString(i);
			std::string valorAlmacenado = "selArmadura" + num;

			const char* variable = valorAlmacenado.c_str();
			UserDefault::getInstance()->setStringForKey(variable, variable);

			valorAlmacenado = "armaduraComprada" + num;
			variable = valorAlmacenado.c_str();

			UserDefault::getInstance()->setStringForKey(variable, "false");

			for (int j = 0; j<3; j++)
			{
				guardarMedalla(i, j, false);
			}
		}
		UserDefault::getInstance()->setStringForKey("monedas", "0");
		UserDefault::getInstance()->setStringForKey("nivel0", "desbloqueado");
		UserDefault::getInstance()->setStringForKey("nivel1", "0");
		UserDefault::getInstance()->setStringForKey("nivel2", "0");
		UserDefault::getInstance()->setStringForKey("nivelseleccionado", "0");
		UserDefault::getInstance()->setStringForKey("iniciado", "iniciado");
	}
}
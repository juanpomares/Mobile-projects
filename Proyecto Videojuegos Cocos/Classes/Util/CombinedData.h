//
//  CombinedData.hpp
//  P5
//
//  Created by Master Móviles on 25/4/16.
//
//

#ifndef CombinedData_hpp
#define CombinedData_hpp

#include <stdio.h>

enum CombinedDataType:int{TCDPersonaje=0, TCDMundo, TCDDobleSalto, TCDObjetoLetal, TCDBombaNuclear, TCDCambioEscala, TCDRecogible, TCDFinNivel};

class CombinedData
{
    
public:
    CombinedData(void* p, int tipo);
    void* mPointer;
    int mTipo;
    
    bool isMundo();
    bool isPersonaje();
    bool isDobleSalto();
    bool isObjetoLetal();
    bool isCambioEscala();
    bool isBombaNuclear();
    bool isRecogible();
    bool isFinNivel();
};

#endif /* CombinedData_hpp */

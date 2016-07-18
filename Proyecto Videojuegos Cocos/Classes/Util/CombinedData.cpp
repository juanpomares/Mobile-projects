//
//  CombinedData.cpp
//  P5
//
//  Created by Master Móviles on 25/4/16.
//
//

#include "CombinedData.h"

CombinedData::CombinedData(void* p, int tipo)
{
    mPointer=p; mTipo=tipo;
}

bool CombinedData::isMundo()        {return mTipo==CombinedDataType::TCDMundo;}
bool CombinedData::isPersonaje()    {return mTipo==CombinedDataType::TCDPersonaje;}
bool CombinedData::isDobleSalto()   {return mTipo==CombinedDataType::TCDDobleSalto;}
bool CombinedData::isObjetoLetal()  {return mTipo==CombinedDataType::TCDObjetoLetal;}
bool CombinedData::isCambioEscala() {return mTipo==CombinedDataType::TCDCambioEscala;}
bool CombinedData::isBombaNuclear() {return mTipo==CombinedDataType::TCDBombaNuclear;}
bool CombinedData::isRecogible()    {return mTipo==CombinedDataType::TCDRecogible;}
bool CombinedData::isFinNivel()     {return mTipo==CombinedDataType::TCDFinNivel;}

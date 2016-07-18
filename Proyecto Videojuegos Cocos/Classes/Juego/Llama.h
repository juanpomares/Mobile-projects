//
//  Llama.h
//  BackToTheJump
//
//  Created by JorgeH on 18/4/16.
//
//

#ifndef BackToTheJump_Llama_h
#define BackToTheJump_Llama_h
#include "Npc.h"

class Llama: public NPC{
    
public:
    
    void preloadResources();
    Node* getNode();
    ~Llama();
    
    CREATE_FUNC(Llama);
};
#endif

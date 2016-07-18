//
//  Engine2D.cpp
//  Test01
//
//  Created by Fidel Aznar on 13/1/15.
//
//

#include "Engine2D.h"

USING_NS_CC;


void Engine2D::createOpenGLContext(){
    //set OpenGL context attributions,now can only set six attributions:
    //red,green,blue,alpha,depth,stencil
    GLContextAttrs glContextAttrs = {8, 8, 8, 8, 24, 8};
    GLView::setGLContextAttrs(glContextAttrs);
}

void Engine2D::createEngine(const char *windowName){
    
    // initialize director
    auto director = Director::getInstance();
    auto glview = director->getOpenGLView();
    if(!glview) {
        glview = GLViewImpl::create(windowName);
        director->setOpenGLView(glview);
    }
    
    // turn on display FPS
    director->setDisplayStats(false);
    
    // set FPS. the default value is 1.0/60 if you don't call this
    director->setAnimationInterval(1.0f / 60);
    director->getOpenGLView()->setDesignResolutionSize(960, 640, ResolutionPolicy::EXACT_FIT);
}

void Engine2D::setScene(Scene *scene){
    auto director = Director::getInstance();
    director->runWithScene(scene);
}

void Engine2D::pause(){
    Director::getInstance()->stopAnimation();
    // if you use SimpleAudioEngine, it must be pause
    // SimpleAudioEngine::getInstance()->pauseBackgroundMusic();
}

void Engine2D::resume(){
    Director::getInstance()->startAnimation();
    // if you use SimpleAudioEngine, it must resume here
    // SimpleAudioEngine::getInstance()->resumeBackgroundMusic();
}
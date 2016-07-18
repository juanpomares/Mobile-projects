
#include "AppDelegate.h"
#include "Juego/CargandoScene.h"



USING_NS_CC;


AppDelegate::AppDelegate() {
    
}

AppDelegate::~AppDelegate() 
{
}

//if you want a different context,just modify the value of glContextAttrs
//it will takes effect on all platforms
void AppDelegate::initGLContextAttrs()
{
    engine2d.createOpenGLContext();
}

bool AppDelegate::applicationDidFinishLaunching() {
    
    engine2d.createEngine("Back to the Jump");
    
    /*auto scene = Game::create();
    scene->preloadResources();
    scene->start();
    engine2d.setScene(scene);*/
    
    
    //Director::getInstance()->replaceScene(TransitionFade::create(TRANSITION_TIME, scene));
    
    auto scene = CargandoScene::crearEscena();
    
    engine2d.setScene(scene);
    
    return true;
    
}

// This function will be called when the app is inactive. When comes a phone call,it's be invoked too
void AppDelegate::applicationDidEnterBackground() {
    engine2d.pause();
}

// this function will be called when the app is active again
void AppDelegate::applicationWillEnterForeground() {
    engine2d.resume();
}

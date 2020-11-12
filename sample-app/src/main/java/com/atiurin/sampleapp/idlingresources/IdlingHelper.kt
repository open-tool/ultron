package com.atiurin.sampleapp.idlingresources
const val RELEASE_BUILD = false;

object IdlingHelper{
    @JvmStatic
    fun ifAllowed(resourceAction:() -> Unit){
        if (!RELEASE_BUILD){
            resourceAction()
        }
    }
}
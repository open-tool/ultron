package com.atiurin.ultron.listeners

abstract class AbstractListener {
    var id: String
    constructor(id: String){
        this.id = id
    }
    constructor(){
        this.id = this::class.qualifiedName.orEmpty()
    }
}
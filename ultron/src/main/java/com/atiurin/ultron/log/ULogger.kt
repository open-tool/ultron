package com.atiurin.ultron.log

abstract class ULogger {
    var id: String

    constructor(id: String){
        this.id = id
    }
    constructor(){
        this.id = this::class.java.name
    }

    abstract fun info(message: String): Any
    abstract fun info(message: String, throwable: Throwable): Any
    abstract fun debug(message: String): Any
    abstract fun debug(message: String, throwable: Throwable): Any
    abstract fun warn(message: String): Any
    abstract fun warn(message: String, throwable: Throwable): Any
    abstract fun error(message: String): Any
    abstract fun error(message: String, throwable: Throwable): Any
}
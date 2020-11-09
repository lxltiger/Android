package com.lxl.essence.coroutines

import kotlinx.coroutines.*
import java.lang.Thread.sleep

fun main() = runBlocking<Unit>{
    val la=launch {
        repeat(100) { num ->
            print("sleep $num")
            delay(500)
        }
    }
    delay(1300)
    print("cancle")
    la.cancel()
//    la.join()
    print("exit")
    /*runBlocking {
        sleep(1300)
    }*/
}


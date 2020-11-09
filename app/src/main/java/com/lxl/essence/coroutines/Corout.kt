package com.lxl.essence.coroutines

import kotlinx.coroutines.*

//运行不能取消的代码库
fun main1() = runBlocking<Unit>{
    val job=launch {
        try {
            repeat(100){it->
                println("sleep $it")
                delay(200)
            }
        }finally {
            //在finally中调用挂起函数会抛出异常，为次需要使用withContext+NonCancelable帮忙
            withContext(NonCancellable){
                println("ready to suspend")
                delay(2000)
                println("now end in finlly")
            }
        }
    }

    delay(1300)
    println("no longer to wait")
    job.cancelAndJoin()
    println("end")

}

//超时处理
fun main()= runBlocking {
    //如果超时返回Null而不是抛出异常
    val result= withTimeoutOrNull(2300){
        repeat(20){num->
            print("sleep $num")
            delay(100)
        }
        "done"//如果超时之前完成返回done
    }
    print("result is $result")
}


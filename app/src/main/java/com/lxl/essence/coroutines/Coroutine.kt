package com.lxl.essence.coroutines

import kotlinx.coroutines.*
import java.lang.Thread.sleep


class Basic {
    fun basic() {
        //GlobalScope launch的协程 ，其生命周期和应用一样
        GlobalScope.launch {
            //不会阻塞线程，仅挂起当前协程，只能在协程中使用
            delay(200)
            println("kotlin")
        }
        println("hello")
        //如果直接结束，协程也会结束 所以阻塞2s
        sleep(2000)
    }

    fun basic2() {
        GlobalScope.launch {
            delay(200)
            println("kotlin2")
        }
        println("hello2")
        //使用这个Coroutine替代阻塞式的sleep,它会阻塞当前线程，直到其中的suspend 函数都执行结束
        runBlocking {
            delay(2000)
        }
    }

    fun basic3() = runBlocking<Unit> {
        //在runBlock主协程中开启另一个协程，这个协程不属于runBlock的范围，所以runBlocking不会等待其函数结束
        GlobalScope.launch {
            delay(200)
            println("kotlin3")
        }
        println("hello3")
        //属于runBlocking的suspend 函数，runBlocking会阻塞当前线程等待其结束
        delay(2000)
    }

    fun basic4() = runBlocking<Unit> {
        val job = GlobalScope.launch {
            delay(200)
            println("kotlin4")
        }
        println("hello4")
        //等待子协程结束
        job.join()
    }

    fun basic5() = runBlocking<Unit> {
        //现在这个协程属于runBlocking的范围，所以会等待其结束
        launch {
            delay(100)
            println("kotlin5")
        }
        println("hello5")
    }

    fun customScope() = runBlocking<Unit> {

        launch {
            delay(200)
            println("2")
        }
        //使用coroutineScope自定义协程范围，同runBlocking一样 它也会挂起等待所有子例程结束 但不会阻塞当前线程
        coroutineScope {
            launch {
                delay(500)
                println("3")
            }

            delay(100)
            println("1")
        }

        //需要等待coroutineScope协程结束才能执行
        launch {
            delay(200)
            println("5")
        }

        println("4")

    }

    fun lightWeight() = runBlocking {
        //启动10万个协程
        repeat(100_1000) { num ->
            launch {
                delay(2000)
                print("$num ")
            }
        }
    }
}
class Cancellation {
    //运行不能取消的代码库
    fun main() = runBlocking<Unit> {
        val job = launch {
            try {
                repeat(100) { it ->
                    println("sleep $it")
                    delay(200)
                }
            } finally {
                //在finally中调用挂起函数会抛出异常，为次需要使用withContext+NonCancelable帮忙
                withContext(NonCancellable) {
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
    fun main2() = runBlocking {
        //如果超时返回Null而不是抛出异常
        val result = withTimeoutOrNull(2300) {
            repeat(20) { num ->
                print("sleep $num")
                delay(100)
            }
            "done"//如果超时之前完成返回done
        }
        print("result is $result")
    }
}

fun main() {
    val basic = Basic();
    basic.basic();
    basic.basic2();
    basic.basic3();
    basic.basic4();
    basic.basic5();
    basic.customScope()
    basic.lightWeight()

//    val c = Cancellation();
//    c.main()
}




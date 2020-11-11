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

var count = 0;

class Resource {
    init {
        count++;
    }

    fun close() {
        count--
    }
}

class Cancellation {
    //可取消的协程，挂起函数都是可以取消的，它们检查协程的取消， 并在取消时抛出 CancellationException
    fun cancelable() = runBlocking {
        val job = launch {
            repeat(100) { num ->
                println("sleep $num")
                delay(500)
            }
        }

        delay(1300)
        println("cancel waiting")
        job.cancel()
        //等待job结束
        job.join()
        println("exit")

    }

    //如果协程正在执行计算任务，并且不检查取消操作就不能取消
    fun uncancelable() = runBlocking {
        //如果不指定默认调度器，这个协程会挂起？
        val job = launch(Dispatchers.Default) {
            var nextTime = System.currentTimeMillis();
            var a = 0;
            while (a < 5) {
                if (System.currentTimeMillis() >= nextTime) {
                    println("sleep ${a++}")
                    nextTime += 500;
                }
            }
        }
        delay(1300)
        println("cancel waiting")
        job.cancelAndJoin()
        println("exit")
    }

    fun checkCancelable() = runBlocking {
        val job = launch(Dispatchers.Default) {
            var nextTime = System.currentTimeMillis();
            var a = 0;
            //加上isActive是计算代码可取消
            while (isActive && a < 5) {
                if (System.currentTimeMillis() >= nextTime) {
                    println("sleep ${a++}")
                    nextTime += 500;
                }
            }
        }
        delay(1300)
        println("cancel waiting")
        job.cancelAndJoin()
        println("exit")
    }

    //运行不能取消的代码库
    fun releaseInFinal() = runBlocking<Unit> {
        val job = launch() {
            try {
                repeat(100) { it ->
                    println("sleep $it")
                    delay(200)
                }
            } finally {
                //当协程被取消，在此执行最后操作 一般不会在此调用挂起函数
                //如果在finally中调用挂起函数会抛出异常，
                // 如果一定要用 使用withContext+NonCancelable帮忙
                withContext(NonCancellable) {
                    println("ready to suspend")
                    delay(2000)
                    println("now end in finlly")
                }
            }
        }

        delay(1300)
        println("no longer to wait")
        //取消后会等待协程中的finally执行完毕
        job.cancelAndJoin()
        println("end")

    }

    //超时处理
    fun handleTimeout() = runBlocking {
        //如果超时返回Null而不是抛出异常
        val result = withTimeoutOrNull(1300) {
            //不可取消协程，超时抛出异常
            repeat(20) { num ->
                print("sleep $num")
                delay(500)
            }
            "done"//如果超时之前完成返回done
        }
        print("result is $result")
    }

    fun unSafeAsynTimeOut() {
        var num = 0;
        runBlocking {
            repeat(10000) {
                launch {
                    //虽然理论上60比50大，应该在超时之前完成初始化
                    //但超时是异步的，有可能在Resource返回之前就超时了
                    val resource = withTimeout(60) {
                        delay(50)
                        Resource()
                    }
                    //如果超时了这里都不会执行 资源不会释放
                    num++
                    //在协程外部释放资源是危险的
                    resource.close()
                }
            }
        }
        print("num $num")
        print("count $count")
    }

    fun safeAsynTimeOut() {
        var num = 0;
        //阻塞当前线程 直到其中协程执行完毕
        runBlocking {
            repeat(100_000) {
                launch {
                   var resource:Resource?=null
                    try {
                        withTimeout(60) {
                            delay(50)
                            resource=Resource()
                        }
                    }finally {
                        num++
                        resource?.close()
                    }
                }
            }
        }
        print("num $num")
        print("count $count")
    }
}

fun basic() {
    val basic = Basic();
    basic.basic();
    basic.basic2();
    basic.basic3();
    basic.basic4();
    basic.basic5();
    basic.customScope()
    basic.lightWeight()


}

fun cancellation() {
    val c = Cancellation();
    c.cancelable()
    c.uncancelable()
    c.checkCancelable()
    c.releaseInFinal()
    c.handleTimeout()
    c.unSafeAsynTimeOut()
    c.safeAsynTimeOut()
}





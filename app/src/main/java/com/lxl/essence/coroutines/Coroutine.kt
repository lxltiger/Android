package com.lxl.essence.coroutines

import kotlinx.coroutines.*
import java.lang.RuntimeException
import java.lang.Thread.sleep
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.system.measureTimeMillis


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
                    var resource: Resource? = null
                    try {
                        withTimeout(60) {
                            delay(50)
                            resource = Resource()
                        }
                    } finally {
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


class Compose {
    suspend fun timeConsumeOne(): Long {
        delay(1000)
        return 12
    }

    suspend fun timeConsumeTwo(): Long {
        delay(2000)
        return 15
    }

    fun sequential() = runBlocking {
        //协程内的函数默认是顺序执行的
        val elapse = measureTimeMillis {
            val one = timeConsumeOne()
            val two = timeConsumeTwo()
            println("reuslt is ${one + two}")
        }
        println("time elapse $elapse")
    }

    fun concurrent() = runBlocking {
        val elapse = measureTimeMillis {
//            asyn和launch一样开启一个协程，不同的是launch返回的job不带结果，而asyn返回一个deffer，它是可以在未来获取结果的
            val one = async { timeConsumeOne() }
            val two = async { timeConsumeTwo() }
            //先打印 上面代码不会阻塞
            println("waiting two")
//            可以使用await等待deffer返回结果，两个并发执行，耗时取决于较长的，而不是像顺序执行那样相加
            println("two consume ${two.await()}")
            println("waiting one")
            println("one consume ${one.await()}")
        }
        println("time elapse $elapse")
    }

    fun lazyConcurrent() = runBlocking {
        val elapse = measureTimeMillis {
//            使用懒加载，不会执行方法直到调用start或await方法 让用户有更细粒度的控制
            val one = async(start = CoroutineStart.LAZY) { timeConsumeOne() }
            val two = async(start = CoroutineStart.LAZY) { timeConsumeTwo() }
            //这里需要先使用start 否则依赖await方法启动会阻塞到结果返回
            one.start()
            two.start()
            println("waiting")
            println("time consume ${one.await() + two.await()}")
        }
        println("time elapse $elapse")
    }

    suspend fun sum(): Long = coroutineScope {
        val one = async { timeConsumeOne() }
        val two = async { timeConsumeTwo() }
        one.await() + two.await()
    }

    //如果sum方法出错抛出异常，其中的协程都会被取消
    suspend fun failedSum(): Int = coroutineScope {
        val one = async<Int> {
            try {
                delay(Long.MAX_VALUE)
                43
            } finally {
                println("cancel")
            }
        }

        //立马异步执行
        val two = async<Int> {
            delay(2400)
            println("Second child throws an exception")
            throw ArithmeticException()
        }

        one.await() + two.await()
    }

    fun safeConcurrent() = runBlocking {
        /* val elapse = measureTimeMillis {
             println("time cosume ${sum()}")
         }
         println("time elapse $elapse")*/

        try {
            failedSum()
        } catch (e: ArithmeticException) {
            println("Computation failed with ArithmeticException")
        }
    }
}


fun compose() {
    val compose = Compose();
    compose.sequential()
    compose.concurrent()
    compose.lazyConcurrent()
    compose.safeConcurrent()
}

class Activity {

    private val mainScope = CoroutineScope(Dispatchers.Default);

    fun doSomething() {
        repeat(10) { i ->
            mainScope.launch {
                delay((i + 1) * 200L)
                println("Coroutine ${i} is done")
            }
        }
    }

    fun destroy() {
        mainScope.cancel()
    }
}

class Dispatcher {
    //使用 -Dkotlinx.coroutines.debug JVM参数 可以打印线程和协程的信息
    fun log(msg: String, coroutineContext: CoroutineContext) {
        println("[${Thread.currentThread().name} ${coroutineContext[Job]}] ${msg}")
    }

    fun simpleContext() = runBlocking {
        //如果没有指定调度器，继承父协程 也就是和runBlocking在一个上下文
        launch {
            log("main runBlocking", coroutineContext)
            delay(100)
            //从挂起函数恢复执行 依然在统一线程
            log("main runBlocking", coroutineContext)

        }
        //不限定调度器 不建议使用
        launch(Dispatchers.Unconfined) {
            //运行在runBlocking所在线程
            log("Unconfined ", coroutineContext)
            delay(100)
            //从挂起函数恢复后，运行在挂起函数所在线程而不是runBlocking所在线程
            //协程可以在一个线程上执行，挂起恢复后又从另一个线程恢复执行，所以不适合更新UI
            log("Unconfined ", coroutineContext)
        }
    }

    fun logDebug() = runBlocking {
        val one = async {
            delay(200)
            log("calcualte one", coroutineContext)
            12
        }
        val two = async {
            delay(1200)
            log("calcualte two", coroutineContext)
            12
        }
        log("sum is ${one.await() + two.await()}", coroutineContext)
    }


    fun parent1() = runBlocking {
        val request = launch {
            repeat(3) { i ->
                launch {
                    delay((i + 1) * 300L)
                    log("${i} is done", coroutineContext)
                }
            }
            println("request: I'm done and I don't explicitly join my children that are still active")
        }
        //显式等待request的结束
        request.join()
        println("Now processing of the request is complete")
    }

    fun parent2() = runBlocking {
        //coroutineScope会挂起等待子例程结束
        coroutineScope {
            repeat(3) { i ->
                launch {
                    delay((i + 1) * 300L)
                    log("${i} is done", coroutineContext)
                }
            }
            println("request: I'm done and I don't explicitly join my children that are still active")
        }
        println("Now processing of the request is complete")
    }

    fun parent3() = runBlocking {
//        如果父协程被取消，它的子协程通常也会被取消
        val request = launch {
            // 使用 GlobalScope 启动的协程不会受父协程取消的影响  是独立额
            GlobalScope.launch {
                log("job1: I run in GlobalScope and execute independently!", coroutineContext)
                delay(1000)
                log("job1: I am not affected by cancellation of the request", coroutineContext)
            }
            // 另一个则承袭了父协程的上下文
            launch {
                delay(100)
                log("job2: I am a child of the request coroutine", coroutineContext)
                delay(1000)
                log("job2: I will not execute this line if my parent request is cancelled", coroutineContext)
            }
        }
        delay(500)
        request.cancel()
        delay(1000)
        log("who exist", coroutineContext)
    }

    fun nameCoroutine() = runBlocking {
        log("Started main coroutine", coroutineContext)
        //给协程命名  这样在debugging mode时， 协程的名字会包含在线程名字之中：如：[main @v1coroutine#2]
        val v1 = async(CoroutineName("v1coroutine")) {
            delay(300)
            log("compute v1", coroutineContext)
            23
        }

        val v2 = async(CoroutineName("v2coroutine")) {
            delay(700)
            log("compute v2", coroutineContext)
            2
        }
        log("The answer for v1 * v2 = ${v1.await() * v2.await()}", coroutineContext)
    }

    fun scope() = runBlocking {
        val activity = Activity();
        println("Launched coroutines")
        activity.doSomething()
        delay(500L) // delay for half a second
        println("Destroying activity!")
        activity.destroy()
        delay(1000)

    }
}

fun main() {
    val dispatcher = Dispatcher();
//    dispatcher.simpleContext()
//    dispatcher.logDebug()
//    dispatcher.scope()
//    dispatcher.parent2()
//    dispatcher.nameCoroutine()
    dispatcher.scope()
}


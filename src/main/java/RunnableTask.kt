import java.util.*

abstract class RunnableTask(private val message: String = "Task complete", private val displayProgress: Boolean = true) : Runnable {

    var conditionsDone: MutableMap<String, Boolean> = HashMap()
    var progress = intArrayOf(0)
    var isRunning = booleanArrayOf(false)
    abstract override fun run()
    abstract fun progress()  // user has to set the progress after condition is done
    open fun end() {}
    private var i = 0

    fun start(refreshRateMilliseconds: Int) {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (!isRunning[0]) {
                    isRunning[0] = true
                    Thread(this@RunnableTask).run()
                } else if (progress[0] >= 100) {
                    conditionsDone.clear()
                    progress[0] = 0
                    isRunning[0] = false
                    stopTimer(timer)
                    if (displayProgress) {
                        println(message)
                    }
                    end()
                } else {
                    progress()
                }
            }
        }, 0, refreshRateMilliseconds.toLong())
    }

    fun atProgress(progress: Int, function: () -> Unit, message: String = "", progressAmount: Int = 100 - progress) {
        if (getCurrentProgress() == progress) {
            function()
            setProgress(progress + progressAmount)
            println(RunnableTask::class.java.simpleName + " $message progress made: $progressAmount!")
        }
    }

    fun getCurrentProgress(): Int {
        var currentProgress = 0
        progress.forEach { value ->
            currentProgress += value
        }
        return currentProgress
    }

    fun setStartingProgress(amount: Int) {
        increaseProgress(amount)
    }

    fun progressAfter(condition: Boolean, name: String, progressAmount: Int) {
        if (condition) {
            if (conditionsDone[name] == null) {
                conditionsDone[name] = true
                increaseProgress(progressAmount)
                if (displayProgress) {
                    println(RunnableTask::class.java.simpleName + " " + name + " progress made: " + progressAmount + "!")
                }
            }
        }
    }

    fun stopTimer(timer: Timer) {
        timer.cancel()
        timer.purge()
    }

    fun increaseProgress(value: Int) {
        progress[0] += value //todo refactor this for the good position in the map
    }

    fun isRunning(): Boolean {
        return isRunning[0]
    }

    fun setProgress(progress: Int) {
        this.progress[0] = progress
    }

    fun getProgress(): Int {
        return progress[0]
    }

    fun stopRunning() {
        isRunning[0] = false
    }

    private fun defaultMessage() {

    }
}
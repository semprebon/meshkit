package org.semprebon.mesh

class ProgressTimer {
    var previousTime = System.currentTimeMillis()

    fun log(message: String) {
        val time = (System.currentTimeMillis() - previousTime)/1000
        System.out.println("${time}s ${message}")
        previousTime = System.currentTimeMillis()
    }
}
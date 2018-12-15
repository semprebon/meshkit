package org.semprebon.mesh

class ProgressMonitor(val maxEvents: Int) {
    var numEvents = 0
    var nextThreshold = maxEvents/20
    fun reportEvent() { reportEvent(1) }

    fun reportEvent(count: Int) {
        numEvents += count
        if (numEvents-count < nextThreshold && numEvents >= nextThreshold ) {
            //ystem.out.print("\b\b\b${100 * numEvents / maxEvents}%")
            System.out.print(".")
            nextThreshold += (maxEvents - numEvents)/20
        }
    }
}
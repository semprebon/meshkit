package org.semprebon.mesh

import java.util.function.Predicate
import java.util.function.UnaryOperator

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

    fun <T>monitorProgressFilter(f: UnaryOperator<T>): UnaryOperator<T> {
        return UnaryOperator { v -> reportEvent(1); f.apply(v) }
    }
}
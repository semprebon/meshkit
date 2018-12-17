package org.semprebon.mesh.filters

import java.util.function.Predicate
import java.util.function.UnaryOperator

class FilteredOperation<T>(val predicate: Predicate<T>, val operator: UnaryOperator<T>): UnaryOperator<T> {

    override fun apply(v: T) = if (predicate.test(v)) operator.apply(v) else v

}
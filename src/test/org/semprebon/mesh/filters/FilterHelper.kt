package org.semprebon.mesh.filters

import org.semprebon.mesh.MeshTestHelper
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

import java.util.function.Predicate

open class FilterHelper: MeshTestHelper() {

    fun <T>assertTrue(predicate: Predicate<T>, value: T) {
        assertTrue(predicate.test(value), "${value} should be included by filter")
    }

    fun <T>assertFalse(predicate: Predicate<T>, value: T) {
        assertFalse(predicate.test(value), "${value} should not be included by filter")
    }
}
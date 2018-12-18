package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.semprebon.mesh.MeshTestHelper
import java.util.function.Predicate
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TriangulateTest {

    companion object: MeshTestHelper()

    val triangulate = Triangulate()
    val triangle = listOf(
        Vector3D(0.0, 0.0, 0.0),
        Vector3D(2.0, 0.0, 0.0),
        Vector3D(0.0, 2.0, 0.0))

    val square = listOf(
        Vector3D(0.0, 0.0, 0.0),
        Vector3D(2.0, 0.0, 0.0),
        Vector3D(2.0, 2.0, 0.0),
        Vector3D(0.0, 2.0, 0.0))

    @Test
    fun `with triangle`() {
        val result = triangulate.apply(triangle)
        assertEquals(1, result.size)
        assertEquals(triangle, result.first())
    }

    @Test
    fun `with square`() {
        val result = triangulate.apply(square)
        assertEquals(2, result.size)
        assertTrue(result.all { it.size == 3})
        assertTrue(result.flatMap { it }.all { square.contains(it) })
    }

}
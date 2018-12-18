package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.semprebon.mesh.MeshTestHelper

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SubdivideFaceTest {

    companion object: MeshTestHelper()

    val subdivide = SubdivideFace()
    val triangle = listOf(v(0.0, 0.0, 0.0), v(2.0, 0.0, 0.0), v(0.0, 2.0, 0.0))

    val square = listOf(
        v(0.0, 0.0, 0.0), v(2.0, 0.0, 0.0),
        v(2.0, 2.0, 0.0), v(0.0, 2.0, 0.0)
    )

    @Test
    fun `with triangle`() {
        val result = subdivide.apply(triangle)
        assertEquals(3, result.size)
        Assertions.assertTrue(result.all { it.size == 3 })
        triangle.forEach {originalVertex ->
            assertEquals(2, result.count { it.contains(originalVertex) })
        }
    }

    @Test
    fun `with square`() {
        val result = subdivide.apply(square)
        assertEquals(4, result.size)
        Assertions.assertTrue(result.all { it.size == 3 })
        square.forEach {originalVertex ->
            assertEquals(2, result.count { it.contains(originalVertex) })
        }
    }

}
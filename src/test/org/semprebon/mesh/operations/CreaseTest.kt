package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreaseTest {

    val triangleFace = listOf(
        Vector3D(0.0, 0.0, 1.0), Vector3D(2.0, 0.0, 1.0),
        Vector3D(0.0, 2.0, 1.0))

    val nonOverlappingSegment = listOf(Vector2D(3.0, 0.0), Vector2D(3.0, 4.0))
    val singleSegmentCrossesFace = listOf(Vector2D(-1.0, 1.0), Vector2D(2.0, 1.0))

    @Test
    fun `line not overlapping face`() {
        val result = Crease(nonOverlappingSegment).apply(triangleFace)
        assertEquals(1, result.size)
        assertEquals(triangleFace, result.first())
    }

    @Test
    fun `line endpoint on face`() {
        val result = Crease(singleSegmentCrossesFace).apply(triangleFace)
        assertEquals(3, result.size)
        assertTrue(result.all { it.size == 3})
        val resultVertices = result.flatMap { it }
        assertTrue(triangleFace.all { v -> resultVertices.contains(v) })
    }
}
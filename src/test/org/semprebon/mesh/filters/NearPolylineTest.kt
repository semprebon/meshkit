package org.semprebon.mesh.filters

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.semprebon.mesh.MeshTestHelper

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NearPolylineTest {
    companion object: FilterHelper()

    val polyline = listOf(v(0.0, 0.0), v(1.0, 1.0), v(2.0, 0.0))
    val predicate = NearPolyline(polyline, 0.2)

    @Test
    fun `parallel to segment`() {
        assertTrue(predicate, v(0.1, 0.0, 2.0))
        assertTrue(predicate, v(0.0, 0.1, -2.0))
        assertTrue(predicate, v(2.0, 0.0, 0.0))

        assertFalse(predicate, v(0.3, 0.0, 2.0))
        assertFalse(predicate, v(0.0, 0.3, 2.0))
    }

    @Test
    fun `beyond segment`() {
        assertTrue(predicate, v(-0.1, -0.1, 2.0))
        assertTrue(predicate, v(1.0, 1.1, -2.0))

        assertFalse(predicate, v(-0.3, -0.3, 2.0))
        assertFalse(predicate, v(1.0, 1.3, 2.0))
    }
}
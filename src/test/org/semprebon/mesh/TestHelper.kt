package org.semprebon.mesh

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import org.junit.jupiter.api.Assertions
import kotlin.math.PI

open class TestHelper {
    val tolerance = 0.0001

    fun assertEquals(expected: Vector3D, actual: Vector3D) {
        Assertions.assertEquals(expected.x, actual.x, tolerance)
        Assertions.assertEquals(expected.y, actual.y, tolerance)
        Assertions.assertEquals(expected.z, actual.z, tolerance)
    }

    fun assertEquals(expected: Mesh.Face, actual: Mesh.Face) {
        for (i in 0..2) {
            assertEquals(expected.vertices[i], actual.vertices[i])
        }
    }
}
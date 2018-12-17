package org.semprebon.mesh

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.junit.jupiter.api.Assertions
import java.util.*

open class MeshTestHelper {
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

    fun <T>assertContains(expected: T, actual: Collection<T>) {
        Assertions.assertTrue(actual.contains(expected), "${actual} should contain ${expected}")
    }

    fun <T>assertIsSublistOf(expected: List<T>, actual: List<T>) {
        Assertions.assertTrue(
            Collections.indexOfSubList(expected, actual) != -1,
            "${actual} should be in ${expected}"
        )
    }

    /**
     * Returns a simple mesh of four points, three triangles with a common vertex
     */
    fun simpleMesh(): Mesh {
        val mesh = Mesh()
        val vs = listOf(
            Vector3D(0.0, 0.0, 0.0),
            Vector3D(2.0, 0.0, 0.0),
            Vector3D(1.0, 1.0, 0.0),
            Vector3D(0.0, 2.0, 0.0))
        mesh.Face(vs[0], vs[1], vs[2])
        mesh.Face(vs[2], vs[1], vs[3])
        mesh.Face(vs[0], vs[2], vs[3])
        return mesh
    }

    /**
     * Returns a disconnected mesh of two separate triangles
     */
    fun disconnectedMesh(): Mesh {
        val mesh = Mesh()
        val vs = listOf(
            Vector3D(0.0, 0.0, 0.0),
            Vector3D(1.0, 0.0, 0.0),
            Vector3D(0.0, 1.0, 0.0),

            Vector3D(0.0, 0.0, 1.0),
            Vector3D(1.0, 0.0, 1.0),
            Vector3D(0.0, 1.0, 1.0))
        mesh.Face(vs[0], vs[1], vs[2])
        mesh.Face(vs[3], vs[4], vs[5])
        return mesh
    }

    /**
     * Returns a mesh of two separate triangles connected at a single point
     */
    fun pointConnectedMesh(): Mesh {
        val mesh = Mesh()
        val vs = listOf(
            Vector3D(0.0, 0.0, 0.0),
            Vector3D(1.0, 0.0, 0.0),
            Vector3D(0.0, 1.0, 0.0),
            Vector3D(0.0, -1.0, 0.0),
            Vector3D(-1.0, 0.0, 0.0))
        mesh.add(vs[0], vs[1], vs[2])
        mesh.add(vs[0], vs[3], vs[4])
        return mesh
    }
}
package org.semprebon.mesh

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import org.junit.jupiter.api.Assertions
import java.util.*

open class MeshTestHelper {
    val tolerance = 0.0001

    fun assertEquals(expected: Vector3D, actual: Vector3D) {
        Assertions.assertEquals(expected.x, actual.x, tolerance)
        Assertions.assertEquals(expected.y, actual.y, tolerance)
        Assertions.assertEquals(expected.z, actual.z, tolerance)
    }

    fun assertEquals(expected: FaceVertexMesh.Face, actual: FaceVertexMesh.Face) {
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

    fun v(x: Double, y: Double, z: Double) = Vector3D(x, y, z)
    fun v(x: Double, y: Double) = Vector2D(x, y)

    fun triangleFrom(vertexes: List<Vector2D>) =
        listOf(Conversion.to3d(vertexes[0]), Conversion.to3d(vertexes[1]), Conversion.to3d(vertexes[2]))

    fun trianglesFrom(path: List<Vector2D>): List<List<Vector3D>> {
        if (path.size == 3) return listOf(triangleFrom(path))
        else {
            val newPath = path.take(1) + path.drop(2)
            return listOf(triangleFrom(path.take(3))) + trianglesFrom(newPath)
        }
    }

    fun meshFrom(path: List<Vector2D>): FaceVertexMesh = FaceVertexMesh(trianglesFrom(path))

    /**
     * Create a mesh of unit squares with its lower left corner at 0,0
     */
    fun squareMesh(n:Int, z: Double = 0.0): FaceVertexMesh {
        val mesh = FaceVertexMesh(tolerance)
        for (i in 0..n-1) {
            for (j in 0..n-1) {
                mesh.add(
                    v(i.toDouble(), j.toDouble(), z),
                    v((i+1).toDouble(), j.toDouble(), z),
                    v((i+1).toDouble(), (j+1).toDouble(), z),
                    v(i.toDouble(), (j+1).toDouble(), z))
            }
        }
        return mesh
    }

    /**
     * Returns a simple mesh of four points, three triangles with a common vertex
     */
    fun simpleMesh(): FaceVertexMesh {
        val mesh = FaceVertexMesh()
        val vs = listOf(
            Vector3D(0.0, 0.0, 0.0),
            Vector3D(2.0, 0.0, 0.0),
            Vector3D(0.5, 0.5, 0.0),
            Vector3D(0.0, 2.0, 0.0))
        mesh.Face(vs[0], vs[1], vs[2])
        mesh.Face(vs[2], vs[1], vs[3])
        mesh.Face(vs[0], vs[2], vs[3])
        return mesh
    }

    /**
     * Returns a disconnected mesh of two separate triangles
     */
    fun disconnectedMesh(): FaceVertexMesh {
        val mesh = FaceVertexMesh()
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
    fun pointConnectedMesh(): FaceVertexMesh {
        val mesh = FaceVertexMesh()
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
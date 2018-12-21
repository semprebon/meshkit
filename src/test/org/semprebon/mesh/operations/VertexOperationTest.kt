package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.semprebon.mesh.FaceVertexMesh
import org.semprebon.mesh.MeshTestHelper
import java.util.function.Predicate
import java.util.function.UnaryOperator
import org.junit.jupiter.api.Assertions.assertEquals
import org.semprebon.mesh.filters.FilteredOperation

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VertexOperationTest {
    companion object: MeshTestHelper()

    val simpleMesh = simpleMesh()

    class onlyEven(val mesh: FaceVertexMesh): Predicate<Vector3D> {
        fun isEven(x: Int) = (x % 2) == 0

        override fun test(vertex: Vector3D): Boolean {
            return isEven(mesh.indexOf(vertex))
        }
    }

    val addOneToZ =  UnaryOperator { v: Vector3D -> v.add(Vector3D(0.0, 0.0, 1.0))!! }

    @Nested
    inner class `apply` {
        @Test
        fun `with no filter`() {
            val newMesh = VertexOperation(addOneToZ).apply(simpleMesh)
            simpleMesh.vertices.forEachIndexed { i, v ->
                assertEquals(v.x, newMesh.vertices[i].x, tolerance)
                assertEquals(v.y, newMesh.vertices[i].y, tolerance)
                assertEquals(v.z+1.0, newMesh.vertices[i].z, tolerance)
            }
        }

        @Test
        fun `with even filter`() {
            val newMesh = VertexOperation(FilteredOperation(onlyEven(simpleMesh), addOneToZ)).apply(simpleMesh)
            simpleMesh.vertices.forEachIndexed { i, v ->
                assertEquals(v.x, newMesh.vertices[i].x, tolerance)
                assertEquals(v.y, newMesh.vertices[i].y, tolerance)
                val expectedZ = if ((i % 2) == 0) v.z+1.0 else v.z
                assertEquals(expectedZ, newMesh.vertices[i].z, tolerance)
            }
        }
    }
}
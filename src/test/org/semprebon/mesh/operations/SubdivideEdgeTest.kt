package org.semprebon.mesh.operations

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.semprebon.mesh.MeshTestHelper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SubdivideEdgeTest {
    companion object : MeshTestHelper()

    val square = squareMesh(1,1.0)

    @Test
    fun `subdivide a square`() {
        val mesh = SubdivideEdge().apply(square)

        assertEquals(4, mesh.faces.size)
        assertEquals(9, mesh.vertices.size)
        mesh.faces.forEachIndexed { i, face ->
            assertEquals(4, face.vIndexes.size)
            assertTrue(face.vertices.contains(v(0.5,0.5,1.0)))
            assertTrue(face.vertices.contains(square.vertices[i]))
        }
        val face = mesh.faces[0]

    }
}
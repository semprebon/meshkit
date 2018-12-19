package org.semprebon.mesh.operations

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.semprebon.mesh.MeshTestHelper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.semprebon.mesh.MeshVisualizer
import org.semprebon.mesh.filters.NearPolyline
import org.semprebon.mesh.filters.Util

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SubdivideEdgeTest {
    companion object : MeshTestHelper()

    val square = squareMesh(1,1.0)
    val square10x10 = squareMesh(10, 0.0)

    val polyLine = listOf(v(0.0, 0.0), v(0.3,0.3), v(0.6, 0.3))
    val nearPolyline = NearPolyline(polyLine, 0.5)

    @Test
    fun `subdivide a square`() {
        val mesh = SubdivideEdge().apply(square)

        MeshVisualizer.start("subdivideEdgeSquare.png") { ->
            MeshVisualizer.visualize(mesh)
        }
        assertEquals(4, mesh.faces.size)
        assertEquals(9, mesh.vertices.size)
        mesh.faces.forEachIndexed { i, face ->
            assertEquals(4, face.vIndexes.size)
            assertTrue(face.vertices.contains(v(0.5,0.5,1.0)))
            assertTrue(face.vertices.contains(square.vertices[i]))
        }
        val face = mesh.faces[0]

    }

    @Test
    fun `subdivide with filter squares in array`() {
        val mesh = SubdivideEdge(Util.anyVerticesOfFace(nearPolyline)).apply(square10x10)
        val squaresDivided = 15

        MeshVisualizer.start("subdivideEdgeGrid.png") { ->
            MeshVisualizer.visualize(mesh)
        }

        assertEquals(squaresDivided*3, mesh.faces.size - square10x10.faces.size)
        assertEquals(squaresDivided*5, mesh.vertices.size - square10x10.vertices.size)
        mesh.faces.forEachIndexed { i, face ->
            assertEquals(4, face.vIndexes.size)
        }
    }
}
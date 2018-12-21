package org.semprebon.mesh.operations

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.semprebon.mesh.MeshTestHelper
import org.semprebon.mesh.MeshVisualizer
import org.semprebon.mesh.filters.NearPolyline
import org.semprebon.mesh.filters.Util

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SubdivideFacesTest {
    companion object : MeshTestHelper()

    val square = squareMesh(1, 1.0)
    val square10x10 = squareMesh(10, 0.0)
    val square100x100 = squareMesh(100, 0.0)

    val polyLine = listOf(v(0.0, 0.0), v(3.0, 3.0), v(6.0, 3.0))
    val nearPolyline = NearPolyline(polyLine, 0.5)

    @Test
    fun `subdivide a square`() {
        val mesh = SubdivideFaces().apply(square)

        MeshVisualizer.start("subdivideFacesTestSquare.png") { ->
            MeshVisualizer.visualize(mesh)
        }
        Assertions.assertEquals(5, mesh.faces.size)
        Assertions.assertEquals(8, mesh.vertices.size)
        Assertions.assertEquals(12, mesh.edges().size)
    }

    @Test
    fun `subdivide with filter squares in array`() {
        val mesh = SubdivideFaces(Util.anyVerticesOfFace(nearPolyline)).apply(square10x10)
        val squaresDivided = 16

        MeshVisualizer.start("subdivideFacesTestGrid.png") { ->
            MeshVisualizer.visualize(mesh)
        }

        Assertions.assertEquals(squaresDivided * 4, mesh.faces.size - square10x10.faces.size)
    }

}
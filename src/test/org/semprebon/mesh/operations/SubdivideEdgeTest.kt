package org.semprebon.mesh.operations

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.semprebon.mesh.MeshTestHelper

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SubdivideEdgeTest {
    companion object : MeshTestHelper()

    val square = squareMesh(1,1.0)

    @Test
    fun `subdivide a square`() {
        val mesh = SubdivideEdge().apply(square)
    }
}
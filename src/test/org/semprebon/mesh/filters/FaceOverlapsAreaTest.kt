package org.semprebon.mesh.filters

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.semprebon.mesh.Conversion
import org.semprebon.mesh.Mesh
import org.semprebon.mesh.MeshTestHelper
import org.semprebon.mesh.operations.Triangulate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FaceOverlapsAreaTest {

    companion object: MeshTestHelper()

    @Test
    fun `road test`() {
        val polygonOriginal = listOf(
            Vector2D(0.0, 0.0),
            Vector2D(3.0, 0.0),
            Vector2D(3.0, 1.0),
            Vector2D(1.0, 1.0),
            Vector2D(1.0, 4.0),
            Vector2D(0.0, 4.0))
        val polygon3D = Triangulate().apply(polygonOriginal.map { Conversion.to3d(it )})
        val polygon2D = polygon3D.map { p -> p.map { Conversion.to2d(it) } }
        val tolerance = 0.01
        val predicate = FaceOverlapsArea(polygon2D, tolerance)

        val mesh2 = Mesh()
        val face = mesh2.Face(Vector3D(0.5, 0.5, 10.0))

        //Assertions.assertTrue(predicate.test(face))
    }

}
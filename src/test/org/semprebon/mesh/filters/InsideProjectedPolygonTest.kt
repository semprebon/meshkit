package org.semprebon.mesh.filters

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InsideProjectedPolygonTest {

    val unitSquare2D = listOf(
        Vector2D(0.0, 0.0), Vector2D(1.0, 0.0),Vector2D(1.0, 1.0),Vector2D(0.0, 1.0))

    val squareFilter = InsideProjectedPolygon(unitSquare2D, 0.01)

    val nonConvexPolygon = listOf(
        Vector2D(0.0, 0.0), Vector2D(2.0, 0.0),Vector2D(2.0, 3.0),Vector2D(1.0, 2.0),
        Vector2D(1.0, 1.0), Vector2D(0.0, 1.0)
    )
    val nonConvextFilter = InsideProjectedPolygon(nonConvexPolygon, 0.01)

    @Nested
    inner class `apply` {
        @Test
        fun `with point in convex polygon`() {
            assertTrue(squareFilter.test(Vector3D(0.5, 0.5, 1.0)))
        }

        @Test
        fun `with point on convex polygon`() {
            assertTrue(squareFilter.test(Vector3D(0.0, 0.0, 0.0)))
            assertTrue(squareFilter.test(Vector3D(0.5, 0.0, -1.0)))
        }

        @Test
        fun `with point outside convex polygon`() {
            assertFalse(squareFilter.test(Vector3D(1.5, 0.5, 1.0)))
            assertFalse(squareFilter.test(Vector3D(1.5, 0.0, 0.0)))
            assertFalse(squareFilter.test(Vector3D(-1.0, -1.0, -1.0)))
        }

        @Test
        fun `with point in nonconvex polygon`() {
            assertTrue(nonConvextFilter.test(Vector3D(0.5, 0.5, 1.0)))
            assertTrue(nonConvextFilter.test(Vector3D(1.5, 2.0, -1.0)))
        }

        @Test
        fun `with point on nonconvex polygon`() {
            assertTrue(nonConvextFilter.test(Vector3D(0.0, 0.0, 0.0)))
            assertTrue(nonConvextFilter.test(Vector3D(2.0, 3.0, -1.0)))
            assertTrue(nonConvextFilter.test(Vector3D(1.0, 2.0, -1.0)))
            assertTrue(nonConvextFilter.test(Vector3D(1.0, 2.0, -1.0)))
        }

        @Test
        fun `with point outside nonconvex polygon`() {
            assertFalse(nonConvextFilter.test(Vector3D(0.8, 1.1, 0.0)))
            assertFalse(nonConvextFilter.test(Vector3D(1.5, 2.8, -1.0)))
            assertFalse(nonConvextFilter.test(Vector3D(1.5, 3.0, -1.0)))
        }
    }

}
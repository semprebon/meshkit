package org.semprebon.mesh;

import org.junit.jupiter.api.Test;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.*

public class MeshTest {

    companion object {
        fun <T>assertContains(expected: T, actual: Collection<T>) {
            assertTrue(actual.contains(expected), "${actual} should contain ${expected}");
        }

        fun <T>assertIsSublistOf(expected: List<T>, actual: List<T>) {
            assertTrue(Collections.indexOfSubList(expected, actual) != -1,
                "${actual} should be in ${expected}")
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
    }

    class `edges` {
        @Test
        fun `simple mesh`() {
            val es = simpleMesh().edges()
            assertEquals(6, es.size)
        }
    }
    
    class `perimeter` {
        @Test
        fun `simple mesh`() {
            val p = simpleMesh().perimeter()
            val expected = listOf(
                Vector3D(0.0, 0.0, 0.0),
                Vector3D(2.0, 0.0, 0.0),
                Vector3D(0.0, 2.0, 0.0))
            assertIsSublistOf(expected + expected, p)
        }

    }
}

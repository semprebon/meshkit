package org.semprebon.mesh

import org.junit.jupiter.api.Test
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FaceVertexMeshTest {

    companion object: MeshTestHelper()

    class `add` {
        @Test
        fun `add to empty mesh`() {
            val mesh = FaceVertexMesh()
            mesh.add(Vector3D(0.0, 0.0, 0.0),
                Vector3D(1.0, 0.0, 0.0),
                Vector3D(0.0, 1.0, 0.0))
            assertEquals(3, mesh.vertices.size)
            assertContains(Vector3D(0.0, 0.0, 0.0), mesh.vertices)
            assertContains(Vector3D(1.0, 0.0, 0.0), mesh.vertices)
            assertContains(Vector3D(0.0, 1.0, 0.0), mesh.vertices)
            assertEquals(1, mesh.faces.size)
        }

        @Test
        fun `add polygon with existing vertex`() {
            val mesh = simpleMesh()
            mesh.add(mesh.vertices[0], mesh.vertices[1], Vector3D(0.0, 0.0, 1.0))
            assertEquals(5, mesh.vertices.size)
            assertContains(Vector3D(0.0, 0.0, 1.0), mesh.vertices)
            assertEquals(4, mesh.faces.size)
        }

        @Test
        fun `add polygon with vertex equal to existing`() {
            val mesh = simpleMesh()
            mesh.add(Vector3D(0.0, 0.0, 0.0), Vector3D(2.0, 0.0, 0.0), Vector3D(0.0, 0.0, 1.0))
            assertEquals(5, mesh.vertices.size)
            assertContains(Vector3D(0.0, 0.0, 1.0), mesh.vertices)
            assertEquals(4, mesh.faces.size)
        }

        @Test
        fun `add degenerate polygon`() {
            val mesh = simpleMesh()
            assertThrows<IllegalArgumentException> {
                mesh.add(Vector3D(0.0, 0.0, 0.0), Vector3D(2.0, 0.0, 0.0))
            }
        }
    }

    class `splitEdgeFace` {
        @Test
        fun `unit square`() {
            val mesh = squareMesh(1)
            mesh.splitEdge(mesh.Edge(1, 0), v(0.5, 0.0, 0.0))
            assertEquals(5, mesh.vertices.size)
            assertContains(Vector3D(0.5, 0.0, 0.0), mesh.vertices)


            val vertices = mesh.faces[0].vertices
            val i = vertices.indexOf(v(0.5, 0.0, 0.0))
            val inc = Geometry.Incrementer.forList(vertices)

            val expectedVertices = listOf(v(0.0, 0.0, 0.0), v(1.0, 0.0, 0.0))
            val adjacentVertices = listOf(vertices[inc.prev(i)], vertices[inc.next(i)])
            assertTrue(adjacentVertices.contains(v(0.0, 0.0, 0.0)), "0,0 is adjacent to new vertex 0,0.5")
            assertTrue(adjacentVertices.contains(v(1.0, 0.0, 0.0)), "1,0 is adjacent to new vertex 0,0.5")
        }

        @Test
        fun `outside edge`() {
            val mesh = simpleMesh()
            mesh.splitEdge(mesh.Edge(0, 1), Vector3D(1.0, 0.0, 0.0))
            assertEquals(5, mesh.vertices.size)
            assertContains(Vector3D(1.0, 0.0, 0.0), mesh.vertices)
            val i = mesh.indexOf(Vector3D(1.0, 0.0, 0.0))
            assertTrue(mesh.edges().any { edge -> edge.isCoincident(mesh.Edge(0, i)) },
                "${mesh.edges()} should have edge like (0,${i})")
            assertTrue(mesh.edges().any { edge -> edge.isCoincident(mesh.Edge(1, i)) },
                "${mesh.edges()} should have edge like (1,${i})")
        }

        @Test
        fun `inside edge`() {
            val mesh = simpleMesh()
            mesh.splitEdge(mesh.Edge(0, 2), Vector3D(0.3, 0.3, 0.0))
            assertEquals(5, mesh.vertices.size)
            assertContains(Vector3D(0.3, 0.3, 0.0), mesh.vertices)
            val i = mesh.indexOf(Vector3D(0.3, 0.3, 0.0))
            assertTrue(mesh.edges().any { edge -> edge.isCoincident(mesh.Edge(0, i)) },
                "${mesh.edges()} should have edge like (0,${i})")
            assertTrue(mesh.edges().any { edge -> edge.isCoincident(mesh.Edge(2, i)) },
                "${mesh.edges()} should have edge like (1,${i})")
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

        @Test
        fun `disconnected mesh`() {
            val p = disconnectedMesh().perimeter()
            assertEquals(3, p.size)
        }

        @Test
        fun `point connected mesh`() {
            val p = pointConnectedMesh().perimeter()
            assertEquals(6, p.size)
        }
    }

    class `seperate`() {
        @Test
        fun `with completely joined mesh`() {
            val meshes = simpleMesh().separate()
            assertEquals(1, meshes.size)
        }

        @Test
        fun `with disjoint mesh`() {
            val meshes = disconnectedMesh().separate()
            assertEquals(2, meshes.size)
        }
    }

    class `object` {
        class `signedArea` {
            @Test
            fun `counter-clockwise right triangle on z=0 plane`() {
                val polygon = listOf(
                    Vector3D(0.0, 0.0, 0.0),
                    Vector3D(1.0, 0.0, 0.0),
                    Vector3D(0.0, 1.0, 0.0))
                assertEquals(0.5, FaceVertexMesh.signedArea(polygon, Vector3D.PLUS_K))
            }
        }
    }
}

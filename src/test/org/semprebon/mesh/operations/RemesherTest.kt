package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.semprebon.mesh.MeshTestHelper
import java.util.function.Predicate
import org.semprebon.mesh.Mesh
import org.semprebon.mesh.filters.Util

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RemesherTest {
    companion object: MeshTestHelper() {
        val remesher = Remesher(SubdivideFace())
    }

    @Test
    fun `with no filter`() {
        val originalMesh = simpleMesh()
        val mesh = remesher.apply(originalMesh)

        assertEquals(originalMesh.faces.size*3, mesh.faces.size)
        originalMesh.vertices.forEach { assertTrue(mesh.vertices.contains(it)) }
    }

    @Test
    fun `with filter`() {
        val notOriginFilter = object : Predicate<List<Vector3D>> {
            override fun test(polygon: List<Vector3D>) = polygon.none { it.equals(Vector3D(0.0, 0.0, 0.0)) }
        }
        assertTrue(notOriginFilter.test(listOf(Vector3D(1.0, 0.0, 0.0))))
        assertFalse(notOriginFilter.test(listOf(Vector3D(1.0, 0.0, 0.0), Vector3D(0.0, 0.0, 0.0))))

        val remesher = Remesher(Remesher.filteredFaceSplitter(
            Util.createFacePredicateFor(notOriginFilter), Remesher.faceSplitterFor(SubdivideFace())))

        val originalMesh = simpleMesh()
        val mesh = remesher.apply(originalMesh)

        assertEquals(5, mesh.faces.size)
        assertEquals(5, mesh.vertices.size)
        originalMesh.vertices.forEach { assertTrue(mesh.vertices.contains(it)) }
        assertTrue(mesh.faces.map(Mesh.Face::vertices).contains(originalMesh.faces[0].vertices))
        assertFalse(mesh.faces.map(Mesh.Face::vertices).contains(originalMesh.faces[1].vertices))
        assertTrue(mesh.faces.map(Mesh.Face::vertices).contains(originalMesh.faces[2].vertices))
    }
}
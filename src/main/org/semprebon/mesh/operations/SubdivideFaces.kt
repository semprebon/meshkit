package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.semprebon.mesh.Geometry
import org.semprebon.mesh.Mesh
import org.semprebon.mesh.filters.Util
import java.util.function.Predicate

/**
 * Divides the face by joining pairs of new vertices.
 *
 * For an n-gon face, this creates n triangle faces with a smaller n-gon in the center.
 */
class SubdivideFaces(faceFilter: Predicate<Mesh.Face> = Util.ALL_FACES) : EdgeAndFaceRemesher(faceFilter) {

    inner class Remesher: NewVertexFaceRemesher() {
        var center: Vector3D? = null

        override fun before(face: Mesh.Face): Mesh.Face {
            center = Geometry.center(face.vertices)
            return face
        }

        override faceGenerator()
    }
}
    inner class RemeshFaceWithNewVertices: FaceSplitter {

        private fun newFace(face: Mesh.Face, startVIndex: Int, center: Vector3D): List<Vector3D> {
            val inc = Geometry.Incrementer(face.vIndexes.size-1)
            val newFace = listOf(
                face.vertices[startVIndex],
                face.vertices[inc.prev(startVIndex)],
                face.vertices[inc.next(startVIndex)])
            return newFace
        }

        private fun createFaces(face: Mesh.Face, newVIndexes: List<Int>): List<List<Vector3D>> {
            val center = Geometry.center(face.vertices)
            val newFaces = face.vIndexes.mapIndexed { i, vIndex ->
                if (!newVIndexes.contains(vIndex)) newFace(face, i, center) else emptyList()
            }.filter { !it.isEmpty() }
            return newFaces
        }

        override fun apply(face: Mesh.Face): List<List<Vector3D>> {
            val newVIndexes: List<Int> = face.vIndexes.filter { vIndex -> midpointIndexes!!.contains(vIndex) }
            return createFaces(face, newVIndexes)
        }
    }

    override val faceMeshingAlgorithm = NewVertexFaceRemesher { face: Mesh.Face, index: Int -> generateFace()
}
package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.semprebon.mesh.Geometry
import org.semprebon.mesh.Mesh
import org.semprebon.mesh.filters.Util
import java.util.function.Predicate

/**
 * Converts each face into a quadrilateral by bisecting each edge and connecting the
 * new edges to the center
 */
class QuadFaces(faceFilter: Predicate<Mesh.Face> = Util.ALL_FACES) : EdgeAndFaceRemesher(faceFilter) {


    private fun newFace(face: Mesh.Face, startVIndex: Int, center: Vector3D): List<Vector3D> {
        val inc = Geometry.Incrementer(face.vIndexes.size-1)
        val newFace = listOf(
            face.vertices[startVIndex],
            face.vertices[inc.next(startVIndex)],
            center,
            face.vertices[inc.prev(startVIndex)])
        return newFace
    }

    inner class FaceRemesher: NewVertexFaceRemesher() {


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

    override val faceMeshingAlgorithm = NewVertexFaceRemesher { face: Mesh.Face, index: Int -> newFace(face, index, center))
}
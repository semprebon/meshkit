package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.semprebon.mesh.Geometry
import org.semprebon.mesh.FaceVertexMesh
import org.semprebon.mesh.filters.Util
import java.util.function.Predicate

/**
 * Divides the face by joining pairs of new vertices.
 *
 * For an n-gon face, this creates n triangle faces with a smaller n-gon in the center.
 */
class SubdivideFaces(faceFilter: Predicate<FaceVertexMesh.Face> = Util.ALL_FACES) : EdgeAndFaceRemesher(faceFilter) {


    inner class FaceRemesher : NewVertexFaceRemesher() {

        override fun createFace(face: FaceVertexMesh.Face, newIndex: Int): List<Vector3D> {
            val inc = Geometry.Incrementer(face.vIndexes.size - 1)
            val newFace = listOf(
                face.vertices[newIndex],
                face.vertices[inc.prev(newIndex)],
                face.vertices[inc.next(newIndex)]
            )
            return newFace
        }

        override fun after(face: FaceVertexMesh.Face, faces: List<List<Vector3D>>): List<List<Vector3D>> {
            return faces + listOf(newVIndexes!!.map { face.mesh().vertices[it] })
        }
    }

    override val faceMeshingAlgorithm = FaceRemesher()
}

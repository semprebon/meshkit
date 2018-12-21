package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.semprebon.mesh.Geometry
import org.semprebon.mesh.FaceVertexMesh
import org.semprebon.mesh.filters.Util
import java.util.function.Predicate

/**
 * Converts each face into a quadrilateral by bisecting each edge and connecting the
 * new edges to the center
 */
class QuadFaces(faceFilter: Predicate<FaceVertexMesh.Face> = Util.ALL_FACES) : EdgeAndFaceRemesher(faceFilter) {


    inner class FaceRemesher: NewVertexFaceRemesher() {

        var center: Vector3D? = null

        override fun createFace(face: FaceVertexMesh.Face, startVIndex: Int): List<Vector3D> {
            val inc = Geometry.Incrementer(face.vIndexes.size-1)
            val newFace = listOf(
                face.vertices[startVIndex],
                face.vertices[inc.next(startVIndex)],
                center!!,
                face.vertices[inc.prev(startVIndex)])
            return newFace
        }

        override fun before(face: FaceVertexMesh.Face): FaceVertexMesh.Face {
            center = Geometry.center(face.vertices)
            return face
        }
    }

    override val faceMeshingAlgorithm = FaceRemesher()
}
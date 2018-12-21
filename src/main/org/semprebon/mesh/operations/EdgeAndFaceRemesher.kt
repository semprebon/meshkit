package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.semprebon.mesh.Geometry
import org.semprebon.mesh.FaceVertexMesh
import org.semprebon.mesh.filters.Util
import java.util.function.Predicate

/**
 * Abstract class for remesher that modifies both edges and faces
 */
abstract class EdgeAndFaceRemesher(val faceFilter: Predicate<FaceVertexMesh.Face> = org.semprebon.mesh.filters.Util.ALL_FACES) : Remesher(Dummy()) {
    var midpointIndexes: List<Int>? = null

    class Dummy : FaceSplitter {
        override fun apply(face: FaceVertexMesh.Face): List<List<Vector3D>> { return listOf() }
    }

    /**
     * Generates one new face for each new vertex
     */
    abstract inner class NewVertexFaceRemesher: FaceSplitter {

        open fun before(face: FaceVertexMesh.Face) = face
        open fun after(originalFace: FaceVertexMesh.Face, newFaces: List<List<Vector3D>>) = newFaces
        var newVIndexes: List<Int>? = null

        abstract fun createFace(face: FaceVertexMesh.Face, index: Int): List<Vector3D>

        private fun createFaces(face: FaceVertexMesh.Face, newVIndexes: List<Int>): List<List<Vector3D>> {
            val center = Geometry.center(face.vertices)
            val newFaces = face.vIndexes.mapIndexed { i, vIndex ->
                if (!newVIndexes.contains(vIndex)) createFace(face, i) else emptyList()
            }.filter { !it.isEmpty() }
            return newFaces
        }

        override fun apply(face: FaceVertexMesh.Face): List<List<Vector3D>> {
            newVIndexes = face.vIndexes.filter { vIndex -> midpointIndexes!!.contains(vIndex) }
            return createFaces(face, newVIndexes!!)
        }
    }

    abstract val faceMeshingAlgorithm: NewVertexFaceRemesher

    open fun generaterFacesForFace(face: FaceVertexMesh.Face): List<List<Vector3D>> {
        val adjustedFace = faceMeshingAlgorithm.before(face)
        val newFaces = faceMeshingAlgorithm.apply(adjustedFace)
        return faceMeshingAlgorithm.after(face, newFaces)
    }

    override fun apply(mesh: FaceVertexMesh): FaceVertexMesh {
        val newMesh = FaceVertexMesh(mesh.faces.map(FaceVertexMesh.Face::vertices), mesh.tolerance)
        val meshingAlgorithm = filteredFaceSplitter(faceFilter, faceMeshingAlgorithm)

        val edgeFilter = Util.eitherFaceOfEdge(faceFilter)
        midpointIndexes = newMesh.edges().filter { edgeFilter.test(it) }.map { bisect(it) }

        return FaceVertexMesh(newMesh.faces.flatMap { if (faceFilter.test(it)) generaterFacesForFace(it) else listOf(it.vertices) })
    }

    private fun bisect(edge: FaceVertexMesh.Edge): Int {
        val v = edge.start.add(edge.end).scalarMultiply(0.5)!!
        return edge.mesh().splitEdge(edge, v)
    }
}
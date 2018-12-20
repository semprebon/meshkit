package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.semprebon.mesh.Mesh
import org.semprebon.mesh.filters.Util
import java.util.function.BiFunction
import java.util.function.Predicate

/**
 * Abstract class for remesher that modifies both edges and faces
 */
abstract class EdgeAndFaceRemesher(val faceFilter: Predicate<Mesh.Face> = org.semprebon.mesh.filters.Util.ALL_FACES) : Remesher(Dummy()) {
    var midpointIndexes: List<Int>? = null

    class Dummy : FaceSplitter {
        override fun apply(face: Mesh.Face): List<List<Vector3D>> { return listOf() }
    }

    interface FaceGenerator: BiFunction<Mesh.Face, Int, List<Vector3D>>

    /**
     * Generates one new face for each new vertex
     */
    open inner class NewVertexFaceRemesher(val faceGenerator: FaceGenerator): FaceSplitter {

        open fun before(face: Mesh.Face) = face
        open fun after(originalFace: Mesh.Face, newFaces: List<List<Vector3D>>) = newFaces

        override fun apply(face: Mesh.Face): List<List<Vector3D>> {
            val mesh: Mesh = face.mesh()
            val newVIndexes: List<Int> = face.vIndexes.filter { vIndex -> midpointIndexes!!.contains(vIndex) }
            return face.vIndexes.mapIndexed { i, vIndex ->
                if (!newVIndexes.contains(vIndex)) faceGenerator.apply(face, i) else emptyList()
            }.filter { !it.isEmpty() }
        }
    }

    abstract val faceMeshingAlgorithm: NewVertexFaceRemesher

    open fun generaterFacesForFace(face: Mesh.Face): List<List<Vector3D>> {
        val adjustedFace = faceMeshingAlgorithm.before(face)
        val newFaces = faceMeshingAlgorithm.apply(adjustedFace)
        return faceMeshingAlgorithm.after(face, newFaces)
    }
    override fun apply(mesh: Mesh): Mesh {
        val newMesh = Mesh(mesh.faces.map(Mesh.Face::vertices), mesh.tolerance)
        val meshingAlgorithm = filteredFaceSplitter(faceFilter, faceMeshingAlgorithm)

        val edgeFilter = Util.eitherFaceOfEdge(faceFilter)
        midpointIndexes = newMesh.edges().filter { edgeFilter.test(it) }.map { bisect(it) }

        return Mesh(newMesh.faces.flatMap { generaterFacesForFace(it) })
    }

    private fun bisect(edge: Mesh.Edge): Int {
        val v = edge.start.add(edge.end).scalarMultiply(0.5)!!
        return edge.mesh().splitEdge(edge, v)
    }
}
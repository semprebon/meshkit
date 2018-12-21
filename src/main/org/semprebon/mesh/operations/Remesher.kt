package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.semprebon.mesh.FaceVertexMesh
import java.util.function.Function
import java.util.function.Predicate

/**
 * Restructures the mesh, replacing existing vertices with new vertices
 */
open class Remesher(val meshingAlgorithm: FaceSplitter) {

    constructor(f: PolygonSplitter): this(faceSplitterFor(f))

    interface FaceSplitter: Function<FaceVertexMesh.Face, List<List<Vector3D>>>

    interface PolygonSplitter: Function<List<Vector3D>, List<List<Vector3D>>>

    companion object {
        fun faceSplitterFor(f: PolygonSplitter): FaceSplitter
            = object: FaceSplitter { override fun apply(face: FaceVertexMesh.Face) = f.apply(face.vertices) }

        fun filteredFaceSplitter(predicate: Predicate<FaceVertexMesh.Face>, meshingAlgorithm: FaceSplitter): FaceSplitter {
            return object : FaceSplitter {
                override fun apply(face: FaceVertexMesh.Face): List<List<Vector3D>> {
                    return if (predicate.test(face)) meshingAlgorithm.apply(face) else listOf(face.vertices)
                }
            }
        }

    }

    open fun apply(mesh: FaceVertexMesh): FaceVertexMesh {
        val newFaces = mesh.faces.flatMap { meshingAlgorithm.apply(it) }
        return FaceVertexMesh(newFaces)
    }
}
package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.semprebon.mesh.Mesh
import java.util.function.UnaryOperator

/**
 * Restructures the mesh, replacing existing vertices with new vertices
 */
class RemeshFaces(val remesher: PolygonSplitter) {

    interface PolygonSplitter {
        fun apply(face: List<Vector3D>): List<List<Vector3D>>
    }

    fun apply(mesh: Mesh): Mesh {
        return Mesh(mesh.faces.flatMap { remesher.apply(it.vertices) })
    }

}
package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.semprebon.mesh.Geometry

/**
 * Divides each n-gon face into n triangular faces with a new vertex in the center
 */
class SubdivideFace: Remesher.PolygonSplitter {

    override fun apply(face: List<Vector3D>): List<List<Vector3D>> {
        val base = Geometry.center(face)
        return face.zip(face.drop(1) + face.take(1))
            .map { listOf(it.first, it.second, base) }
    }

}
package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D

/**
 * Divides each n-gon face into n triangular faces with a new vertex in the center
 */
class SubdivideFace: Remesher.PolygonSplitter {

    override fun apply(face: List<Vector3D>): List<List<Vector3D>> {
        val base = center(face)
        return face.zip(face.drop(1) + face.take(1))
            .map { listOf(it.first, it.second, base) }
    }

    fun center(vertices: List<Vector3D>): Vector3D {
        return vertices.reduce { a, b -> a.add(b) }.scalarMultiply(1.0/vertices.size)
    }

}
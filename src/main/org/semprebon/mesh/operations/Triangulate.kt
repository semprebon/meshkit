package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D

/**
 * Converts non-triangular faces to a fan of triangles
 */
class Triangulate: Remesher.PolygonSplitter {
    override fun apply(face: List<Vector3D>): List<List<Vector3D>> {
        val base = face.first()
        return (1..face.size-2).map { listOf(base, face[it], face[it+1]) }.toList()
    }
}
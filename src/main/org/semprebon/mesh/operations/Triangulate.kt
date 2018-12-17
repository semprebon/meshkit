package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import java.awt.Polygon

/**
 * Converts non-triangular faces to triangles
 */
class Triangulate: RemeshFaces.PolygonSplitter {
    override fun apply(face: List<Vector3D>): List<List<Vector3D>> {
        if (face.size == 3) return listOf(face)

        val base = face.first()
        return (1..face.size-2).map { listOf(base, face[it], face[it+1]) }.toList()
    }
}
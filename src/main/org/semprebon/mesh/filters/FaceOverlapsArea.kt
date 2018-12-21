package org.semprebon.mesh.filters

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import org.semprebon.mesh.Conversion
import org.semprebon.mesh.FaceVertexMesh
import java.util.function.Predicate

/**
 * Determine if a face overlaps the specified polygon(s)
 */
class FaceOverlapsArea(val triangles: List<List<Vector2D>>, val tolerance: Double): Predicate<FaceVertexMesh.Face> {

    override fun test(face: FaceVertexMesh.Face): Boolean {
        val face2D = face.vertices.map { Conversion.to2d(it) }
        return triangles.any { trianglesOverlap(face2D, it) }
    }

    fun determinant(vertices: List<Vector2D>) =
        vertices[0].x * (vertices[1].y - vertices[2].y) +
                vertices[1].x * (vertices[2].y - vertices[0].y) +
                vertices[2].x * (vertices[0].y - vertices[1].y)

    fun checkEdge(triangle: List<Vector2D>) = determinant(triangle) <= tolerance

    fun trianglesOverlap(t1: List<Vector2D>, t2: List<Vector2D>): Boolean {
        // for each edge E of t1
        for (i in 0 until 3) {
            val j = (i + 1) % 3
            // Check all points of t2 lay on the external side of edge E.
            // If they do, the triangles do not overlap.
            if (checkEdge(listOf(t1[i], t1[j], t2[0])) &&
                checkEdge(listOf(t1[i], t1[j], t2[1])) &&
                checkEdge(listOf(t1[i], t1[j], t2[2]))) return false
        }

        // for each edge E of t2
        for (i in 0 until 3) {
            val j = (i + 1) % 3
            // Check all points of t1 lay on the external side of edge E.
            // If they do, the triangles do not overlap.
            if (checkEdge(listOf(t2[i], t2[j], t1[0])) &&
                checkEdge(listOf(t2[i], t2[j], t1[1])) &&
                checkEdge(listOf(t2[i], t2[j], t1[2]))) return false
        }

        // The triangles overlap
        return true
    }
}
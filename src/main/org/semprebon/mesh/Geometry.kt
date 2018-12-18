package org.semprebon.mesh

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D

object Geometry {
    val tolerance = 0.005

    fun center(vertices: List<Vector3D>): Vector3D {
        return vertices.reduce { a, b -> a.add(b) }.scalarMultiply(1.0/vertices.size)
    }

    fun determinant(vertices: List<Vector2D>) =
        vertices[0].x * (vertices[1].y - vertices[2].y) +
                vertices[1].x * (vertices[2].y - vertices[0].y) +
                vertices[2].x * (vertices[0].y - vertices[1].y)


    fun sign(p1: Vector2D, p2: Vector2D, p3: Vector2D): Double {
        return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y)
    }

    fun pointInTriangle(pt: Vector2D, tri: List<Vector2D>): Boolean {
        val d1: Double
        val d2: Double
        val d3: Double
        val hasNeg: Boolean
        val hasPos: Boolean

        d1 = sign(pt, tri[0], tri[1])
        d2 = sign(pt, tri[1], tri[2])
        d3 = sign(pt, tri[2], tri[0])

        hasNeg = d1 < 0 || d2 < 0 || d3 < 0
        hasPos = d1 > 0 || d2 > 0 || d3 > 0

        return !(hasNeg && hasPos)
    }

    fun checkEdge(triangle: List<Vector2D>) = determinant(triangle) <= tolerance

    fun triangleIncludes(t1: List<Vector2D>, p: Vector2D): Boolean {
        // for each edge E of t1
        for (i in 0 until 3) {
            val j = (i + 1) % 3
            if (checkEdge(listOf(t1[i], t1[j], p)) &&
                checkEdge(listOf(t1[i], t1[j], p)) &&
                checkEdge(listOf(t1[i], t1[j], p))) return false
        }
        return true
    }

    fun trianglesOverlap(t1: List<Vector2D>, t2: List<Vector2D>): Boolean {
        // for each edge E of t1
        for (i in 0 until 3) {
            val j = (i + 1) % 3
            // Check all points of t2 lay on the external side of edge E.
            // If they do, the faces do not overlap.
            if (checkEdge(listOf(t1[i], t1[j], t2[0])) &&
                checkEdge(listOf(t1[i], t1[j], t2[1])) &&
                checkEdge(listOf(t1[i], t1[j], t2[2]))) return false
        }

        // for each edge E of t2
        for (i in 0 until 3) {
            val j = (i + 1) % 3
            // Check all points of t1 lay on the external side of edge E.
            // If they do, the faces do not overlap.
            if (checkEdge(listOf(t2[i], t2[j], t1[0])) &&
                checkEdge(listOf(t2[i], t2[j], t1[1])) &&
                checkEdge(listOf(t2[i], t2[j], t1[2]))) return false
        }

        // The faces overlap
        return true
    }

    class Incrementer(val limit: Int) {
        fun next(i: Int) = (i + 1) % limit
        fun prev(i: Int) = (i - 1) % limit
    }


}
package org.semprebon.mesh

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D

object Conversion {
    fun to2d(p: Vector3D) = Vector2D(p.x, p.y)

    fun triangleTo2d(triangle: Mesh.Face) = triangle.vertices.map { p -> to2d(p) }

    fun to3d(p: Vector2D, z: Double = 0.0) = Vector3D(p.x, p.y, z)

//
//    fun findAdjacent(faces: List<Triangle>, polygon: List<Vector2D>)
//            : Triple<Int, Int, Vector2D?>? {
//        val triple = faces
//                .mapIndexed { i, t ->
//                    val (pi, v) = adjacency(polygon, t)
//                    Triple(pi, i, v) }
//                .filter { it -> it != null && it.first >= 0 }
//                .firstOrNull()
//        if (triple == null) {
//            System.out.println("Disjoint faces: " + faces);
//            System.out.println("Polyon thus far: " + polygon)
//        }
//        return triple
//    }
//
//    fun adjacency(polygon: List<Vector2D>, triangle : Triangle): Pair<Int, Vector2D?> {
//        val t = Conversion.to2d(triangle.vertices.asList())
//        val pair = polygon
//                .mapIndexed { i, p ->
//                    Pair(i, adjacency(Pair(p,polygon[(i+1)%polygon.size]), t)) }
//                .filter { it.second != null }
//                .firstOrNull()
//        return if (pair == null) Pair(-1, Vector2D(0.0,0.0)) else pair
//    }

    fun adjacency(edge: Pair<Vector2D, Vector2D>, triangle: List<Vector2D>) : Vector2D? {
        if (sameEdge(edge, Pair(triangle[0], triangle[1]))) return triangle[2]
        if (sameEdge(edge, Pair(triangle[1], triangle[2]))) return triangle[0]
        if (sameEdge(edge, Pair(triangle[2], triangle[0]))) return triangle[1]
        return null
    }

    fun nearTo(p1: Vector2D, p2: Vector2D) =
            nearTo(p1.x, p2.x) && nearTo(p1.y, p2.y)

    fun nearTo(a: Double, b: Double) = Math.abs(a - b) < 0.0001

//    fun sameEdge(e1: Pair<Vector2D,Vector2D>, e2: Pair<Vector2D, Vector2D>)
//                    = (e1 == e2) || (e1.first == e2.second && e1.second == e2.first)
    fun sameEdge(e1: Pair<Vector2D,Vector2D>, e2: Pair<Vector2D, Vector2D>)
            = (nearTo(e1.first, e2.first) && nearTo(e1.second, e2.second))
                || (nearTo(e1.first, e2.second) && nearTo(e1.second, e2.first))
}
package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Line
import org.apache.commons.math3.geometry.euclidean.threed.Plane
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet
import org.apache.commons.math3.geometry.euclidean.twod.SubLine
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import org.apache.commons.math3.geometry.partitioning.Region
import org.semprebon.mesh.Conversion

class Crease(val segment: List<Vector2D>, val tolerance: Double = 0.001): Remesher.PolygonSplitter {

    companion object {
        val edgeIndexes = (0..2).zip(listOf(1, 2, 0))

        fun nextIndex(i: Int) = if (i < 2) i+1 else 0
        fun prevIndex(i: Int) = if (i > 0) i-1 else 2
    }

    val line = SubLine(segment[0], segment[1], tolerance)

    fun isOutside(triangle: PolygonsSet, point: Vector2D) = triangle.checkPoint(point) == Region.Location.OUTSIDE

    fun edges(face: List<Vector2D>) = face.zip(face.drop(1) + face.take(1))

    fun intersects(edge: Pair<Vector2D, Vector2D>): Vector2D? =
        SubLine(edge.first, edge.second, tolerance).intersection(line, false)

    data class Intersection(val startIndex: Int, val endIndex: Int, val point: Vector2D?) {
        fun isNull() = point == null
        fun isValid() = point != null
    }

    override fun apply(face: List<Vector3D>): List<List<Vector3D>> {                        
        val face2D = face.map { Conversion.to2d(it) }
        val triangle = PolygonsSet(tolerance, *face2D.toTypedArray())
        val outsides = segment.map { isOutside(triangle, it) }

        val intersects = edgeIndexes
            .map { (i, j) -> Intersection(i, j, intersects(Pair(face2D[i], face2D[j]))) }

        if (outsides.all { it }) {
            if (intersectionCount(intersects) == 2) return mapToFace(face, bisect(face2D, intersects))
            else return listOf(face)
        } else return listOf(face)
    }

    private fun intersectionCount(intersections: List<Intersection>) = intersections.count(Intersection::isValid)

    private fun bisect(face: List<Vector2D>, intersects: List<Intersection>): List<List<Vector2D>> {
        val goodEdgeIndex = intersects.indexOfFirst(Intersection::isNull)
        val goodEdge
                = listOf(face[intersects[goodEdgeIndex].startIndex], face[intersects[goodEdgeIndex].endIndex])
        val goodVertexIndex = prevIndex(goodEdgeIndex)
        val intersectingEdge
                = listOf(intersects[nextIndex(goodEdgeIndex)].point!!, intersects[prevIndex(goodEdgeIndex)].point!!)
        val face1 = intersectingEdge.reversed() + listOf(face[goodVertexIndex])
        val face2 = intersectingEdge + listOf(face[goodEdgeIndex])
        val face3 = goodEdge + listOf(intersectingEdge.last())
        return listOf(face1, face2, face3)
    }

    inner class FaceTransform(val face: List<Vector3D>) {
        val face2d = face.map { Conversion.to2d(it) }
        val plane = Plane(face[0], face[1], face[2], tolerance)

        fun map(v: Vector2D): Vector3D {
            val i = face2d.indexOf(v)
            if (i >= 0) return face[i]
            else return plane.intersection(Line(Conversion.to3d(v), Conversion.to3d(v, 10000.0), tolerance))
        }
    }

    private fun mapToFace(face3D: List<Vector3D>, faces2D: List<List<Vector2D>>): List<List<Vector3D>> {
        val transform = FaceTransform(face3D)
        return faces2D.map { mapToFace(transform, it) }
    }

    private fun mapToFace(transform: FaceTransform, faces: List<Vector2D>) = faces.map { transform.map(it) }
}
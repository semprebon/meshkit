package org.semprebon.mesh.filters

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.geometry.euclidean.twod.Line
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import org.semprebon.mesh.Conversion
import java.util.function.Predicate
import kotlin.math.PI

class NearPolyline(val perimeter: List<Vector2D>, val distance: Double): Predicate<Vector3D> {

    data class SegmentZone(val start: Vector2D, val end: Vector2D, val distance: Double) {
        val line = Line(start, end, distance)
        val xRange: ClosedRange<Double> = start.x..end.x
        val yRange: ClosedRange<Double> = start.y..end.y

        fun projection(v: Vector2D) = Line(v, line.angle + PI/2.0, distance).intersection(line)

        fun isNear(v: Vector2D, distance: Double): Boolean {
            if (!line.contains(v)) return false
            if (start.distance(v) < distance || end.distance(v) < distance) return true

            val p = projection(v)
            return (p.x in xRange) && (p.y in yRange)
        }
    }

    val segmentZones = perimeter.dropLast(1).zip(perimeter.drop(1))
        .map { SegmentZone(it.first, it.second, distance) }

    override fun test(v: Vector3D): Boolean {
        return segmentZones.any { it.isNear(Conversion.to2d(v), distance) }
    }
}
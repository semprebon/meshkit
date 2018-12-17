package org.semprebon.mesh.filters

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import org.apache.commons.math3.geometry.partitioning.Region
import org.semprebon.mesh.Conversion
import java.util.function.Predicate

class InsideProjectedPolygon(polygon: List<Vector2D>, tolerance: Double): Predicate<Vector3D> {

    val polygonsSet = PolygonsSet(tolerance, *polygon.toTypedArray())

    override fun test(v: Vector3D): Boolean {
        return polygonsSet.checkPoint(Conversion.to2d(v)) != Region.Location.OUTSIDE
    }


}
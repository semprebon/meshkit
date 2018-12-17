package org.semprebon.mesh.filters

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import org.apache.commons.math3.geometry.euclidean.twod.hull.MonotoneChain
import org.apache.commons.math3.geometry.partitioning.Region
import org.semprebon.mesh.Conversion
import java.util.function.Predicate

class InsideProjectedHull(polygon: Collection<Vector2D>, tolerance: Double): Predicate<Vector3D> {
    val hull = MonotoneChain(false, tolerance).generate(polygon).createRegion()

    override fun test(v: Vector3D) = hull.checkPoint(Conversion.to2d(v)) == Region.Location.OUTSIDE
}
package org.semprebon.mesh.filters

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import org.semprebon.mesh.Mesh
import java.util.function.Predicate

class IntersectsPolyLine(polyLine: List<Vector2D>): Predicate<Mesh.Edge> {

    override fun test(t: Mesh.Edge): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
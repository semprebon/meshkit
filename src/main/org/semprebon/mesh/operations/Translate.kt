package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import java.util.function.Predicate
import java.util.function.UnaryOperator

/**
 * Apply a transform to the mesh
 */
class Translate(val offset: Vector3D): UnaryOperator<Vector3D> {

    override fun apply(vertex: Vector3D) = vertex.add(offset)

}
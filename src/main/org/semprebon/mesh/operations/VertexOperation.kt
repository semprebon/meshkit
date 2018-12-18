package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.semprebon.mesh.Mesh
import org.semprebon.mesh.filters.Util
import java.util.function.Predicate
import java.util.function.UnaryOperator

class VertexOperation(val operation: UnaryOperator<Vector3D>): UnaryOperator<Mesh> {

    override fun apply(mesh: Mesh): Mesh {
        val newVertices
                = mesh.vertices.map { operation.apply(it) }
        return Mesh(mesh.faces.map { face -> face.vIndexes.map { newVertices[it] } }, mesh.tolerance)
    }
}
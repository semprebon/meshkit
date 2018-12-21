package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.semprebon.mesh.FaceVertexMesh
import java.util.function.UnaryOperator

class VertexOperation(val operation: UnaryOperator<Vector3D>): UnaryOperator<FaceVertexMesh> {

    override fun apply(mesh: FaceVertexMesh): FaceVertexMesh {
        val newVertices
                = mesh.vertices.map { operation.apply(it) }
        return FaceVertexMesh(mesh.faces.map { face -> face.vIndexes.map { newVertices[it] } }, mesh.tolerance)
    }
}
package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.semprebon.mesh.Mesh

class SubdivideEdge: Remesher(NewEdgeSplitter()) {


    class NewEdgeSplitter: PolygonSplitter {
        var midpoints: List<Vector3D> = emptyList()

        override fun apply(t: List<Vector3D>): List<List<Vector3D>> {
            val newVertices = t.filter { midpoints.contains(it) }
            val center =
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    override fun apply(mesh: Mesh): Mesh {
        val newMesh = Mesh(mesh.faces.map(Mesh.Face::vertices), mesh.tolerance)

        (meshingAlgorithm as NewEdgeSplitter).midpoints = newMesh.edges().map { edge ->
            val v = edge.start.add(edge.end).scalarMultiply(0.5)!!
            mesh.splitEdge(edge, v)
            v
        }
        return super.apply(newMesh)
    }

}
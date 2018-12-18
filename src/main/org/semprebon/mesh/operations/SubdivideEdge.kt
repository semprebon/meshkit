package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.semprebon.mesh.Geometry
import org.semprebon.mesh.Mesh

class SubdivideEdge: Remesher(NewEdgeSplitter()) {


    class NewEdgeSplitter: FaceSplitter {
        var midpointIndexes: List<Int> = emptyList()
        var mesh: Mesh = Mesh()

        fun newFace(face: Mesh.Face, startVIndex: Int, center: Vector3D): List<Vector3D> {
            val inc = Geometry.Incrementer(face.vIndexes.size)
            return listOf(
                face.vertices[startVIndex],
                face.vertices[inc.next(startVIndex)],
                center,
                face.vertices[inc.prev(startVIndex)])
        }

        override fun apply(face: Mesh.Face): List<List<Vector3D>> {
            val mesh: Mesh = face.mesh()
            val newVIndexes: List<Int> = face.vIndexes.filter { vIndex -> midpointIndexes.contains(vIndex) }
            val center = Geometry.center(face.vertices)
            return face.vIndexes.mapIndexed { i, vIndex ->
                if (newVIndexes.contains(vIndex)) newFace(face, i, center) else emptyList()
            }.filter { !it.isEmpty() }
        }
    }

    override fun apply(mesh: Mesh): Mesh {
        val newMesh = Mesh(mesh.faces.map(Mesh.Face::vertices), mesh.tolerance)
        (meshingAlgorithm as NewEdgeSplitter).mesh = newMesh

        meshingAlgorithm.midpointIndexes = newMesh.edges().map { edge ->
            val v = edge.start.add(edge.end).scalarMultiply(0.5)!!
            newMesh.splitEdge(edge, v)
        }
        return super.apply(newMesh)
    }

}
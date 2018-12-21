package org.semprebon.mesh

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D

class SplitEdgeMesh : IMesh {
    var vertices = ArrayList<Vertex>()
    var edges = ArrayList<Edge>()

    inner class Edge(var startIndex: Int, var oppositeIndex: Int, var faceIndex: Int, var nextIndex: Int) {
        fun faceIndexes() = listOf(faceIndex, edges[oppositeIndex].faceIndex)
        fun vertexIndexes() = listOf(startIndex, edges[nextIndex].startIndex)
    }

    inner class Vertex(var point: Vector3D, var firstEdgeIndex: Int) {
        fun faceIndexes(): List<Int> {
            return ArrayList<Int>().also { result ->
                forEachEdgeFrom(firstEdgeIndex) { result.add(it.faceIndex) }
            }
        }

        fun edgeIndexes(): List<Int> {
            return ArrayList<Int>().also { result ->
                forEachEdgeIndexFrom(firstEdgeIndex) { result.add(it) }
            }
        }
    }

    inner class Face(var firstEdgeIndex: Int) {
        fun edgeIndexes(): List<Int> {
            return ArrayList<Int>().also { result ->
                forEachEdgeIndexFrom(firstEdgeIndex) { result.add(it) }
            }
        }

        fun vertexIndexes(): List<Int> {
            return ArrayList<Int>().also { result ->
                forEachEdgeFrom(firstEdgeIndex) { result.add(it.startIndex) }
            }
        }
    }

    private fun forEachEdgeIndexFrom(firstEdgeIndex: Int, consumer: (Int) -> Unit) {
        val result = ArrayList<Int>()
        var i = firstEdgeIndex
        do {
            consumer(i)
            i = edges[edges[i].oppositeIndex].nextIndex
        } while (i != firstEdgeIndex)
    }

    private fun forEachEdgeFrom(firstEdgeIndex: Int, consumer: (Edge) -> Unit) {
        forEachEdgeIndexFrom(firstEdgeIndex) { consumer(edges[it]) }
    }



    override fun vertices(): ArrayList<Vector3D> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun faces(): ArrayList<FaceVertexMesh.Face> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun indexOf(v: Vector3D): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun edges(): Collection<FaceVertexMesh.Edge> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun perimeter(): List<Vector3D> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun extractConnectedSubmesh(remainingVertices: MutableList<Vector3D>): FaceVertexMesh {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun separate(): List<FaceVertexMesh> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
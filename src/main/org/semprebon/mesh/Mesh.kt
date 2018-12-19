package org.semprebon.mesh

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import java.lang.IllegalArgumentException
import kotlin.math.min

class Mesh(val tolerance: Double) {
    val vertices = ArrayList<Vector3D>()
    val faces = ArrayList<Face>()

    constructor() : this(DEFAULT_TOLERANCE)

    constructor(vs: List<Vector3D>, fs: List<Face>, tolerance: Double): this(tolerance) {
        vertices.addAll(vs)
        fs.map { Face(it.vIndexes) }
    }

    constructor(polygons: List<List<Vector3D>>) : this(polygons, DEFAULT_TOLERANCE)
    constructor(polygons: List<List<Vector3D>>, tolerance: Double) : this(tolerance) { polygons.forEach { Face(it) } }

    companion object {
        val DEFAULT_TOLERANCE = 0.001
        
        fun signedArea(vertices: List<Vector3D>, normal: Vector3D) =
            vertices.zip(vertices.drop(1) + vertices.take(1))
                .map { (v1, v2) -> v1.crossProduct(v2) }
                .reduce { acc, v -> acc.add(v) }
                .dotProduct(normal) / 2.0
    }

    fun indexOf(v: Vector3D) : Int {
        return vertices.indexOf(v)
    }

    fun assureIndexOf(v: Vector3D): Int {
        if (!vertices.contains(v)) vertices.add(v)
        return indexOf(v)
    }

    fun edges(): Collection<Edge> = faces.flatMap { it.edges() }.map { it.canonical() }.distinct()

    fun add(vararg polygon: Vector3D): Face {
        if (polygon.size <= 2) throw IllegalArgumentException("Polygon ${polygon} has too few sides")
        return Face(polygon.asList())
    }

    /**
     * Add a new vertex on an edge. This increases the number of vertices of
     * both adjoining faces by one
     *
     * This does not preserve triangularity.
     *
     * @param edge edge to be split
     * @param newVertex vertex to add. This should be close to lying on the existing edge
     */
    fun splitEdge(edge: Edge, newVertex: Vector3D): Int {
        fun splitEdgeOnFace(faceIndex: Int, edge: Edge, vertex: Int) {
            val face = faces[faceIndex]
            val index = if (face.edges().contains(edge)) edge.startIndex else edge.endIndex
            val newVIndexes = face.vIndexes.take(index+1) + listOf(vertex) + face.vIndexes.drop(index+1)
            faces[faceIndex] = Face(newVIndexes.toTypedArray())
         }
        val faceIndexPair = edge.faceIndexes()
        val vertexIndex = assureIndexOf(newVertex)
        if (faceIndexPair.first != null) {
            splitEdgeOnFace(faceIndexPair.first!!, edge, vertexIndex)
        }
        if (faceIndexPair.second != null) {
            splitEdgeOnFace(faceIndexPair.second!!, edge, vertexIndex)
        }
        return vertexIndex
    }

    inner class Edge(val startIndex: Int, val endIndex: Int) {
        val start = vertices[startIndex]
        val end = vertices[endIndex]

        fun swap() = Edge(endIndex, startIndex)

        fun length() = this.start.distance(this.end)

        fun isOpposite(other: Edge) = (start == other.end) && (end == other.start)

        fun isCoincident(other: Edge) = isOpposite(other) || ((start == other.start) && (end == other.end))

        fun faceIndexes(): Pair<Int?, Int?> {
            val matches = faces.mapIndexed { idx, face -> Pair(idx, face) }
                .filter { it.second.edges().any { this.isCoincident(it) } }
                .map { it.first }
            val left = matches.firstOrNull()
            val right = matches.drop(1).firstOrNull()
            return Pair(left, right)
        }

        fun isCanonical() = startIndex < endIndex

        fun canonical() = if (isCanonical()) this else swap()
        
        fun isOnPerimeter() = faceIndexes().second == null

        fun isReference(index: Int) = faceIndexes().first == index

        fun referenceFace() = faces[faceIndexes().first!!]

        fun hasPointIndex(i: Int) = startIndex == i || endIndex == i

        override fun toString() = "(${startIndex}->${endIndex})"

        override fun equals(other: Any?): Boolean {
            if (other == null) return false
            if (other === this) return true
            if (other.javaClass !== javaClass) return false
            val rhs = other as Edge
            val e = EqualsBuilder()
                    .append(startIndex, rhs.startIndex)
                    .append(endIndex, rhs.endIndex)
                    .isEquals()
            return e
        }

        override fun hashCode(): Int { return HashCodeBuilder(56339, 804733)
                .append(startIndex).append(endIndex).toHashCode()
        }
    }


    /**
     * Represents a face in the mesh.
     *
     * By convention, face vertices are oriented so that they go in a counter-clockwise
     * direction relative to the face normal
     */
    inner class Face(var vIndexes: Array<Int>) {
        constructor(vertices: List<Vector3D>) : this(vertices.map { assureIndexOf(it) }.toTypedArray()) {
            faces.add(this)
        }

        constructor(vararg vertices: Vector3D): this(vertices.asList())

        var vertices: List<Vector3D>
                get() = vIndexes.map { this@Mesh.vertices[it] }
                set(value) {
                    vIndexes.zip(value)
                            .forEach { (i: Int, v: Vector3D) -> this@Mesh.vertices[i] = v }
                }

        val normal: Vector3D
                get() = vertices[0].crossProduct(vertices[1]).normalize()

        fun edges(): Collection<Edge> =
                vIndexes.zip(vIndexes.drop(1) + vIndexes.take(1)).map { Edge(it.first, it.second) }

        fun mesh() = this@Mesh
    }

    /**
     * Find the remaining perimeter of the mesh starting at the given point, pulling from the specified edges
     */
    fun remainingPerimeterFor(vIndex: Int, edges: MutableCollection<Edge>): List<Vector3D> {
        if (edges.size == 0) return emptyList()

        val nextEdge = edges.find { it.hasPointIndex(vIndex) } ?: return emptyList()

        edges.remove(nextEdge)
        val nextPoint = if (nextEdge.startIndex == vIndex) nextEdge.endIndex else nextEdge.startIndex
        return listOf(vertices[nextPoint]) + remainingPerimeterFor(nextPoint, edges)
    }

    /**
     * Find one perimeter of the mesh
     */
    fun perimeter(): List<Vector3D> {
        val result = ArrayList<Vector3D>()
        val edges = ArrayList<Edge>(edges().filter { it.isOnPerimeter() })
        if (edges.isEmpty()) return result

        var index = edges.first().startIndex

        while (edges.size > 0) {
            val nextEdge = edges.find { it.hasPointIndex(index) } ?: return result

            result.add(vertices[index])
            edges.remove(nextEdge)
            index = if (nextEdge.startIndex == index) nextEdge.endIndex else nextEdge.startIndex
        }
        return result
    }

    fun extractConnectedSubmesh(remainingVertices: MutableList<Vector3D>): Mesh {
        val workList = ArrayList<Vector3D>()
        val resultVertices = ArrayList<Vector3D>()

        workList.add(remainingVertices[0])
        while (!workList.isEmpty()) {
            val next = workList.removeAt(0)
            remainingVertices.remove(next)
            resultVertices.add(next)

            val adjacentVertices = faces
                .filter { it.vertices.contains(next) }
                .flatMap { it.vertices }
                .filter { remainingVertices.contains(it) }
            workList.addAll(adjacentVertices)
        }

        val newFaces = faces
            .filter { resultVertices.contains(it.vertices.first()) }
            .map { it.vertices }
        return Mesh(newFaces)
    }

    /**
     * seperate mesh into connected submeshes
     */
    fun separate(): List<Mesh> {
        val result = ArrayList<Mesh>()
        val remainingVertices = vertices.toMutableList()
        while (!remainingVertices.isEmpty()) {
            result.add(extractConnectedSubmesh(remainingVertices))
        }
        return result
    }
}
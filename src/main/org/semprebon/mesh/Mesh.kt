package org.semprebon.mesh

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.geometry.euclidean.twod.Segment
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D

class Mesh(val vertices: MutableList<Vector3D>, val faces: MutableList<Face>) {

    constructor() : this(ArrayList(), ArrayList())

    companion object {
        val tolerance = 0.005

        fun determinant(vertices: List<Vector2D>) =
                vertices[0].x * (vertices[1].y - vertices[2].y) +
                        vertices[1].x * (vertices[2].y - vertices[0].y) +
                        vertices[2].x * (vertices[0].y - vertices[1].y)

        fun sign(p1: Vector2D, p2: Vector2D, p3: Vector2D): Double {
            return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y)
        }

        fun PointInTriangle(pt: Vector2D, tri: List<Vector2D>): Boolean {
            val d1: Double
            val d2: Double
            val d3: Double
            val has_neg: Boolean
            val has_pos: Boolean

            d1 = sign(pt, tri[0], tri[1])
            d2 = sign(pt, tri[1], tri[2])
            d3 = sign(pt, tri[2], tri[0])

            has_neg = d1 < 0 || d2 < 0 || d3 < 0
            has_pos = d1 > 0 || d2 > 0 || d3 > 0

            return !(has_neg && has_pos)
        }

        fun checkEdge(triangle: List<Vector2D>) = determinant(triangle) <= tolerance

        fun triangleIncludes(t1: List<Vector2D>, p: Vector2D): Boolean {
            // for each edge E of t1
            for (i in 0 until 3) {
                val j = (i + 1) % 3
                if (checkEdge(listOf(t1[i], t1[j], p)) &&
                        checkEdge(listOf(t1[i], t1[j], p)) &&
                        checkEdge(listOf(t1[i], t1[j], p))) return false
            }
            return true
        }

        fun trianglesOverlap(t1: List<Vector2D>, t2: List<Vector2D>): Boolean {
            // for each edge E of t1
            for (i in 0 until 3) {
                val j = (i + 1) % 3
                // Check all points of t2 lay on the external side of edge E.
                // If they do, the faces do not overlap.
                if (checkEdge(listOf(t1[i], t1[j], t2[0])) &&
                        checkEdge(listOf(t1[i], t1[j], t2[1])) &&
                        checkEdge(listOf(t1[i], t1[j], t2[2]))) return false
            }

            // for each edge E of t2
            for (i in 0 until 3) {
                val j = (i + 1) % 3
                // Check all points of t1 lay on the external side of edge E.
                // If they do, the faces do not overlap.
                if (checkEdge(listOf(t2[i], t2[j], t1[0])) &&
                        checkEdge(listOf(t2[i], t2[j], t1[1])) &&
                        checkEdge(listOf(t2[i], t2[j], t1[2]))) return false
            }

            // The faces overlap
            return true
        }
    }

    fun indexOf(v: Vector3D) : Int {
        return vertices.indexOf(v);
    }

    fun assureIndexOf(v: Vector3D): Int {
        if (!vertices.contains(v)) vertices.add(v)
        return indexOf(v)
    }

    fun edges(): Collection<Edge> = faces.flatMap { it.edges() }.map { it.canonical() }.distinct()

    fun add(polygon: List<Vector3D>): Face {
        return Face(polygon)
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

        override fun equals(obj: Any?): Boolean {
            if (obj == null) return false
            if (obj === this) return true
            if (obj.javaClass !== javaClass) return false
            val rhs = obj as Edge
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

    inner class Face(var vIndexes: Array<Int>) {
        constructor(vertices: List<Vector3D>) : this(vertices.map { assureIndexOf(it) }.toTypedArray()) {
            faces.add(this)
        }

        constructor(vararg vertices: Vector3D): this(vertices.asList())

        var vertices: List<Vector3D>
                get() = vIndexes.map { this@Mesh.vertices[it] }
                set(value) {
                    vIndexes.zip(value)
                            .forEach { (i: Int, v: Vector3D) -> this@Mesh.vertices.set(i, v) }
                }

        val normal: Vector3D
                get() = vertices[0].crossProduct(vertices[1]).normalize()

        fun edges(): Collection<Edge> =
                vIndexes.zip(vIndexes.drop(1) + vIndexes.take(1)).map { Edge(it.first, it.second) }
    }

    /**
     * Find the remaining perimeter of the mesh starting at the given point, pulling from the specified edges
     */
    fun remainingPerimeterFor(vIndex: Int, edges: MutableCollection<Edge>): List<Vector3D> {
        if (edges.size == 0) return emptyList()

        val nextEdge = edges.find { it.hasPointIndex(vIndex) }
        if (nextEdge == null) return emptyList()

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
            val nextEdge = edges.find { it.hasPointIndex(index) }
            if (nextEdge == null) return result

            edges.remove(nextEdge)
            index = if (nextEdge.startIndex == index) nextEdge.endIndex else nextEdge.startIndex
        }
        return result
    }
}
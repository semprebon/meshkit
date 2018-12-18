package org.semprebon.mesh.operations

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.semprebon.mesh.Mesh
import java.util.function.Function
import java.util.function.Predicate

/**
 * Restructures the mesh, replacing existing vertices with new vertices
 */
open class Remesher(val meshingAlgorithm: PolygonSplitter) {

    interface PolygonSplitter: Function<List<Vector3D>, List<List<Vector3D>>>

    companion object {
        fun filteredRemesher(predicate: Predicate<List<Vector3D>>, remesher: PolygonSplitter): PolygonSplitter {
            return object : PolygonSplitter {
                override fun apply(polygon: List<Vector3D>): List<List<Vector3D>> {
                    return if (predicate.test(polygon)) remesher.apply(polygon) else listOf(polygon)
                }
            }
        }
    }

    open fun apply(mesh: Mesh): Mesh {
        val newFaces = mesh.faces.flatMap { meshingAlgorithm.apply(it.vertices) }
        return Mesh(newFaces)
    }
}
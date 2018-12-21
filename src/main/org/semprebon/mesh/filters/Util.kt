package org.semprebon.mesh.filters

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.semprebon.mesh.FaceVertexMesh
import java.util.function.Predicate

object Util {

    fun <T> allPredicate() = Predicate<T> { v -> true }

    val ALL_VERTICES = allPredicate<Vector3D>()
    val ALL_FACES = allPredicate<FaceVertexMesh.Face>()

    fun allVerticesOfFace(predicate: Predicate<Vector3D>) = object: Predicate<FaceVertexMesh.Face> {
        override fun test(face: FaceVertexMesh.Face) = face.vertices.all(predicate::test)
    }

    fun anyVerticesOfFace(predicate: Predicate<Vector3D>) = object: Predicate<FaceVertexMesh.Face> {
        override fun test(face: FaceVertexMesh.Face) = face.vertices.any(predicate::test)
    }

    fun eitherFaceOfEdge(predicate: Predicate<FaceVertexMesh.Face>) = object: Predicate<FaceVertexMesh.Edge> {
        override fun test(edge: FaceVertexMesh.Edge): Boolean = edge.faces().any { predicate.test(it) }
    }

    fun createFacePredicateFor(predicate: Predicate<List<Vector3D>>)
        = object: Predicate<FaceVertexMesh.Face> { override fun test(face: FaceVertexMesh.Face) = predicate.test(face.vertices) }

}
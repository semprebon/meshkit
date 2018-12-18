package org.semprebon.mesh.filters

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.semprebon.mesh.Mesh
import java.util.function.Predicate

object Util {

    fun <T> allPredicate() = Predicate<T> { v -> true }

    val ALL_VERTICES = allPredicate<Vector3D>()

    fun allVerticesOfFace(predicate: Predicate<Vector3D>) = object: Predicate<Mesh.Face> {
        override fun test(face: Mesh.Face) = face.vertices.all(predicate::test)
    }

    fun anyVerticesOfFace(predicate: Predicate<Vector3D>) = object: Predicate<Mesh.Face> {
        override fun test(face: Mesh.Face) = face.vertices.any(predicate::test)
    }

    fun createFacePredicateFor(predicate: Predicate<List<Vector3D>>)
        = object: Predicate<Mesh.Face> { override fun test(face: Mesh.Face) = predicate.test(face.vertices) }

}
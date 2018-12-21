package org.semprebon.mesh

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D

interface IMesh {
    fun vertices(): ArrayList<Vector3D>
    fun faces(): ArrayList<FaceVertexMesh.Face>
    fun indexOf(v: Vector3D) : Int
    fun edges(): Collection<FaceVertexMesh.Edge>
    /**
     * Find one perimeter of the mesh
     */
    fun perimeter(): List<Vector3D>

    fun extractConnectedSubmesh(remainingVertices: MutableList<Vector3D>): FaceVertexMesh
    /**
     * seperate mesh into connected submeshes
     */
    fun separate(): List<FaceVertexMesh>

}
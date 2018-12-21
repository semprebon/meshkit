package org.semprebon.stl

import org.apache.commons.io.EndianUtils
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.semprebon.mesh.FaceVertexMesh
import java.io.FileOutputStream
import java.io.OutputStream

class STLWriter(val filename: String) {

    fun write(mesh: FaceVertexMesh) {
        val file = FileOutputStream(filename)
        writeHeader(file)
        EndianUtils.writeSwappedInteger(file, mesh.faces.size)
        for (triangle in mesh.faces) {
            writePoint(file, triangle.normal)
            for (vertex in triangle.vertices) {
                writePoint(file, vertex)
            }
            EndianUtils.writeSwappedShort(file, 0)
        }
        file.close()
        println("FaceVertexMesh")
    }

    fun writeHeader(out: OutputStream) {
        for (i in 0..79) out.write(0)
    }

    fun writePoint(out: OutputStream, point: Vector3D) {
        EndianUtils.writeSwappedFloat(out, point.x.toFloat())
        EndianUtils.writeSwappedFloat(out, point.y.toFloat())
        EndianUtils.writeSwappedFloat(out, point.z.toFloat())
    }
}
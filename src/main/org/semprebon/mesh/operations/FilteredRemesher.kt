package org.semprebon.mesh.operations

import org.semprebon.mesh.FaceVertexMesh
import java.util.function.Predicate

class FilteredRemesher(meshingAlgorithm: Remesher.FaceSplitter, filter: Predicate<FaceVertexMesh.Face>)
        : Remesher(meshingAlgorithm) {

}
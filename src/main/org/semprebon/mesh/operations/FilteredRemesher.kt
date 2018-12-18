package org.semprebon.mesh.operations

import org.semprebon.mesh.Mesh
import java.util.function.Predicate

class FilteredRemesher(meshingAlgorithm: Remesher.FaceSplitter, filter: Predicate<Mesh.Face>)
        : Remesher(meshingAlgorithm) {

}
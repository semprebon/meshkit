package org.semprebon.stl

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.assertEquals
import java.nio.file.FileSystems

class ParserTest {
    companion object {

    }

    class `parseSTLFile`() {
        @Test
        fun `with binary STL file`() {
            val mesh = Parser().parse(FileSystems.getDefault().getPath("resources/house_binary.stl"))
            assertEquals(16, mesh.faces.size)
            assertEquals(10, mesh.vertices.size)
        }
    }
}
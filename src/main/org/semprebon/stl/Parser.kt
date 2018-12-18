package org.semprebon.stl

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.semprebon.mesh.Mesh
import java.io.DataInputStream
import java.io.FileInputStream
import java.io.FileReader
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.file.Path
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Parse an STL file. Based on a java STL parser by cyanobacteruim / Christopher Collin Hall and Andrew Goh
 */
class Parser {
    companion object {
        val logger = Logger.getLogger(Parser::class.java.name)
        val DELIMS = Regex(" \t\n")
    }

    /**
     * Parses an STL file, attempting to automatically detect whether the file
     * is an ASCII or binary STL file
     * @param filepath The file to parse
     * @return A list of faces representing all of the faces in the STL
     * file.
     * @throws IOException Thrown if there was a problem reading the file
     * (typically means the file does not exist or is not a file).
     * @throws IllegalArgumentException Thrown if the STL is not properly
     * formatted
     */
    @Throws(IOException::class)
    fun parse(filepath: Path): Mesh {
        val parser = BinaryParser(filepath)
        return parser.parse()
    }

    /**
     * Determine if the file is ascii or binary STL. Ascii STL should start with the word
     * "SOLID" (or "solid")
     */
    private fun isAscii(filepath: Path): Boolean {
        val reader = FileReader(filepath.toString())
        val buffer = CharArray(100)
        val charCount = reader.read(buffer, 0, 80)
        buffer.fill(' ', charCount)
        return buffer.joinToString().replace(DELIMS, "").startsWith("solid", true)
    }

    fun readblock(allBytes: ByteArray, offset: Int, length: Int): String {
        var length = length
        if (allBytes.size - offset < length) length = allBytes.size - offset
        val charset = Charset.forName("UTF-8")
        val decode = charset.decode(ByteBuffer.wrap(allBytes, offset, length))
        return decode.toString().toLowerCase()
    }

    fun readline(buf: String, sb: StringBuffer, offset: Int): Int {
        val il = buf.indexOf('\n', offset)
        if (il > -1)
            sb.append(buf.substring(offset, il - 1))
        else
            sb.append(buf.substring(offset))
        return il
    }

//    @Throws(IllegalArgumentException::class)
//    fun isbinaryfile(allBytes: ByteArray): Boolean {
//        if (allBytes.size < 84)
//            throw IllegalArgumentException("invalid binary file, length<84")
//        val numtriangles = byteatoint(Arrays.copyOfRange(allBytes, 80, 84))
//        if (allBytes.size >= 84 + numtriangles * 50)
//            return true //is binary file
//        else {
//            val msg = "invalid binary file, num faces does not match length specs"
//            throw IllegalArgumentException(msg)
//        }
//    }

//    /**
//     * Reads an STL ASCII file content provided as a String
//     * @param content ASCII STL
//     * @return A list of faces representing all of the faces in the STL
//     * file.
//     * @throws IllegalArgumentException Thrown if the STL is not properly
//     * formatted
//     */
//    fun readASCII(filepath: Path, mesh: Mesh) {
//        val tokenizer = StreamTokenizer(BufferedReader(FileReader(filepath.toString())))
//        do {
//
//        } while (tokenizer.ttype != StreamTokenizer.TT_EOF)
//
//        Logger.getLogger(STLParser::class.java!!.getName()).log(Level.FINEST, "Parsing ASCII STL format")
//        // string is lowercase
//        val faces = ArrayList<Triangle>()
//
//        var position = 0
//        scan@ run {
//            while ((position < content.length) and (position >= 0)) {
//                position = content.assureIndexOf("facet", position)
//                if (position < 0) {
//                    break@scan
//                }
//                try {
//                    val vertices = arrayOfNulls<Vector3D>(3)
//                    for (v in vertices.indices) {
//                        position = content.assureIndexOf("vertex", position) + "vertex".length
//                        while (Character.isWhitespace(content[position])) {
//                            position++
//                        }
//                        var nextSpace: Int
//                        val vals = DoubleArray(3)
//                        for (d in vals.indices) {
//                            nextSpace = position + 1
//                            while (!Character.isWhitespace(content[nextSpace])) {
//                                nextSpace++
//                            }
//                            val value = content.substring(position, nextSpace)
//                            vals[d] = java.lang.Double.parseDouble(value)
//                            position = nextSpace
//                            while (Character.isWhitespace(content[position])) {
//                                position++
//                            }
//                        }
//                        vertices[v] = Vector3D(vals[0], vals[1], vals[2])
//                    }
//                    position = content.assureIndexOf("endfacet", position) + "endfacet".length
//                    faces.add(Triangle(vertices[0], vertices[1], vertices[2]))
//                } catch (ex: Exception) {
//                    var back = position - 128
//                    if (back < 0) {
//                        back = 0
//                    }
//                    var forward = position + 128
//                    if (position > content.length) {
//                        forward = content.length
//                    }
//                    throw IllegalArgumentException(
//                        "Malformed STL syntax near \"" + content.substring(
//                            back,
//                            forward
//                        ) + "\"", ex
//                    )
//                }
//
//            }
//        }
//
//        return faces
//    }
//
//
    /**
     * Parses binary STL file
     * @param filepath file
     */
    class BinaryParser(filepath: Path) {
        val input = DataInputStream(FileInputStream(filepath.toString()))

        fun parse(): Mesh {
            logger.log(Level.FINEST, "Parsing binary STL format")
            val mesh = Mesh()

                // skip the header
                readHeader()

                val numberTriangles = readInteger()

                // read faces
                while (input.available() > 0) {
                    val normal = readVector()
                    mesh.add(*readTriangle())
                    readAttributeByteCount()
                }
            return mesh
        }

        private fun readHeader() {
            val header = ByteArray(80)
            input.read(header)
        }

        private fun readInteger(): Int {
            return Integer.reverseBytes(input.readInt())
        }

        private fun readReal(): Double {
            return java.lang.Float.intBitsToFloat(Integer.reverseBytes(input.readInt())).toDouble()
        }

        private fun readVector(): Vector3D {
            val p1 = readReal()
            val p2 = readReal()
            val p3 = readReal()
            return Vector3D(p1, p2, p3)
        }

        private fun readTriangle(): Array<Vector3D> {
            return arrayOf(readVector(), readVector(), readVector())
        }

        private fun readAttributeByteCount(): Short {
            return java.lang.Short.reverseBytes(input.readShort())
        }
    }
}
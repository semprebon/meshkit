package org.semprebon.mesh

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.geometry.euclidean.twod.Segment
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Path2D
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.io.BufferedWriter
import java.io.File
import javax.imageio.ImageIO

object MeshVisualizer {
    var out: BufferedWriter? = null
    var image : BufferedImage? = null

    val width = 500
    val height = 500

    fun start(filename: String, process: () -> Unit) {
        image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        process.invoke  ()
        ImageIO.write(image, "PNG", File(filename));
    }

    fun map(coord: Double) : Int = coord.toInt()
    fun map(point: Vector2D) = Pair(map(point.x), map(point.y))
    fun map(point: Vector3D) = Pair(map(point.x), map(point.y))

    fun map(edge: Mesh.Edge) : Pair<Pair<Int, Int>,Pair<Int,Int>>
            = Pair(map(edge.start), map(edge.end))

    fun setupGraphics(img: BufferedImage, min: Vector2D, max: Vector2D) : Graphics2D {
        val graphics = img.createGraphics()
        graphics.paint = Color.LIGHT_GRAY
        graphics.fillRect(0, 0, width, height)

        graphics.paint = Color.BLACK
        graphics.drawString("min=" + min + " max=" + max, 0, 440)
        val scale = Math.max(max.x + min.x, (max.y + min.y))
        //graphics.scale(10.0*width/scale, -10.0*height/scale)
        graphics.scale(0.8*width/(max.x-min.x), -0.8*height/(max.y-min.y))
        graphics.translate(-min.x/0.8, -max.y/0.8)
        val points = doubleArrayOf(min.x, min.y, max.x, max.y)
        val newPoints = doubleArrayOf(0.0, 0.0, 0.0, 0.0)
        graphics.transform.transform(points, 0, newPoints, 0, 2)
        graphics.paint = Color.WHITE
        graphics.draw(Rectangle2D.Double(min.x, min.y, max.x-min.x, max.y-min.y))

        // draw axes
        //graphics.setStroke(BasicStroke((2*(max.x-min.x)/width).toFloat()))
        graphics.setStroke(BasicStroke(0.01f))
        graphics.paint = Color.RED
        graphics.drawLine(map(min.x), 0, map(max.x), 0)
        graphics.paint = Color.GREEN
        graphics.drawLine(0, map(min.y), 0, map(max.y))
        return graphics
    }

    fun limits(mesh: Mesh) : Pair<Vector2D, Vector2D> {
        val (xs, ys) = mesh.vertices.map { Pair(it.x, it.y) }.unzip()
        return Pair(Vector2D(xs.min()!!, ys.min()!!), Vector2D(xs.max()!!, ys.max()!!))
    }

    fun visualize(mesh: Mesh) {
        val img = image
        if (img == null) return
        val (min, max) = limits(mesh)
        val graphics = setupGraphics(img, min, max)

        graphics.paint = Color.BLUE
        mesh.edges().forEach { edge ->
            val (p1, p2)= map(edge)
            graphics.drawLine(p1.first, p1.second, p2.first, p2.second)
        }
    }
}

package site.siredvin.datafortress.subsystems.webserver

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.loader.FileLoader
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator

/**
 * So, this code exists only for one reason:
 * to actually test web pages without full minecraft reload
 */
fun main() {
    val loader = FileLoader()
    loader.prefix = "./projects/core/src/main/resources/"
    val engine = PebbleEngine.Builder().loader(loader).cacheActive(false).templateCache(null).tagCache(null).build()
    WebserverMain.main(7000, engine)
    LinearInterpolator()
}

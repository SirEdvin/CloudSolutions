package site.siredvin.datafortress.subsystems.webserver

import com.mitchellbosecke.pebble.PebbleEngine
import io.javalin.Javalin
import io.javalin.rendering.template.JavalinPebble

object WebserverMain {
    @JvmStatic
    fun main(port: Int, pebbleEngine: PebbleEngine? = null) {
        JavalinPebble.init(pebbleEngine = pebbleEngine)
        val app = Javalin.create().start(port)

        app.get("/") {
                ctx ->
            ctx.render(
                "data/datafortress/hello.peb",
                mapOf(),
            )
        }
    }
}

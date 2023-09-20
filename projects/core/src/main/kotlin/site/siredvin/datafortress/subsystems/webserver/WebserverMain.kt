package site.siredvin.datafortress.subsystems.webserver

import com.google.gson.GsonBuilder
import com.mitchellbosecke.pebble.PebbleEngine
import io.javalin.Javalin
import io.javalin.json.JsonMapper
import io.javalin.rendering.template.JavalinPebble
import site.siredvin.datafortress.subsystems.tsdb.dummy.TSDBDummyManager
import java.lang.reflect.Type
import java.time.Instant
import java.util.UUID

object WebserverMain {
    @JvmStatic
    fun main(port: Int, pebbleEngine: PebbleEngine? = null) {
        JavalinPebble.init(pebbleEngine = pebbleEngine)
        val gson = GsonBuilder().create()

        val gsonMapper = object : JsonMapper {

            override fun <T : Any> fromJsonString(json: String, targetType: Type): T =
                gson.fromJson(json, targetType)

            override fun toJsonString(obj: Any, type: Type) =
                gson.toJson(obj)
        }

        val app = Javalin.create { it.jsonMapper(gsonMapper) }.start(port)

        app.get("/") { ctx ->
            ctx.render(
                "data/datafortress/hello.peb",
                mapOf(),
            )
        }

        app.get("/data") { ctx ->
            val shiftToPast = ctx.queryParamAsClass("shift", Long::class.java).get()
            ctx.json(
                TSDBDummyManager.query(
                    UUID(0, 0).toString(),
                    "t*",
                    emptyMap(),
                    Instant.now().minusSeconds(shiftToPast),
                    Instant.now(),
                ).map { it.toJson() },
            )
        }
    }
}

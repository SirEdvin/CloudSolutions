package site.siredvin.datafortress.subsystems.webserver

import be.ceau.chart.ScatterLineChart
import be.ceau.chart.color.Color
import be.ceau.chart.data.ScatterLineData
import be.ceau.chart.datapoint.ScatterDataPoint
import be.ceau.chart.dataset.ScatterLineDataset
import be.ceau.chart.enums.ScalesPosition
import be.ceau.chart.options.LineOptions
import be.ceau.chart.options.elements.Fill
import be.ceau.chart.options.scales.LinearScale
import be.ceau.chart.options.scales.LinearScales
import com.mitchellbosecke.pebble.PebbleEngine
import io.javalin.Javalin
import io.javalin.rendering.template.JavalinPebble

object WebserverMain {
    @JvmStatic
    fun main(port: Int, pebbleEngine: PebbleEngine? = null) {
        JavalinPebble.init(pebbleEngine = pebbleEngine)
        val app = Javalin.create().start(port)

        val lineDataset = ScatterLineDataset()
            .setData(
                (0..10).map { ScatterDataPoint(it, it) },
            ).setLabel("Test1").setFill(Fill<Boolean>(false))
            .setBorderColor(Color.AQUA)

        val lineData = ScatterLineData().addDataset(lineDataset)

        val lineChart = ScatterLineChart(lineData).setOptions(
            LineOptions().setScales(
                LinearScales().addxAxis(
                    LinearScale().setPosition(ScalesPosition.BOTTOM),
                ).addyAxis(LinearScale().setPosition(ScalesPosition.LEFT)),
            ),
        )

        app.get("/") {
                ctx ->
            ctx.render(
                "data/datafortress/hello.peb",
                mapOf(
                    "lineChart" to lineChart.toJson(),
                ),
            )
        }
    }
}

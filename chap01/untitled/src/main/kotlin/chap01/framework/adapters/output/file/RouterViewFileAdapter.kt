package chap01.framework.adapters.output.file

import chap01.application.ports.output.RouterViewOutputPort
import chap01.domain.Router
import chap01.domain.RouterId
import chap01.domain.RouterType
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader


class RouterViewFileAdapter private constructor() : RouterViewOutputPort {
    override fun fetchRouters(): List<Router> {
        return readFileAsString()
    }

    companion object {
        var instance = RouterViewFileAdapter()
            private set

        private fun readFileAsString(): List<Router> {
            val routers = mutableListOf<Router>()
            try {
                BufferedReader(
                    InputStreamReader(
                        RouterViewFileAdapter::class.java.getClassLoader().getResourceAsStream("routers.txt") as InputStream
                    )
                ).lines().use { stream ->
                    stream.forEach { line: String ->
                        val routerEntry =
                            line.split(";".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        val id = routerEntry[0]
                        val type = routerEntry[1]
                        val router = Router(RouterType.valueOf(type), RouterId.of(id))
                        routers.add(router)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return routers
        }
    }
}

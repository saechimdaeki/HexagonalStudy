package chap03.framework.adapters.output.file

import chap03.application.ports.output.RouterViewOutputPort
import chap03.domain.entity.Router
import chap03.domain.vo.RouterId
import chap03.domain.vo.RouterType
import java.io.BufferedReader
import java.io.InputStreamReader


class RouterViewFileAdapter private constructor() : RouterViewOutputPort {
    override fun fetchRouters(): List<Router> {
        return readFileAsString()
    }

    private fun readFileAsString(): List<Router> {
        val routers: MutableList<Router> = ArrayList()
        try {
            RouterViewFileAdapter::class.java.getClassLoader().getResourceAsStream("routers.txt")?.let {
                InputStreamReader(
                    it
                )
            }?.let {
                BufferedReader(
                    it
                ).lines().use { stream ->
                    stream.forEach { line: String ->
                        val routerEntry =
                            line.split(";".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        val id = routerEntry[0]
                        val type = routerEntry[1]
                        val router = Router(RouterType.valueOf(type), RouterId.withId(id), null)
                        routers.add(router)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return routers
    }

    private fun RouterViewFileAdapter(): RouterViewFileAdapter {
        return RouterViewFileAdapter()
    }

    companion object {
        private var instance: RouterViewFileAdapter? = null

        fun getInstance(): RouterViewFileAdapter {
            if (instance == null) {
                instance = RouterViewFileAdapter()
            }
            return instance!!
        }
    }
}

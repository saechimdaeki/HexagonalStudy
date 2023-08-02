package chap03.framework.adapters.input.stdin

import chap03.application.ports.input.RouterNetworkInputPort
import chap03.application.usecases.RouterNetworkUseCase
import chap03.domain.entity.Router
import chap03.domain.vo.Network
import chap03.domain.vo.RouterId
import chap03.framework.adapters.output.file.RouterNetworkFileAdapter


class RouterNetworkCLIAdapter {
    var routerNetworkUseCase: RouterNetworkUseCase? = null

    init {
        setAdapters()
    }

    fun addNetwork(routerId: RouterId, network: Network): Router {
        return routerNetworkUseCase?.addNetworkToRouter(routerId, network) ?: throw Exception("RouterNetworkUseCase is not set")
    }

    private fun setAdapters() {
        routerNetworkUseCase = RouterNetworkFileAdapter.instance?.let { RouterNetworkInputPort(it) }
    }
}

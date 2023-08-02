package chap03.application.ports.input

import chap03.application.ports.output.RouterNetworkOutputPort
import chap03.application.usecases.RouterNetworkUseCase
import chap03.domain.entity.Router
import chap03.domain.service.NetworkOperation
import chap03.domain.vo.Network
import chap03.domain.vo.RouterId


class RouterNetworkInputPort(private val routerNetworkOutputPort: RouterNetworkOutputPort) : RouterNetworkUseCase {


    override fun addNetworkToRouter(routerId: RouterId, network: Network): Router {
        val router: Router = fetchRouter(routerId)
        return createNetwork(router, network)
    }

    private fun fetchRouter(routerId: RouterId): Router {
        return routerNetworkOutputPort.fetchRouterById(routerId)
    }

    private fun createNetwork(router: Router, network: Network): Router {
        val newRouter = NetworkOperation.createNewNetwork(router, network)
        return if (persistNetwork(router)) newRouter else router
    }

    private fun persistNetwork(router: Router): Boolean {
        return routerNetworkOutputPort.persistRouter(router)
    }
}

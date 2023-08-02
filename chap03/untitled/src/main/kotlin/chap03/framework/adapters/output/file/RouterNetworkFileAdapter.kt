package chap03.framework.adapters.output.file

import chap03.application.ports.output.RouterNetworkOutputPort
import chap03.domain.entity.Router
import chap03.domain.entity.Switch
import chap03.domain.vo.*


class RouterNetworkFileAdapter private constructor() : RouterNetworkOutputPort {
    private val routers = mutableListOf<Router>()
    override fun fetchRouterById(routerId: RouterId): Router {
        var retrievedRouter: Router? = null
        for (router in routers) {
            if (router.getRouterId().id == routerId.id) {
                retrievedRouter = router
                break
            }
        }
        retrievedRouter ?: throw RuntimeException("Router not found")
        return retrievedRouter
    }

    private fun createSampleRouter() {
        val routerId = RouterId.withId("ca23800e-9b5a-11eb-a8b3-0242ac130003")
        val network = Network(IP("10.0.0.0"), "HR", 8)
        val networkSwitch = Switch(SwitchType.LAYER3, SwitchId.withoutId(), listOf(network), IP("9.0.0.9"))
        val router = Router(RouterType.EDGE, routerId, networkSwitch)
        routers.add(router)
    }

    override fun persistRouter(router: Router): Boolean {
        return routers.add(router)
    }

    init {
        createSampleRouter()
    }

    companion object {
        var instance: RouterNetworkFileAdapter? = null
            get() {
                if (field == null) {
                    field = RouterNetworkFileAdapter()
                }
                return field
            }
            private set
    }
}

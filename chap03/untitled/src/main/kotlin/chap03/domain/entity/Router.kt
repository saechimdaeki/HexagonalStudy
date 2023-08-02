package chap03.domain.entity

import chap03.domain.vo.IP
import chap03.domain.vo.Network
import chap03.domain.vo.RouterId
import chap03.domain.vo.RouterType
import java.util.function.Predicate


class Router(private val routerType: RouterType, private val routerid: RouterId, private var networkSwitch: Switch?) {
    fun addNetworkToSwitch(network: Network) {
        networkSwitch = networkSwitch?.addNetwork(network)
    }

    fun createNetwork(address: IP, name: String, cidr: Int): Network {
        return Network(address, name, cidr)
    }

    fun retrieveNetworks(): List<Network>? {
        return networkSwitch?.getNetworks()
    }

    fun getRouterId(): RouterId {
        return routerid
    }

    fun getRouterType(): RouterType {
        return routerType
    }

    override fun toString(): String {
        return "Router{" +
                "type=" + routerType +
                ", id=" + routerid +
                '}'
    }

    companion object {
        fun filterRouterByType(routerType: RouterType?): Predicate<Router> {
           return if (routerType == RouterType.CORE) isCore else isEdge
        }
        private val isCore: Predicate<Router>
            get() = Predicate<Router> { p: Router -> p.getRouterType() === RouterType.CORE }
        private val isEdge: Predicate<Router>
            get() = Predicate<Router> { p: Router -> p.getRouterType() === RouterType.EDGE }
    }
}
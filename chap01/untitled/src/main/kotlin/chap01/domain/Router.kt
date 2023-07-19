package chap01.domain

import java.util.function.Predicate
import java.util.stream.Collectors


class Router(routerType: RouterType, routerId: RouterId) {
    private val routerType: RouterType
    private val routerId: RouterId

    init {
        this.routerType = routerType
        this.routerId = routerId
    }

    fun getRouterType(): RouterType {
        return routerType
    }

    override fun toString(): String {
        return "Router{" +
                "routerType=" + routerType +
                ", routerId=" + routerId +
                '}'
    }

    companion object {
        fun filterRouterByType(routerType: RouterType): Predicate<Router> {
            return if (routerType.equals(RouterType.CORE)) isCore else isEdge
        }

        private val isCore: Predicate<Router>
            get() = Predicate<Router> { p -> p.getRouterType() === RouterType.CORE }
        private val isEdge: Predicate<Router>
            get() = Predicate<Router> { p -> p.getRouterType() === RouterType.EDGE }

        fun retrieveRouter(routers: List<Router>, predicate: Predicate<Router>): List<Router> {
            return routers.stream()
                .filter(predicate)
                .collect(Collectors.toList())
        }
    }
}
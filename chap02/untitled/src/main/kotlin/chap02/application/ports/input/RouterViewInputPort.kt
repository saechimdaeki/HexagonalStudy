package chap02.application.ports.input

import chap02.application.ports.output.RouterViewOutputPort
import chap02.application.usecases.RouterViewUseCase
import chap02.domain.entity.Router
import chap02.domain.service.RouterSearch
import java.util.function.Predicate

class RouterViewInputPort(
    private val routerViewOutputPort: RouterViewOutputPort
) : RouterViewUseCase {

    override fun getRouters(filter: Predicate<Router>): List<Router?> {
        val routers = routerViewOutputPort.fetchRouters()
        return RouterSearch.retrieveRouter(routers, filter)    }
}
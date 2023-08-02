package chap03.application.ports.input

import chap03.application.ports.output.RouterViewOutputPort
import chap03.application.usecases.RouterViewUseCase
import chap03.domain.entity.Router
import chap03.domain.service.RouterSearch
import java.util.function.Predicate

class RouterViewInputPort(
    private val routerViewOutputPort: RouterViewOutputPort
) : RouterViewUseCase {

    override fun getRouters(filter: Predicate<Router>): List<Router?> {
        val routers = routerViewOutputPort.fetchRouters()
        return RouterSearch.retrieveRouter(routers, filter)    }
}
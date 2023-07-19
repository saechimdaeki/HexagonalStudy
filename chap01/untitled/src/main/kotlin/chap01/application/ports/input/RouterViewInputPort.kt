package chap01.application.ports.input

import chap01.application.ports.output.RouterViewOutputPort
import chap01.application.usecases.RouterViewUseCase
import chap01.domain.Router
import java.util.function.Predicate

class RouterViewInputPort(
    private val routerViewOutputPort: RouterViewOutputPort
) : RouterViewUseCase {
    override fun getRouters(filter: Predicate<Router>): List<Router> {
        val routers = routerViewOutputPort.fetchRouters()
        return Router.retrieveRouter(routers, filter)
    }
}
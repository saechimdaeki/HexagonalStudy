package chap03.application.ports.output

import chap03.domain.entity.Router
import chap03.domain.vo.RouterId

interface RouterNetworkOutputPort {

    fun fetchRouterById(routerId: RouterId) : Router

    fun persistRouter(router: Router) : Boolean
}
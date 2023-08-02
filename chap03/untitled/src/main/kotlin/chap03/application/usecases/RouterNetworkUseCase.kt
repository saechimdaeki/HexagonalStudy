package chap03.application.usecases

import chap03.domain.entity.Router
import chap03.domain.vo.Network
import chap03.domain.vo.RouterId


interface RouterNetworkUseCase {
    fun addNetworkToRouter(routerId: RouterId, network: Network): Router
}

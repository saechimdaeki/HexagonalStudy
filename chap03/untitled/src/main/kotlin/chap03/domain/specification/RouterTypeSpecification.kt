package chap03.domain.specification

import chap03.domain.entity.Router
import chap03.domain.specification.shared.AbstractSpecification
import chap03.domain.vo.RouterType


class RouterTypeSpecification : AbstractSpecification<Router>() {
    override fun isSatisfiedBy(router: Router): Boolean {
        return router.getRouterType().equals(RouterType.EDGE) || router.getRouterType().equals(RouterType.CORE)
    }
}

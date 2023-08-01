package chap02.domain.specification

import chap02.domain.entity.Router
import chap02.domain.specification.shared.AbstractSpecification
import chap02.domain.vo.RouterType


class RouterTypeSpecification : AbstractSpecification<Router>() {
    override fun isSatisfiedBy(router: Router): Boolean {
        return router.getRouterType().equals(RouterType.EDGE) || router.getRouterType().equals(RouterType.CORE)
    }
}

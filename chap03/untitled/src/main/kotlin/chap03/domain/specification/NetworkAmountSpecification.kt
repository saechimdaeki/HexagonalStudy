package chap03.domain.specification

import chap03.domain.entity.Router
import chap03.domain.specification.shared.AbstractSpecification


class NetworkAmountSpecification : AbstractSpecification<Router>() {

    companion object {
        const val MAXIMUM_ALLOWED_NETWORKS = 6
    }

    override fun isSatisfiedBy(router: Router): Boolean {
        return router.retrieveNetworks()!!.size <= MAXIMUM_ALLOWED_NETWORKS
    }
}

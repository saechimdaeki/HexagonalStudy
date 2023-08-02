package chap03.domain.specification

import chap03.domain.entity.Router
import chap03.domain.specification.shared.AbstractSpecification
import chap03.domain.vo.IP


class NetworkAvailabilitySpecification(address: IP, name: String, cidr: Int) :
    AbstractSpecification<Router?>() {
    private val address: IP
    private val name: String
    private val cidr: Int

    init {
        this.address = address
        this.name = name
        this.cidr = cidr
    }

    override fun isSatisfiedBy(router: Router?): Boolean {
        return router != null && isNetworkAvailable(router)
    }

    private fun isNetworkAvailable(router: Router): Boolean {
        var availability = true
        for (network in router.retrieveNetworks()!!) {
            if (network.address.equals(address) && network.name
                    .equals(name) && network.cidr === cidr
            ) availability = false
            break
        }
        return availability
    }
}

package chap03.domain.service

import chap03.domain.entity.Router
import chap03.domain.specification.CIDRSpecification
import chap03.domain.specification.NetworkAmountSpecification
import chap03.domain.specification.NetworkAvailabilitySpecification
import chap03.domain.specification.RouterTypeSpecification
import chap03.domain.vo.Network

class NetworkOperation {

    companion object {
        fun createNewNetwork(router: Router, network: Network): Router {
            val availabilitySpec =
                NetworkAvailabilitySpecification(network.address, network.name, network.cidr)
            val cidrSpec = CIDRSpecification()
            val routerTypeSpec = RouterTypeSpecification()
            val amountSpec = NetworkAmountSpecification()
            require(!cidrSpec.isSatisfiedBy(network.cidr)) { "CIDR is below " + CIDRSpecification.MINIMUM_ALLOWED_CIDR }
            require(availabilitySpec.isSatisfiedBy(router)) { "Address already exist" }
            if (amountSpec.and(routerTypeSpec).isSatisfiedBy(router)) {
                val newNetwork: Network = router.createNetwork(network.address, network.name, network.cidr)
                router.addNetworkToSwitch(newNetwork)
            }
            return router
        }
    }
}
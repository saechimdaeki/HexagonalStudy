package chap02.domain.entity

import chap02.domain.vo.IP
import chap02.domain.vo.Network
import chap02.domain.vo.SwitchId
import chap02.domain.vo.SwitchType


class Switch(private val switchType: SwitchType,
             private val switchId: SwitchId,
             private val networks: List<Network>,
             private val address: IP
) {

    fun addNetwork(network: Network): Switch {
        val networks = mutableListOf(network)
        networks.add(network)
        return Switch(switchType, switchId, networks, address)
    }

    fun getNetworks(): List<Network> {
        return networks
    }
}

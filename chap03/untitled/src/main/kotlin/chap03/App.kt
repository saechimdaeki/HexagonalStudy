package chap03

import chap03.domain.vo.IP
import chap03.domain.vo.Network
import chap03.domain.vo.RouterId
import chap03.framework.adapters.input.stdin.RouterNetworkCLIAdapter


fun main() {
    val cli = RouterNetworkCLIAdapter()
    val routerId = RouterId.withId("ca23800e-9b5a-11eb-a8b3-0242ac130003")
    val network = Network(IP("20.0.0.0"), "Marketing", 8)
    val router = cli.addNetwork(routerId, network)
    println(router)
}
package chap03.domain.vo


class Network(address: IP, name: String, cidr: Int) {
    val address: IP
    val name: String
    val cidr: Int

    init {
        require(!(cidr < 1 || cidr > 32)) { "Invalid CIDR value" }
        this.address = address
        this.name = name
        this.cidr = cidr
    }

    override fun toString(): String {
        return "Network{" +
                "address=" + address +
                ", name='" + name + '\'' +
                ", cidr=" + cidr +
                '}'
    }
}

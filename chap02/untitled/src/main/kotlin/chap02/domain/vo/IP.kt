package chap02.domain.vo


class IP (address: String?) {
    private val address: String
    private var protocol: Protocol? = null

    init {
        requireNotNull(address) { "Null IP address" }
        this.address = address
        protocol = if (address.length <= 15) {
            Protocol.IPV4
        } else {
            Protocol.IPV6
        }
    }

}

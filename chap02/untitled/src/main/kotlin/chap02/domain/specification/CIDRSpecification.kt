package chap02.domain.specification

import chap02.domain.specification.shared.AbstractSpecification


class CIDRSpecification : AbstractSpecification<Int>() {
    override fun isSatisfiedBy(cidr: Int): Boolean {
        return cidr > MINIMUM_ALLOWED_CIDR
    }

    companion object {
        const val MINIMUM_ALLOWED_CIDR = 8
    }


}


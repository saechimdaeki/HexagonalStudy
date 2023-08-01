package chap02.domain.specification.shared


abstract class AbstractSpecification<T> : Specification<T> {
    abstract override fun isSatisfiedBy(t: T): Boolean
    override fun and(specification: Specification<T>): Specification<T> {
        return AndSpecification(this, specification)
    }
}

package chap03.domain.specification.shared


interface Specification<T> {
    fun isSatisfiedBy(t: T): Boolean
    fun and(specification: Specification<T>): Specification<T>
}
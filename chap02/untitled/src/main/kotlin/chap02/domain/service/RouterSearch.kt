package chap02.domain.service

import chap02.domain.entity.Router
import java.util.function.Predicate

class RouterSearch {


    companion object {
        fun retrieveRouter(routers: List<Router>, predicate: Predicate<Router>): List<Router> {
            return routers
                .filter { predicate.test(it) }
                .toList()
        }
    }
}
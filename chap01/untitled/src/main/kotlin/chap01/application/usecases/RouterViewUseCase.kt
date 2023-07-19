package chap01.application.usecases

import chap01.domain.Router
import java.util.function.Predicate

interface RouterViewUseCase {
    fun getRouters(filter: Predicate<Router>): List<Router>

}
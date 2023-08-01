package chap02.application.usecases

import chap02.domain.entity.Router
import java.util.function.Predicate

interface RouterViewUseCase {
    fun getRouters(filter: Predicate<Router>): List<Router?>

}
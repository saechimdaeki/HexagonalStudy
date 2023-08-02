package chap03.application.usecases

import chap03.domain.entity.Router
import java.util.function.Predicate

interface RouterViewUseCase {
    fun getRouters(filter: Predicate<Router>): List<Router?>

}
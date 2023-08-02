package chap03.application.ports.output

import chap03.domain.entity.Router

interface RouterViewOutputPort {
    fun fetchRouters(): List<Router>

}
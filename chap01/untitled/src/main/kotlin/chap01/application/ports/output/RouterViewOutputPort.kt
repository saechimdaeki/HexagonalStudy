package chap01.application.ports.output

import chap01.domain.Router

interface RouterViewOutputPort {
    fun fetchRouters(): List<Router>

}
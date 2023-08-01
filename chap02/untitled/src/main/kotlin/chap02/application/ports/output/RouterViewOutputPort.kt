package chap02.application.ports.output

import chap02.domain.entity.Router

interface RouterViewOutputPort {
    fun fetchRouters(): List<Router>

}
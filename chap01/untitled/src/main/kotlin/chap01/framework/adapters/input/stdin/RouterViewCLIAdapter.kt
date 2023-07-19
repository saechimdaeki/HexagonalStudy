package chap01.framework.adapters.input.stdin

import chap01.application.ports.input.RouterViewInputPort
import chap01.application.usecases.RouterViewUseCase
import chap01.domain.Router
import chap01.domain.RouterType
import chap01.framework.adapters.output.file.RouterViewFileAdapter


class RouterViewCLIAdapter {
    lateinit var routerViewUseCase: RouterViewUseCase

    init {
        setAdapters()
    }

    fun obtainRelatedRouters(type: String): List<Router> {
        return routerViewUseCase.getRouters(
            Router.filterRouterByType(RouterType.valueOf(type))
        )
    }

    private fun setAdapters() {
        routerViewUseCase = RouterViewInputPort(RouterViewFileAdapter.instance)
    }
}
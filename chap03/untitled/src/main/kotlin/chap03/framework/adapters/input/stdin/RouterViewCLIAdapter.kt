package chap03.framework.adapters.input.stdin

import chap03.application.ports.input.RouterViewInputPort
import chap03.application.usecases.RouterViewUseCase
import chap03.domain.entity.Router
import chap03.domain.vo.RouterType
import chap03.framework.adapters.output.file.RouterViewFileAdapter


class RouterViewCLIAdapter {
    lateinit var routerViewUseCase: RouterViewUseCase

    init {
        setAdapters()
    }

    fun obtainRelatedRouters(type: String): List<Router?> {
        return routerViewUseCase.getRouters(
            Router.filterRouterByType(RouterType.valueOf(type))
        )
    }

    private fun setAdapters() {
        routerViewUseCase = RouterViewInputPort(RouterViewFileAdapter.getInstance())
    }
}
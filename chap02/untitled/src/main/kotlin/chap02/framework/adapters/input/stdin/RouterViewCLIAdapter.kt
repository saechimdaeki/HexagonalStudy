package chap02.framework.adapters.input.stdin

import chap02.application.ports.input.RouterViewInputPort
import chap02.application.usecases.RouterViewUseCase
import chap02.domain.entity.Router
import chap02.domain.vo.RouterType
import chap02.framework.adapters.output.file.RouterViewFileAdapter


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
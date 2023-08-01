package chap02

import chap02.framework.adapters.input.stdin.RouterViewCLIAdapter


fun main() {
    var cli = RouterViewCLIAdapter()
    var type ="CORE"
    println(cli.obtainRelatedRouters(type))
}
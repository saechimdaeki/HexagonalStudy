package chap01

import chap01.framework.adapters.input.stdin.RouterViewCLIAdapter


fun main() {
    var cli = RouterViewCLIAdapter()
    var type ="CORE"
    println(cli.obtainRelatedRouters(type))
}
## 헥사고날 아키텍처 이해

헥사고날 아키텍처의 주된 아이디어 중 하나는 비즈니스 코드를 기술 코드로부터 분리하는 것이다. 그뿐만 아니라 기술 측면이 비즈니스 측면에 의존하는지도 확인해 비즈니스 측면이 

비즈니스 목푤르 달성하는데 사용되는 기술에 대한 우려 없이도 발전할 수 있게 해야한다. 또한 관련된 비즈니스 코드에 피해를 주지 안혹도 기술 코드를 변경할 수 있어야 한다.

이러한 목표를 달성하려면 비즈니스 코드가 어디에 존재하는지, 기술 문제로부터 격리되고 보호돼야 하는 위치를 결정해야 한다. 이것이 바로 첫번째 헥사곤,

즉 `도메인 헥사곤`을 생성하게 할 것이다. 

도메인 헥사곤에서는 소프트웨어가 해결하기를 원하는 핵심 문제를 설명하는 요소들을 결합한다. 도메인 헥사곤에서 활용되는 주된 요소는 엔티티와 값 객체이다. 

엔티티는 식별자를 할당할 수 있는 것을 의미하며, 값 자체는 엔티티들을 합성하기 위해 사용하는 불변 컴포넌트다. 

또한 도메인 헥사곤에서 나오는 비즈니스 규칙을 사용, 처리하고 조정하는 방법이 필요하다. 이는 `애플리케이션 헥사곤`이 하는일이다. 애플리케이션 헥사곤은

비즈니스 측면과 기술 측면 사이에 있으며, 양쪽과 상호작용하는 중개자 역할을 한다. 애플리케이션 헥사곤은 포트와 유스케이스를 활용해 이러한 기능을 수행한다.

`프레임워크 헥사곤`은 외부 인터페이스를 제공한다. 프레임워크 헥사곤은 애플리케이션의 긴능의 노출 방법을 결정할 수 있는 곳이다. 예를 들어, 프레임워크 헥사곤에서

REST나 gRPC 엔드포인트를 지정한다. 외부 소스에서 무언가를 소비하기 위해 데이터베이스, 메시지 브로커, 또는 시스템에서 데이터를 가져오는 메커니즘을 정의하기 위해

프레임워크 헥사곤을 사용한다

## 도메인 헥사곤


`도메인 헥사곤`은 실 세계 문제를 이해하고 모델링하는 활동을 나타낸다. 통신회사의 네트워크 및 토폴리지 인벤토리 생성 프로젝트를 가정하면 이 프로젝트의 주된 목적은

네트워크를 구성하는 모든 리소스에 대한 포괄적인 뷰를 제공하는 것이다. 여기서 목표는 도메인 헥사곤을 사용해 이러한 네트워크 및 토폴리지 요소를 코드로 식별 및 분류하고

연관시키는 데 필요한 지식을 모델링하는 것이다. 해당 지식은 가능한 한 기술에 구애받지 않는 형태로 표현돼야 한다

도메인 헥사곤의 도메인 안에는 엔티티와 값객체가 있는데 이를 보자

#### `엔티티`

엔티티는 좀 더 표현력 있는 코드를 작성하는데 도움을 준다. 

```kotlin
class Router(routerType: RouterType, routerId: RouterId) {
    private val routerType: RouterType
    private val routerId: RouterId

    init {
        this.routerType = routerType
        this.routerId = routerId
    }

    fun getRouterType(): RouterType {
        return routerType
    }

    override fun toString(): String {
        return "Router{" +
                "routerType=" + routerType +
                ", routerId=" + routerId +
                '}'
    }

    companion object {
        fun filterRouterByType(routerType: RouterType): Predicate<Router> {
            return if (routerType.equals(RouterType.CORE)) isCore else isEdge
        }

        private val isCore: Predicate<Router>
            get() = Predicate<Router> { p -> p.getRouterType() === RouterType.CORE }
        private val isEdge: Predicate<Router>
            get() = Predicate<Router> { p -> p.getRouterType() === RouterType.EDGE }

        fun retrieveRouter(routers: List<Router>, predicate: Predicate<Router>): List<Router> {
            return routers.stream()
                .filter(predicate)
                .collect(Collectors.toList())
        }
    }
}
```

#### `값 객체`

값 객체는 무언가 고유하게 식별할 필요가 없는 경우는 물론이고, 객체의 정체성보다 속성에 관심을 갖는 경우에도 코드의 표현력을

보완하는데 도움이 된다. 값 객체를 사용해 엔티티 객체를 구성할 수 있다. 따라서 도메인 전체에서 예상치 못한 불일치를 방지하기 위해

값 객체를 변경할 수 없게 해야한다.

```kotlin
enum class RouterType {
    EDGE,
    CORE
}
```

## 애플리케이션 헥사곤

`애플리케이션 헥사곤`은 애플리케이션 특화 작업을 추상적으로 처리하는 곳이다. 이 헥사곤은 도메인 비즈니스 규칙에 기반한 소프트웨어 사용자의 의도와 기능을 표현한다

애플리케이션 헥사곤에는 유즈케이스, 입력포트, 출력포트라는 것이 있다

#### `유즈케이스`

유즈케이스는 도메인 제약사항을 지원하기 위해 시스템의 동작을 소프트웨어 영역 내에 존재하는 애플리케이션 특화 오퍼레이션을 통해 나타낸다

유즈케이스는 엔티티 및 다른 유즈케이스와 직접 상호작용하고 그것들을 유연한 컴포넌트로 만들 수 있다. 자바/코틀린에서 유즈케이스는 

소프트웨어가 할 수 있는것을 표현하는 인터페이스로 정의된 추상화로 나타낸다

```kotlin
interface RouterViewUseCase {
    fun getRouters(filter: Predicate<Router>): List<Router>
}
```

#### `입력 포트`

유즈케이스가 소프트웨어가 하는 일을 설명하는 인터페이스라면 여전히 유즈케이스 인터페이스를 구현해야 한다. 이것이 입력 포트의 역할이다

애플리케이션 수준에서 유즈케이스에 직접 연결되는 컴포넌트가 되기 때문에 입력 포트는 도메인 용어로 소프트웨어의 의도를 구현할 수 있게 한다.

다음은 유즈케이스에 서술된 소프트웨어의 의도를 만족시키는 입력포트를 구현한 것이다

```kotlin
class RouterViewInputPort(
    private val routerViewOutputPort: RouterViewOutputPort
) : RouterViewUseCase {
    override fun getRouters(filter: Predicate<Router>): List<Router> {
        val routers = routerViewOutputPort.fetchRouters()
        return Router.retrieveRouter(routers, filter)
    }
}
```

입력포트의 구현에서는 이처럼 애플리케이션 외부에서 라우터의 리스트를 가져올 수도 있다.

#### `출력 포트`

유즈케이스가 목표를 달성하기 위해 외부 리소스에서 데이터를 가져와야 하는 상황이 있다. 이것이 출력포트의 역할이다.

출력포트는 유즈케이스나 입력 포트가 오퍼레이션을 수행하기 위해 어떤 종류의 데이터를 외부에서 가져와야 하는지를 기술에 구애받지 않고

설명하는 인터페이스로 표현된다. 출력포트가 기술에 구애받지 않는다고 말한것은 예를 들어, 해당 데이터가 특정 관계형 데이터베이스 기술이나

파일 시스템에서 오는지 신경쓰지 않기 때문이다.

```kotlin
interface RouterViewOutputPort {
    fun fetchRouters(): List<Router>
}
```
## 프레임워크 헥사곤

도메인 헥사곤으로 제한되는 중요 비즈니스 규칙은 잘 구성되고 다음으로 애플리케이션 헥사곤이 뒤따른다. 이제 소프트웨어와 통신할 수 있는 기술을

결정해야 하는 순간이 왔는데 통신은 두 가지 형태로 발생할 수 있다. 하나는 드라비잉이고 하나는 드리븐이다.

드라이버는 입력어댑터를 사용하고 드리븐의경우 출력 어댑터를 사용한다

#### `드라이빙 오퍼레이션과 입력 어댑터`

`드라이빙 오퍼레이션`은 소프트웨어에 동작을 요청하는 것이다. 예를 들어 프론트엔드 애플리케이션이 될 수 있다.

이러한 통신은 입력 어댑터 상단에 구축된 애플리케이션 프로그래밍 인터페이스(API)를 통해 일어난다.


이 API는 외부 엔티티가 시스템과 상호작용하고, 외부 엔티티의 요청을 도메인 애플리케이션으로 변환하는 방법을 정의한다.

드라이빙이라는 용어를 사용하는 이유는 외부 엔티티들이 시스템의 동작을 유도(드라이빙)하기 때문이다.

헥사고날 아키텍처를 통해 시나리오 모두에 대한 입력 어댑터를 생성하고, 도메인 관점에서 각 어댑터를 같은 입력 포트에 연결해 차례로

작동하도록 요청다운스트림을 변환할 수 있다.

```kotlin
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
```

#### `드리븐 오퍼레이션과 출력 어댑터`

드라이빙 오퍼레이션의 반대편에는 드리븐 오퍼레이션이 있다. 이 오퍼레이션은 애플리케이션에서 트리거되고, 외부에서 소프트웨어 요구사항을

충족시키는 데 필요한 데이터를 가져온다. 일반적으로 드리븐 오퍼레이션은 일부 드라이빙 오퍼레이션에 응답해 발생한다

```kotlin
class RouterViewFileAdapter private constructor() : RouterViewOutputPort {
    override fun fetchRouters(): List<Router> {
        return readFileAsString()
    }

    companion object {
        var instance = RouterViewFileAdapter()
            private set

        private fun readFileAsString(): List<Router> {
            val routers = mutableListOf<Router>()
            try {
                BufferedReader(
                    InputStreamReader(
                        RouterViewFileAdapter::class.java.getClassLoader().getResourceAsStream("routers.txt") as InputStream
                    )
                ).lines().use { stream ->
                    stream.forEach { line: String ->
                        val routerEntry =
                            line.split(";".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        val id = routerEntry[0]
                        val type = routerEntry[1]
                        val router = Router(RouterType.valueOf(type), RouterId.of(id))
                        routers.add(router)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return routers
        }
    }
}
```

출력포트는 이처럼 애플리케이션이 외부로부터 필요로 하는 데이터를 나타낸다. 



## 헥사고날의 장점은 다음과 같다

### `변경 허용`

헥사고날 아키텍처의 포트와 어댑터라는 특성은 마찰이 적고 기술변화를 흡수할 준비가 되어있는 애플리케이션을 위한 아키텍처 원칙을 강력한 이점으로 제공

### `유지보수성`

비즈니스 규칙을 변경해야 하는 경우 유일하게 변경해야 하는것은 도메인 헥사곤, 아직 애플리케이션에서 지원하지 않는 특정 기술이나 프로토콜을 사용하는

기존 기능을 고객이 트리거할 수 있게 허용해야 하는경우 프레임워크 헥사곤에서 새로운 어댑터를 생성하기만 하면 된다.

이렇게 관심사가 분리되어 있다

### `테스트 용이성`

코드의 가장 중요한 부분을 테스트하는 데 필요한 유연성을 제공함으로써 더욱더 계속해서 통합하는 방식을 허용한다.

이는 기술 의존성이 없는 경우에도 마찬가지이기에 테스트에 용이하다.
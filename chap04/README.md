

## 외부와 상호작용하는 어댑터 만들기 

~~(chap4부터는 kotlin으로변환이 너무 번거로워 예제 코드 사용)~~

### 드라이빙 오퍼레이션 허용을 위한 입력 어댑터 사용

입력 어댑터는 원격 통신 프로토콜과 같다. 입력 어댑터는 헥사고날 시스템이 제공하는 기능에 액세스 수단으로 지원되는 기술을 정의하는

프로토콜처럼 동작하기 때문에 이러한 비교는 유효하다. 입력 어댑터는 헥사곤 내부와 외부 사이의 명확한 경계를 표시하고 드라이빙 오퍼레이션을 수행한다.

헥사곤 외부에는 헥사곤 애플리케이션과 상호작용하는 사용자나 시스템이 있을 수 있다. 이러한 사용자나 시스템을 가리켜 애플리케이션

유즈케이스를 형성하는 중추적인 역할을 하는 주요 액터라고 한다. 주요 액터와 헥사고날 애플리케이션 사이의 상호작용은 입력 어댑터를 통해 일어난다.

이러한 상호작용은 드라이빙 오퍼레이션으로 정의된다. 주요 액터가 이들을 드라이브하고, 헥사고날 시스템의 상태와 행위를 시작하게 하고

영향을 준다는 의미에서 드라이빙으로 표현한다.

입력 어댑터를 모두 모으면 헥사고날 애플리케이션의 API를 형성한다. 입력 어댑터는 헥사고날 시스템을 외부 세계에 노출시키는 경계에

있기 때문에 본질적으로 시스템과의 상호작용에 관심을 갖는 모두에게 인터페이스가 된다.

주요 액터와 헥사고날 애플리케이션 사이의 연결이 입력 어댑터를 통해 발생한다는 것을 배웠고 이제 입력 어댑터를 시스템의 다른 헥사곤에 연결하는 방법을 보자

#### `입력 어댑터 생성`

`입력 포트`는 유스케이스의 목표를 달성하기 위해 입력 포트가 수행하는 오퍼레이션의 수행 방법을 지정해 유스케이스를 구현하는 수단이다

입력 포트 객체는 입력 어댑터가 보낸 자극을 통해 오퍼레이션을 수행하는 데 필요한 모든 데이터를 수신한다.

그러나 입력 데이터를 도메인 헥사곤과 호환되는 형식으로 변환하기 위해 이 단계에서 최종적인 변환이 발생할 수 있다


먼저 어댑터 추상 기반 클래스를 정의하고 두 개의 어댑터 구현이 따르는데, 하나는 HTTP REST연결에서 데이터를 수신하고, 다른 하나는

콘솔 STDIN 연결을 위한것이다.

#### `기반 어댑터`

```java
public abstract class RouterNetworkAdapter {
    protected Router router;
    protected RouterNetworkUseCase routerNetworkUseCase;

    protected Router addNetworkToRouter(Map<String,String> params) {
        var routerId = RouterId.withId(params.get("routerId"));
        var network = new Network(IP.fromAddress(params.get("address")),
            params.get("name"), Integer.valueOf(params.get("cidr")));

        return routerNetworkUseCase.addNetworkToRouter(routerId, network);
    }

    public abstract Router processRequest(Object requestParams);
}
```

입력 포트를 직접 참조하지 않는다. 대신, 유즈케이스 인터페이스 참조를 활용한다. 

이 유즈케이스 참조는 입력 어댑터의 생성자에 의해 전달되고 초기화된다

`REST 입력 어댑터`

REST 어댑터를 생성해보자. RouterNetworkRestAdapter 생성자를 정의하는 것으로 구현을 시작한다

```java
public RouterNetworkRestAdapter(RouterNetworkUseCase outerNetworkUseCase) {
    this.routerNetworkUseCase = routerNetworkUseCase;
}
```

클라이언트가 RouterNetworkRestAdapter 입력 어댑터를 호출하고 초기화하는 방법은 다음과 같다

```java
RouterNetworkOutputPort outputPort = RouterNetworkH2Adapter.getInstance();
RouterNetworkUseCase usecase = new RouterNetworkInputPort(outputPort);
RouterNetworkAdapter inputAdapter = new RouterNetworkRestAdapter(usecase);
```

여기서 의도는 REST 입력 어댑터가 H2 인메모리 데이터베이스 출력 어댑터를 필요로 한다는 것을 표현하는 것이다. 여기서는 입력 어댑터가

액티비티를 수행하는 데 필요한 출력 어댑터 객체를 명시적으로 서술하고 있다.

RouterNetworkAdapter 생성자를 정의한 후, processRequest메서드를 구현한다

```java
@Override
public Router processRequest(Object requestParams) {
    /** 코드 생략 **/
    httpserver.createContext("/network/add", (exchange -> {
        if ("GET".equals(exchange.getRequestMethod())) {
            var query = exchange.getRequestURI().getRawQuery();
            httpParams(query, params);
            router = this.addNetworkToRouter(params);
            ObjectMapper mapper = new ObjectMapper();
            var routerJson = mapper.writeValueAsString(RouterJsonFileMapper.toJson(router));
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, routerJson.getBytes().length);
            OutputStream output = exchange.getResponseBody();
            output.write(routerJson.getBytes());
            output.flush();
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
        /** 코드 생략**/
    }))
}
```
이 메서드는 /network/add 에서 GET 요청을 수신하기 위해 HTTP 엔드포인트를 생성하는데 사용되는 httpServer객체를 수신한다.

processRequest를 호출하는 클라이언트 코드는 다음 코드와 비슷하다
```java
var httpserver = HttpServer.create(new InetSocketAddress(8080), 0);
routerNetworkAdapter.processRequest(httpserver);
```

REST 어댑터는 HTTP 요청을 통해 사용자 데이터를 수신하고 요청 매개변수를 파싱한다. 그리고 RouterNetworkAdapter 부모 클래스에

정의된 addNetworkToRouter를 호출하는데 사용한다

```Java
router = this.addNetworkToRouter(params);
```

입력 어댑터는 유즈케이스 참조를 사용해 입력 포트를 트리거하는 데 사용되는 사용자 데이터를 적절한 매개변수로 변환하는 것을 담당한다

```java
routerNetworkUseCase.addNetworkToRouter(routerId, network);
```

이 시점에 데이터는 프레임워크 헥사곤에서 떠나 애플리케이션 헥사곤으로 간다. 다른 어댑터에 같은 입력포트를 연결하는 방법을 살펴보자

`CLI 입력 어댑터`

```java
public class RouterNetworkCLIAdapter extends RouterNetworkAdapter {
    public RouterNetworkCLIAdapter(RouterNetworkUseCase routerNetworkUseCase) {
        this.routerNetworkUseCase = routerNetworkUseCase;
    }
    /** 생략 **/
}
```

다읔 모드는 클라이언트가 RouterNetworkCLIAdapter 입력 어댑터를 초기화하는 방법을 보여준다

```java
RouterNetworkOutputPort outputPort = RouterNetworkFileAdapter.getInstance();
RouterNetworkUseCase usecase = new RouterNetworkInputPort(outputPort);
RouterNetworkAdapter inputAdapter = new RouterNetworkCLIAdapter(routerNetworkUseCase);
```

먼저 RouterNetworkOutputPort 출력 포트의 참조를 얻는다. 그다음 해당 참조를 사용해 RouterNetworkUseCase 유즈케이스를 검색한다.

마지막으로, 앞에서 정의된 유즈케이스를 사용해 RouterNetworkAdapter를 얻는다

다음은 CLI 어댑터의 processRequest 메서드를 구현하는 방법이다

```java
@Override
public Router processRequest(Object requestParams) {
    var paramse = stdinParams(requestParams);
    router = this.addNetworkToRouter(params);
    ObjectMapper mapper = new ObjectMapper();
    try {
        var routerJson = 
            mapper.writeValueAsString(RouterJsonFileMapper.toJson(router));
        System.out.println(routerJson);
    } catch (JsonProcessingException e){
        e.printStackTrace();
    }
    return router;
}
```

REST 어댑터와 CLI 어댑터의 processRequest 메서드는 입력 데이터를 처리하는 데 차이가 있지만, 두 어댑터 모두 한가지

공통점이 있다. 입력 데이터를 params 변수에 넣으면 얻배터 기반 클래스에서 상속된 두 어댑터 모두 addNetworkToRouter 메소드를 호출한다는 점이다


### 입력 어댑터 호출하기

선택할 어댑터를 제어하기 위한 클라이언트 코드는 다음과 같다


```java
public class App {
    /** 생략 **/
    void setAdapter(String adapter) {
        switch (adapter) {
            case "rest":
                outputPort = RouterNetworkH2Adapter.getInstance();
                usecase = new RouterNetworkInputPort(outputPort);
                inputAdapter = new RouterNetworkRestAdapter(usecase);
                rest();
                break;
            default:
                outputPort = RouterNetworkFileAdapter.getInstance();
                usecase = new RouterNetworkInputPort(outputPort);
                inputAdapter = new RouterNetworkCLIAdapter(usecase);
                cli();
        }
    }
}
```

### 출력 어댑터 생성

출력 어댑터는 입력 어댑터와 함께 프레임워크 헥사곤을 구성하는 두 번째 컴포넌트다. 헥사고날 아키텍처에서 출력 어댑터의 역할은

드리븐 오퍼레이션을 처리하는 것이다. 드리븐 오퍼레이션은 일부 데이터를 보내거나 받기 위해 외부 시스템과 상호작용하는 

헥사고날 애플리케이션 자체에 의해 시작된 오퍼레이션이라는 점을 기억하자. 드리븐 오퍼레이션은 유즈케이스를 통해 서술되며, 유즈케이스의

입력 포터 구현에 있는 오퍼레이션에 의해 트리거된다. 유즈케이스에서 외부 시스템에 있는 데이터를 처리할 필요성이 언급될 수 있다

유즈케이스에서 외부 시스템에 있는 데이터를 처리해야 하는 필요성을 이야기할때마다 헥사고날 애플리케이션이 이러한 요구사항을

만족하기 위해 적어도 하나 이상의 출력 어댑터와 출력 포트가 필요하다는 것을 의미

애플리케이션 헥사곤에 인터페이스로 출력 포트를 넣고 프레임워크 헥사곤에 해당 인터페이스의 구현으로 출력 어댑터를 넣음으로써

다양한 기술을 지원하는 헥사고날 시스템을 구성한다

이러한 구조에서는 애플리케이션 헥사곤의 출력 포트 인터페이스와 반드시 일치해야 하는 프레임워크 헥사곤의 출력 어댑터를 갖는다

`결과적으로 애플리케이션 헥사곤은 도메인 헥사곤의 도메인 모델에 의존해야 한다`


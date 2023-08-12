## 드라이빙 오퍼레이션과 드리븐 오퍼레이션의 본질 탐색

- ~~해당 챕터는 예제 코드를 보며 공부하는 편이 나음~~

### 드라이빙 오퍼레이션을 통한 헥사고날 애플리케이션에 대한 요청 호출

헥사고날 아키텍쳐 시점으로 보면 시스템의 입력 측은 드라이빙 오퍼레이션에 의해 제어된다.

사실상 이것들은 헥사고날 애플리케이션의 동작을 시작하게 하고 유도하기 때문에 `드라이빙 오퍼레이션`으로 불린다

드라이빙 오퍼레이션은 다양한 관점을 가정할 수 있다. 명령행 콘솔을 통해 직접 시스템과 상호작용하는 사용자이거나

브라우저에 표시하기 위한 데이터를 요청하는 웹 사용자 인터페이스(UI) 애플리케이션, 특정 테스트 케이스 검증을

원하는 테스트 에이전트일 수 있다.

### 드리븐 오퍼레이션을 통한 외부 리소스 처리

비즈니스 애플리케이션의 일반적 특징은 다른 시스템으로 데이터를 보내거나 데이터를 요청해야 한다는 점이다.

비즈니스 로직을 손상시키지 않고 외부 리소스와 상호작용하기 위해 헥사고날 시스템이 사용할 수 있는

출력포트와 출력 어댑터가 헥사고날 아키텍처의 컴포넌트라는 사실을 살펴보았다. 이러한 외부 리소스는

`보조 액터(secondary actor)`로 알려져 있으며, 헥사고날 애플리케이션에 없는 데이터나 기능을 제공한다.

헥사고날 애플리케이션이 보조 액터에게 요청을 보내는 경우, 즉 일반적으로 헥사곤 애플리케이션의 유즈케이스 중

하나에서 드라이빙 오퍼레이션을 처음 트리거하는 주요 액터를 대신하여 요청을 보내는 경우, 이러한 요청을 `드리븐`오퍼레이션이라 한다

이러한 오퍼레이션은 헥사고날 시스템에 의해 통제되고 유도되기 때문에 드리븐 오퍼레이션이라고 불린다

정리하면 `드라이빙 오퍼레이션`은 헥사고날 시스템의 행위를 유도하는 주요 액터의 요청에서 비롯

`드리븐 오퍼레이션`은 헥사고날 시스템에 의해 데이터베이스나 다른 시스템 같은 보조 액터 쪽으로 시작된 요청

### 데이터 지속성

데이터 지속성을 기반으로 하는 드리븐 오퍼레이션이 가장 일반적이다. 4챕터에서 만들었던 H2 출력 어댑터는 인메모리 데이터베이스를

활용해 데이터 지속성을 처리하는 드리븐 오퍼레이션의 예다. 이러한 종류의 드리븐 오퍼레이션은 헥사고날 시스템과 

데이터베이스 사이에서 객체를 처리하고 변환하기 위해 ORM 기법을 사용할 때가 많다.

트랜잭션 메커니즘도 지속성 기반 드리븐 오퍼레이션의 일부다. 트랜잭션을 활용할 때 헥사고날 시스템이 

직접 트랜잭션 경꼐를 처리하거나 이러한 책임을 애플리케이션 서버로 위임할 수 있다

### 메시징과 이벤트

모든 시스템이 동기식 통신에 의존하는 것은 아니다. 상황에 따라 애플리케이션의 런타임 흐름을 방해하지 않고

특정 이벤트를 트리거하고 싶을 수도 있다. 시스템 컴포넌트 사이의 통신이 비동기적으로 발생하는 기법의 영향을

크게 받는 아키텍처 유형이 있다. 시스템의 컴포넌트들이 더 이상 다른 애플리케이션이 제공하는 인터페이스에 연결되어

있지 않기 때문에 이 같은 시스템은 그러한 기법을 사용하여 더욱 느슨하게 결합된다.

`블로킹`은 애플리케이션 흐름이 진행되기 위해 응답을 기다려야 하는 연결을 의미한다. 논블로킹 방식은 애플리케이션이

요청을 보내고 즉각적인 응답 없이도 계속 진행할 수 있다. 또한 애플리케이션이 메시지나 이벤트에 반응해 어떤 조치를

취하는 경우도 있다. 메시지 기반 시스템은 헥사고날 애플리케이션에 의해 유도되는 보조 액터다. 헥사고날 애플리케이션에서

통신이 시작되는 데이터베이스와 달리, 메시지 기반 시스템이 헥사고날 애플리케이션과 통신을 시작하는 상황이 있다

그러나 메시지를 받거나 보내려면 헥사고날 시스템은 항상 먼저 메시지 시스템과 통신 흐름을 설정해야 한다.

헥사고날 애플리케이션이 카프카로 이벤트를 내보내고 소비할 수 있도록 적절한 포트와 어댑터를 추가한다

```java
public interface NotifyEventOutputPort {
    void sendEvent(String event);
    String getEvent();
}
```

다음으로 NotifyEventOutputPort 출력 어댑터로 출력 포트를 구현한다. 먼저 카프카 연결 속성을 정의하여 NotifyEventKafkaAdapter 

어댑터의 구현을 시작한다

```java
public class NotifyEventKafkaAdapter implements NotifyEventOutputPort {
    private static String KAFKA_BROKERS = "localhost:9092";
    private static String GROUP_ID_CONFIG = "consumerGroup1";
    private static String CLIENT_ID = "hexagonalclient";
    private static String TOPIC_NAME = "topology-inventory-events";
    private static String OFFSET_RESET_EARLIER = "earliest";
    private static String Integer MAX_NO_MESSAGE_FOUND_COUNT = 100;

    /** 코드 생략 **/
}
```

이후 NotifyEventOutputPort에 선언된 sendEvent를 오버라이드한다. 이 메서드를 통해 카프카 producer 

인스턴스로 메시지를 보낼 수 있다

```java
@Override
public void sendEvent(String eventMessage) {
    var record = new ProducerRecord<Long, String>(TOPIC_NAME, eventMessage);

    try{
        var metadata = producer.send(record().get());
        getEvent();
    } catch(Exception e){
        e.printStackTrace();
    }
}
```

카프카 메시지를 소비하고 그것을 WebSocket 서버로 보내기 위해 이 메서드를 호출한다

```java
@Override
public String getEvent() {
    int noMessageToFetch = 0;
    AtomicReference<String> event = new AtomicReference<>("");
    while (true) {
        /** 코드 생략 **/ 
        consumerRecords.forEach(record -> {
            event.set(record.value());
        });
    }
    var eventMessage = event.toString();
    if (sendToWebsocket) 
        sendMessage(eventMessage);
    return eventMessage;
}
```

메시지를 검색한 후 getEvent 메서드는 WebSocket 서버로 메시지를 전달하는 sendMessage를 호출한다

```java
public void sendMessage(String message) {
    try {
        var client = new WebSocketClientAdapter(new URI("ws://localhost:8887"));
        client.connectBlocking();
        client.send(message);
        client.closeBlocking();
    } catch (URISyntaxException || InterruptedException e) {
        e.printStackTrace();
    }
}
```

WebSocket 서버는 이렇게 구성되어 있다

```java
public class NotifyEventWebSocketAdapter extends WebSocketServer {
    /** 코드 생략 **/
    public static void startServer() throws Exception {
        var ws = new NotifyEventWebSocketAdapter(new InetSocketAddress("localhost", 8887));
        ws.setReuseAddr(true);
        ws.start();
        BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String in = sysin.readLine();
            ws.broadcast(in);
            if (in.equals("exit")) {
                ws.stop();
                break;
            }
        }
    }
    /** 코드 생략 **/
}
```

startServer 메서드는 WebSocket 서버의 호스트와 포트를 포함하는 NotifyEventWebSocketAdapter의 

인스턴스를 생성한다. 헥사고날 애플리케이션을 시작할 때 가장 먼저 실행되는 것 중 하나는 8887 포트에서 WebSocket 서버를

불러오기 위해 startServer 메서드를 호출하는 것이다

```java
void setAdapter(String adapter) throws Exception {
    switch (adapter) {
        case "rest":
            routerOutputPort = RouterNetworkH2Adapter.getInstance();
            notifyOutputPort = NotifyEventKafkaAdapter.getInstance();
            usecase = new RouterNetworkInputPort(routerOutputPort, notifyOutputPort);
            inputAdapter = new RouterNetworkRestAdapter(usecase);
            rest();
            NotifyEventWebSocketAdapter.startServer();
            break;
        default:
            routerAdapter = RouterNetworkFileAdapter.getInstance();
            usecase = new RouterNetworkInputPort(routerOutputPort);
            inputAdapter = new RouterNetworkCLIAdapter(usecase);
            cli();
    }
}
```

WebSocket 서버 클래스와 함께 카프카에서 오는 이벤트를 처리하기 위한 WebSocket 클라이언트 클래스도 구현해야 한다

```java
public class WebSocketClientAdapter extends WebSocketClient {

    public WebSocketClientAdapter(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onMessage(String message){
        String channel = message;
    }

    @Override
    public void onOpen(ServerHandshake handshake){

    }

    @Override
    public void onClose(int code, String reaseon, boolean remote){

    }

    @Override 
    public void onError(Exception ex){
        ex.printStackTrace();
    }
}
```

카프카 토픽에서 메시지가 소비되면 헥사고날 애플리케이션은 WebSocketClientAdapter를 사용해 WebSocket 서버로

메시지를 전달한다. 

헥사고날 애플리케이션에서 마지막으로 해야할 일은 방금 생성한 포트와 어댑터를 사용해 이벤트를 보내는 

addNetworkToRouter와 getRouter 메서드를 작성하는 것이다

```java
public class RouterNetworkInputPort implements RouterNetworkUseCase {
    /** 코드 생략 **/
    @Override
    public Router addNetworkToRouter(RouterId routerId, Network network) {
        var router = fetchRouter(routerId);
        notifyEventOutputPort.sendEvent("Adding " +network.getName() + 
            " network to router " + router.getId().getUUID());
        return createNetwork(router, network);
    }

    @Override
    public Router getRouter(RouterId routerId) {
        notifyEventOutputPort.sendEvent("Retrieveing router ID" + routerId.getUUID());
        return fetchRouter(routerId);
    }
}
```

이제 addNetworkToRouter와 getRouter 메서드에서 모두 sendEvent를 호출한다. 따라서 네트워크를 추가하거나 라우터를

검색할 때마다 헥사고날 애플리케이션은 무슨 일이 발생했는지 알려주는 이벤트를 보낼것이다

마지막으로 정리하자면

#### 요약

`드라이빙 오퍼레이션`이 입력 어댑터를 호출해 헥사고날 애플리케이션의 행위를 유도한다. 드라이빙 오퍼레이션을 설명하기 위해

토폴로지 및 인벤토리 헥사고날 애플리케이션이 제공하는 입력 어댑터를 통해 데이터를 요청하는 주요 액터 역할의

프론트엔드 애플리케이션을 만들었다. 

`드리븐 오퍼레이션`측면에서는 헥사고날 애플리케이션이 카프카 같은 메시지 기반 시스템과 함께 동작하게 하는 방법을

살펴보았다. 헥사고날 시스템에 대한 메시지 기반 시스템의 영향을 더 잘 이해하기 위해 애플리케이션이 카프카로

메시지를 보내고 소비할 수 있도록 포트와 어댑터를 만들었다.

다음장에서는 자바 모듈 시스템과 쿼커스 프레임워크 기능을 통합하는 운영 수준의 헥사고날 시스템을 구축하자


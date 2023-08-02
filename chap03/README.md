## 포트와 유즈케이스를 통한 동작 처리

### 유즈케이스 작성 방법

입력 데이터, 가능한 행위, 유즈케이스 결과에 대해 상세하고 표준화된 정보를 규정하는 글로 작성된 유즈케이스를 만드는 정교한 방법이 있다

형식을 갖춘 유즈케이스는 다음과 같다
- 액터 : 인프라 엔지니어
- 목표 : 에지 라우터에 새로운 네트워크를 추가
- 범위 : 인프라 부서
- 트리거 : 다른 네트워크를 통한 네트워크 액세스를 분리하는 특별한 이유
- 입력 데이터 : 라우터 ID, 네트워크 이름, 주소 , CIDR
- 액션

    1. 라우터 ID를 찾는다
    2. 네트워크 주소가 이미 존재하는지 확인한다
    3. CIDR이 최솟값 아래인지 확인한다 
    4. 이전 검사에서 문제가 없다면 통보된 라우터에 네트워크를 추가한다

헥사고날 애플리케이션을 개발할 때 제안하는 바는 유즈케이스를 구현보다는 추상적 개념으로 설계하는 것이다.


```java
public interface RouterNetworkUseCase {
    Router addNetworkToRouter(RouterId routerId, Network network);
}
```
다음 두 가지 이유로 유즈케이스를 인터페이스로 정의한다
- 유즈케이스 목표를 달성하는 다양한 방법 제공
- 구현보다는 추상적 개념에 대한 의존성 허용

### 입력 포트를 갖는 유즈케이스 구현

헥사고날에는 드라이빙 오퍼레이션과 드리븐 오퍼레이션이라는 개념이 있다. 드라이빙 액터는 애플리케이션에 요청을 보내는 사람이며,

드리븐 액터는 애플리케이션에서 액세스 하는 외부 컴포넌트를 나타낸다.

드라이빙 액터와 헥사고날 시스템에 의해 노출되는 드라이빙 오퍼레이션 사이의 통신 흐름을 허용하기 위해 `입력포트`를 사용한다

유즈케이스는 애플리케이션이 지원해야 하는 동작을 알려주고, 입력 포트는 이러한 동작의 수행 방법을 알려준다

입력 포트는 드라이빙 액터로부터 데이터가 프레임워크 헥사곤의 어댑터 중 하나를 통해 헥사고날 시스템에 도달할 때

데이터가 흐르도록 하는 파이프와 같기 때문에 통합하는 역할을 한다. 

입력포트는 헥사고날 시스템의 교차로에 있으며, 외부에서 들어온 것들이 도메인 헥사곤과 애플리케이션 헥사곤 방향으로

가도록 변환한다. 입력 포트는 외부 시스템과의 통신을 조정하는 데도 필수적이다


입력 포트를 만드는 방법을 살펴보자

```java
@RequiredArgsConstructor
public class RouterNetworkInputPort implements RouterNetworkUseCase {
    private final RouterNetworkOutputPort routerNetworkOutputPort;

    @Override
    public Router addNetworkToRouter(RouterId routerId, Network network) {
        var router = fetchRouter(routerId);
        return createNetwork(router, network);
    }

    private Router fetchRouter(RouterId routerId) {
        return routerNetworkOutputPort.fetchRouterById(routerId);
    }

    private Router createNetwork(Router router, Network network) {
        var newRouter = NetworkOperation.createNewNetwork(router, network);
        return persistNetwork(router) ? netRouter : router;
    }

    private boolean persistNetwork(Router router) {
        return routerNetworkOutputPort.persistRouter(router);
    }
}
```


입력 포트 구현을 통해 라우터에 네트워크를 추가하는 유즈케이스 목표를 만족시키기 위해 소프트웨어가 수행해야 하는 동작에

대한 명확한 뷰를 갖게 된다. 

출력포트를 살펴보자

```java
public interface RouterNetworkOutputPort {
    Router fetchRouterById(RouterId routerId);
    boolean persistRouter(Router router);
}
```

이 출력포트는 애플리케이션이 데이터를 외부 소스로부터 얻어 유지하려는 의도를 나타낸다. 헥사곤 시스템은 외부 소스가

데이터베이스나 플랫 파일, 또는 다른 시스템인지 항상 알지 못한다. 여기서는 외부에서 데이터를 가져오려는 의도만 명시된다

입력 포트는 문제 영역에 대한 어떤 특정 항목도 포함되어 있지 않다. 주된 관심사는 도메인 서비스를 통한 내부 호출과

외부 포트를 통한 외부 호출을 조정하고 데이터를 처리하는 것이다. 입력 포트는 오퍼레이션의 실행 순서를

설정하고 도메인 헥사곤이 이해할 수 있는 형식으로 데이터를 제공한다

외부 호출은 외부 시스템으로 데이터를 전송하거나 외부 시스템에 데이터를 보관하기 위해 헥사고날 애플리케이션이 수행하는 상호작용이다.

### 출력 포트를 이용한 외부 데이터 처리

`출력 포트`는 보조 포트로도 알려져 있으며 외부 데이터를 처리하려는 애플리케이션의 의도를 나타낸다.

출력포트를 통해 시스템이 외부 세계와 통신할 수 있도록 준비하며 이러한 통신을 허용함으로써 출력 포트를 

드리븐 액터와 오퍼레이션에 연결할 수 있다. 드리븐 액터는 외부 시스템이지만 드리븐 오퍼레이션은 이런 시스템과

통신하는데 사용된다는 것을 기억하자

`리포지토리만 문제가 아니다`

데이터베이스에서 지속성과 관련된 애플리케이션의 행위를 설명하기 위해 리포지토리나 데이터 접근 객체 같은 용어를 사용하는데

익숙할 수 있다. 헥사고날 애플리케이션에서는 리포지토리를 출력포트로 대체한다

예를 들어 다음과 같은 코드가 있다 가정하자

```java
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
    PasswordResetToken findByUser(User user);
    Stream<PasswordResetToken> findAllByExpiryDateLessThan(Date now);
    void deleteByExpiryDateLessThan(Date now);

    @Modifying
    @Query("delete from PasswordResetToken t where t.expiryDate <= ?1")
    void deleteAllExpiredSince(Date now);
}
```

스프링 프레임워크의 JpaRepository 인터페이스와 @Query 애노테이션의 사용은 패스워드 데이터가 데이터베이스에서 제공된다는

개념을 강화한다

출력포트의 기본 개념은 지속성이나 모든 종류의 외부 통신이 데이터베이스 시스템에서 발생할 것이라 추론하지 않는 것이다

대신, 출력포트의 범위는 더 넓다. 예를 들면, 출력 포트는 데이터베이스, 메시징 시스템, 또는 로컬 파일 시스템이나 네트워크 파일

시스템 같은 모든 시스템과의 통신에 관심을 갖는다

헥사고날 접근 방식은 다음과 같다

```java
public interface PasswordResetTokenOutputPort{
    PasswordResetToken findByToken(String token);
    PasswordResetToken findByUser(User user);
    Stream<PasswordResetToken> findAllByExpiryDateLessThan(Date now);
    void deleteByExpiryDateLessThan(Date now)l;
    void deleteAllExpiredSince(Date now);
}
```

특정 프레임워크에서 타입을 확장하지 않고 @Qeury 같은 애노테이션 사용을 피함으로써 출력 포트를 POJO로 바꾼다

오늘 출력 포트에서 얻는 데이터는 관계형 데이터베이스에서 직접 가져올 수 있다. 내일은 같은 데이터를 애플리케이션의

REST API에서 얻을 수 있다. 애플리케이션 헥사곤 내의 컴포넌트는 데이터를 얻는 방법과 무관하기 때문에 

이러한 세부사항은 애플리케이션 헥사곤 관점에서는 필요하지 않다

이들의 주된 관심사는 액티비티를 수행하는 데 필요한 데이터의 종류를 표현하는 것이다. 그리고 이러한 애플리케이션 헥사곤 컴포넌트가

어떤 데이터를 필요로 하는지 정의하는 방법은 도메인 헥사곤의 엔티티와 값 객체를 기반으로 한다.

이러한 방법으로 출력 포트가 필요로 하는 데이터 타입을 명시하는 경우, 같은 출력 포트로 다양한 어댑터를 연결할 수 있다.

`어디에 출력 포트를 사용하는가?`

출력 포트는 입력 포트를 가진 유즈케이스를 구현할 때 명시적으로 사용된다.

```java
@RequiredArgsConstructor
public class RouterNetworkInputPort implements RouterNetworkUseCase {
    private final RouterNetworkOutputPort routerNetworkOutputPort;

    @Override
    public Router addRouterNetworkToRouter(RotuerId routerId, Network network) {
        var router = fetchRouter(routerId);
        return createNetwork(router, network);
    }

    private Router fetchRouter(RouterId routerId){
        return routerNetworkOutputPort.fetchRouterById(routerId);
    }

    private Router createNetwork(Router router, Network network) {
        var newRouter = NetworkOperation.createNewNetwork(router, network);
        return persistNetwork(router) ? newRouter : router;
    }

    private boolean persistNetwork(Router router){
        return routerNetworkOutputPort.persistRouter(router);
    }
}
```

유즈케이스에서 정의되고 입력 포트에 의해 구현되는 오퍼레이션 중 일부 오퍼레이션은 외부 소스에서 데이터를 가져오거나

데이터를 유지하는 역할을 한다. 이것이 유즈케이스 목표를 달성하는 데 필요한 데이터를 제공하기 위해 출력포트가 필요한 이유다


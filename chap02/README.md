## 도메인 헥사곤으로 비즈니스 규칙 감싸기

### 도인 엔티티의 순수성

문제 영역을 모델링할 때 주된 초점은 가능한 한 정확하게 실제 시나리오를 코드로 변환하는 것이다. 

문제 영역 모델릉의 핵심은 엔티티를 만드는 것이다. 엔티티가 비즈니스 요구사항과 밀접한 관계를 가져야 하기 때문에

이러한 엔티티를 기술적인 요구사항으로부터 보호하기 위해 노력해야 한다. 비즈니스 관련 코드와 기술 관련 코드가 

혼동되는 것을 방지하기 위해 노력해야 한다. (기술적이란 뜻은 소프트웨어 맥락에서만 존재하고 의미있는 것)

도메인 엔티티는 비즈니스 관심사만 처리한다는 점에서 순수해야 한다. 

### 값 객체를 통한 서술력 현상

문제 영역을 모델링하기 위해 프로그래밍 언어의 내장 타입만 사용하는 것으로는 충분하지 않다. 시스템의 본질과 목적을

더욱 명확하게 하기 위해 이러한 내장 타입, 심지어 생성한 타입도 잘 정의된 값 객체로 감싸야 한다.

의미를 전달하려는 이 같은 노력은 값 객체에 대한 다음 두 가지 기본 특성을 기반으로 한다
- 값 객체는 불변이다
- 값 개체는 식별자를 갖지 않는다 

값 객체는 폐기할 수 있어야하고 엔티티나 다른 객체 타입을 구성하는 데 사용할 수 있는 쉽게 교체 가능한 객체여야 한다

`엔티티 속성에 값 객체를 사용하지 않는 예를 보자`

```java
public class Event implements Comparable<Event> {
    private EventId id;
    private OffsetDateTime timestamp;
    private String protocol;
    private String activity;
    ...
}
```

이러한 타입은 네트워크 트래픽 액티비티에 다음과 같이 남을 수 있다

    casanova.58183 > menuvivofibra.br.domain

이 경우 문자열이기 때문에 다음과 같이 호스트나 목적지 호스트를 조회하려는 클라이언트에게 부담을 준다

```java
var srcHost = event.getActivity().split(">")[0]
```

`다음은 값 객체를 사용한 예시다`

```java
public class Activity {
    private String description;
    private final String srcHost;
    private final String dstHost;
}

public class Event implements Comparable<Event> {
    private EventId id;
    private OffsetDateTime timestamp;
    private String protocol;
    private Activity activity;
    ...
}
```

다음 코드에서 볼 수 있듯 클라이언트 코드가 더 명확해지고 표현력도 좋아진다.

또한 클라이언트는 출발지 호스트와 목적지 호스트를 조회하기 위해 데이터 자체를 처리할 필요가 없다

```java
var srcHost = event.getActivity().retrieveSrcHost();
```

## 애그리게잇을 통한 일관성 보장

관련 엔티티와 값 객체의 그룹이 함께 전체적인 개념을 설명해야 하는 경우에는 `애그리게잇`을 사용해야 한다.

애그리게잇 내부의 객체들은 일관되고 격리된 방식으로 동작한다. 이러한 일관성을 달성하려면 애그리게잇 객체에

대한 모든 변경은 해당 애그리게잇에 부과되는 변경사항에 따라 결정되는 것이 보장돼야 한다

애그리게잇 영역과 상호작용할 진입점을 정의해야한다. 진입점은 애그리게잇 루트로 알려져 있으며, 애그리게잇의 일부인 엔티티와 값

객체들에 대한 참조를 유지한다. 애그리게잇이 제공하는 바운더리를 통해 바운더리 내부의 객체가 수행하는 오퍼레이션에서

더 나은 일관성을 보장할 수 있게 된다.

성능과 확장성 관점에서 항상 애그리게잇을 가능한 한 작게 유지하기 위해 노력해야 한다. 이유는 큰 애그리게잇 객체는 더 많은 메모리를 사용한다.

동시에 너무많은 애그리게잇 객체를 인스턴스로 만들면 JVM의 전반적 성능이 저하될 수 있다.

애그리게잇 루트가 라우터, 값객체 IP, Network, 엔티티를 switch라고 가정하자

```java
public class Network {
    private final IP address;
    private final String name;
    private final int didr;

    public Network(IP address, String name, int cider) {
        if (cider < 1 || cidr >32) {
            throw new IllegalArgumentException();
        }
        this.address = address;
        this.cidr = cidr;
    }
}
```

그리고 엔티티를 다음과 같이 작성한다

```java
public class Switch {
    private SwitchType type;
    private SwitchId switchId;
    private List<Network> networks;
    private IP address;

    public Switch addNetwork(Network network) {
        var networks = new ArrayList<>(Arrays.asList(network));
        networks.add(network);
        return new Switch(this.switchType, this.switchId, networks, this.address);
    }
}
```

지금까지 생성한 값 객체를 토대로 애그리게잇 루트를 갖는 바운더리를 만든다. 다음이 Router 엔티티 클래스의 역할이다

```java
public class Router {
    private final RouterType routerType;
    private final RouterId routerid;
    private Switch networkSwitch;

    public static Predicate<Router> filterRouterByType(RouterType routerType) {
        return routerType.equals(RouterType.CORE) ? Router.isCore() : Router.isEdge();
    }

    public void addNetworkToSwitch(Network network){
        this.networkSwitch = networkSwitch.addNetwork(network);
    }

    public Network createNetwork(IP address, String name, long cidr) {
        return new Network(address, name, cidr);
    }

    ...
}
```

지금은 라우터가 내부 통신과 외부 통신 모두에서 사용하는 IP 주소 리스트를 나타내기 위해 값 객체를 추가했다.

새로운 네트워크를 생성하는 메서드가 있고 다른 하나는 스위치에 기존 네트워크를 연결하는 메서드도 있다.

이러한 메서드를 애그리게잇 루트에 두면 해당 컨텍스트 하위의 모든 객체를 처리하는 책임을 애그리게잇 루트에 위임하게 된다

따라서 이러한 객체 집합을 다루는 경우 일관성이 향상된다. 

이것은 또한 엔티티가 어떤 종류의 동작도 없는 데이터 객체일 뿐인 빈약한 도메인 모델을 방지하기 위한 노력이기도 하다

### 도메인 서비스 활용

문제 영역을 모델링 할 때 당면한 작업이 도메인 헥사곤, 엔티티, 값 객체, 애그리게잇 등 어떤 객체 범주에도 적합하지 않은

상황에 직면하게 될 것이다. 앞서 라우터 항목 조회를 담당하는 메서드를 Router엔티티에서 제거한 상황을 만났는데

일반적으로 다른 라우터를 리스트로 만들지 않기 때문에 해당 메서드는 잘못된 위치에 있는 것처럼 보였다.

이처럼 번거로운 상황을 처리하기 위해 라우터를 리스트로 만드는 메서들르 별도의 객체로 리팩터링했는데 

이러한 객체를 가리켜 `도메인 서비스` 라고 부른다

### 정책 패턴과 명세 패턴을 활용한 비즈니스 규칙 처리

정책 패턴과 명세 패턴은 코드의 비즈니스 규칙을 더 잘 구조화하기 위한 두가지 패턴이다

`정책`은 전략으로도 알려져 있으며 코드 블록으로 문제 영역의 일부를 캡슐화하는 패턴이다. 전략패턴에 익숙한 사람들은

알고리즘이라는 용어를 캡슐화된 코드 블록을 설명하는데 사용할 수 있다. 정책의 주된 특성은 제공된 데이터에 대해

어떤 작업이나 처리를 한다는점이다. 정책은 커플링을 피하기 위해 의도적으로 엔티티와 값 객체를 분리해 유지한다.

이러한 디커플링은 직접적인 영향이나 부작용 없이 한 부분을 발전시키는 잘 알려진 혜택을 제공한다

반면, `명세`는 객체의 특성을 보장하는데 사용되는 조건이나 프레디케이트와 같다. 그러나 명세의 특징은 단순히 논리적인

연산자보다는 더 표현적인 방법으로 프레디케이트를 캡슐화한다는 것이다. 이러한 명세들을 캡슐화하면

재사용할 수 있고 함께 결합해서 문제영역을 더 잘 표현할 수 있다

함께 사용한다면 정책과 명세는 코드 전반에 걸쳐 비즈니스 규칙의 견고성과 일관성을 향상시키는 믿을만한 기법이다.

명세는 정책에 적합한 개체만 처리되는 것을 보장한다. 정책을 통해 다양하면서도 쉽게 변경할 수 있는 알고리즘들을 자유자재로 활용할수있다

명세를 사용해 NetworkOperation 서비스를 리팩터링해보자

```java
public interface Specifiaction<T> {
    boolean isSatisfiedBy(T t);
    Specification<T> and(Specification<T> specification);
}
```

그리고 다른 명세와 결합할 수 있도록 하는 and 메서드를 구현하는 추상클래스를 만들자

```java
public abstract class AbstractSpecifiaction<T> implements Specification<T> {
    public abstract boolean isSatisfiedBy(T t);
    public Specification<T> and(final Specification<T> specification) {
        return new AndSpecification<T>(this,specification);
    }
}
```

```java
public class AndSpecification<T> extends AbstractSpecification<T> {
    private Specification<T> spec1;
    private Specification<T> spec2;

    public boolean isSatisfiedBy(final T t) {
        return spec1.isSatisfiedBy(t) && spec2.isSatisfiedBy(t);
    }
}
```

자체적인 명세를 생성할 준비는 끝났다. 첫 번째 명세는 새로운 네트워크 생성에 허용되는 최소 CIDR을 제한하는

비즈니스 규칙에 관한 것이다. 코드는 다음과 같다

```java
if (cidr < MINIMUM_ALLOWED_CIDR)
    throw new IllegalArgumentException();
```
이에 해당하는 명세는 다음과 같다

```java
public class CIDRSpecification extends AbstractSpecification<Integer> {
    public final static int MINIMUM_ALLOWED_CIDR = 8;

    @Override
    public booelan isSatisfiedBy(Integer cidr) {
        return cird > MINIMUM_ALLOWED_CIDR;
    } 
}
```

이러한 명세들을 작성후 명세들을 사용하기 위해 다음과 같이 새로운 네트워크의 생성을 담당하는 도메인 서비스를 리팩터링하자

사용하는 예시는 프로젝트 코드에 kotlin으로 명시했으니 생략한다.


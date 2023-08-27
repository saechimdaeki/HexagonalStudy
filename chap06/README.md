## 도메인 헥사곤 만들기

도메인 헥사곤은 헥사고날 애플리케이션의 개발을 시작하는 곳이다. 도메인을 기반으로 다른 모든 헥사곤을 도출한다

핵심적인 기본 비즈니스 로직은 도메인 헥사곤에 있기 때문에 도메인 헥사곤은 헥사고날 시스템의 두뇌라고 할 수 있다


### 문제 영역 이해 

코어 라우터는 코어 라우터와 에지 라우터 모두에 연결할 수 있다는 사실을 고려해 문제 영역의 모델링을 시작한다. 에지 라우터는 

스위치와 네트워크에 연결된다.


![image](https://github.com/saechimdaeki/Dev-Diary/assets/40031858/930b0a8e-2a82-4915-8502-de31d4cad975)


코어라우터는 더 빠르고 높은 트래픽 부하를 처리하며, 스위치와 스위치의 네트워크에서 생성된 트래픽을 직접적으로

처리하지 않는다. 반면, 에지 라우터는 스위치의 네트워크에서 생성된 트래픽을 직접 처리한다.

이 시나리오에서 에지라우터는 다른 에지 라우터와 연결할 수 없고 코어 라우터와 스위치에만 연결된다.

### 값 객체 정의

값 객체는 더 정교한 값 객체와 가장 중요한 엔티티를 만드는데 사용되는 기반요소이므로 먼저 값 객체를 생성하는 것을

시작하기를 권장한다.

1. id 값 객체 클래스로 시작

```java
@RequiredArgsConstructor
public class Id {
    private final UUID id;

    public static Id withId(String id){
        return new Id(UUID.fromString(id));
    }

    public static id withoutId(){
        return new Id(UUID.randomUUID());
    }
}
```

2. 사용하는 열거형 클래스들을 만든다

```java
public enum Vendor {
    CISCO,
    NETGEAR,
    HP,
    //..
}

public enum Model{
    XYZ0001,
    XYZ0002,
    //...
}

public enum Protocol{
    IPV4,
    IPV6;
}

public enum RouterType{
    EDGE,
    CORE;
}

public enum SwitchType{
    LAYER2,
    LAYER3;
}

```

3. 모든 라우터와 스위치는 위치를 갖기에 Location 값 객체를 생성해야 한다

```java
public class Location {
    private String address;
    private String city;
    private String state;
    private int zipCode;
    private String country;

    private float latitude;
    private final longitude;
}
```

위 값 객체들은 전체 시스템을 구성하는 다른 값 객체와 엔티티들의 기본적인 기반 요소이기 때문에 가장 중요한 객체들이다

이를 기반으로 더 정교한 값 객체를 생성할 수 있다.

1. IP값 객체를 작성하자

```java
public class Ip {
    private String ipAddress;
    private Protocol;
    private Ip(String ipAddress){
        if (ipAddress == null)
            throw new Exeption();
        this.ipAddress = ipAddress;
        if (ipAddress.length()<= 15)
            this.protocol = Protocol.IPV4;
        else 
            this.protocol = Protocol.IPV6;
    }
}
```

2. 그다음 스위치에 추가될 네트워크를 나타내는 값 객체를 생성한다


```java
public class Network {
    private IP networkAddress;
    private String networkName;
    private int networkCidr;

    public Network(Ip networkAddress, String networkName, int networkCidr) {
        if (networkCidr <1 || networkCidr >32) {
            throw new Exception();
        }
        this.networkAddress = networkAddress;
        this.networkName = networkName;
        this.networkCidr = networkCidr;
    }
}
```

### 엔티티와 명세 정의

엔티티를 특징짓는 것은 식별자와 비즈니스 규칙, 데이터의 존재라는 점을 기억하자

`Equpment와 Router 추상 엔티티`

```java
public abstract class Equipment {
    protected Id id;
    protected Vendor vendor;
    protected Model model;
    protected IP ip;
    protected Location location

    public static Predicate<Equpment> getVendorPredicate(Vendor vendor){
        return r-> r.getVendor().equals(vendor);
    } 
}
```

Equpment에서 파생되는 Router 추상 클래스를 생성한다

```java
public abstract class Router extends Equpment {
    protected final RouterType routerType;

    public static Predicate<Router> getRouterTypePredicate(RouterType routerType) {
        return r-> r.getRouterType().euqlas(routerType)
    }
    /* 생략 */
}
```]


`코어 라우터 엔티티와 명세`

```java
public class CoreRouter extends Router {
    /** 생략 **/
    public Router addRouter(Router anyRouter) {
        var sameCountryRouterSpec = new SameCountrySpec(this);
        var sameIpSpec = new SameIpSpec(this);
        sameCountryRouterSpec.check(anyRouter);

    }
}
```

코어 라우터는 다른 코어 라우터와 에지라우터에 연결될 수 있다. 이러한 동작을 CoreRouter에서 허용하려면 파라미터로

Router 추상 클래스 타입을 받는 addRouter 메서드를 만들어야 한다. 또한 에지 라우터가 코어 라우터와 

같은 국가 안에 있는지 확인하기 위해 SameCountrySpec 명세를 사용해야 한다

이 규칙은 코어 라우터를 다른 코어 라우터에 연결하려고 할 때는 적용 되지 않는다

에지 라우터가 항상 코어 라우터와 같은 국가에 있는지 확인하는 스펙을 명시하자

```java
@AllArgsConstructor
public class SameCountrySpec extends AbstractSpecification<Equipment> {
    private Equipment equipment;
}
```

SameCountrySpec 생성자는 equipment 프라이빗 타입의 필드를 초기화하는 데 사용하는 Equipment 객체를 받는다

SameCountrySpec을 구현하면서 isSatisfiedBy 메서드를 다음과 같이 재정의한다

```java
@Override 
public boolean isSatisfiedBy(Equipment anyEquipment) {
    if (anyEquipment instanceof CoreRouter) 
        return true;
    else if(anyEquipment != null && this.equipment != null) {
        return this
            .equipment
            .getLocation()
            .getCountry()
            .equals(anyEquipment.getLocation().getCountry());
    } else
        return false;
}
```

SameCountrySpec 구현은 코어 라우터에는 적용되지 않는다. 이것이 객체가 CoreRouter인 경우 항상 true를 반환하는 이유.

`에지 라우터와 명세`

```java
public class EdgeRouter extends Router {
    private Map<Id,Switch> switches;

    public void addSwitch(Switch anySwitch) {
        var sameCountryRouterSpec = new SameCountrySpec(this);
        var sameIpSpec = new SameIpSpec(this);

        sameCountryRouterSpec.check(anySwitch);
        sameIpSpec.check(anySwitch);

        this.switches.put(anySwitch.id, anySwitch);
    }

}
```

addSewitch의 목적은 스위치를 에지 라우터에 연결하는 거다. 

### 도메인 서비스 정의

`라우터 서비스`

앞에서 Router, CoreRouter, EdgeRouter 엔티티를 구현할 때 라우터 컬렉션을 필터링하는 프레디케이트를 반환하는 메서드도 작성했다.

도메인 서비스를 사용하면 다음과 같이 그러한 컬렉션을 필터링하는 다음과 같은 프레디케이트를 사용할 수 있다.

```java
public class RouterService {
    public static List<Rotuer> filterAndRetrieveRouter(List<Router> routers, Predicate<Equipment> routerPredicate){
        return routers
            .stream()
            .filter(routerPredicate)
            .collect(Collectors.<Router>.toList());
    }

    public static Router findById(Map<Id, Router> routers, Id id){
        return routes.get(id);
    }
}
```

`스위치 서비스`

```java
public class SwitchService {
    public static List<Switch> filterAndRetriveSwitch(List<Switch> swithces,
    Predicate<Switch> switchPredicate) {
        return switches
                .stream()
                .filter(switchPredicate)
                .collect(Collectors.<Switch>toList());
    }

    public static Switch findById(Map<Id, Switch> switches, Id id) {
        return switches.get(id);
    }
}
```
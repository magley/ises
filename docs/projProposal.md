# Експертски систем за информациону безбедност

-   Филип Контић, SV 20/2020
-   Лазар Магазин, SV 25/2020

## Мотивација

Информациона безбедност је веома важна тема у савременом рачунарству. Скоро да не постоји софтвер који не комуницира преко интернета или који не приступа локалним подацима. Развој безбедног софтвера подразумева како дизајн и моделовање безбедносних захтева, тако и митигацију у случају пропуста. Ово је скупо и захтева много времена и људске експертизе.

По OWASP Top 10 листи се закључује да су најчешћи безбедносни пропусти тривијалне мане које инжењери занемарују из незнања или мањка времена. Међутим, штета коју изазове злоупотреба тих пропуста се прекасно детектује. Употребом система који аутоматски прати логове апликације и благовремено реагује на њих, било би могуће умањити последице напада на апликацију.

## Преглед проблема

Потребно је развити генеричну апликацију за куповину артикала. Платформа треба имати _secure auditing and logging_ помоћу којег се детектује сумњиво понашање корисника.

Сама платформа је неадекватно заштићена од нападача. Ово се огледа како у њеној архитектури тако и у њеним функционалностима. Идеја иза овога је да се безбедносна контрола и мониторинг уграде у саму апликацију. Тиме бисмо демонстрирали како би се експертски систем за безбедност интегрисао у производ који је развијен без размишљања о самој безбедности (нпр. стартап где је примарни фокус био да се што пре направи MVP).

Апликација по својој природи треба да стриктно сагледа акције корисника. Пријава корисника на систем, објављивање артикала на продају, куповина артикала, остављање рецензије, качење фајлова су неке од акција које се прате у систему и које корисник може злоупотребити.

Апликација би за сваку сумњиву радњу и контекст корисника (описаног IP адресом или корисничким налогом) давала "казнене бодове". Казнени бод представља напомену о
сумњивом понашању корисника и у себи укључује вредност бодова, тип акције и опис.
Казнени бод не гарантује да је корисник заиста покушао да злоупотреби систем, нити
се резултује казном. На основу сакупљених казнених бодова, корисник би био адекватно кажњен. У најгорим ситуацијама где се детектује опасност већих размера, апликација би привремено постала недоступна обичним корисницима.
Дакле, крајњи корисник нашег KBS-а чини администрација и/или део развојног тима који је задужен за безбедност.

Слична решења која смо пронашли пате од преуског домена применљивости [1] [2]. У данашње време су много популарнија решења заснована на техникама дубоког учења [3] [4]. Овакви производи су најчешће комерцијални [5].
Наше решење је засновано на онтолошком _if-then_ закључивању, отвореног је кода, и дизајнирано је да се може применити у веб апликацијама других домена.

## Методологија рада

Три корисничке улоге у софтверу су: непријављени корисник, корисник, администратор.

**Непријављени корисници** могу да врше претрагу артикала као и да прегледају саме артикле. Такође им је омогућена регистрација и пријава на систем.

**Пријављени корисници** могу да постављају артикле на продају, купују артикле других корисника и да остављају рецензију на артикле и кориснике. Пријављени корисник има "рачун" везан за налог. Уплата на рачун се симулира. Поред овога, корисници могу да мењају своје личне податке.

**Администратори** имају увид у све акције (постављање артикала, куповина артикала, рецензија) на нивоу система као и на нивоу корисника (које захтеве је корисник упутио на систем и сл.). Администратори додатно могу да праве извештаје.

### Улази

Улазне податке уноси корисник током рада са апликацијом:

-   контекст захтева (симулира се, део је сваког захтева):
    -   IP адреса извора
    -   IP адреса дестинације
    -   Port извора
    -   Port дестинације
    -   Протокол (`TCP`)
    -   Endpoint
-   кориснички захев:
    -   регистрација корисника (име, презиме, имејл адреса, лозинка, профилна слика)
    -   пријава корисника (имејл адреса, лозинка)
    -   уплата на рачун (износ)
    -   објава артикла (назив, опис, слика, цена)
    -   куповина артикла
    -   рецензија (артикал/корисник, оцена, коментар)

За сваку акцију се прати и корисник који је ту акцију извршио (његов налог, евентуално и IP адреса).

Наведени улази и њихови подаци нису коначни и склони су проширењу/измени/уклањању.

### Излази

-   казна (корисник, казнени бодови, тип)
-   предузета мера:
    -   активација аларма
    -   одбијање захтева
    -   привремено блокирање корисника
    -   трајно блокирање корисника
    -   привремени прекид рада система

Наведени излази нису коначни.

### База знања

База знања је сачињена од продукционих правила везаних за безбедност веб апликација са плаћањем.
Правила су писана ручно, од којих су нека динамички направљена употребом шаблона.
Испод су дати примери правила који чине базу знања..
Корисници преко интеракције са апликацијом попуњавају радну меморију чињеницама и догађајима.
Систем евалуира правила и на основу њих окида правила која производе нове чињенице или извршавају пословну логику.

Испод су наведена правила и примери употребе CEP, FC, BC и Template механизама.
Овај списак ће се по потреби проширити.

Пример простих правила:

```ruby
class Note {
    user,
    points,
    type,
    message,
}

class Request {
    srcIp,
    srcPort,
    destIp,
    destPort,
    protocol,
    endpoint,
    user,
    timestamp,
    type,
    ...
}

class Alarm {
    type,
    severity,
    desc,
}

class Block {
    user,
    duration,
}

class PurchaseBlock {
    user,
    duration,
}


when
    count(Request($user) over 10s) > 50
then
    Note($user, 5, Type.Requests, "Too many requests")


when
    count_unique(Login(same $password, count $email) over 6h) > 5
then
    new Note(None, 2, Type.Auth_Pass, "Weak password {password}")
    new WeakPassword($password)


when
    Login($password)
    WeakPassword($password)
then
    user.shouldChangePassword = true;


when
    count_unique(WeakPassword) > 50
then
    new Alarm(Type.Auth_Pass, Severity.Medium, "Please increase password complexity for new users.")


when
    count_unique(WeakPassword) > 100
then
    for user in [ unique(list(WeakPassword)).user ]
        user.Notify("Your password is too weak.")
    end


when
    accumulate(
        Note($user, $points, Type.Transaction) over 1w,
        sum($points)
    ) > 15
then
    new PurchaseBlock($user, 48h)
```

Пример сложеног ланца правила (**CEP** и **FC**):

```ruby

when # Level 1
    count(FailedLogin($ip, $email) over 1min) > 5
then
    new Note($ip, 3, Type.Auth, "Brute forcing user {email}")

when # Level 2
    accumulate(
        Note($ip, $points, Type.Auth) over 24h,
        sum($points)
    ) > 100
then
    new Block($ip, 24h)


when # Level 3
    count_unique(Block over 24h) > 100
then
    new Alarm(Severity.High)
    new Attack(Type.AuthenticationAttack)


when # Level 1
    count(Request($srcIp, $destIp, $srcIp == $destIp) over 1min) > 1000
then
    new Alarm(Severity.SuperHigh)
    new Attack(Type.DoS) # TCP Flood Attack


when # Level 1
    $t1: Transaction($ip1, $accountId1, $time1) &&
    $t2: Transaction($ip2, $accountId2, $time2) &&
    $t1 != $t2 &&
    ($time1 - $time2) < 1min
then
    new Alarm(Severity.SuperHigh)
    new Attack(Type.AccessControlAttack)


when # Level 1
    Transaction($ip, $port1) &&
    ($port1 != 3000 || ($ip not in internalIpAddresses))
then
    new Alarm(Severity.Medium)
    new Attack(Type.AccessControlAttack) # CORS


when # Level 1
    SearchQuery($ip, $queryText) &&
    queryText like "' UNION SELECT"
then
    new Alarm(Severity.Low)
    new Attack(Type.Injection)
    new Note($ip, 5, Type.Injection)
    # Hibernate protects us against SQLi, but someone tried to attack either way

when # Level 2
    accumulate(
        Note($ip, $points, Type.Injection) over 10h,
        sum($points)
    ) > 25
then
    new Block($ip, 30m)


when # Level 4
    Attack($type1, $severity1) &&
    Attack($type2, $severity2) &&
    $type1 != $type2 &&
    ($severity1 >= Severity.SuperHigh || $severity2 >= Severity.SuperHigh)
then
    new Alarm(Severity.SuperHigh)
    Server.Config.SetAvailability(ServerAvailability.RestrictAccessToAdmins)


when # Level 4
    Attack($type1, $severity1) &&
    $severity1 >= Severity.Critical
then
    new Alarm(Severity.Critical)
    Server.Config.SetAvailability(ServerAvailability.RestrictAccessToAdmins)
    # Do something else...

when # Level 4
    Attack($type1) &&
    Attack($type2) &&
    Attack($type3) &&
    Attack($type4) &&
    $type1 != $type2 != $type3 != $type4
then
    new Alarm(Severity.Critical)
    Server.Config.SetAvailability(ServerAvailability.RestrictAccessToAdmins)
```

```ruby
when
    $r: Request($ip)
    Block($ip)
then
    $r.setRejected(true);
    # Finish the request early, returning 403 Forbidden.
```

**Backward chaining**:

1. Хијерархијски RBAC: Сваки endpoint зависи од једне (или потенцијално више)
   пермисија које мора имати корисник који шаље захтев. Улога има ниједну или
   више пермисија. Улога може имати највише једну улогу као родитеља, при чему она
   наслеђује пермисије родитељске улоге. Сваки корисник има једну улогу.
   Неаутентификовани корисник нема улогу.

Пример стабла улога:

```
        [SuperAdmin]
           |      \
         Admin     \
           |        \
      [Ban users]  [Unlock system]
```

Пример уланчавања уназад:

```ruby
declare Role {
    id: int
    name: string,
    permissions: Permission[],
    parent: Role
}

declare Permission {
    id: int,
    name: string
}

declare User {
    ...,
    role: Role
}

query RoleHasPermission(role: Role, permission: Permission) {
    Role(id == $role.id, $permissions: permissions)
    Permission(id == $permission.id, this in $permissions)
    or
    $parentRole: Role($role.parent == this)
    RoleHasPermission($rparentRole, $permission)
}

query UserHasPermission(user: User, permission: Permission) {
    User(id == user.id, $role: role)
    RoleHasPermission($role, $permission)
}
```

Пример употребе уланчавања уназад у правилима:

```ruby
class RbacQuery {
    user: User,
    permission: Permission,
    granted: Bool
}

rule "Check access for user"
when
    $q: RbacQuery($user: user, $permission: permission)
    !UserHasPermission($user, $permission)
then
    $q.granted = false;
end
```

Употреба у коду:

```java
void preAuthorize2(String permissionName) {
    User user = authFacade.getUser();
    Permission permission = permissionService.findByName(permissionName);

    RbacQuery query = new RbacQuery(user, permission);
    ksession.insert(query);
    ksession.fireAllRules();

    if (query.granted == false) {
        throw UnauthorizedException();
    }
}

@Post("/user/ban?userId")
void banUser(Long userId) {
    preAuthorize2("ban_users");
    ...
}
```

2. Извештавање система ради препознавања најчешћих догађаја који доводе до нарушавања безбедности. Пример:

-   добавити све кориснике који су у последњих месец дана блокирани бар три пута због високих транзакција
-   добавити IP адресе које су спамовале сервер у периоду између два датума
-   читање логова... **TODO**

**Template**-и ће се реализовати параметризацијом правила користећи UI (или учитавањем
података из xlsx табеле). Пример:

```ruby
when
    count(Transaction($user, amount > 1000) over 1h) >= 3
then
    new Note($user, 2, Type.Transaction, "Suspicious transactions")

when
    count(Transaction($user, amount > 5000) over 6h) >= 3
then
    new Note($user, 3, Type.Transaction, "Suspicious transactions")

# Template
when
    count(Transaction($user, amount > ${AMOUNT}) over ${TIME}) >= ${NUM_OF_TRANSACTIONS}
then
    new Note($user, ${POINTS}, Type.Transaction, "Suspicious transactions")
```

```ruby
when
    $user: User
    $user.balance <= 0.5 * (
        $user.balance + accumulate(Transaction($user, $amount) over 1h, sum($amount))
    )
then
    new Note($user, 3, Type.Transaction, "User spent too much, account possibly breached")

when
    $user: User
    $user.balance <= 0.1 * (
        $user.balance + accumulate(Transaction($user, $amount) over 24h, sum($amount))
    )
then
    new Note($user, 7, Type.Transaction, "User spent too much, account possibly breached")

when
    $user: User
    $user.balance <= 0.1 * (
        $user.balance + accumulate(Transaction($user, $amount) over 1h, sum($amount))
    )
then
    new Note($user, 10, Type.Transaction, "User spent too much, account possibly breached")

# Template
when
    $user: User
    $user.balance <= ${REMAINING_BALANCE} * (
        $user.balance + accumulate(Transaction($user, $amount) over ${TIME}, sum($amount))
    )
then
    new Note($user, ${POINTS}, Type.Transaction, "User spent too much, account possibly breached")
```

## Пример резоновања

Постоји корисник `U` и артикли `A1`, `A2`, `A3`.

0. У радној меморији се налазе следеће чињенице:

```java
Transaction($U, $A1, 1500, 10/10/2024 18:05)
Transaction($U, $A2, 1700, 10/10/2024 18:26)
Note($U, 2, Type.Transaction, "Suspicious transaction" 9/10/2024 18:05)
Note($U, 3, Type.Transaction, "Suspicious transaction" 8/10/2024 18:05)
Note($U, 4, Type.Transaction, "Suspicious transaction" 7/10/2024 18:05)
Note($U, 5, Type.Transaction, "Suspicious transaction" 6/10/2024 18:05)
```

1. Корисник `U` жели за купи артикал `A3` који кошта `2800`.
2. У систем улази `Transaction($U, $A3, 2800, 10/10/2024 18:44)`
3. Чињеница се пропушта кроз правила.
4. LHS следећег правила је задовољен:

```ruby
when
    count(Transaction($U, amount > 1000) over 1h) >= 3
```

5. Окида се RHS истог тог правила:

```ruby
then
    new Note($U, 2, Type.Transaction, "Suspicious transactions")
```

6. Та чињеница се пропушта кроз базу знања и задовољава се следећи LHS:

```ruby
when
    accumulate(
        Note($U, $points, Type.Transaction) over 1w,
        sum($points)
    ) > 15
```

7. Због тога се окида RHS одговарајућег правила:

```ruby
then
    new PurchaseBlock($U, 48h)
```

## Литература

[1] Rajput, Quratulain, et al. "Ontology based expert-system for suspicious transactions detection." Computer and Information Science 7.1 (2014): 103.

[2] Zhang, Guo-Yin, Jian Li, and Guo-Chang Gu. "Research on defending DDoS attack-an expert system approach." 2004 IEEE International Conference on Systems, Man and Cybernetics (IEEE Cat. No. 04CH37583). Vol. 4. IEEE, 2004.

[3] Niyaz, Quamar, Weiqing Sun, and Ahmad Y. Javaid. "A deep learning based DDoS detection system in software-defined networking (SDN)." arXiv preprint arXiv:1611.07400 (2016).

[4] Naomi, J. Fenila, et al. "Intelligent transaction system for fraud detection using deep learning networks." Journal of Physics: Conference Series. Vol. 1916. No. 1. IOP Publishing, 2021.

[5] https://youverify.co/, Датум приступа: 18.4.2024.

[/] https://www.piranirisk.com/blog/how-to-identify-unusual-or-suspicious-transactions, Датум приступа: 19.4.2024.

[/] https://www.notiones.eu/2023/07/13/use-of-expert-systems-in-cyber-defense/, Датум приступа: 19.4.2024.

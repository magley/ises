# Експертски систем за информациону безбедност

- Филип Контић, SV 20/2020
- Лазар Магазин, SV 25/2020

## Мотивација

Информациона безбедност је веома важна тема у савременом рачунарству. Скоро да не постоји софтвер који не комуницира преко интернета или који не приступа локалним подацима. Развој безбедног софтвера подразумева како дизајн и моделовање безбедносних захтева, тако и митигацију у случају пропуста. Ово је скупо и захтева много времена и људске експертизе. 
Употребом алата за аутоматизацију ових задатака би се знатно уштедело на времену и новцу потребном за препознавање и реаговање на малициозно понашање.

## Преглед проблема

Потребно је развити генеричну апликацију за куповину артикала. Платформа треба имати  _secure auditing and logging_ помоћу којег се детектује сумњиво понашање корисника.

Сама платформа је неадекватно заштићена од нападача. Ово се огледа како у њеној архитектури тако и у њеним функционалностима. Идеја иза овога је да се безбедносна контрола и мониторинг уграде у саму апликацију. Тиме бисмо демонстрирали како би се експертски систем за безбедност интегрисао у производ који је развијен без размишљања о самој безбедности (нпр. стартап где је примарни фокус био да се што пре направи MVP).

Апликација по својој природи треба да стриктно сагледа акције корисника. Пријава корисника на систем, објављивање артикала на продају, куповина артикала, остављање рецензије, качење фајлова су неке од акција које се прате у систему и које корисник може злоупотребити.

Апликација би за сваку сумњиву радњу и контекст корисника (описаног IP адресом или корисничким налогом) давала "казнене бодове". На основу сакупљених казнених бодова, корисник би био адекватно кажњен у одређеној мери. У ситуацијама где се детектује потенцијална опасност, апликација би привремено постала недоступна.
Дакле, крајњи корисник нашег KBS-а чини администрација и/или део развојног тима који је задужен за безбедност. 

Слична решења која смо пронашли пате од преуског домена применљивости [1] [2]. У данашње време су много популарнија решења заснована на техникама дубоког учења [3] [4]. Овакви производи су најчешће комерцијални [5].
Наше решење је засновано на онтолошком _if-then_ закључивању, отвореног је кода, и дизајнирано је да се може применити у веб апликацијама других домена.

## Методологија рада

Три корисничке улоге у софтверу су: непријављен корисник, корисник, администратор.

Непријављени корисници могу да врше претрагу артикала као и да прегледају саме артикле. Такође им је омогућена регистрација и пријава на систем.

Пријављени корисници могу да постављају артикле на продају, купују артикле других корисника и да остављају рецензију на артикле и кориснике. Пријављени корисник има "рачун" везан за налог. Уплата на рачун се симулира. Поред овога, корисници могу да мењају своје личне податке.

Администратор има увид у све акције (постављање артикала, куповина артикала, рецензија) на нивоу система као и на нивоу корисника (које  захтеве је корисник упутио на систем и сл.). Администратор додатно може да прави извештаје.

### Улази

Улазне податке уноси корисник током рада са апликацијом:

- контекст корисника (претходне казне)
- кориснички захев:
    - регистрација корисника (име, презиме, имејл адреса, лозинка, профилна слика)
    - пријава корисника (имејл адреса, лозинка)
    - уплата на рачун (износ)
    - објава артикла (назив, опис, слика, цена)
    - куповина артикла
    - рецензија (артикал/корисник, оцена, коментар)

За сваку акцију се прати и корисник који је ту акцију извршио (његов налог, евентуално и IP адреса).

Наведени улази и њихови подаци нису коначни и склони су проширењу/измени/уклањању.

### Излази

- казна (корисник, казнени бодови, тип)
- предузета мера:
    - активација аларма
    - одбијање захтева
    - привремено блокирање корисника
    - трајно блокирање корисника
    - привремени прекид рада система

Наведени излази нису коначни.

### База знања

Базу знања попуњавају корисници током интеракције са апликацијом.
Систем користи чињенице и догађаје из базе знања како би реаговао на сумњиво понашање.

Пример резоновања:

```ruby
class Note {
    user,
    points,
    type,
    message,
}

class Request {
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


# Казнити корисника који шаље превише захтева у секунди.
when
    count(Request($user) over 10s) > 50
then
    Note($user, 5, Type.Requests, "Too many requests")


# Обележити често коришћену лозинку као слабу.
when
    count(Login($password) over 10 min) > 100
then
    new Note(None, 2, Type.Auth_Pass, "Weak password {password}")
    new WeakPassword($password)


# Ако у систему постоји много слабих лозинки, обавести развојни тим да повећа сложеност.
when
    count_unique(WeakPassword) > 50
then
    new Alarm(Type.Auth_Pass, Severity.Medium, "Please increase password complexity for new users.")


# Ако у систему постоји превише слабих лозинки, обавестити кориснике да промене шифру.
when
    count_unique(WeakPassword) > 100
then
    for user in [ unique(list(WeakPassword)).user ]
        user.Notify("Your password is too weak.")
    end
```

Пример сложеног ланца правила (CEP, FC):

```ruby
# Ако се деси превише неуспелих пријава са једне локације, казни IP адресу.
when
    count(FailedLogin($ip, $email) over 1min) > 5
then
    new Note($ip, 3, Type.Auth, "Brute forcing user {email}")

# Ако иста IP адреса добија превише казнених поена за аутентификацију, блокирај је.
when
    accumulate(
        Note($ip, $points, Type.Auth) over 24h, 
        sum($points)
    ) > 100
then
    new Block($ip, 24h)

# Ако велик број IP адреса у скорије време буде блокирано, обавести да се можда извршио DDoS напад.
when
    count_unique(Block over 24h) > 100
then  
    new Alarm(Type.DDoS, Severity.High, "Large number of recent IPs have been blocked. User intervention needed.")

    # Admin has to check the logs and manually re-enable access to users.
    Server.Config.SetAvailability(ServerAvailability.RestrictAccessToAdmins)
```

Template-и ће се реализовати параметризацијом правила користећи UI (или учитавањем
података из табеле). Пример:

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

# -----------------------------------------------------------------------------

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

Backward chaining: Извештаји или упити на основу рецензија артикала/корисника (**TODO**: Прецизиније...)

## Литература

[1] Rajput, Quratulain, et al. "Ontology based expert-system for suspicious transactions detection." Computer and Information Science 7.1 (2014): 103.

[2] Zhang, Guo-Yin, Jian Li, and Guo-Chang Gu. "Research on defending DDoS attack-an expert system approach." 2004 IEEE International Conference on Systems, Man and Cybernetics (IEEE Cat. No. 04CH37583). Vol. 4. IEEE, 2004.

[3] Niyaz, Quamar, Weiqing Sun, and Ahmad Y. Javaid. "A deep learning based DDoS detection system in software-defined networking (SDN)." arXiv preprint arXiv:1611.07400 (2016).

[4] Naomi, J. Fenila, et al. "Intelligent transaction system for fraud detection using deep learning networks." Journal of Physics: Conference Series. Vol. 1916. No. 1. IOP Publishing, 2021.

[5] https://youverify.co/, Датум приступа: 18.4.2024.

[/] https://www.piranirisk.com/blog/how-to-identify-unusual-or-suspicious-transactions, Датум приступа: 19.4.2024.

[/] https://www.notiones.eu/2023/07/13/use-of-expert-systems-in-cyber-defense/, Датум приступа: 19.4.2024.
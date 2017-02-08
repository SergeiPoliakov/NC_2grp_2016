test/ (старая версия проекта с hibernate)

IDE: IntellJ IDEA
База данных: oracle database 11g
Сервер: tomcat

Виталий. Версия 0.0.2
Добавил новую сущность "Событие".

Анатолий. Добавлена сущность "Сообщение" 

Виталий. Сделал вход на сайт. Вход осуществляется по почте и паролю. 
Нельзя зарегистрироваться, если такая почта уже есть. 
Проверка вводимых данных, например, корректность имени или города.
Пока правда об этом никак пользователю не сообщается (просто отказ в регистрации).
Отказ во входе, если данные введены неправильно.
Хранение текущей сессии(httpsession). Теперь не нужно вводить id отправителя письма 
или кто создает событие. 
Нельзя без входа на сайт отправить письмо или создать событие.

Виталий. Сделал страницу с профилем, где отображается информация о пользователе.
Также добавил возможность изменять профиль. Немного мелких правок. 

eav/ (текущая версия)

IDE: IntellJ IDEA
База данных: oracle database 11g
Сервер: tomcat

Виталий
Сделал реализацию класса с JDBC для нашей EAV модели.
Пока реализовал простые select-ы и удаление (буду дополнять)
Протестировал работу методов в веб-приложении.
В классе представлены два способа соединения с БД: через DriverManager и с помощью JNDI.
Немного изменил структуру БД (разбил ФИО на разные атрибуты)

Виталий
Добавил регистрацию пользователя в eav модели(пока корявенько).
Исправил баги с удалением.
Еще немного поменял БД.

Виталий
Немного правок.
Добавил метод на получение конкретного поля из параметров.
Перенес свой старые стили, чтобы не смотреть на скучный белый экзан.

Виталий, Анатолий.
Разобрались с работой Spring Security. Сделали с помощью него логин на сайт.
Опробовали работу с ролями. Сделали запоминание пользователя в cookie.
Подкючено хэширование паролей(bcrypt) как при добавлении пользователя в БД, так и проверки пароля во время логина.
Изменена структура проекта, убрано много лишнего.
Добавлена новая главная страница для авторизированного пользователя и обычного гостя. 
Много других мелких правок.
ВНИМАНИЕ! Для успешной работы проверяйте логин и пароль для подключения к БД(файл context.xml и oracle-datasource.xml)
ВНИМАНИЕ! Для проверки добавляйте новых пользователей. Пользователя у которых в базе не хэшированные пароли не смогут войти.

Виталий
Добавил страницу с профилем текущего пользователя. Изменять профиль пока нельзя, поэтому не нажимайте "Сохранить". 
Пароль можно будет изменить и восстановить на отдельной странице, которая надеюсь когда-нибудь появится.

Виталий
Добавил изменение профиля текущего пользователя.
Не могу понять как мне устанавливать активным элемент radio в зависимости от значения в БД.   

Анатолий
Реализовано добавление событий - страница jsp с формой создания, переход с главной страницы авторизованного пользователя.

Анатолий
Реализована полноценная работа с событиями: создание, редактирование, удаление, вывод списка событий для текущего пользователя. Настроена безопасность в Spring Security для реализованного функционала событий.

Константин
Страница пользователя (user.jsp) со скудным функционалом. Хватает из базы данные о пользователе,  так же частично реализовано добавление событий из БД на таймлайн(при учёте что событие только одно). Так же рекомендую(обязую) хранить данные в следующем виде:
Приоритет задачи: строка Style1/Style2/ Style3 - где 1 - самый высокий приоритет.
Даты: строка формата dd.MM.yyyy hh:mm (02.12.2017 23:08 например).
Поменял на страницах addEvent, editEvent тип вводимых данных - с дат на строки(для полей, где нужно указать дату). Теперь с этих страниц вроде нормально добавляется в базу (хотя отображает всё по старому).

Виталий
Добавил поиск пользователей

Анатолий
Реализовано добавление пользователей в список друзей

Виталий, Анатолий
Добавили отправку сообщений другому пользовалю. Прикрепили темплейты в виде верхнего меню. 

Константин
Страницы: список друзей(потом по их аналогии можно сделать список встреч, уведомления) , встреча. 
Пока что всё на HTML, т.к. в базе пока что нет нужных данных. На странице встреч много кнопок слева - просто сделал заранее все, а там они будут выводиться в зависимости от того, кто зашёл на страницу
Нужно добавить в БД следующие сущности:
Meeting(встреча) с полями: 
	название,  
	дата начала, 
	дата окончания, 
	описание, 
	организатор(пользователь, который создал встречу), 
	теги(были же идеи с поиском по тегам),
	участники.
Запрос(заявка, как угодно) с полями:
	отправитель,
	получатель,
	тип (заявка в друзья, приглашение на встречу).
Запрос нужен будет для добавления пользователей в друзья и приглашения в группу. Например пользователь А добавил пользователя Б в друзья. 
В БД создаётся запрос, где отправитель -пользователь А, получатель - пользователь Б. Пользователь Б будет видеть на своей странице уведомлений
(которую тоже нужно сделать), что ему пришла заявка в друзья. Если он принимает её - заявка удаляется, а пользователям записывается, что они теперь друзья(вроде у пользователя есть поле друзья?)
Если заявку отклоняет - то она просто уничтожается. Аналогично с добавлением на встречи, разве что отправитель/получатель будет сущность Meeting.

Анатолий
Обновлены liguibase-скрипты для текущей стадии проекта (добавлен отдельный скрипт для добавления в базу сущности Event и тестовых данных). Можно накатить изменения на предыдущие версии, или, если "что-то пошло не так", последовательно применить все три скрипта (сначала удаление, потом создание и добавление Event). После этого все должно заработать.
Доработаны страныцы jsp с выводом списков: страница истории сообщений (оформлена в виде чата), страница вывода пользователей, страница списка событий.
Добавлена страница Встречи (meeting), пока еще не весь функционал перенесен на визуальные решения Кости
Проведены мелкие правки UI (хедер, кнопки и пр.)

Виталий
Решена проблема с хедером (спасибо Косте). Добавил возможность просмотра расписания другого пользователя. Решил совместить его профиль с расписанием, чтобы не создавать
нагромождение полупустых страниц. Обновил базу данных: добавил атрибуты для встреч.

Анатолий
Исправлено добавление в базу к списку друзей, реализовано удаление и вывод списка друзей, подключено к Костиному варианту оформления в виде карточек друзей (плюс сделал вариант вывода списком, allFriends_old_List.jsp)
Собрал все в крайний вариант с рабочим хедером, подключил кнопки

(Простите, там немного глючил гитхаб, пришлось немного постестить с коммитами и ревертами, на функциональности это не отразится)

Константин
Работа с встречами в dbHelper. Создание, изменение встречи, добавление/удаление пользователей, получение списка всех встреч/списка встреч конкретного пользователя
UPD Страницы со списком встреч пользователя, а так же просмотр конкретной встречи (пока подгружается информация о встрече, расписания пока нету)
UPD2 Отображение расписания участников встречи на странице непосредственно встречи

Анатолий
Добавлен футер на страницы jsp

Виталий
Добавил уровень веб-сервисов. На функционале это никак не отразилось, зато проект теперь имеет более правильную структуру. + мелкие правки в коде.

Анатолий
Добавлено левое выплывающее вертикальное меню, вроде на все страницы jsp прицепил, все теперь как надо. Но пока ссылки в меню на кнопках не установлены на ресурсы, это потом, когда установится окончательная структура оформления


Инструкция по запуску приложения Task Management System

2. Клонировать репозиторий:
```
git clone https://github.com/tatfil/taskSystem
cd taskSystem
```

2. Запустить приложение с помощью Docker Compose:
   Убедиться, что Docker Daemon запущен.  
В той же директории, где находится docker-compose.yml файл, запустить:
```
docker-compose up -d
```
Это:

- Соберет Docker образ для приложения.
- Скачает нужный образ PostgreSQL.
- Запустит оба контейнера в соответствии с настойками в файле docker-compose.yml.


3. Чтобы проверить запущены ли контейнеры можно воспользоваться командой:
```
docker ps
```
Вы должны увидеть два контейнера: один для приложения и один для базы данных.

4. Ссылка для доступа к приложению:

   Swagger: http://localhost:8080/swagger-ui/index.html


5. Остановить приложение:
```
docker-compose down
```
## Cron Expression Parser

Application aims to parse provided cron expression. Valid expression will result in printing interpretation of when
command will be run.

Parser implementation is based on description from [Wikipedia](https://en.wikipedia.org/wiki/Cron).

### Requirements

JDK 17

### Build

Run `./gradlew build`

### Test

Run `./gradlew test`

### Use

To freely use parser use `./gradlew jar` method to generate jar. It will be located in `${PROJECT_ROOT_DIR}/build/libs` with a name `shift4-homework.jar`.

Example usage:
```
~$ java -jar build/libs/shift4-homework.jar "*/15 0 1-3 * 1-5 /usr/bin/find"
minute          0 15 30 45
hour            0
day of month    1 2 3
month           1 2 3 4 5 6 7 8 9 10 11 12
day of week     1 2 3 4 5
command         /usr/bin/find
```

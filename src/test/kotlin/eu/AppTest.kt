package eu

import org.junit.jupiter.api.Test

class AppTest {
    @Test
    fun test() {
        val app = App()
        app.main("* * * * * /var/test")
    }
}

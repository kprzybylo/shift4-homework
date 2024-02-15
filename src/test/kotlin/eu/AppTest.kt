package eu

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AppTest {
    @Test
    fun `does not allow invalid format cron expression`() {
        val app = App()

        assertThrows<AssertionError> {
            app.main("* * * * *")
        }
    }
}

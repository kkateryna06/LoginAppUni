package com.example.lab3

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FormularzUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // Zadanie 2: Asercje i wyszukiwanie elementów
    @Test
    fun test_czyElementyStartoweSaWidoczne() {
        // Weryfikacja obecności nagłówka tekstowego
        composeTestRule.onNodeWithText("Panel Logowania").assertIsDisplayed()

        // Weryfikacja obecności pól formularza na podstawie etykiet tekstowych
        composeTestRule.onNodeWithText("E-mail").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hasło").assertIsDisplayed()

        // Weryfikacja obecności przycisku
        composeTestRule.onNodeWithText("Zaloguj").assertIsDisplayed()
    }

    // Zadanie 3: Automatyzacja interakcji i walidacji danych formularza
    @Test
    fun test_poprawneLogowanie_wyswietlaKomunikatSukcesu() {
        // Wprowadzenie poprawnego adresu e-mail za pomocą testTag
        composeTestRule.onNodeWithTag("email_input").performTextInput("student@uczelnia.pl")

        // Wprowadzenie hasła spełniającego kryteria walidacji (min. 6 znaków)
        composeTestRule.onNodeWithTag("password_input").performTextInput("Tajnie123!")

        // Kliknięcie w przycisk logowania
        composeTestRule.onNodeWithTag("login_button").performClick()

        // Asercja: Czy pojawił się tekst informujący o sukcesie?
        composeTestRule.onNodeWithTag("success_message").assertIsDisplayed()
        composeTestRule.onNodeWithText("Zalogowano pomyślnie").assertIsDisplayed()
    }

    // Zadanie 4: Testowanie odporności interfejsu na błędne akcje
    @Test
    fun test_blednyFormatEmail_blokujePrzycisk_i_wyswietlaBlad() {
        // Wprowadzenie niepoprawnego formatu e-mail
        composeTestRule.onNodeWithTag("email_input").performTextInput("bledny_email_at_domain.com")
        composeTestRule.onNodeWithTag("password_input").performTextInput("123") // Za krótkie hasło

        // Asercja: Przycisk powinien być nieaktywny
        composeTestRule.onNodeWithTag("login_button").assertIsNotEnabled()

        // Asercja: Czy komunikat o błędzie walidacji jest widoczny?
        composeTestRule.onNodeWithTag("error_message").assertIsDisplayed()
    }

    @Test
    fun test_rageClickingStabilnoscInterfejsu() {
        composeTestRule.onNodeWithTag("email_input").performTextInput("user@test.pl")
        composeTestRule.onNodeWithTag("password_input").performTextInput("Haslo123")

        // Symulacja szybkiego, wielokrotnego klikania w przycisk (Rage Clicking)
        repeat(10) {
            composeTestRule.onNodeWithTag("login_button").performClick()
        }

        // Interfejs powinien zachować stabilność, a komunikat sukcesu powinien być widoczny
        composeTestRule.onNodeWithTag("success_message").assertIsDisplayed()
    }

    // Zadanie 5: Testowanie stanów UI przy zmianach środowiskowych
    @Test
    fun test_rotacjaEkranu_zachowujeStanFormularza() {
        val wpisanyEmail = "test_rotacji@wp.pl"

        // Wprowadzenie tekstu przed obrotem
        composeTestRule.onNodeWithTag("email_input").performTextInput(wpisanyEmail)

        // Pobranie instancji UiDevice z frameworku UI Automator w celu zmiany orientacji urządzenia
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Obrót do orientacji poziomej (Landscape)
        device.setOrientationLeft()

        // Opcjonalne: Odczekanie na zakończenie animacji przejścia w Compose
        composeTestRule.waitForIdle()

        // Asercja po obrocie: Sprawdzenie, czy tekst nadal istnieje w komponencie formularza
        // Używamy dwóch argumentów, ponieważ OutlinedTextField zawiera w semantyce etykietę i wpisany tekst
        composeTestRule.onNodeWithTag("email_input").assertTextEquals("E-mail", wpisanyEmail)

        // Powrót do orientacji pionowej (Portrait)
        device.setOrientationNatural()
        composeTestRule.waitForIdle()
    }

    // Zadanie dodatkowe: Testowanie granic walidacji hasła oraz stanu przycisku
    @Test
    fun test_walidacjaHasla_graniceIStanPrzycisku() {
        // Wprowadzenie poprawnego e-maila
        composeTestRule.onNodeWithTag("email_input").performTextInput("user@test.pl")

        // Przypadek 1: Dokładnie 5 znaków (powinno być zablokowane)
        composeTestRule.onNodeWithTag("password_input").performTextInput("12345")
        composeTestRule.onNodeWithTag("login_button").assertIsNotEnabled()
        composeTestRule.onNodeWithText("Hasło musi mieć co najmniej 6 znaków").assertIsDisplayed()

        // Przypadek 2: Dokładnie 6 znaków (powinno być odblokowane)
        composeTestRule.onNodeWithTag("password_input").performTextReplacement("123456")
        composeTestRule.onNodeWithTag("login_button").assertIsEnabled()
        composeTestRule.onNodeWithTag("error_message").assertDoesNotExist()
    }
}

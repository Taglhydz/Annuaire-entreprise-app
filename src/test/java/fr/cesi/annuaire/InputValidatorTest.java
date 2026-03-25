package fr.cesi.annuaire;

import fr.cesi.annuaire.service.InputValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InputValidatorTest {

    @Test
    void normalizeFrenchPhone_shouldAllowSpacesAndStoreCompact() {
        String phone = InputValidator.normalizeFrenchPhone("06 12 34 56 78", "Telephone portable");
        assertEquals("0612345678", phone);
    }

    @Test
    void normalizeFrenchPhone_shouldRejectInvalidFormat() {
        assertThrows(IllegalArgumentException.class,
                () -> InputValidator.normalizeFrenchPhone("06-12-34-56-78", "Telephone portable"));
    }

    @Test
    void normalizeFrenchPhone_shouldReturnNullWhenBlank() {
        assertNull(InputValidator.normalizeFrenchPhone("   ", "Telephone fixe"));
    }

    @Test
    void normalizeEmail_shouldLowercaseAndValidate() {
        String email = InputValidator.normalizeEmail("  TEST.USER@Exemple.FR ");
        assertEquals("test.user@exemple.fr", email);
    }

    @Test
    void normalizeEmail_shouldRejectInvalidEmail() {
        assertThrows(IllegalArgumentException.class,
                () -> InputValidator.normalizeEmail("bad_mail"));
    }

    @Test
    void validatePersonName_shouldRejectDigits() {
        assertThrows(IllegalArgumentException.class,
                () -> InputValidator.validatePersonName("Jean2", "Nom"));
    }
}

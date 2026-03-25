package fr.cesi.annuaire;

import fr.cesi.annuaire.repository.DepartmentRepository;
import fr.cesi.annuaire.repository.EmployeeRepository;
import fr.cesi.annuaire.repository.SiteRepository;
import fr.cesi.annuaire.service.DirectoryService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DirectoryServiceValidationTest {

    private final DirectoryService service = new DirectoryService(
            null,
            new SiteRepository(null),
            new DepartmentRepository(null),
            new EmployeeRepository(null));

    @Test
    void createSite_shouldRejectBlankCity() {
        assertThrows(IllegalArgumentException.class, () -> service.createSite("  "));
    }

    @Test
    void createDepartment_shouldRejectBlankName() {
        assertThrows(IllegalArgumentException.class, () -> service.createDepartment(""));
    }

    @Test
    void createEmployee_shouldRejectInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> service.createEmployee(
                "Durand",
                "Luc",
                "0102030405",
                "0607080910",
                "email_invalide",
                1L,
                1L));
    }

    @Test
    void createEmployee_shouldRejectMissingLastName() {
        assertThrows(IllegalArgumentException.class, () -> service.createEmployee(
                "",
                "Luc",
                null,
                null,
                "luc.durand@entreprise.fr",
                1L,
                1L));
    }

    @Test
    void createEmployee_shouldRejectMissingSite() {
        assertThrows(IllegalArgumentException.class, () -> service.createEmployee(
                "Durand",
                "Luc",
                null,
                null,
                "luc.durand@entreprise.fr",
                null,
                1L));
    }
}

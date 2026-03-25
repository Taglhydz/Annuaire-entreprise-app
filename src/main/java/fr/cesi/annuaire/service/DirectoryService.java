package fr.cesi.annuaire.service;

import fr.cesi.annuaire.entity.Department;
import fr.cesi.annuaire.entity.Employee;
import fr.cesi.annuaire.entity.Site;
import fr.cesi.annuaire.repository.DepartmentRepository;
import fr.cesi.annuaire.repository.EmployeeRepository;
import fr.cesi.annuaire.repository.SiteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class DirectoryService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final EntityManager entityManager;

    private final SiteRepository siteRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DirectoryService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.siteRepository = new SiteRepository(entityManager);
        this.departmentRepository = new DepartmentRepository(entityManager);
        this.employeeRepository = new EmployeeRepository(entityManager);
    }

    public DirectoryService(EntityManager entityManager,
                            SiteRepository siteRepository,
                            DepartmentRepository departmentRepository,
                            EmployeeRepository employeeRepository) {
        this.entityManager = entityManager;
        this.siteRepository = siteRepository;
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<Site> getSites() {
        return siteRepository.findAll();
    }

    public List<Department> getDepartments() {
        return departmentRepository.findAll();
    }

    public List<Employee> searchEmployees(String searchText, Long siteId, Long departmentId) {
        return employeeRepository.search(searchText, siteId, departmentId);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Site createSite(String city) {
        String normalized = requireText(city, "La ville est obligatoire");
        Site site = new Site();
        site.setVille(normalized);
        return inTransaction(() -> siteRepository.save(site));
    }

    public Site updateSite(Long id, String city) {
        String normalized = requireText(city, "La ville est obligatoire");
        Site site = siteRepository.findById(requireId(id)).orElseThrow(() -> new IllegalArgumentException("Site introuvable"));
        site.setVille(normalized);
        return inTransaction(() -> siteRepository.update(site));
    }

    public void deleteSite(Long id) {
        Site site = siteRepository.findById(requireId(id)).orElseThrow(() -> new IllegalArgumentException("Site introuvable"));
        inTransaction(() -> {
            siteRepository.delete(site);
            return null;
        });
    }

    public Department createDepartment(String name) {
        String normalized = requireText(name, "Le nom du service est obligatoire");
        Department department = new Department();
        department.setNom(normalized);
        return inTransaction(() -> departmentRepository.save(department));
    }

    public Department updateDepartment(Long id, String name) {
        String normalized = requireText(name, "Le nom du service est obligatoire");
        Department department = departmentRepository.findById(requireId(id))
                .orElseThrow(() -> new IllegalArgumentException("Service introuvable"));
        department.setNom(normalized);
        return inTransaction(() -> departmentRepository.update(department));
    }

    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(requireId(id))
                .orElseThrow(() -> new IllegalArgumentException("Service introuvable"));
        inTransaction(() -> {
            departmentRepository.delete(department);
            return null;
        });
    }

    public Employee createEmployee(String lastName,
                                   String firstName,
                                   String fixedPhone,
                                   String mobilePhone,
                                   String email,
                                   Long siteId,
                                   Long departmentId) {
        Employee employee = new Employee();
        fillEmployee(employee, lastName, firstName, fixedPhone, mobilePhone, email, siteId, departmentId);
        return inTransaction(() -> employeeRepository.save(employee));
    }

    public Employee updateEmployee(Long id,
                                   String lastName,
                                   String firstName,
                                   String fixedPhone,
                                   String mobilePhone,
                                   String email,
                                   Long siteId,
                                   Long departmentId) {
        Employee employee = employeeRepository.findById(requireId(id))
                .orElseThrow(() -> new IllegalArgumentException("Salarie introuvable"));
        fillEmployee(employee, lastName, firstName, fixedPhone, mobilePhone, email, siteId, departmentId);
        return inTransaction(() -> employeeRepository.update(employee));
    }

    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(requireId(id))
                .orElseThrow(() -> new IllegalArgumentException("Salarie introuvable"));
        inTransaction(() -> {
            employeeRepository.delete(employee);
            return null;
        });
    }

    private void fillEmployee(Employee employee,
                              String lastName,
                              String firstName,
                              String fixedPhone,
                              String mobilePhone,
                              String email,
                              Long siteId,
                              Long departmentId) {
        employee.setNom(requireText(lastName, "Le nom est obligatoire"));
        employee.setPrenom(requireText(firstName, "Le prenom est obligatoire"));
        employee.setTelephoneFixe(normalizeNullable(fixedPhone));
        employee.setTelephonePortable(normalizeNullable(mobilePhone));
        employee.setEmail(validateEmail(email));

        Site site = siteRepository.findById(requireId(siteId))
                .orElseThrow(() -> new IllegalArgumentException("Site introuvable"));
        Department department = departmentRepository.findById(requireId(departmentId))
                .orElseThrow(() -> new IllegalArgumentException("Service introuvable"));

        employee.setSite(site);
        employee.setDepartment(department);
    }

    private String validateEmail(String email) {
        String normalized = normalizeNullable(email);
        if (normalized == null) {
            return null;
        }

        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Email invalide");
        }
        return normalized;
    }

    private Long requireId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Identifiant invalide");
        }
        return id;
    }

    private String requireText(String text, String message) {
        String normalized = normalizeNullable(text);
        if (normalized == null) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private <T> T inTransaction(TransactionWork<T> work) {
        Objects.requireNonNull(entityManager, "EntityManager obligatoire");
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            T result = work.run();
            transaction.commit();
            return result;
        } catch (RuntimeException ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    @FunctionalInterface
    private interface TransactionWork<T> {
        T run();
    }
}

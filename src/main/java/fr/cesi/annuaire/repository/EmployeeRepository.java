package fr.cesi.annuaire.repository;

import fr.cesi.annuaire.entity.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class EmployeeRepository {

    private final EntityManager entityManager;

    public EmployeeRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Employee> search(String searchText, Long siteId, Long departmentId) {
        String jpql = """
                SELECT e
                FROM Employee e
                WHERE (:searchText IS NULL OR LOWER(e.nom) LIKE CONCAT('%', LOWER(:searchText), '%'))
                  AND (:siteId IS NULL OR e.site.id = :siteId)
                  AND (:departmentId IS NULL OR e.department.id = :departmentId)
                ORDER BY e.nom, e.prenom
                """;

        TypedQuery<Employee> query = entityManager.createQuery(jpql, Employee.class);
        query.setParameter("searchText", normalize(searchText));
        query.setParameter("siteId", siteId);
        query.setParameter("departmentId", departmentId);
        return query.getResultList();
    }

    public List<Employee> findAll() {
        return entityManager.createQuery("SELECT e FROM Employee e ORDER BY e.nom, e.prenom", Employee.class)
                .getResultList();
    }

    public Optional<Employee> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Employee.class, id));
    }

    public Employee save(Employee employee) {
        entityManager.persist(employee);
        return employee;
    }

    public Employee update(Employee employee) {
        return entityManager.merge(employee);
    }

    public void delete(Employee employee) {
        Employee attached = entityManager.contains(employee) ? employee : entityManager.merge(employee);
        entityManager.remove(attached);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}

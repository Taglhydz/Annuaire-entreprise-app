package fr.cesi.annuaire.repository;

import fr.cesi.annuaire.entity.Department;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class DepartmentRepository {

    private final EntityManager entityManager;

    public DepartmentRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Department> findAll() {
        return entityManager.createQuery("SELECT d FROM Department d ORDER BY d.nom", Department.class)
                .getResultList();
    }

    public Optional<Department> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Department.class, id));
    }

    public Department save(Department department) {
        entityManager.persist(department);
        return department;
    }

    public Department update(Department department) {
        return entityManager.merge(department);
    }

    public void delete(Department department) {
        Department attached = entityManager.contains(department) ? department : entityManager.merge(department);
        entityManager.remove(attached);
    }
}

package fr.cesi.annuaire.repository;

import fr.cesi.annuaire.entity.Site;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class SiteRepository {

    private final EntityManager entityManager;

    public SiteRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Site> findAll() {
        return entityManager.createQuery("SELECT s FROM Site s ORDER BY s.ville", Site.class)
                .getResultList();
    }

    public Optional<Site> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Site.class, id));
    }

    public Site save(Site site) {
        entityManager.persist(site);
        return site;
    }

    public Site update(Site site) {
        return entityManager.merge(site);
    }

    public void delete(Site site) {
        Site attached = entityManager.contains(site) ? site : entityManager.merge(site);
        entityManager.remove(attached);
    }
}

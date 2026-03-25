package fr.cesi.annuaire;

import fr.cesi.annuaire.config.JpaConfig;
import fr.cesi.annuaire.service.AdminAuthService;
import fr.cesi.annuaire.service.DirectoryService;
import fr.cesi.annuaire.ui.MainView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private EntityManagerFactory emf;
    private EntityManager em;

    @Override
    public void start(Stage stage) {
        this.emf = JpaConfig.getEntityManagerFactory();
        this.em = emf.createEntityManager();

        DirectoryService directoryService = new DirectoryService(em);
        AdminAuthService adminAuthService = new AdminAuthService(em);

        MainView mainView = new MainView(directoryService, adminAuthService);

        stage.setTitle("Annuaire entreprise");
        stage.setScene(mainView.buildScene());
        stage.show();
    }

    @Override
    public void stop() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        JpaConfig.shutdown();
    }
}

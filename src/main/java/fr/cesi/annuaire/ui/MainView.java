package fr.cesi.annuaire.ui;

import fr.cesi.annuaire.entity.Department;
import fr.cesi.annuaire.entity.Employee;
import fr.cesi.annuaire.entity.Site;
import fr.cesi.annuaire.service.AdminAuthService;
import fr.cesi.annuaire.service.DirectoryService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Optional;

public class MainView {

    private final DirectoryService directoryService;
    private final AdminAuthService adminAuthService;

    private final ObservableList<Employee> tableData = FXCollections.observableArrayList();

    private Label nomValue;
    private Label prenomValue;
    private Label fixeValue;
    private Label portableValue;
    private Label emailValue;
    private Label siteValue;
    private Label serviceValue;

    public MainView(DirectoryService directoryService, AdminAuthService adminAuthService) {
        this.directoryService = directoryService;
        this.adminAuthService = adminAuthService;
    }

    public Scene buildScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        TextField searchField = new TextField();
        searchField.setPromptText("Recherche nom, prénom ou portable...");

        ComboBox<FilterOption> siteFilter = new ComboBox<>();
        ComboBox<FilterOption> departmentFilter = new ComboBox<>();

        siteFilter.setPrefWidth(220);
        departmentFilter.setPrefWidth(220);

        Button resetButton = new Button("Réinitialiser");

        HBox filterBar = new HBox(10,
            new Label("Nom/Prénom/Portable"), searchField,
                new Label("Site"), siteFilter,
                new Label("Service"), departmentFilter,
                resetButton
        );
        filterBar.setPadding(new Insets(0, 0, 10, 0));

        TableView<Employee> table = buildTable();
        table.setItems(tableData);

        VBox detailsPanel = buildDetailsPanel();

        root.setTop(filterBar);
        root.setCenter(table);
        root.setRight(detailsPanel);

        loadFilters(siteFilter, departmentFilter);
        refreshData(searchField, siteFilter, departmentFilter);

        searchField.textProperty().addListener((obs, oldV, newV) -> refreshData(searchField, siteFilter, departmentFilter));
        siteFilter.valueProperty().addListener((obs, oldV, newV) -> refreshData(searchField, siteFilter, departmentFilter));
        departmentFilter.valueProperty().addListener((obs, oldV, newV) -> refreshData(searchField, siteFilter, departmentFilter));

        resetButton.setOnAction(evt -> {
            searchField.clear();
            siteFilter.getSelectionModel().selectFirst();
            departmentFilter.getSelectionModel().selectFirst();
            refreshData(searchField, siteFilter, departmentFilter);
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> showDetails(newSel));

        Scene scene = new Scene(root, 1300, 700);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.isShiftDown() && event.getCode() == KeyCode.Q) {
                openAdminLoginDialog(searchField, siteFilter, departmentFilter);
                event.consume();
            }
        });

        return scene;
    }

    private void loadFilters(ComboBox<FilterOption> siteFilter, ComboBox<FilterOption> departmentFilter) {
        ObservableList<FilterOption> siteOptions = FXCollections.observableArrayList();
        siteOptions.add(new FilterOption(null, "Tous"));
        for (Site site : directoryService.getSites()) {
            siteOptions.add(new FilterOption(site.getId(), site.getVille()));
        }

        ObservableList<FilterOption> departmentOptions = FXCollections.observableArrayList();
        departmentOptions.add(new FilterOption(null, "Tous"));
        for (Department department : directoryService.getDepartments()) {
            departmentOptions.add(new FilterOption(department.getId(), department.getNom()));
        }

        siteFilter.setItems(siteOptions);
        departmentFilter.setItems(departmentOptions);

        siteFilter.getSelectionModel().selectFirst();
        departmentFilter.getSelectionModel().selectFirst();
    }

    private void refreshData(TextField searchField, ComboBox<FilterOption> siteFilter, ComboBox<FilterOption> departmentFilter) {
        Long siteId = Optional.ofNullable(siteFilter.getValue()).map(FilterOption::getId).orElse(null);
        Long departmentId = Optional.ofNullable(departmentFilter.getValue()).map(FilterOption::getId).orElse(null);

        List<Employee> result = directoryService.searchEmployees(searchField.getText(), siteId, departmentId);
        tableData.setAll(result);
    }

    private TableView<Employee> buildTable() {
        TableView<Employee> table = new TableView<>();

        TableColumn<Employee, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));

        TableColumn<Employee, String> prenomCol = new TableColumn<>("Prénom");
        prenomCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrenom()));

        TableColumn<Employee, String> siteCol = new TableColumn<>("Site");
        siteCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSite().getVille()));

        TableColumn<Employee, String> serviceCol = new TableColumn<>("Service");
        serviceCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDepartment().getNom()));

        TableColumn<Employee, String> portableCol = new TableColumn<>("Portable");
        portableCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTelephonePortable()));

        table.getColumns().add(nomCol);
        table.getColumns().add(prenomCol);
        table.getColumns().add(siteCol);
        table.getColumns().add(serviceCol);
        table.getColumns().add(portableCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        return table;
    }

    private VBox buildDetailsPanel() {
        Label title = new Label("Fiche salarié");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        nomValue = valueLabel();
        prenomValue = valueLabel();
        fixeValue = valueLabel();
        portableValue = valueLabel();
        emailValue = valueLabel();
        siteValue = valueLabel();
        serviceValue = valueLabel();

        VBox panel = new VBox(8,
                title,
                line("Nom", nomValue),
                line("Prénom", prenomValue),
                line("Téléphone fixe", fixeValue),
                line("Téléphone portable", portableValue),
                line("Email", emailValue),
                line("Site", siteValue),
                line("Service", serviceValue)
        );
        panel.setPrefWidth(340);
        panel.setPadding(new Insets(10, 0, 0, 14));

        return panel;
    }

    private HBox line(String label, Label value) {
        Label lbl = new Label(label + " :");
        lbl.setMinWidth(130);
        return new HBox(10, lbl, value);
    }

    private Label valueLabel() {
        Label label = new Label("-");
        label.setTextFill(Color.DARKSLATEGRAY);
        return label;
    }

    private void showDetails(Employee employee) {
        if (employee == null) {
            nomValue.setText("-");
            prenomValue.setText("-");
            fixeValue.setText("-");
            portableValue.setText("-");
            emailValue.setText("-");
            siteValue.setText("-");
            serviceValue.setText("-");
            return;
        }

        nomValue.setText(safe(employee.getNom()));
        prenomValue.setText(safe(employee.getPrenom()));
        fixeValue.setText(safe(employee.getTelephoneFixe()));
        portableValue.setText(safe(employee.getTelephonePortable()));
        emailValue.setText(safe(employee.getEmail()));
        siteValue.setText(employee.getSite() == null ? "-" : safe(employee.getSite().getVille()));
        serviceValue.setText(employee.getDepartment() == null ? "-" : safe(employee.getDepartment().getNom()));
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private void openAdminLoginDialog(TextField searchField,
                                      ComboBox<FilterOption> siteFilter,
                                      ComboBox<FilterOption> departmentFilter) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Authentification administrateur");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.addRow(0, new Label("Username"), usernameField);
        grid.addRow(1, new Label("Mot de passe"), passwordField);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.setDefaultButton(false);
        }
        dialog.setOnShown(event -> Platform.runLater(usernameField::requestFocus));

        dialog.setResultConverter(type -> type == ButtonType.OK);

        Optional<Boolean> confirmed = dialog.showAndWait();
        if (confirmed.isPresent() && confirmed.get()) {
            boolean ok = adminAuthService.authenticate(usernameField.getText(), passwordField.getText());
            if (ok) {
                new AdminDashboardView(directoryService, () -> {
                    loadFilters(siteFilter, departmentFilter);
                    refreshData(searchField, siteFilter, departmentFilter);
                }).show();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Accès refusé");
                alert.showAndWait();
            }
        }
    }
}

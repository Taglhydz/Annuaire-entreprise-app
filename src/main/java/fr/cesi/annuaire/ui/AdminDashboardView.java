package fr.cesi.annuaire.ui;

import fr.cesi.annuaire.entity.Department;
import fr.cesi.annuaire.entity.Employee;
import fr.cesi.annuaire.entity.Site;
import fr.cesi.annuaire.service.DirectoryService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Locale;

public class AdminDashboardView {

    private final DirectoryService directoryService;
    private final Runnable onDataChanged;
    private ComboBox<Site> employeeSiteBox;
    private ComboBox<Department> employeeDepartmentBox;

    public AdminDashboardView(DirectoryService directoryService, Runnable onDataChanged) {
        this.directoryService = directoryService;
        this.onDataChanged = onDataChanged;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Administration - Annuaire entreprise");

        TabPane tabPane = new TabPane(
                buildSitesTab(),
                buildDepartmentsTab(),
                buildEmployeesTab()
        );

        Scene scene = new Scene(tabPane, 1200, 680);
        stage.setScene(scene);
        stage.setOnHidden(event -> onDataChanged.run());
        stage.show();
    }

    private Tab buildSitesTab() {
        ObservableList<Site> allData = FXCollections.observableArrayList();
        ObservableList<Site> filteredData = FXCollections.observableArrayList();
        TableView<Site> table = new TableView<>(filteredData);

        TableColumn<Site, String> cityCol = new TableColumn<>("Ville");
        cityCol.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getVille()));
        table.getColumns().add(cityCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TextField cityField = new TextField();
        cityField.setPromptText("Ville");
        TextField searchField = new TextField();
        searchField.setPromptText("Recherche site par ville...");

        Button addButton = new Button("Ajouter");
        Button updateButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");

        addButton.setOnAction(evt -> withErrorHandling(() -> {
            validateRequiredText(cityField.getText(), "Ville");
            directoryService.createSite(cityField.getText());
            cityField.clear();
            refreshSites(allData, filteredData, searchField.getText());
            onDataChanged.run();
        }));

        updateButton.setOnAction(evt -> withErrorHandling(() -> {
            Site selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                throw new IllegalArgumentException("Sélectionnez un site");
            }
            validateRequiredText(cityField.getText(), "Ville");
            directoryService.updateSite(selected.getId(), cityField.getText());
            cityField.clear();
            refreshSites(allData, filteredData, searchField.getText());
            onDataChanged.run();
        }));

        deleteButton.setOnAction(evt -> withErrorHandling(() -> {
            Site selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                throw new IllegalArgumentException("Sélectionnez un site");
            }
            directoryService.deleteSite(selected.getId());
            cityField.clear();
            refreshSites(allData, filteredData, searchField.getText());
            onDataChanged.run();
        }));

        searchField.textProperty().addListener((obs, oldV, newV) ->
                applySiteFilter(allData, filteredData, newV));

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                cityField.setText(newV.getVille());
            }
        });

        HBox actions = new HBox(8, addButton, updateButton, deleteButton);
        VBox form = new VBox(8, new Label("Ville (*)"), cityField, actions);
        form.setPadding(new Insets(12));

        BorderPane pane = new BorderPane();
        pane.setTop(searchField);
        pane.setCenter(table);
        pane.setRight(form);
        pane.setPadding(new Insets(12));

        refreshSites(allData, filteredData, null);
        Tab tab = new Tab("Sites", pane);
        tab.setClosable(false);
        return tab;
    }

    private Tab buildDepartmentsTab() {
        ObservableList<Department> allData = FXCollections.observableArrayList();
        ObservableList<Department> filteredData = FXCollections.observableArrayList();
        TableView<Department> table = new TableView<>(filteredData);

        TableColumn<Department, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getNom()));
        table.getColumns().add(nameCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TextField nameField = new TextField();
        nameField.setPromptText("Nom du service");
        TextField searchField = new TextField();
        searchField.setPromptText("Recherche service par nom...");

        Button addButton = new Button("Ajouter");
        Button updateButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");

        addButton.setOnAction(evt -> withErrorHandling(() -> {
            validateRequiredText(nameField.getText(), "Service");
            directoryService.createDepartment(nameField.getText());
            nameField.clear();
            refreshDepartments(allData, filteredData, searchField.getText());
            onDataChanged.run();
        }));

        updateButton.setOnAction(evt -> withErrorHandling(() -> {
            Department selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                throw new IllegalArgumentException("Sélectionnez un service");
            }
            validateRequiredText(nameField.getText(), "Service");
            directoryService.updateDepartment(selected.getId(), nameField.getText());
            nameField.clear();
            refreshDepartments(allData, filteredData, searchField.getText());
            onDataChanged.run();
        }));

        deleteButton.setOnAction(evt -> withErrorHandling(() -> {
            Department selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                throw new IllegalArgumentException("Sélectionnez un service");
            }
            directoryService.deleteDepartment(selected.getId());
            nameField.clear();
            refreshDepartments(allData, filteredData, searchField.getText());
            onDataChanged.run();
        }));

        searchField.textProperty().addListener((obs, oldV, newV) ->
                applyDepartmentFilter(allData, filteredData, newV));

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                nameField.setText(newV.getNom());
            }
        });

        HBox actions = new HBox(8, addButton, updateButton, deleteButton);
        VBox form = new VBox(8, new Label("Service (*)"), nameField, actions);
        form.setPadding(new Insets(12));

        BorderPane pane = new BorderPane();
        pane.setTop(searchField);
        pane.setCenter(table);
        pane.setRight(form);
        pane.setPadding(new Insets(12));

        refreshDepartments(allData, filteredData, null);
        Tab tab = new Tab("Services", pane);
        tab.setClosable(false);
        return tab;
    }

    private Tab buildEmployeesTab() {
        ObservableList<Employee> allData = FXCollections.observableArrayList();
        ObservableList<Employee> filteredData = FXCollections.observableArrayList();
        TableView<Employee> table = new TableView<>(filteredData);

        TableColumn<Employee, String> lastNameCol = new TableColumn<>("Nom");
        lastNameCol.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getNom()));

        TableColumn<Employee, String> firstNameCol = new TableColumn<>("Prénom");
        firstNameCol.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getPrenom()));

        TableColumn<Employee, String> siteCol = new TableColumn<>("Site");
        siteCol.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getSite().getVille()));

        TableColumn<Employee, String> depCol = new TableColumn<>("Service");
        depCol.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getDepartment().getNom()));

        table.getColumns().add(lastNameCol);
        table.getColumns().add(firstNameCol);
        table.getColumns().add(siteCol);
        table.getColumns().add(depCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TextField lastNameField = new TextField();
        TextField firstNameField = new TextField();
        TextField fixedPhoneField = new TextField();
        TextField mobilePhoneField = new TextField();
        TextField emailField = new TextField();
        TextField searchField = new TextField();
        searchField.setPromptText("Recherche salarié (nom, prénom, email, site, service)...");
        ComboBox<Site> siteBox = new ComboBox<>();
        ComboBox<Department> depBox = new ComboBox<>();
        this.employeeSiteBox = siteBox;
        this.employeeDepartmentBox = depBox;

        siteBox.setItems(FXCollections.observableArrayList(directoryService.getSites()));
        depBox.setItems(FXCollections.observableArrayList(directoryService.getDepartments()));

        GridPane formGrid = new GridPane();
        formGrid.setHgap(8);
        formGrid.setVgap(8);
        formGrid.addRow(0, new Label("Nom (*)"), lastNameField);
        formGrid.addRow(1, new Label("Prénom (*)"), firstNameField);
        formGrid.addRow(2, new Label("Tél fixe"), fixedPhoneField);
        formGrid.addRow(3, new Label("Tél portable"), mobilePhoneField);
        formGrid.addRow(4, new Label("Email"), emailField);
        formGrid.addRow(5, new Label("Site (*)"), siteBox);
        formGrid.addRow(6, new Label("Service (*)"), depBox);

        Button addButton = new Button("Ajouter");
        Button updateButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");

        addButton.setOnAction(evt -> withErrorHandling(() -> {
            validateEmployeeRequiredFields(lastNameField.getText(), firstNameField.getText(), siteBox, depBox);
            directoryService.createEmployee(
                    lastNameField.getText(),
                    firstNameField.getText(),
                    fixedPhoneField.getText(),
                    mobilePhoneField.getText(),
                    emailField.getText(),
                    siteBox.getValue() == null ? null : siteBox.getValue().getId(),
                    depBox.getValue() == null ? null : depBox.getValue().getId());
            clearEmployeeForm(lastNameField, firstNameField, fixedPhoneField, mobilePhoneField, emailField, siteBox, depBox);
                    refreshEmployees(allData, filteredData, searchField.getText());
            onDataChanged.run();
        }));

        updateButton.setOnAction(evt -> withErrorHandling(() -> {
            Employee selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                throw new IllegalArgumentException("Sélectionnez un salarié");
            }
            validateEmployeeRequiredFields(lastNameField.getText(), firstNameField.getText(), siteBox, depBox);
            directoryService.updateEmployee(
                    selected.getId(),
                    lastNameField.getText(),
                    firstNameField.getText(),
                    fixedPhoneField.getText(),
                    mobilePhoneField.getText(),
                    emailField.getText(),
                    siteBox.getValue() == null ? null : siteBox.getValue().getId(),
                    depBox.getValue() == null ? null : depBox.getValue().getId());
            clearEmployeeForm(lastNameField, firstNameField, fixedPhoneField, mobilePhoneField, emailField, siteBox, depBox);
                    refreshEmployees(allData, filteredData, searchField.getText());
            onDataChanged.run();
        }));

        deleteButton.setOnAction(evt -> withErrorHandling(() -> {
            Employee selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                throw new IllegalArgumentException("Sélectionnez un salarié");
            }
            directoryService.deleteEmployee(selected.getId());
            clearEmployeeForm(lastNameField, firstNameField, fixedPhoneField, mobilePhoneField, emailField, siteBox, depBox);
            refreshEmployees(allData, filteredData, searchField.getText());
            onDataChanged.run();
        }));

        searchField.textProperty().addListener((obs, oldV, newV) ->
                applyEmployeeFilter(allData, filteredData, newV));

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, selected) -> {
            if (selected == null) {
                return;
            }
            lastNameField.setText(selected.getNom());
            firstNameField.setText(selected.getPrenom());
            fixedPhoneField.setText(selected.getTelephoneFixe());
            mobilePhoneField.setText(selected.getTelephonePortable());
            emailField.setText(selected.getEmail());
            siteBox.getSelectionModel().select(selected.getSite());
            depBox.getSelectionModel().select(selected.getDepartment());
        });

        HBox actions = new HBox(8, addButton, updateButton, deleteButton);
        VBox form = new VBox(10, formGrid, actions);
        form.setPadding(new Insets(12));

        BorderPane pane = new BorderPane();
        pane.setTop(searchField);
        pane.setCenter(table);
        pane.setRight(form);
        pane.setPadding(new Insets(12));

        refreshEmployees(allData, filteredData, null);

        Tab tab = new Tab("Salaries", pane);
        tab.setClosable(false);
        return tab;
    }

    private void refreshSites(ObservableList<Site> allData, ObservableList<Site> filteredData, String filterValue) {
        allData.setAll(directoryService.getSites());
        applySiteFilter(allData, filteredData, filterValue);
        refreshEmployeeReferenceChoices();
    }

    private void refreshDepartments(ObservableList<Department> allData, ObservableList<Department> filteredData, String filterValue) {
        allData.setAll(directoryService.getDepartments());
        applyDepartmentFilter(allData, filteredData, filterValue);
        refreshEmployeeReferenceChoices();
    }

    private void refreshEmployees(ObservableList<Employee> allData, ObservableList<Employee> filteredData, String filterValue) {
        allData.setAll(directoryService.getAllEmployees());
        applyEmployeeFilter(allData, filteredData, filterValue);
    }

    private void applySiteFilter(ObservableList<Site> allData, ObservableList<Site> filteredData, String filterValue) {
        String term = normalizeFilter(filterValue);
        if (term == null) {
            filteredData.setAll(allData);
            return;
        }
        filteredData.setAll(allData.stream()
                .filter(site -> contains(site.getVille(), term))
                .toList());
    }

    private void applyDepartmentFilter(ObservableList<Department> allData, ObservableList<Department> filteredData, String filterValue) {
        String term = normalizeFilter(filterValue);
        if (term == null) {
            filteredData.setAll(allData);
            return;
        }
        filteredData.setAll(allData.stream()
                .filter(department -> contains(department.getNom(), term))
                .toList());
    }

    private void applyEmployeeFilter(ObservableList<Employee> allData, ObservableList<Employee> filteredData, String filterValue) {
        String term = normalizeFilter(filterValue);
        if (term == null) {
            filteredData.setAll(allData);
            return;
        }

        filteredData.setAll(allData.stream()
                .filter(employee -> contains(employee.getNom(), term)
                        || contains(employee.getPrenom(), term)
                        || contains(employee.getEmail(), term)
                        || (employee.getSite() != null && contains(employee.getSite().getVille(), term))
                        || (employee.getDepartment() != null && contains(employee.getDepartment().getNom(), term)))
                .toList());
    }

    private String normalizeFilter(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private boolean contains(String source, String term) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(term);
    }

    private void validateRequiredText(String value, String fieldLabel) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldLabel + " est obligatoire");
        }
    }

    private void validateEmployeeRequiredFields(String lastName,
                                                String firstName,
                                                ComboBox<Site> siteBox,
                                                ComboBox<Department> depBox) {
        StringBuilder missing = new StringBuilder();
        if (lastName == null || lastName.isBlank()) {
            missing.append("Nom");
        }
        if (firstName == null || firstName.isBlank()) {
            appendComma(missing);
            missing.append("Prénom");
        }
        if (siteBox.getValue() == null) {
            appendComma(missing);
            missing.append("Site");
        }
        if (depBox.getValue() == null) {
            appendComma(missing);
            missing.append("Service");
        }
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Champs obligatoires manquants : " + missing);
        }
    }

    private void appendComma(StringBuilder sb) {
        if (!sb.isEmpty()) {
            sb.append(", ");
        }
    }

    private void clearEmployeeForm(TextField lastNameField,
                                   TextField firstNameField,
                                   TextField fixedPhoneField,
                                   TextField mobilePhoneField,
                                   TextField emailField,
                                   ComboBox<Site> siteBox,
                                   ComboBox<Department> depBox) {
        lastNameField.clear();
        firstNameField.clear();
        fixedPhoneField.clear();
        mobilePhoneField.clear();
        emailField.clear();
        siteBox.getSelectionModel().clearSelection();
        depBox.getSelectionModel().clearSelection();
        siteBox.setItems(FXCollections.observableArrayList(directoryService.getSites()));
        depBox.setItems(FXCollections.observableArrayList(directoryService.getDepartments()));
    }

    private void refreshEmployeeReferenceChoices() {
        if (employeeSiteBox == null || employeeDepartmentBox == null) {
            return;
        }

        Site selectedSite = employeeSiteBox.getValue();
        Department selectedDepartment = employeeDepartmentBox.getValue();

        employeeSiteBox.setItems(FXCollections.observableArrayList(directoryService.getSites()));
        employeeDepartmentBox.setItems(FXCollections.observableArrayList(directoryService.getDepartments()));

        if (selectedSite != null) {
            employeeSiteBox.getSelectionModel().select(
                    employeeSiteBox.getItems().stream()
                            .filter(site -> site.getId().equals(selectedSite.getId()))
                            .findFirst()
                            .orElse(null));
        }
        if (selectedDepartment != null) {
            employeeDepartmentBox.getSelectionModel().select(
                    employeeDepartmentBox.getItems().stream()
                            .filter(dep -> dep.getId().equals(selectedDepartment.getId()))
                            .findFirst()
                            .orElse(null));
        }
    }

    private void withErrorHandling(Runnable action) {
        try {
            action.run();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        }
    }
}

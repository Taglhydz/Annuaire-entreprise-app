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

public class AdminDashboardView {

    private final DirectoryService directoryService;
    private final Runnable onDataChanged;

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
        stage.show();
    }

    private Tab buildSitesTab() {
        ObservableList<Site> data = FXCollections.observableArrayList();
        TableView<Site> table = new TableView<>(data);

        TableColumn<Site, String> cityCol = new TableColumn<>("Ville");
        cityCol.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getVille()));
        table.getColumns().add(cityCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TextField cityField = new TextField();
        cityField.setPromptText("Ville");

        Button addButton = new Button("Ajouter");
        Button updateButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");

        addButton.setOnAction(evt -> withErrorHandling(() -> {
            directoryService.createSite(cityField.getText());
            cityField.clear();
            refreshSites(data);
            onDataChanged.run();
        }));

        updateButton.setOnAction(evt -> withErrorHandling(() -> {
            Site selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                throw new IllegalArgumentException("Selectionnez un site");
            }
            directoryService.updateSite(selected.getId(), cityField.getText());
            cityField.clear();
            refreshSites(data);
            onDataChanged.run();
        }));

        deleteButton.setOnAction(evt -> withErrorHandling(() -> {
            Site selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                throw new IllegalArgumentException("Selectionnez un site");
            }
            directoryService.deleteSite(selected.getId());
            cityField.clear();
            refreshSites(data);
            onDataChanged.run();
        }));

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                cityField.setText(newV.getVille());
            }
        });

        HBox actions = new HBox(8, addButton, updateButton, deleteButton);
        VBox form = new VBox(8, new Label("Ville"), cityField, actions);
        form.setPadding(new Insets(12));

        BorderPane pane = new BorderPane();
        pane.setCenter(table);
        pane.setRight(form);
        pane.setPadding(new Insets(12));

        refreshSites(data);
        Tab tab = new Tab("Sites", pane);
        tab.setClosable(false);
        return tab;
    }

    private Tab buildDepartmentsTab() {
        ObservableList<Department> data = FXCollections.observableArrayList();
        TableView<Department> table = new TableView<>(data);

        TableColumn<Department, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getNom()));
        table.getColumns().add(nameCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TextField nameField = new TextField();
        nameField.setPromptText("Nom du service");

        Button addButton = new Button("Ajouter");
        Button updateButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");

        addButton.setOnAction(evt -> withErrorHandling(() -> {
            directoryService.createDepartment(nameField.getText());
            nameField.clear();
            refreshDepartments(data);
            onDataChanged.run();
        }));

        updateButton.setOnAction(evt -> withErrorHandling(() -> {
            Department selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                throw new IllegalArgumentException("Selectionnez un service");
            }
            directoryService.updateDepartment(selected.getId(), nameField.getText());
            nameField.clear();
            refreshDepartments(data);
            onDataChanged.run();
        }));

        deleteButton.setOnAction(evt -> withErrorHandling(() -> {
            Department selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                throw new IllegalArgumentException("Selectionnez un service");
            }
            directoryService.deleteDepartment(selected.getId());
            nameField.clear();
            refreshDepartments(data);
            onDataChanged.run();
        }));

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                nameField.setText(newV.getNom());
            }
        });

        HBox actions = new HBox(8, addButton, updateButton, deleteButton);
        VBox form = new VBox(8, new Label("Service"), nameField, actions);
        form.setPadding(new Insets(12));

        BorderPane pane = new BorderPane();
        pane.setCenter(table);
        pane.setRight(form);
        pane.setPadding(new Insets(12));

        refreshDepartments(data);
        Tab tab = new Tab("Services", pane);
        tab.setClosable(false);
        return tab;
    }

    private Tab buildEmployeesTab() {
        ObservableList<Employee> data = FXCollections.observableArrayList();
        TableView<Employee> table = new TableView<>(data);

        TableColumn<Employee, String> lastNameCol = new TableColumn<>("Nom");
        lastNameCol.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getNom()));

        TableColumn<Employee, String> firstNameCol = new TableColumn<>("Prenom");
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
        ComboBox<Site> siteBox = new ComboBox<>();
        ComboBox<Department> depBox = new ComboBox<>();

        siteBox.setItems(FXCollections.observableArrayList(directoryService.getSites()));
        depBox.setItems(FXCollections.observableArrayList(directoryService.getDepartments()));

        GridPane formGrid = new GridPane();
        formGrid.setHgap(8);
        formGrid.setVgap(8);
        formGrid.addRow(0, new Label("Nom"), lastNameField);
        formGrid.addRow(1, new Label("Prenom"), firstNameField);
        formGrid.addRow(2, new Label("Tel fixe"), fixedPhoneField);
        formGrid.addRow(3, new Label("Tel portable"), mobilePhoneField);
        formGrid.addRow(4, new Label("Email"), emailField);
        formGrid.addRow(5, new Label("Site"), siteBox);
        formGrid.addRow(6, new Label("Service"), depBox);

        Button addButton = new Button("Ajouter");
        Button updateButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");

        addButton.setOnAction(evt -> withErrorHandling(() -> {
            directoryService.createEmployee(
                    lastNameField.getText(),
                    firstNameField.getText(),
                    fixedPhoneField.getText(),
                    mobilePhoneField.getText(),
                    emailField.getText(),
                    siteBox.getValue() == null ? null : siteBox.getValue().getId(),
                    depBox.getValue() == null ? null : depBox.getValue().getId());
            clearEmployeeForm(lastNameField, firstNameField, fixedPhoneField, mobilePhoneField, emailField, siteBox, depBox);
            refreshEmployees(data);
            onDataChanged.run();
        }));

        updateButton.setOnAction(evt -> withErrorHandling(() -> {
            Employee selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                throw new IllegalArgumentException("Selectionnez un salarie");
            }
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
            refreshEmployees(data);
            onDataChanged.run();
        }));

        deleteButton.setOnAction(evt -> withErrorHandling(() -> {
            Employee selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                throw new IllegalArgumentException("Selectionnez un salarie");
            }
            directoryService.deleteEmployee(selected.getId());
            clearEmployeeForm(lastNameField, firstNameField, fixedPhoneField, mobilePhoneField, emailField, siteBox, depBox);
            refreshEmployees(data);
            onDataChanged.run();
        }));

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
        pane.setCenter(table);
        pane.setRight(form);
        pane.setPadding(new Insets(12));

        refreshEmployees(data);

        Tab tab = new Tab("Salaries", pane);
        tab.setClosable(false);
        return tab;
    }

    private void refreshSites(ObservableList<Site> data) {
        data.setAll(directoryService.getSites());
    }

    private void refreshDepartments(ObservableList<Department> data) {
        data.setAll(directoryService.getDepartments());
    }

    private void refreshEmployees(ObservableList<Employee> data) {
        data.setAll(directoryService.getAllEmployees());
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

    private void withErrorHandling(Runnable action) {
        try {
            action.run();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        }
    }
}

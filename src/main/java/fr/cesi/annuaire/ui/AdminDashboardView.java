package fr.cesi.annuaire.ui;

import fr.cesi.annuaire.entity.Department;
import fr.cesi.annuaire.entity.Employee;
import fr.cesi.annuaire.entity.Site;
import fr.cesi.annuaire.service.DirectoryService;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Locale;

public class AdminDashboardView {

    private static final double CONTROL_HEIGHT = 40;
    private static final int ROWS_PER_PAGE = 14;

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
        stage.setTitle("Administration — Annuaire entreprise");

        TabPane tabPane = new TabPane(
                buildSitesTab(),
                buildDepartmentsTab(),
                buildEmployeesTab()
        );

        Scene scene = new Scene(tabPane, 1200, 680);
        scene.getStylesheets().add(
            getClass().getResource("/styles/app.css").toExternalForm()
        );
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setOnHidden(event -> onDataChanged.run());
        stage.show();
    }

    // ── Bannière d'erreur inline ───────────────────────────────────────────────

    private Label createErrorBanner() {
        Label banner = new Label();
        banner.setVisible(false);
        banner.setManaged(false);
        banner.setWrapText(true);
        banner.setMaxWidth(280);
        banner.getStyleClass().add("error-banner");
        return banner;
    }

    private void showError(Label banner, String message) {
        banner.setText("⚠  " + message);
        banner.setVisible(true);
        banner.setManaged(true);
    }

    private void hideError(Label banner) {
        banner.setVisible(false);
        banner.setManaged(false);
    }

    private void withInlineErrorHandling(Runnable action, Label errorBanner) {
        hideError(errorBanner);
        try {
            action.run();
        } catch (Exception ex) {
            showError(errorBanner, ex.getMessage());
        }
    }

    // ── Helpers boutons ────────────────────────────────────────────────────────

    private Button primaryButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("button-primary");
        btn.setPrefWidth(120);
        btn.setMinWidth(110);
        setHeight(btn);
        return btn;
    }

    private Button dangerButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("button-danger");
        btn.setPrefWidth(120);
        btn.setMinWidth(110);
        setHeight(btn);
        return btn;
    }

    private Button defaultButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(120);
        btn.setMinWidth(110);
        setHeight(btn);
        return btn;
    }

    private void stretchActionButtons(HBox actions, Button... buttons) {
        for (Button button : buttons) {
            button.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(button, Priority.ALWAYS);
        }
        actions.setFillHeight(true);
    }

    private Pagination createPagination() {
        Pagination pagination = new Pagination(1, 0);
        pagination.setMaxPageIndicatorCount(6);
        pagination.setPageFactory(pageIndex -> new Region());
        pagination.getStyleClass().add("compact-pagination");
        pagination.setMinHeight(40);
        pagination.setPrefHeight(40);
        pagination.setMaxHeight(40);
        return pagination;
    }

    private <T> void updatePagination(Pagination pagination,
                                      ObservableList<T> filteredData,
                                      ObservableList<T> pagedData) {
        int total = filteredData.size();
        int pageCount = Math.max(1, (int) Math.ceil((double) total / ROWS_PER_PAGE));
        pagination.setPageCount(pageCount);
        boolean showPagination = pageCount > 1;
        pagination.setVisible(showPagination);
        pagination.setManaged(showPagination);

        int current = pagination.getCurrentPageIndex();
        if (current >= pageCount) {
            pagination.setCurrentPageIndex(pageCount - 1);
            current = pagination.getCurrentPageIndex();
        }
        showPage(filteredData, pagedData, current);
    }

    private <T> void showPage(ObservableList<T> filteredData,
                              ObservableList<T> pagedData,
                              int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        if (fromIndex >= filteredData.size()) {
            pagedData.clear();
            return;
        }
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredData.size());
        pagedData.setAll(filteredData.subList(fromIndex, toIndex));
    }

    // ── Onglet Sites ───────────────────────────────────────────────────────────

    private Tab buildSitesTab() {
        ObservableList<Site> allData = FXCollections.observableArrayList();
        ObservableList<Site> filteredData = FXCollections.observableArrayList();
        ObservableList<Site> pagedData = FXCollections.observableArrayList();
        ObjectProperty<Site> selectedSiteRef = new SimpleObjectProperty<>();
        TableView<Site> table = new TableView<>(pagedData);
        Pagination pagination = createPagination();
        pagination.currentPageIndexProperty().addListener((obs, oldV, newV) ->
            showPage(filteredData, pagedData, newV.intValue()));

        TableColumn<Site, String> cityCol = new TableColumn<>("Ville");
        cityCol.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getVille()));
        table.getColumns().add(cityCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TextField cityField = new TextField();
        cityField.setPromptText("Nom de la ville");
        setHeight(cityField);

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher un site...");
        setHeight(searchField);

        Label errorBanner = createErrorBanner();

        Button addButton    = primaryButton("Ajouter");
        Button newButton    = defaultButton("Nouveau");
        Button updateButton = primaryButton("Modifier");
        Button deleteButton = dangerButton("Supprimer");

        addButton.setDisable(true);
        Tooltip.install(addButton, new Tooltip("Saisissez une ville pour activer ce bouton"));

        cityField.textProperty().addListener((obs, oldV, newV) -> {
            addButton.setDisable(newV == null || newV.isBlank());
            hideError(errorBanner);
        });

        updateButton.setVisible(false);
        deleteButton.setVisible(false);
        newButton.setVisible(false);

        addButton.setOnAction(evt -> withInlineErrorHandling(() -> {
            validateRequiredText(cityField.getText(), "Ville");
            directoryService.createSite(cityField.getText());
            selectedSiteRef.set(null);
            cityField.clear();
            table.getSelectionModel().clearSelection();
            refreshSites(allData, filteredData, pagedData, pagination, searchField.getText());
            onDataChanged.run();
            Platform.runLater(cityField::requestFocus);
        }, errorBanner));

        updateButton.setOnAction(evt -> withInlineErrorHandling(() -> {
            Site selected = selectedSiteRef.get();
            if (selected == null) throw new IllegalArgumentException("Sélectionnez un site");
            validateRequiredText(cityField.getText(), "Ville");
            directoryService.updateSite(selected.getId(), cityField.getText());
            selectedSiteRef.set(null);
            cityField.clear();
            table.getSelectionModel().clearSelection();
            refreshSites(allData, filteredData, pagedData, pagination, searchField.getText());
            onDataChanged.run();
        }, errorBanner));

        deleteButton.setOnAction(evt -> withInlineErrorHandling(() -> {
            Site selected = selectedSiteRef.get();
            if (selected == null) throw new IllegalArgumentException("Sélectionnez un site");
            directoryService.deleteSite(selected.getId());
            selectedSiteRef.set(null);
            cityField.clear();
            table.getSelectionModel().clearSelection();
            refreshSites(allData, filteredData, pagedData, pagination, searchField.getText());
            onDataChanged.run();
        }, errorBanner));

        newButton.setOnAction(evt -> {
            selectedSiteRef.set(null);
            table.getSelectionModel().clearSelection();
            cityField.clear();
            hideError(errorBanner);
            Platform.runLater(cityField::requestFocus);
        });

        searchField.textProperty().addListener((obs, oldV, newV) ->
            applySiteFilter(allData, filteredData, pagedData, pagination, newV));

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            boolean hasSelection = newV != null;
            addButton.setVisible(!hasSelection);
            newButton.setVisible(hasSelection);
            updateButton.setVisible(hasSelection);
            deleteButton.setVisible(hasSelection);
            hideError(errorBanner);
            if (newV != null) {
                selectedSiteRef.set(newV);
                cityField.setText(newV.getVille());
            }
        });

        Label formTitle = new Label("Site");
        formTitle.getStyleClass().add("admin-form-title");

        Label cityLabel = new Label("Ville  *");
        cityLabel.getStyleClass().add("detail-label");

        HBox actions = new HBox(8, addButton, newButton, updateButton, deleteButton);
        actions.setAlignment(Pos.CENTER_LEFT);
        stretchActionButtons(actions, addButton, newButton, updateButton, deleteButton);

        VBox form = new VBox(10, formTitle, cityLabel, cityField, actions, errorBanner);
        form.getStyleClass().add("admin-form-panel");
        form.setPrefWidth(520);

        VBox tableSection = new VBox(8, table, pagination);
        VBox.setVgrow(table, Priority.ALWAYS);

        BorderPane pane = new BorderPane();
        pane.setTop(searchField);
        pane.setCenter(tableSection);
        pane.setRight(form);
        pane.setPadding(new Insets(14));
        BorderPane.setMargin(searchField, new Insets(0, 0, 10, 0));
        BorderPane.setMargin(tableSection, new Insets(0, 14, 0, 0));

        refreshSites(allData, filteredData, pagedData, pagination, null);
        Tab tab = new Tab("Sites", pane);
        tab.setClosable(false);
        return tab;
    }

    // ── Onglet Services ────────────────────────────────────────────────────────

    private Tab buildDepartmentsTab() {
        ObservableList<Department> allData = FXCollections.observableArrayList();
        ObservableList<Department> filteredData = FXCollections.observableArrayList();
        ObservableList<Department> pagedData = FXCollections.observableArrayList();
        ObjectProperty<Department> selectedDepartmentRef = new SimpleObjectProperty<>();
        TableView<Department> table = new TableView<>(pagedData);
        Pagination pagination = createPagination();
        pagination.currentPageIndexProperty().addListener((obs, oldV, newV) ->
            showPage(filteredData, pagedData, newV.intValue()));

        TableColumn<Department, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getNom()));
        table.getColumns().add(nameCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TextField nameField = new TextField();
        nameField.setPromptText("Nom du service");
        setHeight(nameField);

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher un service...");
        setHeight(searchField);

        Label errorBanner = createErrorBanner();

        Button addButton    = primaryButton("Ajouter");
        Button newButton    = defaultButton("Nouveau");
        Button updateButton = primaryButton("Modifier");
        Button deleteButton = dangerButton("Supprimer");

        addButton.setDisable(true);
        Tooltip.install(addButton, new Tooltip("Saisissez un nom de service pour activer ce bouton"));

        nameField.textProperty().addListener((obs, oldV, newV) -> {
            addButton.setDisable(newV == null || newV.isBlank());
            hideError(errorBanner);
        });

        updateButton.setVisible(false);
        deleteButton.setVisible(false);
        newButton.setVisible(false);

        addButton.setOnAction(evt -> withInlineErrorHandling(() -> {
            validateRequiredText(nameField.getText(), "Service");
            directoryService.createDepartment(nameField.getText());
            selectedDepartmentRef.set(null);
            nameField.clear();
            table.getSelectionModel().clearSelection();
            refreshDepartments(allData, filteredData, pagedData, pagination, searchField.getText());
            onDataChanged.run();
            Platform.runLater(nameField::requestFocus);
        }, errorBanner));

        updateButton.setOnAction(evt -> withInlineErrorHandling(() -> {
            Department selected = selectedDepartmentRef.get();
            if (selected == null) throw new IllegalArgumentException("Sélectionnez un service");
            validateRequiredText(nameField.getText(), "Service");
            directoryService.updateDepartment(selected.getId(), nameField.getText());
            selectedDepartmentRef.set(null);
            nameField.clear();
            table.getSelectionModel().clearSelection();
            refreshDepartments(allData, filteredData, pagedData, pagination, searchField.getText());
            onDataChanged.run();
        }, errorBanner));

        deleteButton.setOnAction(evt -> withInlineErrorHandling(() -> {
            Department selected = selectedDepartmentRef.get();
            if (selected == null) throw new IllegalArgumentException("Sélectionnez un service");
            directoryService.deleteDepartment(selected.getId());
            selectedDepartmentRef.set(null);
            nameField.clear();
            table.getSelectionModel().clearSelection();
            refreshDepartments(allData, filteredData, pagedData, pagination, searchField.getText());
            onDataChanged.run();
        }, errorBanner));

        newButton.setOnAction(evt -> {
            selectedDepartmentRef.set(null);
            table.getSelectionModel().clearSelection();
            nameField.clear();
            hideError(errorBanner);
            Platform.runLater(nameField::requestFocus);
        });

        searchField.textProperty().addListener((obs, oldV, newV) ->
            applyDepartmentFilter(allData, filteredData, pagedData, pagination, newV));

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            boolean hasSelection = newV != null;
            addButton.setVisible(!hasSelection);
            newButton.setVisible(hasSelection);
            updateButton.setVisible(hasSelection);
            deleteButton.setVisible(hasSelection);
            hideError(errorBanner);
            if (newV != null) {
                selectedDepartmentRef.set(newV);
                nameField.setText(newV.getNom());
            }
        });

        Label formTitle = new Label("Service");
        formTitle.getStyleClass().add("admin-form-title");

        Label nameLabel = new Label("Nom  *");
        nameLabel.getStyleClass().add("detail-label");

        HBox actions = new HBox(8, addButton, newButton, updateButton, deleteButton);
        actions.setAlignment(Pos.CENTER_LEFT);
        stretchActionButtons(actions, addButton, newButton, updateButton, deleteButton);

        VBox form = new VBox(10, formTitle, nameLabel, nameField, actions, errorBanner);
        form.getStyleClass().add("admin-form-panel");
        form.setPrefWidth(520);

        VBox tableSection = new VBox(8, table, pagination);
        VBox.setVgrow(table, Priority.ALWAYS);

        BorderPane pane = new BorderPane();
        pane.setTop(searchField);
        pane.setCenter(tableSection);
        pane.setRight(form);
        pane.setPadding(new Insets(14));
        BorderPane.setMargin(searchField, new Insets(0, 0, 10, 0));
        BorderPane.setMargin(tableSection, new Insets(0, 14, 0, 0));

        refreshDepartments(allData, filteredData, pagedData, pagination, null);
        Tab tab = new Tab("Services", pane);
        tab.setClosable(false);
        return tab;
    }

    // ── Onglet Salariés ────────────────────────────────────────────────────────

    private Tab buildEmployeesTab() {
        ObservableList<Employee> allData = FXCollections.observableArrayList();
        ObservableList<Employee> filteredData = FXCollections.observableArrayList();
        ObservableList<Employee> pagedData = FXCollections.observableArrayList();
        ObjectProperty<Employee> selectedEmployeeRef = new SimpleObjectProperty<>();
        TableView<Employee> table = new TableView<>(pagedData);
        Pagination pagination = createPagination();
        pagination.currentPageIndexProperty().addListener((obs, oldV, newV) ->
            showPage(filteredData, pagedData, newV.intValue()));

        TableColumn<Employee, String> lastNameCol = new TableColumn<>("Nom");
        lastNameCol.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getNom()));

        TableColumn<Employee, String> firstNameCol = new TableColumn<>("Prénom");
        firstNameCol.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getPrenom()));

        TableColumn<Employee, String> siteCol = new TableColumn<>("Site");
        siteCol.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getSite().getVille()));

        TableColumn<Employee, String> depCol = new TableColumn<>("Service");
        depCol.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getDepartment().getNom()));

        TableColumn<Employee, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getEmail()));

        TableColumn<Employee, String> mobileCol = new TableColumn<>("Portable");
        mobileCol.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getTelephonePortable()));

        table.getColumns().addAll(lastNameCol, firstNameCol, siteCol, depCol, emailCol, mobileCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TextField lastNameField    = new TextField(); lastNameField.setPromptText("Nom");
        TextField firstNameField   = new TextField(); firstNameField.setPromptText("Prénom");
        TextField fixedPhoneField  = new TextField(); fixedPhoneField.setPromptText("ex : 01 23 45 67 89");
        TextField mobilePhoneField = new TextField(); mobilePhoneField.setPromptText("ex : 06 12 34 56 78");
        TextField emailField       = new TextField(); emailField.setPromptText("prenom.nom@entreprise.fr");
        setHeight(lastNameField); setHeight(firstNameField);
        setHeight(fixedPhoneField); setHeight(mobilePhoneField); setHeight(emailField);

        TextField nameSearchField = new TextField();
        nameSearchField.setPromptText("Nom / prénom...");
        setHeight(nameSearchField);

        ComboBox<FilterOption> siteFilterBox = new ComboBox<>();
        ComboBox<FilterOption> departmentFilterBox = new ComboBox<>();
        siteFilterBox.setPrefWidth(180);
        departmentFilterBox.setPrefWidth(180);
        setHeight(siteFilterBox);
        setHeight(departmentFilterBox);
        loadEmployeeSearchFilters(siteFilterBox, departmentFilterBox);

        TextField emailSearchField = new TextField();
        emailSearchField.setPromptText("Email...");
        setHeight(emailSearchField);

        TextField phoneSearchField = new TextField();
        phoneSearchField.setPromptText("Numéro...");
        setHeight(phoneSearchField);

        Label nameSearchLabel = new Label("Nom / Prénom");
        Label siteSearchLabel = new Label("Site");
        Label departmentSearchLabel = new Label("Service");
        Label emailSearchLabel = new Label("Email");
        Label phoneSearchLabel = new Label("Numéro");

        Button resetFiltersButton = new Button("Réinitialiser");
        resetFiltersButton.getStyleClass().add("button-reset");
        setHeight(resetFiltersButton);

        HBox searchBar = new HBox(10,
            nameSearchLabel, nameSearchField,
            siteSearchLabel, siteFilterBox,
            departmentSearchLabel, departmentFilterBox,
            emailSearchLabel, emailSearchField,
            phoneSearchLabel, phoneSearchField,
            resetFiltersButton);
        searchBar.getStyleClass().add("filter-bar");

        ComboBox<Site> siteBox = new ComboBox<>();
        ComboBox<Department> depBox = new ComboBox<>();
        setHeight(siteBox); setHeight(depBox);
        this.employeeSiteBox = siteBox;
        this.employeeDepartmentBox = depBox;

        siteBox.setItems(FXCollections.observableArrayList(directoryService.getSites()));
        depBox.setItems(FXCollections.observableArrayList(directoryService.getDepartments()));

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(8);

        // Labels du formulaire
        String[] labels = { "Nom  *", "Prénom  *", "Tél fixe", "Tél portable", "Email", "Site  *", "Service  *" };
        Region[] fields = { lastNameField, firstNameField, fixedPhoneField, mobilePhoneField, emailField, siteBox, depBox };
        for (int i = 0; i < labels.length; i++) {
            Label lbl = new Label(labels[i]);
            lbl.getStyleClass().add("detail-label");
            formGrid.addRow(i, lbl, fields[i]);
        }

        Button addButton    = primaryButton("Ajouter");
        Button newButton    = defaultButton("Nouveau");
        Button updateButton = primaryButton("Modifier");
        Button deleteButton = dangerButton("Supprimer");

        Label missingFieldsLabel = new Label();
        missingFieldsLabel.setWrapText(true);
        missingFieldsLabel.setMaxWidth(280);
        missingFieldsLabel.setStyle("-fx-text-fill: #718096; -fx-font-size: 11px; -fx-font-style: italic;");

        Label errorBanner = createErrorBanner();

        addButton.setDisable(true);

        Runnable updateAddButtonState = () -> {
            String missing = getMissingEmployeeFields(lastNameField, firstNameField, siteBox, depBox);
            boolean canAdd = missing.isEmpty();
            addButton.setDisable(!canAdd);
            if (canAdd) {
                missingFieldsLabel.setText("");
                missingFieldsLabel.setVisible(false);
                missingFieldsLabel.setManaged(false);
            } else {
                missingFieldsLabel.setText("Champs requis : " + missing);
                missingFieldsLabel.setVisible(true);
                missingFieldsLabel.setManaged(true);
            }
            hideError(errorBanner);
        };
        updateAddButtonState.run();

        lastNameField.textProperty().addListener((obs, o, n)  -> updateAddButtonState.run());
        firstNameField.textProperty().addListener((obs, o, n) -> updateAddButtonState.run());
        siteBox.valueProperty().addListener((obs, o, n)       -> updateAddButtonState.run());
        depBox.valueProperty().addListener((obs, o, n)        -> updateAddButtonState.run());

        fixedPhoneField.textProperty().addListener((obs, o, n)  -> hideError(errorBanner));
        mobilePhoneField.textProperty().addListener((obs, o, n) -> hideError(errorBanner));
        emailField.textProperty().addListener((obs, o, n)       -> hideError(errorBanner));

        updateButton.setVisible(false);
        deleteButton.setVisible(false);
        newButton.setVisible(false);

        addButton.setOnAction(evt -> withInlineErrorHandling(() -> {
            validateEmployeeRequiredFields(lastNameField.getText(), firstNameField.getText(), siteBox, depBox);
            directoryService.createEmployee(
                    lastNameField.getText(), firstNameField.getText(),
                    fixedPhoneField.getText(), mobilePhoneField.getText(), emailField.getText(),
                    siteBox.getValue() == null ? null : siteBox.getValue().getId(),
                    depBox.getValue() == null ? null : depBox.getValue().getId());
            selectedEmployeeRef.set(null);
            clearEmployeeForm(lastNameField, firstNameField, fixedPhoneField, mobilePhoneField, emailField, siteBox, depBox);
            table.getSelectionModel().clearSelection();
                refreshEmployees(allData, filteredData, pagedData, pagination,
                    nameSearchField.getText(),
                    emailSearchField.getText(),
                    phoneSearchField.getText(),
                    siteFilterBox,
                    departmentFilterBox);
            onDataChanged.run();
            Platform.runLater(lastNameField::requestFocus);
        }, errorBanner));

        updateButton.setOnAction(evt -> withInlineErrorHandling(() -> {
            Employee selected = selectedEmployeeRef.get();
            if (selected == null) throw new IllegalArgumentException("Sélectionnez un salarié");
            validateEmployeeRequiredFields(lastNameField.getText(), firstNameField.getText(), siteBox, depBox);
            directoryService.updateEmployee(
                    selected.getId(),
                    lastNameField.getText(), firstNameField.getText(),
                    fixedPhoneField.getText(), mobilePhoneField.getText(), emailField.getText(),
                    siteBox.getValue() == null ? null : siteBox.getValue().getId(),
                    depBox.getValue() == null ? null : depBox.getValue().getId());
            selectedEmployeeRef.set(null);
            clearEmployeeForm(lastNameField, firstNameField, fixedPhoneField, mobilePhoneField, emailField, siteBox, depBox);
            table.getSelectionModel().clearSelection();
                    refreshEmployees(allData, filteredData, pagedData, pagination,
                        nameSearchField.getText(),
                        emailSearchField.getText(),
                        phoneSearchField.getText(),
                        siteFilterBox,
                        departmentFilterBox);
            onDataChanged.run();
        }, errorBanner));

        deleteButton.setOnAction(evt -> withInlineErrorHandling(() -> {
            Employee selected = selectedEmployeeRef.get();
            if (selected == null) throw new IllegalArgumentException("Sélectionnez un salarié");
            directoryService.deleteEmployee(selected.getId());
            selectedEmployeeRef.set(null);
            clearEmployeeForm(lastNameField, firstNameField, fixedPhoneField, mobilePhoneField, emailField, siteBox, depBox);
            table.getSelectionModel().clearSelection();
                refreshEmployees(allData, filteredData, pagedData, pagination,
                    nameSearchField.getText(),
                    emailSearchField.getText(),
                    phoneSearchField.getText(),
                    siteFilterBox,
                    departmentFilterBox);
            onDataChanged.run();
        }, errorBanner));

        newButton.setOnAction(evt -> {
            selectedEmployeeRef.set(null);
            table.getSelectionModel().clearSelection();
            clearEmployeeForm(lastNameField, firstNameField, fixedPhoneField, mobilePhoneField, emailField, siteBox, depBox);
            hideError(errorBanner);
            Platform.runLater(lastNameField::requestFocus);
        });

        Runnable applyFilters = () -> applyEmployeeFilters(allData, filteredData,
            pagedData,
            pagination,
            nameSearchField.getText(),
            emailSearchField.getText(),
            phoneSearchField.getText(),
            siteFilterBox.getValue(),
            departmentFilterBox.getValue());

        nameSearchField.textProperty().addListener((obs, oldV, newV) -> applyFilters.run());
        emailSearchField.textProperty().addListener((obs, oldV, newV) -> applyFilters.run());
        phoneSearchField.textProperty().addListener((obs, oldV, newV) -> applyFilters.run());
        siteFilterBox.valueProperty().addListener((obs, oldV, newV) -> applyFilters.run());
        departmentFilterBox.valueProperty().addListener((obs, oldV, newV) -> applyFilters.run());

        resetFiltersButton.setOnAction(evt -> {
            nameSearchField.clear();
            emailSearchField.clear();
            phoneSearchField.clear();
            siteFilterBox.getSelectionModel().selectFirst();
            departmentFilterBox.getSelectionModel().selectFirst();
            applyFilters.run();
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, selected) -> {
            boolean hasSelection = selected != null;
            addButton.setVisible(!hasSelection);
            missingFieldsLabel.setVisible(!hasSelection && missingFieldsLabel.isManaged());
            newButton.setVisible(hasSelection);
            updateButton.setVisible(hasSelection);
            deleteButton.setVisible(hasSelection);
            hideError(errorBanner);
            if (selected != null) {
                selectedEmployeeRef.set(selected);
                lastNameField.setText(selected.getNom());
                firstNameField.setText(selected.getPrenom());
                fixedPhoneField.setText(selected.getTelephoneFixe());
                mobilePhoneField.setText(selected.getTelephonePortable());
                emailField.setText(selected.getEmail());
                siteBox.getSelectionModel().select(selected.getSite());
                depBox.getSelectionModel().select(selected.getDepartment());
            }
        });

        Label formTitle = new Label("Salarié");
        formTitle.getStyleClass().add("admin-form-title");

        HBox actions = new HBox(8, addButton, newButton, updateButton, deleteButton);
        actions.setAlignment(Pos.CENTER_LEFT);
        stretchActionButtons(actions, addButton, newButton, updateButton, deleteButton);

        VBox form = new VBox(10, formTitle, formGrid, missingFieldsLabel, actions, errorBanner);
        form.getStyleClass().add("admin-form-panel");
        form.setPrefWidth(380);
        form.setMinWidth(340);

        VBox tableSection = new VBox(8, table, pagination);
        VBox.setVgrow(table, Priority.ALWAYS);

        BorderPane pane = new BorderPane();
        pane.setTop(searchBar);
        pane.setCenter(tableSection);
        pane.setRight(form);
        pane.setPadding(new Insets(14));
        BorderPane.setMargin(searchBar, new Insets(0, 0, 10, 0));
        BorderPane.setMargin(tableSection, new Insets(0, 14, 0, 0));

        refreshEmployees(allData, filteredData, pagedData, pagination,
            nameSearchField.getText(),
            emailSearchField.getText(),
            phoneSearchField.getText(),
            siteFilterBox,
            departmentFilterBox);

        Tab tab = new Tab("Salariés", pane);
        tab.setClosable(false);
        return tab;
    }

    // ── Refresh & filtres ──────────────────────────────────────────────────────

    private void refreshSites(ObservableList<Site> allData,
                              ObservableList<Site> filteredData,
                              ObservableList<Site> pagedData,
                              Pagination pagination,
                              String filterValue) {
        allData.setAll(directoryService.getSites());
        applySiteFilter(allData, filteredData, pagedData, pagination, filterValue);
        refreshEmployeeReferenceChoices();
    }

    private void refreshDepartments(ObservableList<Department> allData,
                                    ObservableList<Department> filteredData,
                                    ObservableList<Department> pagedData,
                                    Pagination pagination,
                                    String filterValue) {
        allData.setAll(directoryService.getDepartments());
        applyDepartmentFilter(allData, filteredData, pagedData, pagination, filterValue);
        refreshEmployeeReferenceChoices();
    }

    private void refreshEmployees(ObservableList<Employee> allData,
                                  ObservableList<Employee> filteredData,
                                  ObservableList<Employee> pagedData,
                                  Pagination pagination,
                                  String nameFilter,
                                  String emailFilter,
                                  String phoneFilter,
                                  ComboBox<FilterOption> siteFilterBox,
                                  ComboBox<FilterOption> departmentFilterBox) {
        allData.setAll(directoryService.getAllEmployees());
        reloadSiteSearchFilters(siteFilterBox);
        reloadDepartmentSearchFilters(departmentFilterBox);
        applyEmployeeFilters(allData, filteredData, pagedData, pagination, nameFilter, emailFilter, phoneFilter,
                siteFilterBox.getValue(), departmentFilterBox.getValue());
    }

    private void applySiteFilter(ObservableList<Site> allData,
                                 ObservableList<Site> filteredData,
                                 ObservableList<Site> pagedData,
                                 Pagination pagination,
                                 String filterValue) {
        String term = normalizeFilter(filterValue);
        filteredData.setAll(term == null ? allData :
            allData.stream().filter(site -> contains(site.getVille(), term)).toList());
        updatePagination(pagination, filteredData, pagedData);
    }

    private void applyDepartmentFilter(ObservableList<Department> allData,
                                       ObservableList<Department> filteredData,
                                       ObservableList<Department> pagedData,
                                       Pagination pagination,
                                       String filterValue) {
        String term = normalizeFilter(filterValue);
        filteredData.setAll(term == null ? allData :
            allData.stream().filter(dep -> contains(dep.getNom(), term)).toList());
        updatePagination(pagination, filteredData, pagedData);
    }

    private void applyEmployeeFilters(ObservableList<Employee> allData,
                                      ObservableList<Employee> filteredData,
                                      ObservableList<Employee> pagedData,
                                      Pagination pagination,
                                      String nameFilter,
                                      String emailFilter,
                                      String phoneFilter,
                                      FilterOption siteFilter,
                                      FilterOption departmentFilter) {
        String normalizedName = normalizeFilter(nameFilter);
        String normalizedEmail = normalizeFilter(emailFilter);
        String normalizedPhoneRaw = phoneFilter == null ? "" : phoneFilter.trim();
        if (containsLetters(normalizedPhoneRaw)) {
            filteredData.clear();
            updatePagination(pagination, filteredData, pagedData);
            return;
        }
        String normalizedPhone = digitsOnly(normalizedPhoneRaw);
        Long siteId = siteFilter == null ? null : siteFilter.getId();
        Long departmentId = departmentFilter == null ? null : departmentFilter.getId();

        filteredData.setAll(allData.stream().filter(employee -> {
            if (siteId != null && (employee.getSite() == null || !siteId.equals(employee.getSite().getId()))) {
                return false;
            }
            if (departmentId != null && (employee.getDepartment() == null || !departmentId.equals(employee.getDepartment().getId()))) {
                return false;
            }
            if (normalizedName != null) {
                String[] parts = normalizedName.split("\\s+");
                String nom = employee.getNom() == null ? "" : employee.getNom().toLowerCase(Locale.ROOT);
                String prenom = employee.getPrenom() == null ? "" : employee.getPrenom().toLowerCase(Locale.ROOT);
                for (String part : parts) {
                    if (!nom.contains(part) && !prenom.contains(part)) {
                        return false;
                    }
                }
            }
            if (normalizedEmail != null && !contains(employee.getEmail(), normalizedEmail)) {
                return false;
            }
            if (!normalizedPhone.isBlank()) {
                String portableDigits = digitsOnly(employee.getTelephonePortable());
                String fixeDigits = digitsOnly(employee.getTelephoneFixe());
                if (!portableDigits.contains(normalizedPhone) && !fixeDigits.contains(normalizedPhone)) {
                    return false;
                }
            }
            return true;
        }).toList());
        updatePagination(pagination, filteredData, pagedData);
    }

    private String normalizeFilter(String value) {
        return (value == null || value.isBlank()) ? null : value.trim().toLowerCase(Locale.ROOT);
    }

    private boolean contains(String source, String term) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(term);
    }

    private boolean containsLetters(String value) {
        if (value == null) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            if (Character.isLetter(value.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private String digitsOnly(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.replaceAll("\\D", "");
    }

    private void loadEmployeeSearchFilters(ComboBox<FilterOption> siteFilterBox,
                                           ComboBox<FilterOption> departmentFilterBox) {
        reloadSiteSearchFilters(siteFilterBox);
        reloadDepartmentSearchFilters(departmentFilterBox);
    }

    private void reloadSiteSearchFilters(ComboBox<FilterOption> siteFilterBox) {
        Long selectedId = siteFilterBox.getValue() == null ? null : siteFilterBox.getValue().getId();
        ObservableList<FilterOption> options = FXCollections.observableArrayList();
        options.add(new FilterOption(null, "Tous les sites"));
        for (Site site : directoryService.getSites()) {
            options.add(new FilterOption(site.getId(), site.getVille()));
        }
        siteFilterBox.setItems(options);
        selectFilterValue(siteFilterBox, selectedId);
    }

    private void reloadDepartmentSearchFilters(ComboBox<FilterOption> departmentFilterBox) {
        Long selectedId = departmentFilterBox.getValue() == null ? null : departmentFilterBox.getValue().getId();
        ObservableList<FilterOption> options = FXCollections.observableArrayList();
        options.add(new FilterOption(null, "Tous les services"));
        for (Department department : directoryService.getDepartments()) {
            options.add(new FilterOption(department.getId(), department.getNom()));
        }
        departmentFilterBox.setItems(options);
        selectFilterValue(departmentFilterBox, selectedId);
    }

    private void selectFilterValue(ComboBox<FilterOption> comboBox, Long selectedId) {
        if (selectedId == null) {
            comboBox.getSelectionModel().selectFirst();
            return;
        }
        for (FilterOption option : comboBox.getItems()) {
            if (selectedId.equals(option.getId())) {
                comboBox.getSelectionModel().select(option);
                return;
            }
        }
        comboBox.getSelectionModel().selectFirst();
    }

    private void setHeight(Region control) {
        control.setMinHeight(CONTROL_HEIGHT);
        control.setPrefHeight(CONTROL_HEIGHT);
    }

    // ── Validation ─────────────────────────────────────────────────────────────

    private void validateRequiredText(String value, String fieldLabel) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldLabel + " est obligatoire");
        }
    }

    private void validateEmployeeRequiredFields(String lastName, String firstName,
                                                ComboBox<Site> siteBox, ComboBox<Department> depBox) {
        StringBuilder missing = new StringBuilder();
        if (lastName == null || lastName.isBlank())   { missing.append("Nom"); }
        if (firstName == null || firstName.isBlank())  { appendComma(missing); missing.append("Prénom"); }
        if (siteBox.getValue() == null)                { appendComma(missing); missing.append("Site"); }
        if (depBox.getValue() == null)                 { appendComma(missing); missing.append("Service"); }
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Champs obligatoires manquants : " + missing);
        }
    }

    private String getMissingEmployeeFields(TextField lastNameField, TextField firstNameField,
                                            ComboBox<Site> siteBox, ComboBox<Department> depBox) {
        StringBuilder missing = new StringBuilder();
        if (lastNameField.getText() == null || lastNameField.getText().isBlank())   { missing.append("Nom"); }
        if (firstNameField.getText() == null || firstNameField.getText().isBlank()) { appendComma(missing); missing.append("Prénom"); }
        if (siteBox.getValue() == null)                                             { appendComma(missing); missing.append("Site"); }
        if (depBox.getValue() == null)                                              { appendComma(missing); missing.append("Service"); }
        return missing.toString();
    }

    private void appendComma(StringBuilder sb) {
        if (!sb.isEmpty()) sb.append(", ");
    }

    // ── Utilitaires formulaire salarié ─────────────────────────────────────────

    private void clearEmployeeForm(TextField lastNameField, TextField firstNameField,
                                   TextField fixedPhoneField, TextField mobilePhoneField,
                                   TextField emailField, ComboBox<Site> siteBox,
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
        if (employeeSiteBox == null || employeeDepartmentBox == null) return;

        Site selectedSite = employeeSiteBox.getValue();
        Department selectedDepartment = employeeDepartmentBox.getValue();

        employeeSiteBox.setItems(FXCollections.observableArrayList(directoryService.getSites()));
        employeeDepartmentBox.setItems(FXCollections.observableArrayList(directoryService.getDepartments()));

        if (selectedSite != null) {
            employeeSiteBox.getSelectionModel().select(
                employeeSiteBox.getItems().stream()
                    .filter(s -> s.getId().equals(selectedSite.getId()))
                    .findFirst().orElse(null));
        }
        if (selectedDepartment != null) {
            employeeDepartmentBox.getSelectionModel().select(
                employeeDepartmentBox.getItems().stream()
                    .filter(d -> d.getId().equals(selectedDepartment.getId()))
                    .findFirst().orElse(null));
        }
    }
}

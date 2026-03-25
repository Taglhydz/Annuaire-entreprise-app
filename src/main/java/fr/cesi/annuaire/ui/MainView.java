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
import javafx.scene.control.Pagination;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class MainView {

    private static final double CONTROL_HEIGHT = 40;
    private static final int ROWS_PER_PAGE = 15;

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
        root.setPadding(new Insets(16));

        // Deux champs de recherche séparés
        TextField nameSearchField = new TextField();
        nameSearchField.setPromptText("Nom / prénom...");
        setHeight(nameSearchField);

        TextField phoneSearchField = new TextField();
        phoneSearchField.setPromptText("Portable...");
        setHeight(phoneSearchField);

        ComboBox<FilterOption> siteFilter = new ComboBox<>();
        ComboBox<FilterOption> departmentFilter = new ComboBox<>();

        siteFilter.setPrefWidth(200);
        departmentFilter.setPrefWidth(200);
        setHeight(siteFilter);
        setHeight(departmentFilter);

        Button resetButton = new Button("↺  Réinitialiser");
        resetButton.getStyleClass().add("button-reset");
        setHeight(resetButton);

        Label nameLabel = new Label("Nom / Prénom");
        Label phoneLabel = new Label("Portable");
        Label siteLabel = new Label("Site");
        Label serviceLabel = new Label("Service");

        HBox filterBar = new HBox(10,
            nameLabel, nameSearchField,
            phoneLabel, phoneSearchField,
            siteLabel, siteFilter,
            serviceLabel, departmentFilter,
            resetButton
        );
        filterBar.getStyleClass().add("filter-bar");
        filterBar.setStyle("-fx-padding: 12 16 12 16; -fx-spacing: 10; -fx-alignment: CENTER_LEFT;");

        // Wrapping dans un VBox pour avoir un peu d'espace avant la table
        VBox topArea = new VBox(0, filterBar);
        topArea.setSpacing(0);

        TableView<Employee> table = buildTable();
        ObservableList<Employee> pagedData = FXCollections.observableArrayList();
        table.setItems(pagedData);

        Pagination pagination = new Pagination(1, 0);
        pagination.setMaxPageIndicatorCount(6);
        pagination.setPageFactory(pageIndex -> new Region());
        pagination.getStyleClass().add("compact-pagination");
        pagination.setMinHeight(40);
        pagination.setPrefHeight(40);
        pagination.setMaxHeight(40);
        pagination.currentPageIndexProperty().addListener((obs, oldV, newV) ->
            showPage(tableData, pagedData, newV.intValue()));

        VBox tableSection = new VBox(8, table, pagination);
        VBox.setVgrow(table, Priority.ALWAYS);

        VBox detailsPanel = buildDetailsPanel();

        root.setTop(topArea);
        root.setCenter(tableSection);
        root.setRight(detailsPanel);

        BorderPane.setMargin(tableSection, new Insets(12, 12, 0, 0));
        BorderPane.setMargin(detailsPanel, new Insets(12, 0, 0, 0));

        loadFilters(siteFilter, departmentFilter);
        refreshData(nameSearchField, phoneSearchField, siteFilter, departmentFilter, pagination, pagedData);

        nameSearchField.textProperty().addListener((obs, oldV, newV) ->
            refreshData(nameSearchField, phoneSearchField, siteFilter, departmentFilter, pagination, pagedData));
        phoneSearchField.textProperty().addListener((obs, oldV, newV) ->
            refreshData(nameSearchField, phoneSearchField, siteFilter, departmentFilter, pagination, pagedData));
        siteFilter.valueProperty().addListener((obs, oldV, newV) ->
            refreshData(nameSearchField, phoneSearchField, siteFilter, departmentFilter, pagination, pagedData));
        departmentFilter.valueProperty().addListener((obs, oldV, newV) ->
            refreshData(nameSearchField, phoneSearchField, siteFilter, departmentFilter, pagination, pagedData));

        resetButton.setOnAction(evt -> {
            nameSearchField.clear();
            phoneSearchField.clear();
            siteFilter.getSelectionModel().selectFirst();
            departmentFilter.getSelectionModel().selectFirst();
            refreshData(nameSearchField, phoneSearchField, siteFilter, departmentFilter, pagination, pagedData);
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) ->
            showDetails(newSel));

        Scene scene = new Scene(root, 1300, 700);
        scene.getStylesheets().add(
            getClass().getResource("/styles/app.css").toExternalForm()
        );

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.isShiftDown() && event.getCode() == KeyCode.Q) {
                openAdminLoginDialog(nameSearchField, phoneSearchField, siteFilter, departmentFilter,
                        pagination, pagedData);
                event.consume();
            }
        });

        return scene;
    }

    private void loadFilters(ComboBox<FilterOption> siteFilter, ComboBox<FilterOption> departmentFilter) {
        ObservableList<FilterOption> siteOptions = FXCollections.observableArrayList();
        siteOptions.add(new FilterOption(null, "Tous les sites"));
        for (Site site : directoryService.getSites()) {
            siteOptions.add(new FilterOption(site.getId(), site.getVille()));
        }

        ObservableList<FilterOption> departmentOptions = FXCollections.observableArrayList();
        departmentOptions.add(new FilterOption(null, "Tous les services"));
        for (Department department : directoryService.getDepartments()) {
            departmentOptions.add(new FilterOption(department.getId(), department.getNom()));
        }

        siteFilter.setItems(siteOptions);
        departmentFilter.setItems(departmentOptions);

        siteFilter.getSelectionModel().selectFirst();
        departmentFilter.getSelectionModel().selectFirst();
    }

    private void refreshData(TextField nameSearchField,
                              TextField phoneSearchField,
                              ComboBox<FilterOption> siteFilter,
                              ComboBox<FilterOption> departmentFilter,
                              Pagination pagination,
                              ObservableList<Employee> pagedData) {
        Long siteId = Optional.ofNullable(siteFilter.getValue()).map(FilterOption::getId).orElse(null);
        Long departmentId = Optional.ofNullable(departmentFilter.getValue()).map(FilterOption::getId).orElse(null);

        String phoneQueryRaw = phoneSearchField.getText() == null ? "" : phoneSearchField.getText().trim();
        if (containsLetters(phoneQueryRaw)) {
            tableData.clear();
            updatePagination(pagination, pagedData);
            return;
        }
        String phoneQuery = digitsOnly(phoneQueryRaw);

        List<Employee> result = directoryService.searchEmployees(
            null, siteId, departmentId);

        String nameQuery = nameSearchField.getText().trim();
        if (!nameQuery.isBlank()) {
            String[] parts = nameQuery.toLowerCase(Locale.ROOT).split("\\s+");
            result = result.stream().filter(emp -> {
                String nom = emp.getNom() == null ? "" : emp.getNom().toLowerCase(Locale.ROOT);
                String prenom = emp.getPrenom() == null ? "" : emp.getPrenom().toLowerCase(Locale.ROOT);
                for (String part : parts) {
                    if (!nom.contains(part) && !prenom.contains(part)) return false;
                }
                return true;
            }).toList();
        }

        if (!phoneQuery.isBlank()) {
            result = result.stream()
                    .filter(emp -> digitsOnly(emp.getTelephonePortable()).contains(phoneQuery))
                    .toList();
        }

        tableData.setAll(result);
        updatePagination(pagination, pagedData);
    }

    private void updatePagination(Pagination pagination, ObservableList<Employee> pagedData) {
        int total = tableData.size();
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
        showPage(tableData, pagedData, current);
    }

    private void showPage(ObservableList<Employee> source,
                          ObservableList<Employee> target,
                          int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        if (fromIndex >= source.size()) {
            target.clear();
            return;
        }
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, source.size());
        target.setAll(source.subList(fromIndex, toIndex));
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

        table.getColumns().addAll(nomCol, prenomCol, siteCol, serviceCol, portableCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        return table;
    }

    private VBox buildDetailsPanel() {
        Label title = new Label("Fiche salarié");
        title.getStyleClass().add("detail-title");

        nomValue     = valueLabel();
        prenomValue  = valueLabel();
        fixeValue    = valueLabel();
        portableValue = valueLabel();
        emailValue   = valueLabel();
        siteValue    = valueLabel();
        serviceValue = valueLabel();

        VBox panel = new VBox(10,
            title,
            line("Nom",               nomValue),
            line("Prénom",            prenomValue),
            line("Téléphone fixe",    fixeValue),
            line("Téléphone portable",portableValue),
            line("Email",             emailValue),
            line("Site",              siteValue),
            line("Service",           serviceValue)
        );
        panel.getStyleClass().add("details-panel");
        panel.setPrefWidth(340);

        return panel;
    }

    private HBox line(String labelText, Label value) {
        Label lbl = new Label(labelText);
        lbl.getStyleClass().add("detail-label");
        return new HBox(10, lbl, value);
    }

    private Label valueLabel() {
        Label label = new Label("—");
        label.getStyleClass().add("detail-value");
        return label;
    }

    private void showDetails(Employee employee) {
        if (employee == null) {
            nomValue.setText("—");
            prenomValue.setText("—");
            fixeValue.setText("—");
            portableValue.setText("—");
            emailValue.setText("—");
            siteValue.setText("—");
            serviceValue.setText("—");
            return;
        }
        nomValue.setText(safe(employee.getNom()));
        prenomValue.setText(safe(employee.getPrenom()));
        fixeValue.setText(safe(employee.getTelephoneFixe()));
        portableValue.setText(safe(employee.getTelephonePortable()));
        emailValue.setText(safe(employee.getEmail()));
        siteValue.setText(employee.getSite() == null ? "—" : safe(employee.getSite().getVille()));
        serviceValue.setText(employee.getDepartment() == null ? "—" : safe(employee.getDepartment().getNom()));
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }

    private void setHeight(Region control) {
        control.setMinHeight(CONTROL_HEIGHT);
        control.setPrefHeight(CONTROL_HEIGHT);
    }

    private void openAdminLoginDialog(TextField nameSearchField, TextField phoneSearchField,
                                      ComboBox<FilterOption> siteFilter,
                                      ComboBox<FilterOption> departmentFilter,
                                      Pagination pagination,
                                      ObservableList<Employee> pagedData) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Authentification administrateur");
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/styles/app.css").toExternalForm()
        );

        TextField usernameField = new TextField();
        usernameField.setPromptText("Nom d'utilisateur");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(12, 0, 0, 0));
        grid.addRow(0, new Label("Utilisateur"), usernameField);
        grid.addRow(1, new Label("Mot de passe"), passwordField);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.setDefaultButton(false);
            passwordField.setOnAction(evt -> {
                if (!okButton.isDisabled()) okButton.fire();
            });
        }
        dialog.setOnShown(event -> Platform.runLater(usernameField::requestFocus));
        dialog.setResultConverter(type -> type == ButtonType.OK);

        Optional<Boolean> confirmed = dialog.showAndWait();
        if (confirmed.isPresent() && confirmed.get()) {
            boolean ok = adminAuthService.authenticate(usernameField.getText(), passwordField.getText());
            if (ok) {
                new AdminDashboardView(directoryService, () -> {
                    loadFilters(siteFilter, departmentFilter);
                    refreshData(nameSearchField, phoneSearchField, siteFilter, departmentFilter,
                        pagination, pagedData);
                }).show();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Identifiants incorrects — accès refusé.");
                alert.setHeaderText("Accès refusé");
                alert.showAndWait();
            }
        }
    }
}

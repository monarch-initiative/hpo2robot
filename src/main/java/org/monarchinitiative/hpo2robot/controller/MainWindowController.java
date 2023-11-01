package org.monarchinitiative.hpo2robot.controller;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import org.monarchinitiative.hpo2robot.Launcher;
import org.monarchinitiative.hpo2robot.controller.services.LoadHpoService;
import org.monarchinitiative.hpo2robot.model.RobotItem;
import org.monarchinitiative.hpo2robot.view.ParentTermAdder;
import org.monarchinitiative.hpo2robot.view.ValidatingPane;
import org.monarchinitiative.hpo2robot.view.ValidatingTextEntryPane;
import org.monarchinitiative.hpo2robot.model.Options;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.hpo2robot.view.ViewFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainWindowController extends BaseController implements Initializable {
    public MenuItem newMenuItem;
    public MenuItem exitMenuItem;
    public WebView currentRobotView;

    @FXML
    private TableView<RobotItem> robotTableView;
    public TableColumn<RobotItem, String> parentTermCol;
    public TableColumn<RobotItem, String> definitionCol;
    public TableColumn<RobotItem, String> pmidsCol;
    public TableColumn<RobotItem, String> issueCol;
    public TableColumn<RobotItem, String> newTermLabelCol;
    Logger LOGGER = LoggerFactory.getLogger(MainWindowController.class);

    @FXML
    public ValidatingPane termLabelValidator;

    @FXML
    public ValidatingTextEntryPane definitionPane;
    @FXML
    public ValidatingTextEntryPane commentPane;



    @FXML
    private StackPane ontologyTreeViewPane;

    @FXML
    private ParentTermAdder parentTermAdder;

    @FXML
    private VBox statusBar;
    @FXML
    public Label statusBarLabel;
    private StringProperty statusBarTextProperty;

    private BooleanProperty readinessProperty;

    private Options options;

    private Ontology hpOntology;

    private  OntologyTree ontologyTree;

    /** This gets set to true once the Ontology tree has finished initiatializing. Before that
     * we can check to make sure the user does not try to open a disease before the Ontology is
     * done loading.
     */
    private BooleanProperty ontologyLoadedProperty = new SimpleBooleanProperty(false);
    /** key - label, synonym, or term id of each non-obsolete HP term; value: primary label */
    private Map<String, String> labelMap;

    public MainWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }

    @FXML
    void optionsAction() {
        this.viewFactory.showOptionsWindow();
        options = viewFactory.getOptions();
        System.out.println("After get options");
        if (checkOptionsReadiness()) {
            loadHpoAndSetupOntologyTree();
        }
    }

    /**
     * This method should be called after we have validated that the three
     * files needed in the Options are present and valid. This method then
     * loads the HPO Ontology object and uses it to set up the Ontology Tree
     * browser on the left of the GUI.
     */
    private void loadHpoAndSetupOntologyTree() {
        LOGGER.trace("loading hp.json");
        long start = System.currentTimeMillis();
        SimpleDoubleProperty progress = new SimpleDoubleProperty(0.0);
       // progress.addListener((obj, oldvalue, newvalue) -> updateProgress(newvalue.doubleValue(), 100) );
        LoadHpoService service = new LoadHpoService(options.getHpJsonFile());
        service.setOnSucceeded(e -> {
            this.hpOntology = service.getValue();
            Map<String, String> metamap = hpOntology.getMetaInfo();
            String version = metamap.getOrDefault("data-version", "n/a");
            LOGGER.info("Loaded HPO, version {}", version);
            ontologyLoadedProperty.set(true);
            if (checkOptionsReadiness()) {
                setupGuiOntologyTree(progress);
            }

        });
        service.setOnFailed(e -> {
            LOGGER.error("Could not load hp.jsdon");
            ontologyLoadedProperty.set(false);
        });
        service.start();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LOGGER.trace("Initializing MainWindowController");
        termLabelValidator.setFieldLabel("New Term Label");
        definitionPane.initializeButtonText(ValidatingTextEntryPaneController.CREATE_DEFINITION);
        commentPane.initializeButtonText(ValidatingTextEntryPaneController.CREATE_COMMENT);



        setUpStatusBar();
        readinessProperty = new SimpleBooleanProperty(false);
        setUpKeyAccelerators();
        boolean ready = checkOptionsReadiness();
        if (ready) {
            loadHpoAndSetupOntologyTree();
        }
        setUpTableView();
    }


    private void setUpTableView() {
        robotTableView.setEditable(false);
        newTermLabelCol.setCellValueFactory(new PropertyValueFactory<>("newTermLabelCol"));
        newTermLabelCol.setCellFactory(TextFieldTableCell.forTableColumn());
        newTermLabelCol.setEditable(true);

        definitionCol.setCellValueFactory(new PropertyValueFactory<>("definitionCol"));
        definitionCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<RobotItem, String> call(TableColumn<RobotItem, String> p) {
                return new TableCell<>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setTooltip(null);
                            setText(null);
                        } else {
                            // show just the first 50 chars of the definition
                            Tooltip tooltip = new Tooltip();
                            RobotItem robotItem = getTableView().getItems().get(getTableRow().getIndex());
                            tooltip.setText(robotItem.getNewTermDefinition());
                            setTooltip(tooltip);
                            String displayText = item.length() < 50 ? item : item.substring(0,45) + "...";
                            setText(displayText);
                        }
                    }
                };
            }
        });
        parentTermCol.setCellValueFactory(new PropertyValueFactory<>("parentTermCol"));
        parentTermCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<RobotItem, String> call(TableColumn<RobotItem, String> p) {
                return new TableCell<>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setTooltip(null);
                            setText(null);
                        } else {
                            // show just the first 50 chars of the definition
                            Tooltip tooltip = new Tooltip();
                            RobotItem robotItem = getTableView().getItems().get(getTableRow().getIndex());
                            Set<Term> parents = robotItem.getParentTerms();
                            String displayText = parents.stream().map(Term::getName).collect(Collectors.joining("; "));
                            tooltip.setText(displayText);
                            setTooltip(tooltip);
                            if (parents.isEmpty()) {
                                // should never happen
                                displayText = "error - no parent term";
                            } else {
                                Term parent = parents.iterator().next();
                                String name = parent.getName();
                                displayText = name.length() < 40 ? name : name.substring(0,35) + "...";
                                if (parents.size() > 1) {
                                    displayText = String.format("%s (%d)", displayText, parents.size());
                                }
                            }
                            setText(displayText);
                        }
                    }
                };
            }
        });
       pmidsCol.setCellValueFactory(new PropertyValueFactory<>("pmids"));
       pmidsCol.setCellFactory(new Callback<>() {
           @Override
           public TableCell<RobotItem, String> call(TableColumn<RobotItem, String> p) {
               return new TableCell<>() {
                   @Override
                   public void updateItem(String item, boolean empty) {
                       super.updateItem(item, empty);
                       if (item == null) {
                           setTooltip(null);
                           setText(null);
                       } else {
                           // assume we have 1 or 2 PMIDs and should fit. Consider more customization later
                           Tooltip tooltip = new Tooltip();
                           RobotItem robotItem = getTableView().getItems().get(getTableRow().getIndex());
                           Set<String> pmids = robotItem.getPmids();
                           String displayText = String.join(":", pmids);
                           tooltip.setText(displayText);
                           setTooltip(tooltip);
                           setText(displayText);
                       }
                   }
               };
           }
       });
        issueCol.setCellValueFactory(new PropertyValueFactory<>("issue"));
        issueCol.setCellFactory(TextFieldTableCell.forTableColumn());
        this.robotTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // do not show "extra column"

    }

    private boolean checkOptionsReadiness() {
       this.options = viewFactory.getOptions();
       readinessProperty.set(options.isValid());
       statusBarOptions();
       return options.isValid();
    }


    private void statusBarOptions() {
        if (readinessProperty.get()) {
            statusBarTextProperty.set("input data: ready");
            statusBarLabel.setTextFill(Color.BLACK);
            statusBarLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
            if (! ontologyLoadedProperty.get()) {
                statusBarTextProperty.set("hp.json not loaded.");
                statusBarLabel.setTextFill(Color.RED);
                statusBarLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
            }
        } else {
            statusBarTextProperty.set(this.options.getErrorMessage());
            statusBarLabel.setTextFill(Color.RED);
            statusBarLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        }
    }

    private void setUpStatusBar() {
        statusBarTextProperty = new SimpleStringProperty("Starting");
        statusBar.setStyle("-fx-background-color: gainsboro");
        statusBar.setMinHeight(30);
        statusBar.setPadding(new Insets(10, 50, 10, 50));
        statusBar.setSpacing(10);
        statusBarLabel.textProperty().bind(statusBarTextProperty);
        statusBarLabel.setTextFill(Color.BLACK);
        statusBarLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
    }


    private void setUpKeyAccelerators() {
        newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.META_DOWN));
        exitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.META_DOWN));
        // TODO  -- Other menu entries
    }


    private void setupGuiOntologyTree(SimpleDoubleProperty progress) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                progress.addListener((obj, oldvalue, newvalue) -> updateProgress(newvalue.doubleValue(), 100) );
                //setupGuiOntologyTree(progress);
                updateProgress(100, 100);
                return null;
            }
        };


        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.progressProperty().bind(task.progressProperty());
        progressIndicator.setMinHeight(70);
        progressIndicator.setMinWidth(70);
        progressIndicator.setMaxHeight(70);
        progressIndicator.setMaxWidth(70);
        ontologyTreeViewPane.setMinWidth(250);
        Label initOntoLabel=new Label("initializing HPO browser");

        task.setOnRunning(event -> {
            ontologyTreeViewPane.getChildren().addAll(progressIndicator,initOntoLabel);
            StackPane.setAlignment(progressIndicator, Pos.CENTER);
        });

        task.setOnSucceeded(event -> {
            ontologyTreeViewPane.getChildren().clear();
            ontologyTreeViewPane.getChildren().remove(initOntoLabel);
            setupAutocomplete();
            setupOntologyTreeView();
            this.parentTermAdder.setOntology(this.hpOntology);
            parentTermAdder.linkOntologyTreeAddButton(ontologyTree);
        });

        new Thread(task).start();
    }


   /**
     * Uses the @link WidthAwareTextFields} class to set up autocompletion for the parent HPO name
     */
    private void setupAutocomplete() {


        /*
        if (labelMap != null) {
            WidthAwareTextFields.bindWidthAwareAutoCompletion(parentTermTextField, labelMap.keySet());
        }

         */
    }

    private void setupOntologyTreeView() {
        Consumer<Term> addHook = (this::addPhenotypeTerm);
        this.ontologyTree = new OntologyTree(hpOntology, addHook);
        FXMLLoader ontologyTreeLoader = new FXMLLoader(Launcher.class.getResource("view/OntologyTreeViewPane.fxml"));
        ontologyTreeLoader.setControllerFactory(clazz -> this.ontologyTree);
        try {
            ontologyTreeViewPane.getChildren().add(ontologyTreeLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void addPhenotypeTerm(Term phenotypeTerm) {
       // pa.setText(phenotypeTerm.getName());
        System.out.println("addPhenotypeTerm: adding " + phenotypeTerm);
        // automaticPmidUpdateBox.setSelected(!phenotypeTerm.isPresent());
    }




    /**
     * Write the settings from the current session to file and exit.
     */
    @FXML
    private void exitGui() {
        //settings.saveToFile();
        boolean clean = true;// savedBeforeExit();
        if (clean) {
            javafx.application.Platform.exit();
        }

    }

    public void addRobotItem(ActionEvent actionEvent) {
        // TODO -- assemble and validate the information from the widgets and create a new RobotItem
        String newTermLabel = termLabelValidator.getLabel().toString();
        System.out.println(newTermLabel);
    }
}

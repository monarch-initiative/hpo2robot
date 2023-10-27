package org.monarchinitiative.controller;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.monarchinitiative.Launcher;
import org.monarchinitiative.controller.services.LoadHpoService;
import org.monarchinitiative.model.Options;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.view.ValidatingPane;
import org.monarchinitiative.view.ValidatingTextEntryPane;
import org.monarchinitiative.view.ViewFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainWindowController extends BaseController implements Initializable {
    public MenuItem newMenuItem;
    public MenuItem exitMenuItem;
    Logger LOGGER = LoggerFactory.getLogger(MainWindowController.class);

    @FXML
    public ValidatingPane termLabelValidator;

    @FXML
    public ValidatingTextEntryPane definitionPane;
    @FXML
    public ValidatingTextEntryPane commentPane;

    @FXML
    private TableView<?> robotTableView;

    @FXML
    private StackPane ontologyTreeViewPane;

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
        LoadHpoService service = new LoadHpoService(options.getHpJsonFile());
        service.setOnSucceeded(e -> {
            this.hpOntology = service.getValue();
            Map<String, String> metamap = hpOntology.getMetaInfo();
            String version = metamap.getOrDefault("data-version", "n/a");
            LOGGER.info("Loaded HPO, version {}", version);
            ontologyLoadedProperty.set(true);
            if (checkOptionsReadiness()) {
                setupGuiOntologyTree();
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


    private void setupGuiOntologyTree() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                SimpleDoubleProperty progress = new SimpleDoubleProperty(0.0);
                progress.addListener((obj, oldvalue, newvalue) -> updateProgress(newvalue.doubleValue(), 100) );
                initResources(progress);
                updateProgress(100, 100);
                return null;
            }
        };
        new Thread(task).start();

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
        });

    }

   /**
     * Uses the @link WidthAwareTextFields} class to set up autocompletion for the disease name and the HPO name
     */
    private void setupAutocomplete() {
        /*
        if (hpoSynonym2LabelMap != null) {
            WidthAwareTextFields.bindWidthAwareAutoCompletion(hpoNameTextField, hpoSynonym2LabelMap.keySet());
        }
        if (hpoModifer2idMap != null) {
            WidthAwareTextFields.bindWidthAwareAutoCompletion(modifiertextField, hpoModifer2idMap.keySet());
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
     * Called by the initialize method. Serves to set up the
     * Maps with HPO and Disease name information for the autocompletes.
     */
    private void initResources(DoubleProperty progress) {
        long start = System.currentTimeMillis();
        this.labelMap = ResourceInitializer.initializeLabelMap(hpOntology);
        long end = System.currentTimeMillis();
        String seconds = String.format("%.2f seconds", (end-start)/1000.0 );
        LOGGER.info("initResources in {}", seconds);
        // Set up ontology tree
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                SimpleDoubleProperty progress = new SimpleDoubleProperty(0.0);
                progress.addListener((obj, oldvalue, newvalue) -> updateProgress(newvalue.doubleValue(), 100) );
                initResources(progress);
                updateProgress(100, 100);
                return null;
            }
        };
        new Thread(task).start();

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
            ontologyLoadedProperty.set(true);
            statusBarOptions();
        });

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

}

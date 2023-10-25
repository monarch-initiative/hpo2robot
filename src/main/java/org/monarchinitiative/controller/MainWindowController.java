package org.monarchinitiative.controller;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.monarchinitiative.model.Options;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.view.ValidatingPane;
import org.monarchinitiative.view.ValidatingTextEntryPane;
import org.monarchinitiative.view.ViewFactory;

import java.net.URL;
import java.util.ResourceBundle;


public class MainWindowController extends BaseController implements Initializable {


    @FXML
    private TreeView<String> emailTreeview;

    @FXML
    public ValidatingPane termLabelValidator;

    @FXML
    public ValidatingTextEntryPane definitionPane;
    @FXML
    public ValidatingTextEntryPane commentPane;

    @FXML
    private TableView<?> emailsTableview;

    @FXML
    private StackPane ontologyTreeViewPane;

    @FXML
    private VBox statusBar;
    @FXML
    public Label statusBarLabel;
    private StringProperty statusBarTextProperty;

    private BooleanProperty readinessProperty;

    private Options options;

    /** This gets set to true once the Ontology tree has finished initiatializing. Before that
     * we can check to make sure the user does not try to open a disease before the Ontology is
     * done loading.
     */
    private BooleanProperty doneInitializingOntology = new SimpleBooleanProperty(false);

    public MainWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }

    @FXML
    void optionsAction() {
        this.viewFactory.showOptionsWindow();
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Main init");
        termLabelValidator.setFieldLabel("New Term Label");
        definitionPane.initializeButtonText(ValidatingTextEntryPaneController.CREATE_DEFINITION);
        commentPane.initializeButtonText(ValidatingTextEntryPaneController.CREATE_COMMENT);
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
            doneInitializingOntology.set(true);
        });
        setUpStatusBar();
        readinessProperty = new SimpleBooleanProperty(false);
        checkReadiness();
    }

    private void checkReadiness() {
       this.options = viewFactory.getOptions();
       readinessProperty.set(options.isValid());
       statusBarOptions();


    }


    private void statusBarOptions() {
        if (readinessProperty.get()) {
            statusBarTextProperty.set("input data: ready");
            statusBarLabel.setTextFill(Color.BLACK);
            statusBarLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
        } else {
            statusBarTextProperty.set("Need to load options!");
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
        /*
        Consumer<Main.PhenotypeTerm> addHook = (this::addPhenotypeTerm);
        this.ontologyTree = new OntologyTree(ontology, addHook);
        FXMLLoader ontologyTreeLoader = new FXMLLoader(OntologyTree.class.getResource("OntologyTree.fxml"));
        ontologyTreeLoader.setControllerFactory(clazz -> this.ontologyTree);
        try {
            ontologyTreeView.getChildren().add(ontologyTreeLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

         */
    }

    /**
     * Called by the initialize method. Serves to set up the
     * Maps with HPO and Disease name information for the autocompletes.
     */
    private void initResources(DoubleProperty progress) {
        long start = System.currentTimeMillis();

       /* LOGGER.info("initResources");
        HPOParser hpoParser = new HPOParser();

        LOGGER.info("Done HPOParser CTOR");
        if (progress != null) {
            progress.setValue(75);
        }

        long end = System.currentTimeMillis();
        //multi threading does not seem to help. Concurrency probably does not work for IO operations.
        //https://stackoverflow.com/questions/902425/does-multithreading-make-sense-for-io-bound-operations
        LOGGER.info(String.format("time cost for parsing resources: %ds",  (end - start)/1000));
        start = end;
        ontology = hpoParser.getHpoOntology();
        hponame2idMap = hpoParser.getHpoName2IDmap();
        hpoSynonym2LabelMap = hpoParser.getHpoSynonym2PreferredLabelMap();
        hpoModifer2idMap = hpoParser.getModifierMap();
        if (hpoModifer2idMap == null) {
            LOGGER.error("hpoModifer2idMap is NULL");
        }
        end = System.currentTimeMillis();
        LOGGER.info(String.format("time for parsing OMIM, ontology, synonysm, modifiers: %ds",  (end - start)/1000));
        LOGGER.trace("Done input HPO");

        */
    }


}

package org.monarchinitiative.hpo2robot.controller;

import javafx.application.HostServices;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import org.monarchinitiative.hpo2robot.github.GitHubUtil;
import org.monarchinitiative.hpo2robot.model.Model;
import org.monarchinitiative.hpo2robot.model.RobotItem;
import org.monarchinitiative.hpo2robot.controller.runner.RobotRunner;
import org.monarchinitiative.hpo2robot.view.*;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainWindowController extends BaseController implements Initializable {
    private final Logger LOGGER = LoggerFactory.getLogger(MainWindowController.class);

    private final ObjectProperty<MinimalOntology> hpOntology = new SimpleObjectProperty<>();
    @FXML
    public MenuItem newMenuItem;
    @FXML
    public MenuItem exitMenuItem;
    @FXML
    public MenuItem optionsMenuItem;
    @FXML
    public WebView currentRobotView;
    @FXML
    public PmidXrefAdder pmidXrefAdderBox;
    @FXML
    public GitHubIssueBox gitHubIssueBox;
    @FXML
    public RobotRunnerPane addNewHpoTermBox;
    @FXML
    private TableView<RobotItem> robotTableView;
    @FXML
    public TableColumn<RobotItem, String> parentTermCol;
    @FXML
    public TableColumn<RobotItem, String> definitionCol;
    @FXML
    public TableColumn<RobotItem, String> pmidsCol;
    @FXML
    public TableColumn<RobotItem, String> issueCol;
    @FXML
    public TableColumn<RobotItem, String> newTermLabelCol;
    @FXML
    public ValidatingPane termLabelValidator;
    @FXML
    public ValidatingTextEntryPane definitionPane;
    @FXML
    public OntologyTree ontologyTree;
    @FXML
    private ParentTermAdder parentTermAdder;
    @FXML
    private VBox statusBar;
    @FXML
    public Label statusBarLabel;
    private StringProperty statusBarTextProperty;
    private Optional<HostServices> hostServicesOpt;

    private final Model model;

    /** This gets set to true once the Ontology tree has finished initiatializing. Before that
     * we can check to make sure the user does not try to open a disease before the Ontology is
     * done loading.
     */
    private final BooleanProperty ontologyLoadedProperty = new SimpleBooleanProperty(false);

    private final BooleanProperty robotIssueIsReadyProperty = new SimpleBooleanProperty(false);

    public MainWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
        model = new Model();
    }

    @FXML
    void optionsAction() {
        this.viewFactory.showOptionsWindow();
    }



    /**
     * This method should be called after we have validated that the three
     * files needed in the Options are present and valid. This method then
     * loads the HPO Ontology object and uses it to set up the Ontology Tree
     * browser on the left of the GUI.
     */
    private void loadHpoAndSetupOntologyTree() {
        ontologyTree.addHookProperty().set(this::addPhenotypeTerm);
        ontologyTree.ontologyProperty().bind(hpOntology);

        // Setup event handlers to update HPO in case the user changes path to another one
        viewFactory.getOptions().hpJsonFileProperty().addListener((obs, old, hpJsonFilePath) -> loadHpo(hpJsonFilePath));
        // Do the actual loading..
        loadHpo(viewFactory.getOptions().getHpJsonFile());
        this.model.setOptions(viewFactory.getOptions());
    }

    private void loadHpo(File hpJsonFilePath) {
        if (hpJsonFilePath != null && hpJsonFilePath.isFile()) {
            Task<MinimalOntology> hpoLoadTask = new Task<>() {
                @Override
                protected MinimalOntology call() {
                    MinimalOntology hpoOntology = OntologyLoader.loadOntology(hpJsonFilePath);
                    LOGGER.info("Loaded HPO, version {}", hpoOntology.version().orElse("n/a"));
                    return hpoOntology;
                }
            };
            hpoLoadTask.setOnSucceeded(e -> {
                hpOntology.set(hpoLoadTask.getValue());
                parentTermAdder.setOntology(this.hpOntology.get());
            });
            hpoLoadTask.setOnFailed(e -> {
                LOGGER.warn("Could not load HPO from {}", hpJsonFilePath.getAbsolutePath());
                hpOntology.set(null);
            });
            Thread thread = new Thread(hpoLoadTask);
            thread.start();
        } else {
            hpOntology.set(null);
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LOGGER.trace("Initializing MainWindowController");
        this.hostServicesOpt = this.viewFactory.getHostervicesOpt();
        this.gitHubIssueBox.setHostServices(this.hostServicesOpt);
        termLabelValidator.setFieldLabel("New Term Label");
        setUpStatusBar();
        setUpKeyAccelerators();
        setupStatusBarOptions();
        loadHpoAndSetupOntologyTree();
        setUpTableView();
        setupRobotItemHandlers();
        setUpGithubColumnContextMenu();
        setUpPmidXrefAdder();
        setUpNewTermReadiness();
    }

    /**
     * We are ready to enter a new ROBOT item if we have a valid label, at least one parent term
     * and a valid definition. Here, we bind the new ROBOT item button to the
     * three corresponding Boolean properties.
     */
    private void setUpNewTermReadiness() {
        BooleanBinding readyBinding = parentTermAdder.parentTermsReady()
                .and(termLabelValidator.getIsValidProperty())
                .and(definitionPane.isReadyProperty());
        this.robotIssueIsReadyProperty.bind(readyBinding);
        this.addNewHpoTermBox.bindNewRobotItemButton(robotIssueIsReadyProperty);
        IntegerBinding robotTableSizeBinding = Bindings.size(robotTableView.getItems());
        BooleanProperty tableReadyProperty = new SimpleBooleanProperty();
        tableReadyProperty.bind(robotTableSizeBinding.greaterThan(0));
        this.addNewHpoTermBox.bindWriteRobotFileButton(tableReadyProperty);
    }

    private void setUpPmidXrefAdder() {
        this.pmidXrefAdderBox.setViewFactory(this.viewFactory);
    }

    private void setUpGithubColumnContextMenu() {
        issueCol.setCellFactory(
                (column) -> {
                    final TableCell<RobotItem, String> cell = new TableCell<>();
                    cell.itemProperty().addListener(// ChangeListener
                            (obs, oldValue, newValue) -> {
                                if (newValue != null) {
                                    final ContextMenu cellMenu = new ContextMenu();
                                    MenuItem ghMenuItem = new MenuItem("Open GitHub Issue");
                                    ghMenuItem.setOnAction(e -> {
                                        RobotItem item = cell.getTableRow().getItem();
                                        String gitHubIssue = item.getIssue();
                                        if (hostServicesOpt.isPresent()) {
                                            GitHubUtil.openInGithubAction(gitHubIssue, hostServicesOpt.get());
                                        } else {
                                            LOGGER.error("Could not access HostServices");
                                        }
                                    });
                                    MenuItem summaryMenuItem = new MenuItem("Copy item summary");
                                    summaryMenuItem.setOnAction(e -> {
                                        RobotItem item = cell.getTableRow().getItem();
                                        String summary = item.getIssueSummary();
                                        final Clipboard clipboard = Clipboard.getSystemClipboard();
                                        final ClipboardContent content = new ClipboardContent();
                                        content.putString(summary);
                                        clipboard.setContent(content);

                                    });
                                    MenuItem markedFinishedMenuItem = new MenuItem("Mark finished");
                                    markedFinishedMenuItem.setOnAction(e -> {
                                        TableRow<RobotItem> currentRow = cell.getTableRow();
                                        currentRow.setStyle("-fx-background-color:darkgrey");
                                    });
                                    cellMenu.getItems().addAll(ghMenuItem, summaryMenuItem, markedFinishedMenuItem);
                                    cell.setContextMenu(cellMenu);
                                }
                            }
                    );
                    cell.textProperty().bind(cell.itemProperty());
                    return cell;
                });
    }


    private void clearFields() {
        this.termLabelValidator.clearFields();
        this.parentTermAdder.clearFields();
        this.definitionPane.clearFields();
        this.pmidXrefAdderBox.clearFields();
        this.addNewHpoTermBox.clearFields();
    }

    /**
     * This method uses to the data entered by the user to add another ROBOT item to the table
     */
    private void createNewRobotItem() {
        model.setHpoTermLabel(termLabelValidator.getLabel().get());
        model.setDefinition(this.definitionPane.getDefinition());
        model.setparentTerms(parentTermAdder.getParentTermList());
        model.setComment(this.definitionPane.getComment());
        model.setPmidList(pmidXrefAdderBox.getPmidList());
        model.setSynonymList(pmidXrefAdderBox.getSynonymList());
        Optional<String> opt = gitHubIssueBox.getGitHubIssueNumber();
        opt.ifPresent(model::setGitHubIssue);
        Optional<RobotItem> itemOpt = model.getRobotItemOpt();
        if (itemOpt.isPresent()) {
            model.reset();
            robotTableView.getItems().add(itemOpt.get());
        } else {
            PopUps.alertDialog("Error", "Could not create ROBOT Item");
        }


    }


    private void setUpTableView() {
        robotTableView.setPlaceholder(new Text("No ROBOT items in table"));
        robotTableView.setEditable(false);
        newTermLabelCol.setCellValueFactory(new PropertyValueFactory<>("newTermLabel"));
        newTermLabelCol.setCellFactory(TextFieldTableCell.forTableColumn());
        newTermLabelCol.setEditable(true);
        definitionCol.setCellValueFactory(new PropertyValueFactory<>("newTermDefinition"));
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
                            tooltip.setText(robotItem.getNewTermDefinitionProperty().get());
                            setTooltip(tooltip);
                            String displayText = item.length() < 50 ? item : item.substring(0,45) + "...";
                            setText(displayText);
                        }
                    }
                };
            }
        });
        parentTermCol.setCellValueFactory(new PropertyValueFactory<>("parentTermDisplay"));
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
                            List<Term> parents = robotItem.getParentTerms();
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
       pmidsCol.setCellValueFactory(new PropertyValueFactory<>("pmidString"));
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
                           List<String> pmids = robotItem.getPmids();
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
        this.robotTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN); // do not show "extra column"
        robotTableView.setOnMouseClicked(e -> {
            RobotItem item = robotTableView.getSelectionModel().getSelectedItems().get(0);
            showItemInTable(item);
        });
    }

    /**
     * This method is called if the user clicks on a row of the ROBOT item table, and causes details from
     * that row to be shown in the bottom part of the GUI in a WebView widget.
     * @param item The ROBOT item (table row) that the user has clicked on and thereby marked/brought into focus
     */
    private void showItemInTable(RobotItem item) {
        WebEngine engine = this.currentRobotView.getEngine();
        CurrentRobotItemVisualizer visualizer = new CurrentRobotItemVisualizer(viewFactory.getOptions());
        String html = visualizer.toHTML(item);
        engine.loadContent(html);
    }

    private void setupStatusBarOptions() {
        viewFactory.getOptions().isReadyProperty().addListener((obs, old, novel) -> {
            if (novel) {
                statusBarTextProperty.set("input data: ready");
                statusBarLabel.setTextFill(Color.BLACK);
                statusBarLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
                if (! ontologyLoadedProperty.get()) {
                    statusBarTextProperty.set("hp.json not loaded.");
                    statusBarLabel.setTextFill(Color.RED);
                    statusBarLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
                }
            } else {
                statusBarTextProperty.set(viewFactory.getOptions().getErrorMessage());
                statusBarLabel.setTextFill(Color.RED);
                statusBarLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
            }
        });
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
        optionsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.META_DOWN));
    }

    /**
     * This gets called as the "hook" for the OntologyTree widget
     * @param phenotypeTerm The term that is shown in the OntologyTree widget
     */
    private void addPhenotypeTerm(Term phenotypeTerm) {
        LOGGER.trace("Adding parent term from ontology tree: {}", phenotypeTerm);
        parentTermAdder.setParentTerm(phenotypeTerm.getName());
    }


    /**
     * Write the settings from the current session to file and exit.
     */
    @FXML
    private void exitGui() {
        javafx.application.Platform.exit();
    }


    private File robotTemplateFile = null;

    /**
     * Set up handlers for the three buttons on the new ROBOT item box
     */
    private void setupRobotItemHandlers() {
        EventHandler<ActionEvent> handler = actionEvent -> {
            createNewRobotItem();
            clearFields();
        };
        this.addNewHpoTermBox.setCreateNewRobotItemAction(handler);
        EventHandler<ActionEvent> clearHandler = actionEvent -> this.robotTableView.getItems().clear();
        this.addNewHpoTermBox.setClearRobotAction(clearHandler);
        EventHandler<ActionEvent> exportHandler = actionEvent -> {
            Optional<File> opt = this.model.getRobotSaveFileOpt();
            if (opt.isPresent()) {
                RobotItem.exportRobotItems(robotTableView.getItems(), opt.get());
            } else {
                PopUps.showInfoMessage("Error", "Could not set ROBOT export file");

            }
        };
        this.addNewHpoTermBox.setExportRobotAction(exportHandler);
        EventHandler<ActionEvent> clearRobotFileHandler = actionEvent -> {
            Optional<File> opt = model.getHpoSrcDir();
            if (opt.isPresent()) {
                File hpoSrcDir = opt.get();
                RobotRunner runner = new RobotRunner(hpoSrcDir);
                runner.clearRobotFile();

                //LOGGER.info(gobbledText);
            } else {
                PopUps.showInfoMessage("Error", "Could not set ROBOT export file");
                robotTemplateFile = null;
            }
        };
        this.addNewHpoTermBox.setClearRobotFileHandler(clearRobotFileHandler);
        EventHandler<ActionEvent> copyRobotCommandHandler = actionEvent -> {
            Optional<File> opt = model.getHpoSrcDir();
            if (opt.isPresent()) {
                File hpoSrcDir = opt.get();
                RobotRunner runner = new RobotRunner(hpoSrcDir);
                String command = runner.getCommandString();
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(command);
                clipboard.setContent(content);
                LOGGER.trace(command);
            } else {
                PopUps.showInfoMessage("Error", "Could not get ROBOT command");
                LOGGER.error("Could not get ROBOT command");
            }
        };
        this.addNewHpoTermBox.copyRobotCommandAction(copyRobotCommandHandler);
    }


}

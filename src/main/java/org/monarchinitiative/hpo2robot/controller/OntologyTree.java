package org.monarchinitiative.hpo2robot.controller;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.monarchinitiative.phenol.ontology.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This class acts as the controller of the Pane on the left side of the main HPO dialog. The top part of the pane
 * presents HPO hierarchy as a tree using {@link TreeView}. Details of any selected HPO term are presented at
 * the bottom of the Pane.
 * <p>
 * User can either browse the ontology tree by expanding individual tree elements or jump to any term using a search
 * text field with autocompletion capabilities.
 * <p>
 * The selected term (either present of not) is added to the table using <em>Add</em> button at the bottom of the
 * Pane.
 * <p>
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @author <a href="mailto:aaron.zhangl@jax.org">Aaron Zhang</a>
 * @version 0.1.0
 * @since 0.1
 */
public class OntologyTree extends VBox {

    private static final Logger LOGGER = LoggerFactory.getLogger(OntologyTree.class);

    /**
     * Ontology object containing {@link Term}s and their relationships.
     */
    private final ObjectProperty<MinimalOntology> ontology = new SimpleObjectProperty<>();


    /**
     * Approved {@link Term} is submitted here.
     */
    private final ObjectProperty<Consumer<Term>> addHook = new SimpleObjectProperty<>();

    /**
     * Map of term names to term IDs.
     */
    private final Map<String, TermId> labelToId = new HashMap<>();

    /**
     * Text field with autocompletion for jumping to a particular HPO term in the tree view.
     */
    @FXML
    private TextField searchTextField;

    /**
     * Clicking on this button will perform action described in {@link #goButtonAction()}.
     */
    @FXML
    private Button goButton;

    /**
     * Clicking on this button will perform action described in {@link #addButtonAction()}
     */
    @FXML
    private Button addButton;

    /**
     * Tree hierarchy of the ontology is presented here.
     */
    @FXML
    private TreeView<Term> ontologyTreeView;

    /**
     * WebView for displaying details of the Term that is selected in the {@link #ontologyTreeView}.
     */
    @FXML
    private WebView infoWebView;

    private AutoCompletionBinding<String> autoCompletionBinding;

    public OntologyTree() {
        FXMLLoader loader = new FXMLLoader(OntologyTree.class.getResource("OntologyTree.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectProperty<MinimalOntology> ontologyProperty() {
        return ontology;
    }

    public ObjectProperty<Consumer<Term>> addHookProperty() {
        return addHook;
    }

    /**
     * Expand & scroll to the term selected in the search text field.
     */
    @FXML
    private void goButtonAction() {
        TermId id = labelToId.get(searchTextField.getText());
        if (id != null) {
            // Note, this action handler can be run only if we have the ontology.
            // Otherwise, the entire pane is disabled/not clickable!
            // Hence, .get() is never null!
            ontology.get()
                    .termForTermId(id)
                    .ifPresent(t -> expandUntilTerm(t.id()));
            searchTextField.clear();
        }
    }

    @FXML
    public void searchTextFieldAction() {
        TermId id = labelToId.get(searchTextField.getText());
        if (id != null) {
            // Note, this action handler can be run only if we have the ontology.
            // Otherwise, the entire pane is disabled/not clickable!
            // Hence, .get() is never null!
            ontology.get()
                    .termForTermId(id)
                    .ifPresent(t -> expandUntilTerm(t.id()));
            searchTextField.clear();
        }
    }

    @FXML
    private void addButtonAction() {
        TreeItem<Term> selected = ontologyTreeView.getSelectionModel().getSelectedItem();
        Term phenotypeTerm = selected.getValue();
        addHook.get().accept(phenotypeTerm);
    }


    /**
     * Populate the {@link #ontologyTreeView} with the root {@link Term}s of provided {@link Ontology}. Initialize
     * other JavaFX elements.
     * <p>
     * {@inheritDoc}
     */
    @FXML
    private void initialize() {
        // The pane and all the controls are disabled unless HPO is available.
        disableProperty().bind(ontology.isNull());
        // We can click the Add button only if an item is selected
        addButton.disableProperty().bind(ontologyTreeView.getSelectionModel().selectedItemProperty().isNull()
                .or(addHook.isNull()));
        ontologyTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ontologyTreeView.setCellFactory(new Callback<>() {
            @Override
            public TreeCell<Term> call(TreeView<Term> param) {
                return new TreeCell<>() {
                    @Override
                    public void updateItem(Term term, boolean empty) {
                        super.updateItem(term, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(term.getName());
                        }
                    }
                };
            }
        });
        ontologyTreeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> updateDescription(newValue));
        ontology.addListener((obs, old, hpo) -> {
            if (hpo != null) {
                // HPO is available. Let's populate the tree view.
                // First, update the intro message.
                String introHtmlMessage = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>HPO tree browser</title></head>" +
                        "<body><p>Click on HPO term in the tree browser to display additional information</p></body></html>";
                infoWebView.getEngine().loadContent(introHtmlMessage);

                // Then, set the tree view root.
                Term rootTerm = hpo.termForTermId(hpo.getRootTermId())
                        .orElseThrow(() -> new RuntimeException("Ontology should never lack the root!"));
                TreeItem<Term> root = new TermTreeItem(rootTerm);
                ontologyTreeView.setRoot(root);

                // Last, fill the auto-completion labels
                hpo.getTerms().forEach(term -> labelToId.put(term.getName(), term.id()));

                autoCompletionBinding = TextFields.bindAutoCompletion(searchTextField, labelToId.keySet());
            } else {
                // HPO was unloaded
                String introHtmlMessage = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>HPO tree browser</title></head>" +
                        "<body><p>Ontology is not available so the functions are disabled.</p></body></html>";
                infoWebView.getEngine().loadContent(introHtmlMessage);
                autoCompletionBinding.dispose();
                autoCompletionBinding = null;
                labelToId.clear();
            }
        });
    }


    /**
     * Focus on the HPO term with given ID if the term is contained in the ontology.
     *
     * @param term {@link Term} on which we should focus
     */
    void focusOnTerm(Term term) {
        expandUntilTerm(term.id());
    }


    /**
     * Find the path from the root term to given {@link TermId}, expand the tree and set the selection model of the
     * TreeView to the term position.
     *
     * @param termId {@link TermId} to be displayed
     */
    private void expandUntilTerm(TermId termId) {
        MinimalOntology hpo = ontology.get();
        if (hpo.graph().existsPath(termId, hpo.getRootTermId())) {
            Stack<TermId> termStack = new Stack<>();
            termStack.add(termId);
            Optional<TermId> parentOpt = hpo.graph()
                    .getParentsStream(termId, false)
                    .findFirst();
            while (parentOpt.isPresent()) {
                parentOpt = hpo.graph()
                        .getParentsStream(parentOpt.get(), false)
                        .findFirst();
                parentOpt.ifPresent(termStack::add);
            }
            List<TreeItem<Term>> children = ontologyTreeView.getRoot().getChildren();
            termStack.pop(); // get rid of 'ALl'
            TreeItem<Term> target = ontologyTreeView.getRoot();
            while (! termStack.empty()) {
                TermId current = termStack.pop();
                for (TreeItem<Term> child: children) {
                    if (child.getValue().id().equals(current)) {
                        child.setExpanded(true);
                        target = child;
                        children = child.getChildren();
                        break;
                    }
                }
            }
            ontologyTreeView.requestFocus();
            ontologyTreeView.getSelectionModel().select(target);
            ontologyTreeView.scrollTo(ontologyTreeView.getSelectionModel().getSelectedIndex());
        } else {
            LOGGER.warn("Unable to find the path from {} to {}", hpo.getRootTermId(), termId);
        }
    }


    /**
     * Get currently selected Term. Used in tests.
     *
     * @return {@link TermTreeItem} that is currently selected
     */
    TreeItem<Term> getSelectedTerm() {
        return (ontologyTreeView.getSelectionModel().getSelectedItem() == null) ? null
                : ontologyTreeView.getSelectionModel().getSelectedItem();
    }


    /**
     * Update content of the {@link #infoWebView} with currently selected {@link Term}.
     *
     * @param treeItem currently selected {@link TreeItem} containing {@link Term}
     */
    private void updateDescription(TreeItem<Term> treeItem) {
        if (treeItem == null)
            return;
        Term term = treeItem.getValue();
        String HTML_TEMPLATE = "<!DOCTYPE html>" +
                "<html lang=\"en\"><head><meta charset=\"UTF-8\"><title>HPO tree browser</title></head>" +
                "<body>" +
                "<p><b>Term ID:</b> %s</p>" +
                "<p><b>Term Name:</b> %s</p>" +
                "<p><b>Synonyms:</b> %s</p>" +
                "<p><b>Definition:</b> %s</p>" +
                "</body></html>";

        String termID = term.id().getValue();
        String synonyms = (term.getSynonyms() == null) ? "" : term.getSynonyms().stream()
                .map(TermSynonym::getValue)
                .collect(Collectors.joining(", ")); // Synonyms

        String definition = (term.getDefinition() == null) ? "" : term.getDefinition();

        String content = String.format(HTML_TEMPLATE, termID, term.getName(), synonyms, definition);
        infoWebView.getEngine().loadContent(content);
    }

    /**
     * Inner class that defines a bridge between hierarchy of {@link Term}s and {@link TreeItem}s of the
     * {@link TreeView}.
     */
    class TermTreeItem extends TreeItem<Term> {

        /**
         * List used for caching of the children of this term
         */
        private ObservableList<TreeItem<Term>> childrenList;


        /**
         * Default & only constructor for the TreeItem.
         *
         * @param term {@link Term} that is represented by this TreeItem
         */
        TermTreeItem(Term term) {
            super(term);
        }


        /**
         * Check that the {@link Term} that is represented by this TreeItem is a leaf term as described below.
         * <p>
         * {@inheritDoc}
         */
        @Override
        public boolean isLeaf() {
            return ontology.get().graph().isLeaf(getValue().id());
        }


        /**
         * Get list of children of the {@link Term} that is represented by this TreeItem.
         * <p>
         * {@inheritDoc}
         */
        @Override
        public ObservableList<TreeItem<Term>> getChildren() {
            if (childrenList == null) {
                childrenList = FXCollections.observableArrayList();
                MinimalOntology hpo = ontology.get();
                Iterable<TermId> iterable = hpo.graph().getChildren(getValue().id(), false);
                Set<Term> children = new HashSet<>();
                for (TermId iter : iterable) {
                    Optional<Term> opt = hpo.termForTermId(iter);
                    opt.ifPresent(children::add);
                }

                children.stream()
                        .sorted(Comparator.comparing(Term::getName))
                        .map(OntologyTree.TermTreeItem::new)
                        .forEach(childrenList::add);
                super.getChildren().setAll(childrenList);
            }
            return super.getChildren();
        }

    }

}
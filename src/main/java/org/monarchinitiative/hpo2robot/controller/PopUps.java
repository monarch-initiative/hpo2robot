package org.monarchinitiative.hpo2robot.controller;

/*
 * #%L
 * PhenoteFX
 * %%
 * Copyright (C) 2017 Peter Robinson
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.*;
import org.monarchinitiative.hpo2robot.github.GitHubIssue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PopUps {
    private static final Logger LOGGER = LoggerFactory.getLogger(PopUps.class);

    /**
     * Show information to user.
     *
     * @param text        - message text
     * @param windowTitle - Title of PopUp window
     */
    public static void showInfoMessage(String text, String windowTitle) {
        Alert al = new Alert(AlertType.INFORMATION);
        DialogPane dialogPane = al.getDialogPane();
        dialogPane.getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
        ClassLoader classLoader = PopUps.class.getClassLoader();
        URL url = classLoader.getResource("css/popup.css");
        if (url != null) {
            dialogPane.getStylesheets().add(url.getFile());
            dialogPane.getStyleClass().add("dialog-pane");
        } else {
            LOGGER.error("Could not load popup.css");
        }
        al.setTitle(windowTitle);
        al.setHeaderText(null);
        al.setContentText(text);
        al.showAndWait();
    }

    public static void showErrorMessage(String text) {
        showInfoMessage(text, "Error");
    }

    /**
     * Ask user to provide path to a File
     *
     * @param ownerWindow      - Stage with which the FileChooser will be associated
     * @param initialDirectory - Where to start the search
     * @param title            - Title of PopUp window
     * @return file the user wants to open
     */
    public static File selectFileToOpen(Stage ownerWindow, File initialDirectory, String title) {
        final FileChooser filechooser = new FileChooser();
        filechooser.setInitialDirectory(initialDirectory);
        filechooser.setTitle(title);
        return filechooser.showOpenDialog(ownerWindow);
    }

    /**
     * Ask user to select path where he wants to save a File
     *
     * @param ownerWindow      Parent Stage object
     * @param initialDirectory Where to start the search
     * @param title            Title of PopUp window
     * @return file to be saved
     */
    public static File selectFileToSave(Stage ownerWindow, File initialDirectory, String title, String initialFileName) {
        final FileChooser filechooser = new FileChooser();
        filechooser.setInitialDirectory(initialDirectory);
        filechooser.setInitialFileName(initialFileName);
        filechooser.setTitle(title);
        return filechooser.showSaveDialog(ownerWindow);
    }

    /**
     * Ask user to choose a directory
     *
     * @param ownerWindow      - Stage with which the DirectoryChooser will be associated
     * @param initialDirectory - Where to start the search
     * @param title            - Title of PopUp window
     * @return selected directory
     */
    public static File selectDirectory(Stage ownerWindow, File initialDirectory, String title) {
        final DirectoryChooser dirchooser = new DirectoryChooser();
        dirchooser.setInitialDirectory(initialDirectory);
        dirchooser.setTitle(title);
        return dirchooser.showDialog(ownerWindow);
    }

    /**
     * Request a String from user.
     *
     * @param windowTitle - Title of PopUp window
     * @param promptText  - Prompt of Text field (suggestion for user)
     * @param labelText   - Text of your request
     * @return String with user input
     */
    public static String getStringFromUser(String windowTitle, String promptText, String labelText) {
        TextInputDialog dialog = new TextInputDialog(promptText);
        dialog.setTitle(windowTitle);
        dialog.setHeaderText(null);
        dialog.setContentText(labelText);
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);

    }



    /**
     * Present user a window with buttons
     *
     * @param choices Options to be chosen by user
     * @param labelText Question for options
     * @param windowTitle title
     * @return the user's choice of an option from choices (or null)
     */
    public static String getToggleChoiceFromUser(String[] choices, String labelText, String windowTitle) {
        Alert al = new Alert(AlertType.CONFIRMATION);

        al.setTitle(windowTitle);
        al.setHeaderText(null);
        al.setContentText(labelText);
        List<ButtonType> buttons = Arrays.stream(choices)
                .map(ButtonType::new).collect(Collectors.toList());

        buttons.add(new ButtonType("Cancel", ButtonData.CANCEL_CLOSE));

        al.getButtonTypes().setAll(buttons);

        Optional<ButtonType> result = al.showAndWait();
        if (result.isEmpty()) return null;
        if (result.get().getButtonData() == ButtonData.CANCEL_CLOSE)
            return null;

        return result.get().getText();
    }





    public static void showException(String windowTitle, String header, String contentText, Exception exception) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(windowTitle);
        alert.setHeaderText(header);
        alert.setContentText(contentText);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }


    private static Stage getPopUpStage(String title) {
        Stage window = new Stage();
        window.setResizable(false);
        window.centerOnScreen();
        window.setTitle(title);
        window.initStyle(StageStyle.UTILITY);
        window.initModality(Modality.APPLICATION_MODAL);
        return window;
    }


    /**
     * Ensure that popup Stage will be displayed on the same monitor as the parent Stage
     */
    private static Stage adjustStagePosition(Stage childStage, Stage parentStage) {
        ObservableList<Screen> screensForParentWindow = Screen.getScreensForRectangle(parentStage.getX(), parentStage.getY(),
                parentStage.getWidth(), parentStage.getHeight());
        Screen actual = screensForParentWindow.get(0);
        Rectangle2D bounds = actual.getVisualBounds();

        // set top left position to 35%/25% of screen/monitor width & height
        childStage.setX(bounds.getWidth() * 0.35);
        childStage.setY(bounds.getHeight() * 0.25);
        return childStage;
    }


    public static void alertDialog(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static String getGithubIssueHtml(GitHubIssue issue) {
        final String CSS = """
                p { font-size: 120%; color: darkgray; }
                p.important { border-style: solid; border-width: 0.5px; border-color: blue; }
                """;

        String title = String.format("%s (issue # %s)", issue.getTitle(), issue.getIssueNumber());
        final String HEADER = String.format("""
                <html lang="en">
                                
                <head>
                    <title>%s</title>
                                
                    <style>
                        p { color: dimgray; }
                        p.important { border-style: solid; border-width: 0.5px; border-color: blue; }
                    </style>
                                
                                
                </head>
                                
                <body>""", title);

        final String FOOTER = """
                </body>
                                
                </html>
                """;

        String [] segments = issue.getBody().split("\n");
        final Pattern pattern = Pattern.compile("\\*\\*(.*):?\\*\\*");
        List<String> htmlSegments = new ArrayList<>();
        for (var seg: segments) {
            Matcher matcher = pattern.matcher(seg);
            if (matcher.find()) {
                String hit = matcher.group(1);
                htmlSegments.add(String.format("<h2>%s</h2>", hit));
            } else {
                htmlSegments.add(String.format("<p>%s</p>", seg));
            }
        }
        String htmlBody = String.join("\n", htmlSegments);
        return HEADER +
                "<H1>HPO Issue #" + issue.getIssueNumber() + "</H1>\n" +
                "<p class=\"important\">" + htmlBody + "</p>" +
                FOOTER;
    }


    public static boolean nextGitHubIssue(GitHubIssue issue) {
        Stage toolStage = new Stage();
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();
        webEngine.loadContent(getGithubIssueHtml(issue));
        ScrollPane scrollPane = new ScrollPane();
        VBox vbox = new VBox();
        vbox.getChildren().add(new Label("Git Hub Issue"));
        scrollPane.setContent(browser);
        vbox.getChildren().add(scrollPane);
        Button OK = new Button("Close");
        OK.setOnAction(e -> toolStage.close());
        vbox.getChildren().add(OK);
        Scene toolScene = new Scene(vbox, 1000, 600);
        toolStage.setScene(toolScene);
        toolStage.setAlwaysOnTop(true);
        toolStage.show();
        return true;
    }


    public static Optional<File> selectRobotFileToSave(Window window) {
        final FileChooser filechooser = new FileChooser();
        filechooser.setInitialFileName("hpo2robot.tsv");
        filechooser.setTitle("Set path to save HPO2ROBOT file");
        File f = filechooser.showSaveDialog(window);
        return Optional.ofNullable(f);
    }
}
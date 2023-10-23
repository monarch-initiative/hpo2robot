package org.monarchinitiative;

import javafx.scene.control.TreeItem;
import org.monarchinitiative.controller.services.FetchFoldersService;
import org.monarchinitiative.model.EmailAccount;
import org.monarchinitiative.model.EmailTreeItem;
import org.monarchinitiative.model.Options;

public class Hpo2RobotManager {

    // handle folders
    private Options options;
    private EmailTreeItem<String> foldersRoot = new EmailTreeItem<String>("");

    public TreeItem<String> getFoldersRoot() {
        return foldersRoot;
    }


    public Hpo2RobotManager(Options options) {
        this.options = options;
    }

    public void addEmailAccount(EmailAccount account) {
        EmailTreeItem<String> treeItem = new EmailTreeItem<>(account.getAddress());
        FetchFoldersService service = new FetchFoldersService(account.getStore(), foldersRoot);
        service.start();

        foldersRoot.getChildren().add(treeItem);
        System.out.println("Added accounrt " + account.getAddress());
    }


    public Options getOptions() {
        return options;
    }
}

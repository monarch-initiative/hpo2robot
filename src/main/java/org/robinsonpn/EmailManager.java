package org.robinsonpn;

import javafx.scene.control.TreeItem;
import org.robinsonpn.controller.services.FetchFoldersService;
import org.robinsonpn.model.EmailAccount;
import org.robinsonpn.model.EmailTreeItem;

public class EmailManager {

    // handle folders

    private EmailTreeItem<String> foldersRoot = new EmailTreeItem<String>("");

    public TreeItem<String> getFoldersRoot() {
        return foldersRoot;
    }

    public void addEmailAccount(EmailAccount account) {
        EmailTreeItem<String> treeItem = new EmailTreeItem<>(account.getAddress());
        FetchFoldersService service = new FetchFoldersService(account.getStore(), foldersRoot);
        service.start();

        foldersRoot.getChildren().add(treeItem);
        System.out.println("Added accounrt " + account.getAddress());
    }



}

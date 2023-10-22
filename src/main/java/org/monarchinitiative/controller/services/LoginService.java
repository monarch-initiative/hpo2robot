package org.monarchinitiative.controller.services;

import javax.mail.*;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.monarchinitiative.EmailManager;
import org.monarchinitiative.controller.EmailLoginResult;
import org.monarchinitiative.model.EmailAccount;

public class LoginService extends Service<EmailLoginResult> {

    EmailAccount emailAccount;
    EmailManager emailManager;

    public LoginService(EmailAccount emailAccount, EmailManager emailManager) {
        this.emailAccount = emailAccount;
        this.emailManager = emailManager;
    }

    public EmailLoginResult login() {
        System.out.println("top of login");
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailAccount.getAddress(),emailAccount.getAddress());
            }
        };
        try {
            Session session = Session.getInstance(emailAccount.getProperties(), authenticator);
            Store store = session.getStore("imaps");
            store.connect(emailAccount.getProperties().getProperty("incomingHost"),
                    emailAccount.getAddress(),
                    emailAccount.getPassword());
            emailAccount.setStore(store);
            emailManager.addEmailAccount(emailAccount);
        } catch (NoSuchProviderException nspe) {
            nspe.printStackTrace();
            return EmailLoginResult.FAILED_BY_NETWORK;
        } catch (AuthenticationFailedException afe) {
            afe.printStackTrace();
            return EmailLoginResult.FAILED_BY_CREDENTIALS;
        } catch (MessagingException me) {
            me.printStackTrace();
            return EmailLoginResult.UNEXPECTED_ERROR;
        } catch (IllegalStateException ise) {
            ise.printStackTrace();
            return EmailLoginResult.UNEXPECTED_ERROR;
        }
        return EmailLoginResult.SUCCESS;

    }

    @Override
    protected Task<EmailLoginResult> createTask() {
        System.out.println("creating task");
        return new Task<EmailLoginResult>() {
            @Override
            protected EmailLoginResult call() throws Exception {
                return login();
            }
        };
    }
}

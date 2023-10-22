package org.robinsonpn.model;

import javax.mail.Store;

import java.util.Properties;

public class EmailAccount {

    private String address;
    private String password;

    private Properties properties;
    private Store store;

    public EmailAccount(String address, String password) {
        this.address = address;
        this.password = password;
        // JUST FOR TESTING
        EmailAccount mailSlurp = EmailAccount.mailslurp();
        this.address = mailSlurp.getAddress();
        this.password = mailslurp().getPassword();
        properties = new Properties();
        properties.put("incomingHost", "imap.gmail.com");
        properties.put("mail.store.protocol", "imaps");

        properties.put("mail.transport.protocol", "smtps");
        properties.put("mail.smtps.host", "smtp.gmail.com");
        properties.put("mail.smtps.auth", "true");
        properties.put("outgoingHost", "smtp.gmail.com");
    }

    public String getAddress() {
        return address;
    }

    public String getPassword() {
        return password;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    /**
     * create a temporary account at https://app.mailslurp.com
     * to test this
     * @return
     */
    public static EmailAccount mailslurp() {
        //57552a4b-996e-49a4-8cdc-2054d262fea9@mailslurp.com
        String username = "ZCO4ZTwEmeYYR0HQv3KmUMut0dK5c8BN";
        String password = "EGJ6aeCZQrruLYs8f7DLJdId67Inan8l";
        String email = "5bc81459-8d61-4119-9a96-a6f2dbdeed76@mailslurp.mx";
        return new EmailAccount(email, password);
    }


}

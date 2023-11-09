package org.monarchinitiative.hpo2robot.github;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import org.monarchinitiative.hpo2robot.controller.PopUps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GitHubUtil {


    public static String escape(String string) {
        if (string == null || string.isEmpty()) {
            return "\"\"";
        }

        char         c = 0;
        int          i;
        int          len = string.length();
        StringBuilder sb = new StringBuilder(len + 4);
        String       t;

        sb.append('"');
        for (i = 0; i < len; i += 1) {
            c = string.charAt(i);
            switch (c) {
                case '\\':
                case '"', '/':
                    sb.append('\\');
                    sb.append(c);
                    break;
                //                }
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                default:
                    if (c < ' ') {
                        t = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    /**
     * Extract the github token from the crednetials file
     * https://<username>:<token>@github.com
     * @return a list with the username and token
     */

    public static Optional<List<String>> githubCredentialToken() {
        String userHome = System.getProperty("user.home");
        File credFile = new File(userHome + File.separator + ".git-credentials");
        if (! credFile.isFile()) {
            PopUps.alertDialog("Error", "Cannot connect to git unless we find a .git-credentials file!");
            return Optional.empty();
        }
        try (BufferedReader br = new BufferedReader(new FileReader(credFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("https://") && line.contains(":") && line.contains("@github.com")) {
                    String [] fields = line.substring(8).split(":");
                    String username = fields[0];
                    String token = fields[1];
                    int i = token.indexOf("@github.com");
                    token = token.substring(0, i);
                    return Optional.of(List.of(username, token));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }



    public static void openInGithubAction(String issueNumber, HostServices services) {
        if (issueNumber == null) {
            PopUps.alertDialog("Could not open GitHub", "Issue was null");
            return;
        }
        final String urlPart = "https://github.com/obophenotype/human-phenotype-ontology/issues/";
        String url = urlPart + issueNumber;
        services.showDocument(url);
    }

}

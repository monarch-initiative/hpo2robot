package org.monarchinitiative.hpo2robot.github;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


import java.net.http.HttpClient;


/**
 * Note: Apparently there is no way to retrieve more than 30 labels at a time, and so we cannot reliably
 * retrieve all of the open issues for some label. This may be useful for labels with less than 30 issues
 * to create a word doc, but this will not yet be reliable in general. Keep this class at the experimental/
 * command-line only stage for now.
 */
public class GitHubIssueRetriever {
    private static final Logger logger = LoggerFactory.getLogger(GitHubIssueRetriever.class);

    private final List<GitHubIssue> issues = new ArrayList<>();

   // private HttpURLConnection httpconnection=null;

    private final String username;
    private final String token;




    public GitHubIssueRetriever() {
        Optional<List<String>> opt = GitHubUtil.githubCredentialToken();
        if (opt.isPresent()) {
            List<String> userInfoList = opt.get();
            username = userInfoList.get(0);
            token = userInfoList.get(1);
            System.out.println(username + " -- " + token);
        } else {
            username = null;
            token = null;
        }
        int responsecode = retrieveIssues();
        logger.error(String.format("We retrieved %d issues with response code %d", issues.size() ,responsecode));
    }


    public List<GitHubIssue> getIssues(){ return issues; }







    private void decodeJSON(String s) {
        Object obj= JSONValue.parse(s);
        JSONArray jsonArray = (JSONArray) obj;
        //noinspection unchecked
        jsonArray.forEach(this::parseLabelElement);
    }

    private void parseLabelElement(Object obj) {
        JSONObject jsonObject = (JSONObject) obj;
        String title = jsonObject.get("title").toString();
        String body = jsonObject.get("body").toString();
        String label = jsonObject.get("label")==null?"none":jsonObject.get("label").toString();
        String number = jsonObject.get("number")==null?"?":jsonObject.get("number").toString();
        GitHubIssue.Builder builder = new GitHubIssue.Builder(title).body(body).label(label).number(number);
        issues.add(builder.build());
    }


    private int retrieveIssues()  {
        try {
            final String hpo_github_url ="https://api.github.com/repos/obophenotype/human-phenotype-ontology/issues";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request;
            if (token != null) {
                 request = HttpRequest.newBuilder()
                        .version(HttpClient.Version.HTTP_2)
                        .uri(URI.create(hpo_github_url))
                        .header("Authorization", " " + token)
                        .GET()
                        .build();
            } else {
                request = HttpRequest.newBuilder()
                        .version(HttpClient.Version.HTTP_2)
                        .uri(URI.create(hpo_github_url))
                        .GET()
                        .build();
            }
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            //System.out.println();
            decodeJSON(responseBody);
            return response.statusCode();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return -1; // we should never get here with a good response.
    }



}

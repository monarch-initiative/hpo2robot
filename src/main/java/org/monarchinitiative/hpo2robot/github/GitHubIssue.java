package org.monarchinitiative.hpo2robot.github;

import java.util.ArrayList;
import java.util.List;

/**
 * This class encapsulates information about an issue and will be used to create a summary of the issue for
 * output on the command line or in a dialog.
 */
public class GitHubIssue implements Comparable<GitHubIssue> {
    private final String title;
    private final String body;
    private final String label;
    private final List<String> comments;
    private final String issueNumber;

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getLabel() {
        return label;
    }

    public List<String> getComments() {
        return comments;
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public boolean hasValidIssueNumber(){
        return this.issueNumber!=null && !this.issueNumber.startsWith("?");
    }

    GitHubIssue(String title, String body, String label, List<String> comments, String number){
        this.title=title;
        this.body=body;
        this.label=label;
        this.comments=comments;
        issueNumber=number;

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + title.hashCode();
        hash = 31 * hash + body.hashCode();
        hash = 31 * hash + label.hashCode();
        hash = 31 * hash + comments.hashCode();
        hash = 31 * hash + issueNumber.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof GitHubIssue other)) return false;
        return other.title.equals(title) &&
                other.body.equals(body) &&
                other.label.equals(label) &&
                other.comments.equals(comments) &&
                other.issueNumber.equals(issueNumber);
    }


    public String toString() {
        return String.format("%s: %s [%s] %s",title,body,label, String.join(";", comments));
    }

    @Override
    public int compareTo(GitHubIssue o) {
        return  issueNumber.compareTo(o.issueNumber);
    }


    public static class Builder {
        private final String title;
        private String body="";
        private String label="none";
        private String issueNumber="";
        List<String> comments = new ArrayList<>();

        public Builder(String title){
            this.title=title;
        }

        public Builder body(String b) {
            body=b;
            return this;
        }

        public Builder label(String l) {
            label=l;
            return this;
        }

        public Builder comment(String c) {
            comments.add(c);
            return this;
        }

        public Builder comments(List<String> c) {
            comments.addAll(c);
            return this;
        }

        public Builder number(String number) {
            issueNumber=number;
            return this;
        }

        public GitHubIssue build() {
            return new GitHubIssue(title,body,label,comments,issueNumber);
        }

    }



}

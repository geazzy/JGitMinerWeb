/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.JGitMinerWeb.services.matriz.auxiliary;

import br.edu.utfpr.cm.JGitMinerWeb.model.miner.EntityComment;
import com.google.common.base.Strings;
import java.util.List;

/**
 *
 * @author geazzy
 */
public class AuxWordiness {

    private Integer issueNumber;
    private String url;
    private Integer wordiness;
    private String issueBody;
    private List<EntityComment> comments;

    public AuxWordiness(Integer issueNumber, String url, String issueBody) {
        this.issueNumber = issueNumber;
        this.url = url;
        this.issueBody = issueBody;
        this.wordiness = new Integer(0);
    }

    public AuxWordiness(Integer issueNumber, String url, Integer wordiness) {
        this.issueNumber = issueNumber;
        this.url = url;
        this.wordiness = wordiness;
    }

    public Integer getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(Integer issueNumber) {
        this.issueNumber = issueNumber;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getWordiness() {
        return wordiness;
    }

    public String getIssueBody() {
        return issueBody;
    }

    public void setIssueBody(String issueBody) {
        this.issueBody = issueBody;
    }

    public List<EntityComment> getComments() {
        return comments;
    }

    public void setComments(List<EntityComment> comments) {
        this.comments = comments;
    }

    public void setWordiness() {

        this.wordiness += LuceneUtil.tokenizeString(Strings.nullToEmpty(getIssueBody())).size();

        for (EntityComment entityComment : comments) {

            this.wordiness += LuceneUtil.tokenizeString(Strings.nullToEmpty(entityComment.getBody())).size();

        }

    }

    @Override
    public String toString() {
        return issueNumber + ";" + wordiness + ";" + url;
    }

}

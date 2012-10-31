package ru.megaplan.jira.plugin.commentarius.rest;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 5/26/12
 * Time: 1:28 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonIgnoreProperties()
public class UserTemplateUpdateBean {
    @JsonProperty
    private String username;
    @JsonProperty
    private int number;
    @JsonProperty
    private String label;
    @JsonProperty
    private String header;
    @JsonProperty
    private String body;
    @JsonProperty
    private String footer;
    public String username() {
        return username;
    }
    public int number() {
        return number;
    }
    public String label() {
        return label;
    }
    public String header() {
        return header;
    }
    public String body() {
        return body;
    }
    public String footer() {
        return footer;
    }
}

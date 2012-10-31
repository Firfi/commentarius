package ru.megaplan.jira.plugin.commentarius.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 5/26/12
 * Time: 1:14 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "userTemplateMessage")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserTemplateMessage {
    private String username;
    private int number;
    private String label;
    private String header;
    private String body;
    private String footer;


    public UserTemplateMessage(String username, int number, String label, String header, String body, String footer) {
        this.username = username;
        this.number = number;
        this.label = label;
        this.header = header;
        this.body = body;
        this.footer = footer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }
}

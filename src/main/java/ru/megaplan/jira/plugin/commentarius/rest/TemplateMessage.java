package ru.megaplan.jira.plugin.commentarius.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 5/26/12
 * Time: 1:10 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "templateMessage")
@XmlAccessorType(XmlAccessType.FIELD)
public class TemplateMessage {
    private String type;
    private String small;
    private String full;

    public TemplateMessage(String type, String small, String full) {
        this.type = type;
        this.small = small;
        this.full = full;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getFull() {
        return full;
    }

    public void setFull(String full) {
        this.full = full;
    }
}

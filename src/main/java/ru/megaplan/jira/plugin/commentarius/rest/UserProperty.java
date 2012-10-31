package ru.megaplan.jira.plugin.commentarius.rest;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "property")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserProperty {
    @XmlAttribute
    private String userKey;

    @XmlAttribute
    private String propertyKey;

    @XmlAttribute
    private String oldProperty;

    @XmlAttribute
    private String newProperty;

    public UserProperty() {

    }

    public UserProperty(String userKey, String propertyKey, String oldProperty, String newProperty) {
        this.userKey = userKey;
        this.propertyKey = propertyKey;
        this.oldProperty = oldProperty;
        this.newProperty = newProperty;
    }

    public String getNewProperty() {
        return newProperty;
    }

    public void setNewProperty(String newProperty) {
        this.newProperty = newProperty;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getOldProperty() {
        return oldProperty;
    }

    public void setOldProperty(String oldProperty) {
        this.oldProperty = oldProperty;
    }

}

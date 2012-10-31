package ru.megaplan.jira.plugin.commentarius.rest;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.io.SegmentedStringWriter;

/**
 * Created with IntelliJ IDEA.
 * User: i.loskutov
 * Date: 12.05.12
 * Time: 11:11
 * To change this template use File | Settings | File Templates.
 */
@JsonIgnoreProperties()
public class PropertyUpdateBean {

    @JsonProperty
    private int ID;

    @JsonProperty
    private String roleName;

    @JsonProperty
    private String key;

    @JsonProperty
    private String oldValue;

    @JsonProperty
    private String newValue;

    public String key() {
        return key;
    }

    public String oldValue() {
        return oldValue;
    }

    public String newValue() {
        return newValue;
    }

    public String roleName() {
        return roleName;
    }

    public int ID() {
        return ID;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
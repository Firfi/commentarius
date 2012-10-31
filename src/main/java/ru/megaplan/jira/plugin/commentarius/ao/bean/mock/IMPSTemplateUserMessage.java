package ru.megaplan.jira.plugin.commentarius.ao.bean.mock;

import net.java.ao.schema.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 6/3/12
 * Time: 8:05 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IMPSTemplateUserMessage {
    String getHeader();
    void setHeader(String message);

    String getBody();
    void setBody(String message);

    String getFooter();
    void setFooter(String message);

    String getUserName();
    void setUserName(String username);

    int getNm();
    void setNm(int nm);

    String getLabel();
    void setLabel(String label);
}

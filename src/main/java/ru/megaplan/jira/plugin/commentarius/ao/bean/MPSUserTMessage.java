package ru.megaplan.jira.plugin.commentarius.ao.bean;

import net.java.ao.Entity;
import net.java.ao.OneToMany;
import net.java.ao.Preload;
import net.java.ao.schema.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 5/26/12
 * Time: 12:50 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MPSUserTMessage extends Entity {

    String getHeader();
    void setHeader(String message);

    String getBody();
    void setBody(String message);

    String getFooter();
    void setFooter(String message);

    @NotNull
    String getUserName();
    void setUserName(String username);
    @NotNull
    int getNm();
    void setNm(int nm);
    @NotNull
    String getLabel();
    void setLabel(String label);
}

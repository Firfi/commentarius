package ru.megaplan.jira.plugin.commentarius.ao.bean;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;

/**
 * Created with IntelliJ IDEA.
 * User: i.loskutov
 * Date: 25.05.12
 * Time: 16:49
 * To change this template use File | Settings | File Templates.
 */
@Preload
public interface MPSTemplateMessage extends Entity {

    static final int smallLength = 255;
    static final int fullLength = StringLength.UNLIMITED;

    @NotNull
    @StringLength(value=smallLength)
    String getSmall();
    void setSmall(String small);

    @NotNull
    @StringLength(value=fullLength)
    String getFull();
    void setFull(String full);

    @NotNull
    String getType();
    void setType(String type);

    int getPermissionBean();
    void setPermissionBean(int permissionBean);

    String getCreator();
    void setCreator(String creator);

    String getRoles();
    void setRoles(String roles);


}

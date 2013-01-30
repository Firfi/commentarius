package ru.megaplan.jira.plugin.commentarius.ao.bean.mock;

import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.IPermissionMock;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 6/3/12
 * Time: 10:53 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IMPSTemplateMessageMock {

    String getType();

    void setType(String type);

    String getSmall();

    void setSmall(String small);

    String getFull();

    void setFull(String full);

    int getID();

    void setID(int ID);

    String getCreator();

    void setCreator(String creator);

    String getRoles();

    void setRoles(String rolesIDs);

}

package ru.megaplan.jira.plugin.commentarius.ao.bean.mock;

import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.IPermissionMock;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.PermissionMock;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 6/3/12
 * Time: 11:05 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IGradeMock{
    String getGradeName();

    void setGradeName(String gradeName);

    IPermissionMock getPermissionMock();

    void setPermissionMock(IPermissionMock permissionMock);

    int getID();

    void setID(int ID);
}

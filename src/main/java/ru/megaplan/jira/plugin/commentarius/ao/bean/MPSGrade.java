package ru.megaplan.jira.plugin.commentarius.ao.bean;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Unique;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.PermissionBean;


/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 5/20/12
 * Time: 10:12 AM
 * To change this template use File | Settings | File Templates.
 */
@Preload
public interface MPSGrade extends Entity {
    @NotNull
    String getGrade();
    void setGrade(String grade);
    @NotNull
    int getPermissionBean();
    void setPermissionBean(int permissionBean);
}

package ru.megaplan.jira.plugin.commentarius.ao.bean;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Unique;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 5/20/12
 * Time: 3:57 PM
 * To change this template use File | Settings | File Templates.
 */
@Preload
public interface CommentariusProject extends Entity {
    @NotNull
    @Unique
    String getProjectKey();
    void setProjectKey(String projectKey);
}

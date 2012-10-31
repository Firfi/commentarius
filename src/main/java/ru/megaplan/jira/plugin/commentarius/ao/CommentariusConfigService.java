package ru.megaplan.jira.plugin.commentarius.ao;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.jira.project.Project;
import ru.megaplan.jira.plugin.commentarius.ao.bean.CommentariusProject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 5/20/12
 * Time: 3:49 PM
 * To change this template use File | Settings | File Templates.
 */
@Transactional
public interface CommentariusConfigService {
    List<Project> getAllowedProjects();
    void setAllowedProjects(List<Project> lp);
}

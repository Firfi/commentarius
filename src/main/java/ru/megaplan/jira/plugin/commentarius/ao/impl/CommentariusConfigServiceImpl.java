package ru.megaplan.jira.plugin.commentarius.ao.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import net.java.ao.DBParam;
import org.apache.log4j.Logger;
import ru.megaplan.jira.plugin.commentarius.ao.CommentariusConfigService;
import ru.megaplan.jira.plugin.commentarius.ao.bean.CommentariusProject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 5/20/12
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommentariusConfigServiceImpl implements CommentariusConfigService {

    private final static Logger log = Logger.getLogger(CommentariusConfigServiceImpl.class);
    private final ProjectManager projectManager;
    private final ActiveObjects ao;

    public CommentariusConfigServiceImpl(ActiveObjects ao, ProjectManager projectManager)
    {
        this.ao = checkNotNull(ao);
        this.projectManager = projectManager;
    }

    @Override
    public List<Project> getAllowedProjects() {
        List<Project> result = new ArrayList<Project>();
        CommentariusProject[] mpspa = ao.find(CommentariusProject.class);
        for (CommentariusProject cp : mpspa) {
            Project p = projectManager.getProjectObjByKey(cp.getProjectKey());
            result.add(p);
        }
        return result;
    }

    @Override
    public void setAllowedProjects(List<Project> lp) {
        CommentariusProject[] mpspa = ao.find(CommentariusProject.class);
        log.warn(mpspa.length);
        for (CommentariusProject cp : mpspa) {
            log.debug("deleting : " + cp.getProjectKey());
            ao.delete(cp);
        }
        for (Project p : lp) {
            log.debug("adding : " + p.getKey());
            DBParam cpParam = new DBParam("PROJECT_KEY", p.getKey());
            ao.create(CommentariusProject.class, cpParam);
        }
    }

}

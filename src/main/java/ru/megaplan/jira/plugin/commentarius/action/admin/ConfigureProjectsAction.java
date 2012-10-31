package ru.megaplan.jira.plugin.commentarius.action.admin;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import org.apache.log4j.Logger;
import ru.megaplan.jira.plugin.commentarius.ao.CommentariusConfigService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 5/20/12
 * Time: 5:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigureProjectsAction extends JiraWebActionSupport {

    private final static Logger log = Logger.getLogger(ConfigureProjectsAction.class);

    private final CommentariusConfigService commentariusConfigService;
    private final ProjectManager projectManager;

    private String[] projects;
    private String[] selectedProjects;
    private boolean submitted = false;

    ConfigureProjectsAction(CommentariusConfigService commentariusConfigService,
                            ProjectManager projectManager) {
        this.commentariusConfigService = commentariusConfigService;
        this.projectManager = projectManager;
    }
    public boolean hasPermissions()
    {
        return isHasPermission(Permissions.ADMINISTER);
    }
    @Override
    public String doExecute() {
        if (!hasPermissions()) return PERMISSION_VIOLATION_RESULT;

        if (submitted) {
            if (selectedProjects == null) {
                log.debug("selected projects is null");
                selectedProjects = new String[0];
            }
            List<Project> sp = new ArrayList<Project>();
            for (String projectkey : selectedProjects) {
                sp.add(projectManager.getProjectObjByKey(projectkey));
            }
            commentariusConfigService.setAllowedProjects(sp);
        } else {
            projects = projectListToKeyArray(projectManager.getProjectObjects());
            selectedProjects = projectListToKeyArray(commentariusConfigService.getAllowedProjects());
            return SUCCESS;
        }
        return getRedirect("CommentariusConfigureProjectsAction.jspa"); // drop session state;
    }

    private static String[] projectListToKeyArray(List<Project> lp) {
        List<String> pkl = new ArrayList<String>();
        for (Project p : lp) {
            pkl.add(p.getKey());
        }
        return pkl.toArray(new String[pkl.size()]);
    }

    public String[] getProjects() {
        return projects;
    }

    public void setProjects(String[] projects) {
        this.projects = projects;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public String[] getSelectedProjects() {
        return selectedProjects;
    }

    public void setSelectedProjects(String[] selectedProjects) {
        this.selectedProjects = selectedProjects;
    }

    public boolean isProjectSelected(String projectKey) {
        for (String pk : selectedProjects) {
            if (pk.equals(projectKey)) return true;
        }
        return false;
    }

}

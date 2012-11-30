package ru.megaplan.jira.plugin.commentarius.action.admin;

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import ru.megaplan.jira.plugin.commentarius.ao.ShabloniusConfigService;
import ru.megaplan.jira.plugin.commentarius.ao.bean.mock.IMPSTemplateMessageMock;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: i.loskutov
 * Date: 30.05.12
 * Time: 11:59
 * To change this template use File | Settings | File Templates.
 */
public class AddTemplateAction extends JiraWebActionSupport {

    private final ShabloniusConfigService shabloniusConfigService;
    private final ProjectRoleManager projectRoleManager;
    private final JiraAuthenticationContext jiraAuthenticationContext;
    public AddTemplateAction(ShabloniusConfigService shabloniusConfigService,
                             ProjectRoleManager projectRoleManager, JiraAuthenticationContext jiraAuthenticationContext) {
        this.shabloniusConfigService = shabloniusConfigService;
        this.projectRoleManager = projectRoleManager;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
    }

    ProjectRole[] projectRoles;
    String[] templateTypes;
    Long selectedProjectRoleId;
    String selectedTemplateType;
    String small;
    String full;

    boolean save;

    int id;

    @Override
    public String doDefault() throws Exception
    {
        init();
        return INPUT;
    }

    private void init() {
        Collection<ProjectRole> proles = projectRoleManager.getProjectRoles();
        projectRoles = proles.toArray(new ProjectRole[proles.size()]);
        templateTypes = new String[] {/*"header",*/"body"/*, "footer"*/};
        log.warn(id);
        if (id != 0) {
            IMPSTemplateMessageMock message = shabloniusConfigService.getTemplateMessage(id);
            full = message.getFull();
            small = message.getSmall();
            selectedTemplateType = message.getType();
            selectedProjectRoleId = message.getRole();
        }
    }

    @Override
    public String doExecute() {
        if (id == 0) {
            IMPSTemplateMessageMock message = shabloniusConfigService.getNewMessageMock(selectedTemplateType, small, full, selectedProjectRoleId);
            shabloniusConfigService.addTemplateMessage(message);
        } else {
            IMPSTemplateMessageMock message = shabloniusConfigService.getTemplateMessage(id);
            message.setCreator(jiraAuthenticationContext.getLoggedInUser().getName());
            message.setFull(full);
            message.setRole(selectedProjectRoleId);
            message.setSmall(small);
            shabloniusConfigService.updateTemplateMessage(message);
        }

        return getRedirect("CommentariusConfigureTemplatesAction.jspa"); // go to previous page
    }

    public ProjectRole[] getProjectRoles() {
        return projectRoles;
    }

    public void setProjectRoles(ProjectRole[] projectRoles) {
        this.projectRoles = projectRoles;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getFull() {
        return full;
    }

    public void setFull(String full) {
        this.full = full;
    }

    public Long getSelectedProjectRoleId() {
        return selectedProjectRoleId;
    }

    public void setSelectedProjectRoleId(Long selectedProjectRoleId) {
        this.selectedProjectRoleId = selectedProjectRoleId;
    }

    public String[] getTemplateTypes() {
        return templateTypes;
    }

    public void setTemplateTypes(String[] templateTypes) {
        this.templateTypes = templateTypes;
    }

    public String getSelectedTemplateType() {
        return selectedTemplateType;
    }

    public void setSelectedTemplateType(String selectedTemplateType) {
        this.selectedTemplateType = selectedTemplateType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
    }
}

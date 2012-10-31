package ru.megaplan.jira.plugin.commentarius.action.admin;

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
    public AddTemplateAction(ShabloniusConfigService shabloniusConfigService,
                             ProjectRoleManager projectRoleManager) {
        this.shabloniusConfigService = shabloniusConfigService;
        this.projectRoleManager = projectRoleManager;
    }

    ProjectRole[] projectRoles;
    String[] templateTypes;
    String selectedProjectRole;
    String selectedTemplateType;
    String small;
    String full;

    @Override
    public String doDefault() throws Exception
    {
        init();

        return INPUT;
    }

    private void init() {
        Collection<ProjectRole> proles = projectRoleManager.getProjectRoles();
        projectRoles = proles.toArray(new ProjectRole[proles.size()]);
        templateTypes = new String[] {"header","body", "footer"};
    }

    @Override
    public String doExecute() {
        IMPSTemplateMessageMock message = shabloniusConfigService.getNewMessageMock(selectedTemplateType,small,full);
        message.getPermissionMock().setProjectRoleName(selectedProjectRole);
        shabloniusConfigService.addTemplateMessage(message);
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

    public String getSelectedProjectRole() {
        return selectedProjectRole;
    }

    public void setSelectedProjectRole(String selectedProjectRole) {
        this.selectedProjectRole = selectedProjectRole;
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
}

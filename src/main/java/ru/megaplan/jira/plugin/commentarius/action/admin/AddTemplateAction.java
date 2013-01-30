package ru.megaplan.jira.plugin.commentarius.action.admin;

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.webresource.WebResourceManager;
import ru.megaplan.jira.plugin.commentarius.ao.ShabloniusConfigService;
import ru.megaplan.jira.plugin.commentarius.ao.bean.MPSTemplateMessage;
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

    private final WebResourceManager webResourceManager;
    private final ShabloniusConfigService shabloniusConfigService;
    private final ProjectRoleManager projectRoleManager;
    private final JiraAuthenticationContext jiraAuthenticationContext;
    public AddTemplateAction(WebResourceManager webResourceManager, ShabloniusConfigService shabloniusConfigService,
                             ProjectRoleManager projectRoleManager, JiraAuthenticationContext jiraAuthenticationContext) {
        this.webResourceManager = webResourceManager;
        this.shabloniusConfigService = shabloniusConfigService;
        this.projectRoleManager = projectRoleManager;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
    }

    ProjectRole[] projectRoles;
    String[] templateTypes;
    Long[] selectedProjectRoleId;
    Set<Long> selectedProjectRoleIdsSet;
    String selectedTemplateType;
    String small;
    String full;

    int maxSmall = MPSTemplateMessage.smallLength;
    int maxFull = MPSTemplateMessage.fullLength;

    boolean save;

    int id;

    @Override
    public String doDefault() throws Exception
    {
        init();
        return INPUT;
    }

    private void init() {
        webResourceManager.requireResource("ru.megaplan.jira.plugin.commentarius:validation");
        webResourceManager.requireResource("ru.megaplan.jira.plugin.commentarius:validationcss");
        Collection<ProjectRole> proles = projectRoleManager.getProjectRoles();
        projectRoles = proles.toArray(new ProjectRole[proles.size()]);
        templateTypes = new String[] {/*"header",*/"body"/*, "footer"*/};
        selectedProjectRoleIdsSet = new HashSet<Long>();
        if (id != 0) {
            IMPSTemplateMessageMock message = shabloniusConfigService.getTemplateMessage(id);
            full = message.getFull();
            small = message.getSmall();
            selectedTemplateType = message.getType();
            selectedProjectRoleId = stringToLongArray(message.getRoles());
            selectedProjectRoleIdsSet = new HashSet<Long>(Arrays.asList(selectedProjectRoleId));
        }
        if (selectedProjectRoleIdsSet.isEmpty()) selectedProjectRoleIdsSet.add(10000L);
    }

    @Override
    public String doExecute() {
        String idsString = arrayToString(selectedProjectRoleId);
        if (id == 0) {
            IMPSTemplateMessageMock message = shabloniusConfigService.getNewMessageMock(selectedTemplateType, small, full, idsString);
            shabloniusConfigService.addTemplateMessage(message);
        } else {
            IMPSTemplateMessageMock message = shabloniusConfigService.getTemplateMessage(id);
            message.setCreator(jiraAuthenticationContext.getLoggedInUser().getName());
            message.setFull(full);
            message.setRoles(idsString);
            message.setSmall(small);
            shabloniusConfigService.updateTemplateMessage(message);
        }
        return getRedirect("CommentariusConfigureTemplatesAction.jspa"); // go to previous page
    }

    private static String arrayToString(Object[] arr) {
        if (arr == null || arr.length == 0) return "null";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; ++i) {
            sb.append(arr[i]);
            if (i != arr.length - 1) {
                sb.append(":");
            }
        }
        return sb.toString();
    }

    public static Long[] stringToLongArray(String str) {
        if (str == null) return new Long[0];
        String[] strs = str.split(":");
        Long[] res = new Long[strs.length];
        for (int i = 0; i < strs.length; ++i) {
            res[i] = Long.parseLong(strs[i]);
        }
        return res;
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

    public Long[] getSelectedProjectRoleId() {
        return selectedProjectRoleId;
    }

    public void setSelectedProjectRoleId(Long[] selectedProjectRoleId) {
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

    public int getMaxSmall() {
        return getLengthInString(maxSmall);
    }


    public int getMaxFull() {
        return getLengthInString(maxFull);
    }

    private static int getLengthInString(int length) {
        if (length == -1) {
            return 100500;
        } else return length;
    }

    public Set<Long> getSelectedProjectRoleIdsSet() {
        return selectedProjectRoleIdsSet;
    }
}

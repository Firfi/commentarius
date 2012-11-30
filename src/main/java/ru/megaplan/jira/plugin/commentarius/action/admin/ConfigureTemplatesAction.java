package ru.megaplan.jira.plugin.commentarius.action.admin;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import ru.megaplan.jira.plugin.commentarius.ao.CommentariusConfigService;
import ru.megaplan.jira.plugin.commentarius.ao.ShabloniusConfigService;
import ru.megaplan.jira.plugin.commentarius.ao.bean.MPSTemplateMessage;
import ru.megaplan.jira.plugin.commentarius.ao.bean.mock.IMPSTemplateMessageMock;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: i.loskutov
 * Date: 25.05.12
 * Time: 15:54
 * To change this template use File | Settings | File Templates.
 */
public class ConfigureTemplatesAction extends JiraWebActionSupport {

    private final ShabloniusConfigService shabloniusConfigService;
    private final CommentariusConfigService commentariusConfigService;
    public final ProjectRoleManager projectRoleManager;


    private static final String SEPARATOR = "\n\n";

    private IMPSTemplateMessageMock[] allTemplateMessages;

    private String[] headers;
    private String[] bodies;
    private String[] footers;

    private List<IMPSTemplateMessageMock> headerObjects;
    private List<IMPSTemplateMessageMock> bodyObjects;
    private List<IMPSTemplateMessageMock> footerObjects;

    private String type;
    private String small;
    private String full;

    private String creator;

    private Long role;

    private boolean submitted;
    private boolean delete;
    private boolean add;

    ConfigureTemplatesAction(
            ShabloniusConfigService shabloniusConfigService, CommentariusConfigService commentariusConfigService, ProjectRoleManager projectRoleManager) {
        this.commentariusConfigService = commentariusConfigService;
        this.projectRoleManager = projectRoleManager;
        log.debug("initializing ConfigureTemplatesAction...");
        this.shabloniusConfigService = shabloniusConfigService;
    }

    public boolean hasPermissions()
    {
        for (Project p : commentariusConfigService.getAllowedProjects()) {
            if (isHasProjectPermission(Permissions.PROJECT_ADMIN, p.getGenericValue())) return true;
        }
        return isHasPermission(Permissions.ADMINISTER);
    }

    @Override
    public String doDefault() throws Exception
    {
        return doExecute();
    }



    @Override
    protected String doExecute() throws Exception
    {



        if (!hasPermissions())
        {
            return PERMISSION_VIOLATION_RESULT;
        }
        List<IMPSTemplateMessageMock> msgs = shabloniusConfigService.getAllTemplateMessages();
        allTemplateMessages = msgs.toArray(new IMPSTemplateMessageMock[msgs.size()]);
        headerObjects = shabloniusConfigService.getTemplateMessages("header");
        bodyObjects = shabloniusConfigService.getTemplateMessages("body");
        footerObjects = shabloniusConfigService.getTemplateMessages("footer");

        if (!isSubmitted()) {
            headers = toStringArray(headerObjects);
            bodies = toStringArray(bodyObjects);
            footers = toStringArray(footerObjects);
            return SUCCESS;
        }

        if (isAdd()) {
            checkNotNull(type);
            checkNotNull(small);
            IMPSTemplateMessageMock message = shabloniusConfigService.getNewMessageMock(type,small,full,role);
            shabloniusConfigService.addTemplateMessage(message);
            return getRedirect("CommentariusConfigureTemplatesAction.jspa");
        }

        if (isDelete()) {
            checkNotNull(type);
            String[] cbx = request.getParameterValues(type + "Cbx");
            List<IMPSTemplateMessageMock> messagesToDelete = new ArrayList<IMPSTemplateMessageMock>();
            for (String aCbx : cbx) {
                int num = Integer.parseInt(aCbx);
                if (type.equals("header"))
                    messagesToDelete.add(headerObjects.get(num));
                else if (type.equals("body"))
                    messagesToDelete.add(bodyObjects.get(num));
                else if (type.equals("footer"))
                    messagesToDelete.add(footerObjects.get(num));
            }
            for (IMPSTemplateMessageMock mg : messagesToDelete) {
                shabloniusConfigService.deleteTemplateMessage(mg);
            }
        }


        return getRedirect("CommentariusConfigureTemplatesAction.jspa"); // drop session state
    }


    private String[] toStringArray(List<IMPSTemplateMessageMock> messages) {
        String[] result = new String[messages.size()];
        for (int i = 0; i < messages.size(); ++i) {
            IMPSTemplateMessageMock message = messages.get(i);

            String permname = null;
            if (message.getRole() == null) {
                permname = "null";
            } else {
                ProjectRole role = projectRoleManager.getProjectRole(message.getRole());
                if (role == null) {
                    permname = "deleted role";
                } else {
                    permname = role.getName();
                }
            }
            result[i] = permname + SEPARATOR + message.getSmall() + SEPARATOR + (message.getFull()==null?"":message.getFull());
        }
        return result;
    }


    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public String[] getBodies() {
        return bodies;
    }

    public void setBodies(String[] bodies) {
        this.bodies = bodies;
    }

    public String[] getFooters() {
        return footers;
    }

    public void setFooters(String[] footers) {
        this.footers = footers;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public IMPSTemplateMessageMock[] getAllTemplateMessages() {
        return allTemplateMessages;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Long getRole() {
        return role;
    }

    public void setRole(Long role) {
        this.role = role;
    }

    public ProjectRoleManager getProjectRoleManager() {
        return projectRoleManager;
    }
}


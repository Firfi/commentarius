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

    private String roles;

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
       // List<IMPSTemplateMessageMock> msgs = shabloniusConfigService.getAllTemplateMessages();


        //headerObjects = shabloniusConfigService.getTemplateMessages("header");
        bodyObjects = shabloniusConfigService.getTemplateMessages("body");
        allTemplateMessages = bodyObjects.toArray(new IMPSTemplateMessageMock[bodyObjects.size()]);
        for (IMPSTemplateMessageMock m : allTemplateMessages) {
            m.setFull(m.getFull().replace("\n", "<br/>"));
            m.setRoles(toPrettyRoles(m.getRoles()));
        }
        //footerObjects = shabloniusConfigService.getTemplateMessages("footer");

        if (!isSubmitted()) {
            //headers = toStringArray(headerObjects);
            //bodies = toStringArray(bodyObjects);
            //footers = toStringArray(footerObjects);
            return SUCCESS;
        }

        if (isAdd()) {
            checkNotNull(type);
            checkNotNull(small);
            IMPSTemplateMessageMock message = shabloniusConfigService.getNewMessageMock(type,small,full,roles);
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

    private String toPrettyRoles(String roles) {
        StringBuilder sb = new StringBuilder();
        String[] roleIdsS = roles.split(":");
        for(int i = 0; i < roleIdsS.length; ++i) {
            Long roleId = Long.parseLong(roleIdsS[i]);
            ProjectRole pr = projectRoleManager.getProjectRole(roleId);
            if (pr != null) {
                sb.append(pr.getName()).append("<br/>");
            }
        }
        return sb.toString();
    }


    private String[] toStringArray(List<IMPSTemplateMessageMock> messages) {
        String[] result = new String[messages.size()];
        for (int i = 0; i < messages.size(); ++i) {
            IMPSTemplateMessageMock message = messages.get(i);

            String permname = null;
            if (message.getRoles() == null) {
                permname = "null";
            } else {
                Long[] rolesIds = AddTemplateAction.stringToLongArray(message.getRoles());
                Set<ProjectRole> proles = new HashSet<ProjectRole>();
                for (int j = 0; j < rolesIds.length; ++j) {
                    ProjectRole pr = projectRoleManager.getProjectRole(rolesIds[j]);
                    if (pr != null) {
                        proles.add(pr);
                    }
                }
                if (proles.isEmpty()) {
                    permname = "deleted role";
                } else {
                    Set<String> roleNames = new HashSet<String>();
                    for (ProjectRole pr : proles) {
                        roleNames.add(pr.getName());
                    }
                    permname = Arrays.toString(roleNames.toArray(new String[roleNames.size()]));
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

    public String getRoles() {
        return roles;
    }

    public void setRoles(String role) {
        this.roles = role;
    }

    public ProjectRoleManager getProjectRoleManager() {
        return projectRoleManager;
    }
}


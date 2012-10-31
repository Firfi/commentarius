package ru.megaplan.jira.plugin.commentarius.rest;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import org.apache.log4j.Logger;
import ru.megaplan.jira.plugin.commentarius.ao.CommentariusConfigService;
import ru.megaplan.jira.plugin.commentarius.ao.ShabloniusConfigService;
import ru.megaplan.jira.plugin.commentarius.ao.bean.MPSTemplateMessage;
import ru.megaplan.jira.plugin.commentarius.ao.bean.MPSUserTMessage;
import ru.megaplan.jira.plugin.commentarius.ao.bean.mock.IMPSTemplateMessageMock;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 5/26/12
 * Time: 1:03 AM
 * To change this template use File | Settings | File Templates.
 */
@Path("mpstemplatemessage")
public class MPSTemplateMessageResource {

    private static final Logger log = Logger.getLogger(MPSTemplateMessageResource.class);

    private final ShabloniusConfigService shabloniusConfigService;
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final PermissionManager permissionManager;
    private final ProjectRoleManager projectRoleManager;
    private final CommentariusConfigService commentariusConfigService;

    public MPSTemplateMessageResource(ShabloniusConfigService shabloniusConfigService,
            JiraAuthenticationContext jiraAuthenticationContext,
            PermissionManager permissionManager, ProjectRoleManager projectRoleManager, CommentariusConfigService commentariusConfigService) {
        this.shabloniusConfigService = shabloniusConfigService;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.permissionManager = permissionManager;
        this.projectRoleManager = projectRoleManager;
        this.commentariusConfigService = commentariusConfigService;
    }
    @GET
    @Path("get/{type}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getMPSTemplates(@PathParam("type") final String type) {
        List<TemplateMessage> ltm = new ArrayList<TemplateMessage>();
        User u = jiraAuthenticationContext.getLoggedInUser();
        List<IMPSTemplateMessageMock> lmtm = shabloniusConfigService.getTemplateMessages(type);
        Set<ProjectRole> acceptedRoles = getAcceptedRoles(u);
        for (IMPSTemplateMessageMock mtm : lmtm) {
            if (mtm.getPermissionMock() != null && !acceptedRoles.contains(
                    projectRoleManager.getProjectRole(mtm.getPermissionMock().getProjectRoleName()))) continue;
            ltm.add(new TemplateMessage(mtm.getType(), mtm.getSmall(), mtm.getFull()));
        }
        Collections.sort(ltm, new Comparator<TemplateMessage>() {
            @Override
            public int compare(TemplateMessage templateMessage, TemplateMessage templateMessage1) {
             return templateMessage.getSmall().compareTo(templateMessage1.getSmall());
            }
        });
        return Response.ok(ltm).build();
    }

    private Set<ProjectRole> getAcceptedRoles(User loggedInUser) {
        Set<ProjectRole> result = new HashSet<ProjectRole>();
        for (Project p : commentariusConfigService.getAllowedProjects()) {
            for (ProjectRole pr : projectRoleManager.getProjectRoles(loggedInUser,p)) {
                result.add(pr);
            }
        }
        return result;
    }
    //tete  dsa

    @GET
    @Path("get/user/{username}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getMPSUserTemplates(@PathParam("username") final String username) {
        if (!checkPermissions(username)) return Response.status(Response.Status.FORBIDDEN).build();
        List<UserTemplateMessage> utm = new ArrayList<UserTemplateMessage>();
        List<MPSUserTMessage> messages = shabloniusConfigService.getUserTemplateMessages(username);
        for (MPSUserTMessage message : messages) {
            utm.add(new UserTemplateMessage(message.getUserName(),message.getNm(),message.getLabel(),
                    message.getHeader(), message.getBody(), message.getFooter()));
        }
        return Response.ok(utm).build();
    }
    @POST
    @Path("post/user/message")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response addMPSUserTemplate(UserTemplateUpdateBean bean) {
        if (!checkPermissions(bean.username())) return Response.status(Response.Status.FORBIDDEN).build();
        shabloniusConfigService.addUserTemplateMessage(bean);
        return Response.ok().build();
    }

    private boolean checkPermissions(String username) {
        User loggedInUser = jiraAuthenticationContext.getLoggedInUser();
        return loggedInUser.getName().equals(username) ||
                permissionManager.hasPermission(Permissions.ADMINISTER, loggedInUser);
    }



}

package ru.megaplan.jira.plugin.webfragment.webpanel;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.preferences.UserPreferencesManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.plugin.web.model.WebPanel;
import com.atlassian.velocity.VelocityManager;
import org.apache.log4j.Logger;
import org.apache.velocity.exception.VelocityException;
import ru.megaplan.jira.plugin.commentarius.ao.MPSGradeService;
import ru.megaplan.jira.plugin.commentarius.rest.MPSUserPropertyResource;
import ru.megaplan.jira.plugins.permission.manager.ao.MegaPermissionGroupManager;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.IPermissionMock;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.PermissionMock;

import java.io.IOException;
import java.io.Writer;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: i.loskutov
 * Date: 10.05.12
 * Time: 18:31
 * To change this template use File | Settings | File Templates.
 */
public class CommentariusMPSWebpanel implements WebPanel {

    public static final String gradePropertyKey = MPSUserPropertyResource.gradePropertyKey;
    private static Logger log = Logger.getLogger(CommentariusMPSWebpanel.class);

    private final VelocityManager velocityManager;
    private final UserPreferencesManager userPreferencesManager;
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final MPSGradeService mpsGradeService;
    private final ProjectRoleManager projectRoleManager;
    private final ProjectManager projectManager;
    private final UserManager userManager;
    private final PermissionManager permissionManager;
    private final MegaPermissionGroupManager megaPermissionGroupManager;


    public CommentariusMPSWebpanel(VelocityManager velocityManager,
                                   UserPreferencesManager userPreferencesManager,
                                   JiraAuthenticationContext jiraAuthenticationContext,
                                   MPSGradeService mpsGradeService,
                                   ProjectRoleManager projectRoleManager,
                                   ProjectManager projectManager,
                                   UserManager userManager,
                                   PermissionManager permissionManager,
                                   MegaPermissionGroupManager megaPermissionGroupManager) throws IOException {
        this.velocityManager = velocityManager;
        this.userPreferencesManager = userPreferencesManager;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.mpsGradeService = mpsGradeService;
        this.projectRoleManager = projectRoleManager;
        this.projectManager = projectManager;
        this.userManager = userManager;
        this.permissionManager = permissionManager;
        this.megaPermissionGroupManager = megaPermissionGroupManager;
    }


    @Override
    public String getHtml(Map<String, Object> context) {
        try {
            User u = jiraAuthenticationContext.getLoggedInUser();
            String grade = userPreferencesManager.getPreferences(u).getString(UserUtil.META_PROPERTY_PREFIX + gradePropertyKey);
            context.put("gradePropertyValue", grade); // can be null
            context.put("gradePropertyKey", gradePropertyKey);
            context.put("remoteUserName", u.getName());

            Set<ProjectRole> spr = new HashSet<ProjectRole>();
            Collection<IPermissionMock> permissionMocks = new ArrayList<IPermissionMock>();
            for (Project p : permissionManager.getProjectObjects(Permissions.COMMENT_ISSUE,u)) {
                 spr.addAll(projectRoleManager.getProjectRoles(u,p));
            }
            for (ProjectRole pr : spr) { // TODO : filter by projectrole
                IPermissionMock permissionMock = megaPermissionGroupManager.getNewPermissionMock();
                permissionMock.setProjectRoleName(pr.getName());
                permissionMocks.add(permissionMock);
            }
            context.put("managerOptions", mpsGradeService.all(permissionMocks));
            return velocityManager.getEncodedBody("includes/velocity/", "commentarius.vm", "UTF-8", context);
        } catch (VelocityException e) {
            e.printStackTrace();
            return Arrays.toString(e.getStackTrace());
        }
    }

    @Override
    public void writeHtml(Writer writer, Map<String, Object> stringObjectMap) throws IOException {
        log.warn("commentarius writehtml called : ");
        for (Map.Entry<String, Object> stringObjectEntry : stringObjectMap.entrySet()) {
            log.warn(stringObjectEntry.getKey() + " : " + stringObjectEntry.getValue());
        }
    }
}

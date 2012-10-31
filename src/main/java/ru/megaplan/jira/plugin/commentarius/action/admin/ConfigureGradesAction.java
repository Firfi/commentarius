package ru.megaplan.jira.plugin.commentarius.action.admin;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.velocity.VelocityManager;
import ru.megaplan.jira.plugin.commentarius.ao.CommentariusConfigService;
import ru.megaplan.jira.plugin.commentarius.ao.MPSGradeService;
import ru.megaplan.jira.plugin.commentarius.ao.bean.MPSGrade;
import ru.megaplan.jira.plugin.commentarius.ao.bean.mock.IGradeMock;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.IPermissionMock;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.PermissionMock;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: i.loskutov
 * Date: 12.05.12
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class ConfigureGradesAction extends JiraWebActionSupport {

    private final JiraAuthenticationContext authenticationContext;
    private final WebResourceManager webResourceManager;
    private final ActiveObjects ao;
    private final VelocityManager velocityManager;
    private final MPSGradeService mpsGradeService;
    private final ProjectRoleManager projectRoleManager;
    private final CommentariusConfigService commentariusConfigService;

    private boolean submitted = false;



    private String[] projectRoleNames;
    private IGradeMock[] mpsGrades;
    private Map<Integer, IGradeMock> mpsGradesMap;
    private int[] mpsGradesIds;
    private String projectRoleName;
    private String mpsGradeName;
    private boolean delete;
    private boolean add;

    ConfigureGradesAction(
            JiraAuthenticationContext authenticationContext,
            WebResourceManager webResourceManager,
            ActiveObjects ao,
            VelocityManager velocityManager,
            MPSGradeService mpsGradeService,
            ProjectRoleManager projectRoleManager, CommentariusConfigService commentariusConfigService) {
         this.authenticationContext = authenticationContext;
         this.webResourceManager = webResourceManager;
         this.ao = ao;
         this.velocityManager = velocityManager;
         this.mpsGradeService = mpsGradeService;
        this.projectRoleManager = projectRoleManager;
        this.commentariusConfigService = commentariusConfigService;
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

    private String[] getProjectRoleNames(Collection<ProjectRole> lpr) {
        ArrayList<String> result = new ArrayList<String>();
         for (ProjectRole pr : lpr) {
              result.add(pr.getName());
         }
        return result.toArray(new String[result.size()]);
    }

    @Override
    protected String doExecute() throws Exception
    {


        if (!hasPermissions())
        {
            return PERMISSION_VIOLATION_RESULT;
        }

        if (!isSubmitted()) {
            List<IGradeMock> allGrades = mpsGradeService.all();
            Collections.sort(allGrades, new Comparator<IGradeMock>() {
                @Override
                public int compare(IGradeMock o1, IGradeMock o2) {
                    if (o1.getPermissionMock().getProjectRoleName().equals(o2.getPermissionMock().getProjectRoleName())) {
                        return o1.getGradeName().compareTo(o2.getGradeName());
                    } else {
                        return o1.getPermissionMock().getProjectRoleName().compareTo(o2.getPermissionMock().getProjectRoleName());
                    }
                }
            });
            mpsGrades = allGrades.toArray(new IGradeMock[allGrades.size()]);
            mpsGradesMap = new HashMap<Integer, IGradeMock>();
            for (IGradeMock grade : allGrades) {
                mpsGradesMap.put(grade.getID(), grade);
            }
            projectRoleNames =  getProjectRoleNames(projectRoleManager.getProjectRoles());
            return SUCCESS;
        }

        if (isDelete()) {
             if (mpsGradesIds != null) {
                 Collection<Integer> ids = com.google.common.primitives.Ints.asList(mpsGradesIds) ;
                 mpsGradeService.deleteGrades(ids);
             }
        } else if (isAdd()) {
            IGradeMock gradeMock = mpsGradeService.getNewGradeMock();
            gradeMock.getPermissionMock().setProjectRoleName(projectRoleName);
            gradeMock.setGradeName(mpsGradeName);
            mpsGradeService.addGrade(gradeMock);
        }

        return getRedirect("CommentariusConfigureGradesAction.jspa"); // drop session state
    }




    public void setMpsGrades(IGradeMock[] mpsGrades) {
        this.mpsGrades = mpsGrades;
    }

    public IGradeMock[] getMpsGrades() {
        return mpsGrades;
    }

    public boolean isSubmitted()
    {
        return submitted;
    }

    public void setSubmitted(boolean submitted)
    {
        this.submitted = submitted;
    }


    public boolean isDelete() {
        return delete;
    }

    public boolean isAdd() {
        return add;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public String getMpsGradeName() {
        return mpsGradeName;
    }

    public void setMpsGradeName(String mpsGradeName) {
        this.mpsGradeName = mpsGradeName;
    }

    public String[] getProjectRoleNames() {
        return projectRoleNames;
    }

    public void setProjectRoleNames(String[] projectRoleNames) {
        this.projectRoleNames = projectRoleNames;
    }

    public String getProjectRoleName() {
        return projectRoleName;
    }

    public void setProjectRoleName(String projectRoleName) {
        this.projectRoleName = projectRoleName;
    }

    public int[] getMpsGradesIds() {
        return mpsGradesIds;
    }

    public void setMpsGradesIds(int[] mpsGradesIds) {
        this.mpsGradesIds = mpsGradesIds;
    }
}

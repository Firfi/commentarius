package ru.megaplan.jira.plugin.commentarius.ao.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.apache.log4j.Logger;
import ru.megaplan.jira.plugin.commentarius.ao.bean.MPSGrade;
import ru.megaplan.jira.plugin.commentarius.ao.MPSGradeService;
import ru.megaplan.jira.plugin.commentarius.ao.bean.mock.IGradeMock;
import ru.megaplan.jira.plugins.permission.manager.ao.MegaPermissionGroupManager;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.PermissionBean;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.PermissionGroup;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.IPermissionGroupMock;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.IPermissionMock;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.PermissionGroupMock;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.PermissionMock;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 5/20/12
 * Time: 1:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class MPSGradeServiceImpl implements MPSGradeService {

    private final ActiveObjects ao;
    private final MegaPermissionGroupManager megaPermissionGroupManager;
    private final static Logger log = Logger.getLogger(MPSGradeServiceImpl.class);

    private final static String GRADE_PERMISSION_GROUP_NAME = "ru.megaplan.jira.plugin.commentarius.MPSGRADE";

    public MPSGradeServiceImpl(ActiveObjects ao, MegaPermissionGroupManager megaPermissionGroupManager)
    {

        this.ao = checkNotNull(ao);
        this.megaPermissionGroupManager = megaPermissionGroupManager;
    }







    @Override
    public List<IGradeMock> all() {
        final List<MPSGrade> grades = new ArrayList<MPSGrade>();
        Collections.addAll(grades, ao.find(MPSGrade.class));
        final List<IGradeMock> result = new ArrayList<IGradeMock>(grades.size());
        for (MPSGrade grade : grades) {
            IPermissionMock request = new PermissionMock();
            request.setID(grade.getPermissionBean());
            IPermissionMock permissionMock = null;
            permissionMock = megaPermissionGroupManager.getPermission(request);
            if (permissionMock == null) { // someone deleted permission. what shame...
                request.setProjectRoleName("Users"); //just drop permission to "all"
                request.setPermissionGroupMock(
                        megaPermissionGroupManager.getPermissionGroup(GRADE_PERMISSION_GROUP_NAME));
                request.setID(0);
                permissionMock = megaPermissionGroupManager.getUniquePermission(request);
            }
            IGradeMock gradeMock = new GradeMock(grade);
            gradeMock.setPermissionMock(permissionMock);
            result.add(gradeMock);
        }
        return result;
    }


    @Override
    public List<IGradeMock> all(Collection<IPermissionMock> permissions) {
        final List<MPSGrade> grades = new ArrayList<MPSGrade>();
        for (IPermissionMock pm : permissions) {
            if (pm.getPermissionGroupMock() == null || pm.getPermissionGroupMock().getName() == null) {
                IPermissionGroupMock permissionGroupMock =
                        megaPermissionGroupManager.getPermissionGroup(GRADE_PERMISSION_GROUP_NAME);
                pm.setPermissionGroupMock(permissionGroupMock);
            }
            IPermissionMock response = megaPermissionGroupManager.getPermission(pm);
            if (response == null) {
                continue;
            }
            Collections.addAll(grades, ao.find(MPSGrade.class, Query.select().where("PERMISSION_BEAN = ?", response.getID())));
        }
        final List<IGradeMock> result = new ArrayList<IGradeMock>(grades.size());
        for (MPSGrade g : grades) {
            IGradeMock gradeMock = new GradeMock(g);
            IPermissionMock permissionMock = megaPermissionGroupManager.getNewPermissionMock();
            permissionMock.setID(g.getPermissionBean());
            gradeMock.setPermissionMock(permissionMock);
            result.add(gradeMock);
        }
        return result;
    }


    @Override
    public void deleteGrades(final Collection<Integer> gradeIds) {
        checkNotNull(gradeIds);
        Set<Integer> permissionBeanIds = new HashSet<Integer>();
        for (Integer id : gradeIds) {
            MPSGrade grade = ao.get(MPSGrade.class, id);
            if (grade != null) ao.delete(grade);
            else log.error("something wrong in grade deletion, grade id : " + id);
            permissionBeanIds.add(grade.getPermissionBean());
        }
        cleanUnusedPermissionBeans(permissionBeanIds);
    }

    private void cleanUnusedPermissionBeans(Set<Integer> permissionBeanIds) {
        for (Integer id : permissionBeanIds) {
            MPSGrade[] grades = ao.find(MPSGrade.class,
                    Query.select().where("PERMISSION_BEAN = ?", id).limit(1));
            if (grades.length==0) {
                megaPermissionGroupManager.deletePermission(id);
            }
        }
    }


    /*
   code without @Transactional annotation looks like that :
   ao.executeInTransaction(new TransactionCallback<MPSGrade>() {
           @Override
           public MPSGrade doInTransaction() {
                   for (String grade : gradeNames) {
                       ao.delete(selectGrade(grade));
                   }
               return null;
           }
       });
    */

    @Override
    public void addGrade(final IGradeMock gradeMock) {   //TODO : check for double - then just .save()
        checkNotNull(gradeMock);
        checkNotNull(gradeMock.getGradeName());
        checkNotNull(gradeMock.getPermissionMock());
        String projectRole = gradeMock.getPermissionMock().getProjectRoleName();
        checkNotNull(projectRole);
        IPermissionGroupMock mpsPg = megaPermissionGroupManager.getPermissionGroup(GRADE_PERMISSION_GROUP_NAME);
        checkNotNull(mpsPg);
        if (gradeMock.getGradeName().isEmpty()) return;
        IPermissionMock pb = getPermissionBean(mpsPg, projectRole);
        if (findMpsGrade(gradeMock.getGradeName(),pb) != null) {
             return;
        }
        DBParam gradeParam = new DBParam("GRADE", gradeMock.getGradeName());
        DBParam permissionBeanParam = new DBParam("PERMISSION_BEAN", pb.getID());
        ao.create(MPSGrade.class, gradeParam, permissionBeanParam);
    }

    @Override
    public IGradeMock getNewGradeMock() {
        IGradeMock result = new GradeMock();
        IPermissionMock permissionMock = megaPermissionGroupManager.getNewPermissionMock();
        result.setPermissionMock(permissionMock);
        return result;
    }

    private MPSGrade findMpsGrade(String name, IPermissionMock pb) {
        MPSGrade[] grades = ao.find(MPSGrade.class, Query.select().where("GRADE = ? AND PERMISSION_BEAN = ?", name, pb.getID()));
        if (grades.length == 0) return null;
        else return grades[0];
    }


    private IPermissionMock getPermissionBean(IPermissionGroupMock mpsPg, String projectRole) {
        IPermissionMock permissionMock = megaPermissionGroupManager.getNewPermissionMock();
        permissionMock.setProjectRoleName(projectRole);
        permissionMock.setPermissionGroupMock(mpsPg);
        return megaPermissionGroupManager.getUniquePermission(permissionMock);
    }



    @Override
    public boolean isPresent(IGradeMock gradeMock) {
        MPSGrade mpsGrade = ao.get(MPSGrade.class, gradeMock.getID());
        return (mpsGrade != null);
    }

    private MPSGrade selectGrade(String gradeName) {
        MPSGrade[] results = ao.find(MPSGrade.class, Query.select().where("GRADE = ?", gradeName));
        if (results.length > 1) throw new RuntimeException("we have more than one results by unique field");
        if (results.length == 0) return null;
        return results[0];
    }

    private static class GradeMock implements IGradeMock {
        private String gradeName;
        private IPermissionMock permissionMock;
        private int ID;

        private GradeMock(){};

        private GradeMock(MPSGrade g) {
            gradeName = g.getGrade();
            ID = g.getID();
        }

        @Override
        public String getGradeName() {
            return gradeName;
        }

        @Override
        public void setGradeName(String gradeName) {
            this.gradeName = gradeName;
        }

        @Override
        public IPermissionMock getPermissionMock() {
            return permissionMock;
        }

        @Override
        public void setPermissionMock(IPermissionMock permissionMock) {
            this.permissionMock = permissionMock;
        }

        @Override
        public int getID() {
            return ID;
        }

        @Override
        public void setID(int ID) {
            this.ID = ID;
        }
    }

}

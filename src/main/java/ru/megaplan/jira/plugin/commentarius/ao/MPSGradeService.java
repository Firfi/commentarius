package ru.megaplan.jira.plugin.commentarius.ao;

import com.atlassian.activeobjects.tx.Transactional;
import ru.megaplan.jira.plugin.commentarius.ao.bean.MPSGrade;
import ru.megaplan.jira.plugin.commentarius.ao.bean.mock.IGradeMock;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.IPermissionMock;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 5/20/12
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */
@Transactional
public interface MPSGradeService {
    List<IGradeMock> all();
    List<IGradeMock> all(Collection<IPermissionMock> permissionMocks);
    void deleteGrades(Collection<Integer> gradeIds);
    void addGrade(IGradeMock gradeMock);
    boolean isPresent(IGradeMock grade);

    IGradeMock getNewGradeMock();
}

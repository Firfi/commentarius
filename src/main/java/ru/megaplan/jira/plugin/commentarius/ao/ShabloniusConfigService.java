package ru.megaplan.jira.plugin.commentarius.ao;

import com.atlassian.activeobjects.tx.Transactional;
import ru.megaplan.jira.plugin.commentarius.ao.bean.MPSTemplateMessage;
import ru.megaplan.jira.plugin.commentarius.ao.bean.MPSUserTMessage;
import ru.megaplan.jira.plugin.commentarius.ao.bean.mock.IMPSTemplateMessageMock;
import ru.megaplan.jira.plugin.commentarius.ao.impl.ShabloniusConfigServiceImpl;
import ru.megaplan.jira.plugin.commentarius.rest.UserTemplateUpdateBean;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: i.loskutov
 * Date: 25.05.12
 * Time: 16:54
 * To change this template use File | Settings | File Templates.
 */
@Transactional
public interface ShabloniusConfigService {

    List<IMPSTemplateMessageMock> getTemplateMessages(String type);
    List<IMPSTemplateMessageMock> getAllTemplateMessages();

    void addTemplateMessage(IMPSTemplateMessageMock m);
    void deleteTemplateMessage(IMPSTemplateMessageMock mg);

    List<MPSUserTMessage> getUserTemplateMessages(String username);
    List<MPSUserTMessage> getAllUserTemplateMessages();
    void addUserTemplateMessage(UserTemplateUpdateBean bean);

    IMPSTemplateMessageMock getNewMessageMock(String type, String small, String full);
    IMPSTemplateMessageMock getNewMessageMock();


}

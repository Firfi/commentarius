package ru.megaplan.jira.plugin.commentarius.action.admin;

import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import org.apache.log4j.Logger;
import ru.megaplan.jira.plugin.commentarius.ao.ShabloniusConfigService;
import ru.megaplan.jira.plugin.commentarius.ao.bean.MPSTemplateMessage;
import ru.megaplan.jira.plugin.commentarius.ao.bean.mock.IMPSTemplateMessageMock;

/**
 * Created with IntelliJ IDEA.
 * User: i.loskutov
 * Date: 30.05.12
 * Time: 14:35
 * To change this template use File | Settings | File Templates.
 */
public class DeleteTemplateAction extends JiraWebActionSupport {

    private final ShabloniusConfigService shabloniusConfigService;

    private static final Logger log = Logger.getLogger(DeleteTemplateAction.class);
    private int id;

    public DeleteTemplateAction(ShabloniusConfigService shabloniusConfigService) {
        this.shabloniusConfigService = shabloniusConfigService;
    }

    @Override
    public String doExecute() {
        if (!isHasPermission(Permissions.ADMINISTER)) return PERMISSION_VIOLATION_RESULT;
        IMPSTemplateMessageMock templateMessageMock = shabloniusConfigService.getNewMessageMock();
        templateMessageMock.setID(id);
        shabloniusConfigService.deleteTemplateMessage(templateMessageMock);
        return getRedirect("CommentariusConfigureTemplatesAction.jspa");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

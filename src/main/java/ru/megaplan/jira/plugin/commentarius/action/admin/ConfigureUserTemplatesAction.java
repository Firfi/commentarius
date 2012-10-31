package ru.megaplan.jira.plugin.commentarius.action.admin;

import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import ru.megaplan.jira.plugin.commentarius.ao.ShabloniusConfigService;
import ru.megaplan.jira.plugin.commentarius.ao.bean.MPSUserTMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 5/26/12
 * Time: 7:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigureUserTemplatesAction extends JiraWebActionSupport {

    private final ShabloniusConfigService shabloniusConfigService;
    private boolean submitted;
    private List<String> numCol;
    private List<String>  userCol;
    private List<String>  labelCol;
    private List<String>  headerCol;
    private List<String>  bodyCol;
    private List<String>  footerCol;


    ConfigureUserTemplatesAction(
            ShabloniusConfigService shabloniusConfigService) {
        this.shabloniusConfigService = shabloniusConfigService;
    }

    public boolean hasPermissions()
    {
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

        if (!hasPermissions()) return PERMISSION_VIOLATION_RESULT;

        initColumns();

        if (!isSubmitted()) {
          return SUCCESS;
        }

        return getRedirect("CommentariusConfigureUserTemplatesAction.jspa");
    }

    private void initColumns() {
        List<MPSUserTMessage> messages = shabloniusConfigService.getAllUserTemplateMessages();
        int size = messages.size();
        numCol = new ArrayList<String>(size);
        userCol = new ArrayList<String>(size);
        labelCol = new ArrayList<String>(size);
        headerCol = new ArrayList<String>(size);
        bodyCol = new ArrayList<String>(size);
        footerCol = new ArrayList<String>(size);
        for (MPSUserTMessage m : messages) {
            numCol.add(String.valueOf(m.getNm()));
            userCol.add(m.getUserName());
            labelCol.add(m.getLabel());
            headerCol.add(m.getHeader());
            bodyCol.add(m.getBody());
            footerCol.add(m.getFooter());
        }
    }
    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public List<String> getNumCol() {
        return numCol;
    }

    public void setNumCol(List<String> numCol) {
        this.numCol = numCol;
    }

    public List<String> getUserCol() {
        return userCol;
    }

    public void setUserCol(List<String> userCol) {
        this.userCol = userCol;
    }

    public List<String> getLabelCol() {
        return labelCol;
    }

    public void setLabelCol(List<String> labelCol) {
        this.labelCol = labelCol;
    }

    public List<String> getHeaderCol() {
        return headerCol;
    }

    public void setHeaderCol(List<String> headerCol) {
        this.headerCol = headerCol;
    }

    public List<String> getBodyCol() {
        return bodyCol;
    }

    public void setBodyCol(List<String> bodyCol) {
        this.bodyCol = bodyCol;
    }

    public List<String> getFooterCol() {
        return footerCol;
    }

    public void setFooterCol(List<String> footerCol) {
        this.footerCol = footerCol;
    }
}

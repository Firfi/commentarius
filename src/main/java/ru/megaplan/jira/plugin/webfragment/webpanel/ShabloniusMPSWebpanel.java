package ru.megaplan.jira.plugin.webfragment.webpanel;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.preferences.UserPreferencesManager;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.plugin.web.model.WebPanel;
import com.atlassian.velocity.VelocityManager;
import org.apache.log4j.Logger;
import org.apache.velocity.exception.VelocityException;
import ru.megaplan.jira.plugin.commentarius.ao.MPSGradeService;
import ru.megaplan.jira.plugin.commentarius.rest.MPSUserPropertyResource;
import webwork.action.ServletActionContext;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: i.loskutov
 * Date: 25.05.12
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
public class ShabloniusMPSWebpanel implements WebPanel {
    public static final String gradePropertyKey = MPSUserPropertyResource.gradePropertyKey;
    private static Logger log = Logger.getLogger(CommentariusMPSWebpanel.class);

    private final VelocityManager velocityManager;
    private final UserPreferencesManager userPreferencesManager;
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final MPSGradeService mpsGradeService;


    public ShabloniusMPSWebpanel(VelocityManager velocityManager,
                                   UserPreferencesManager userPreferencesManager,
                                   JiraAuthenticationContext jiraAuthenticationContext,
                                   MPSGradeService mpsGradeService) throws IOException {
        this.velocityManager = velocityManager;
        this.userPreferencesManager = userPreferencesManager;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.mpsGradeService = mpsGradeService;
    }


    @Override
    public String getHtml(Map<String, Object> context) {
        try {
            return velocityManager.getEncodedBody("includes/velocity/", "shablonius.vm", "UTF-8", context);
        } catch (VelocityException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return "";
    }

    @Override
    public void writeHtml(Writer writer, Map<String, Object> stringObjectMap) throws IOException {
        log.warn("Shablonius writehtml called : ");
        for (Map.Entry<String, Object> stringObjectEntry : stringObjectMap.entrySet()) {
            log.warn(stringObjectEntry.getKey() + " : " + stringObjectEntry.getValue());
        }
    }
}

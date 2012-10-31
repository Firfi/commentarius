package ru.megaplan.jira.plugin.webfragment.condition;


import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.ofbiz.OfBizUserDao;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.plugin.webfragment.conditions.AbstractIssueCondition;
import com.atlassian.jira.plugin.webfragment.conditions.AbstractJiraCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.plugin.PluginParseException;
import org.apache.log4j.Logger;
import ru.megaplan.jira.plugin.commentarius.ao.CommentariusConfigService;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Adm_i.loskutov
 * Date: 10.05.12
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
public class CommentariusProjectsCondition extends AbstractJiraCondition {

    Logger log = Logger.getLogger(this.getClass());
    CommentariusConfigService commentariusConfigService;
   // private final ProjectManager projectManager;
   // private String projectkey;

//    public ProjectKeyCondition(ProjectManager projectManager) {
//       //  this.projectManager = projectManager;
//    }
//
//    @Override
//    public void init(Map params) {
//       // System.out.println("test");
//       // projectkey = com.google.common.base.Preconditions.checkNotNull(params.get("projectkey")).toString();
//    }

//    @java.lang.Override
//    public boolean shouldDisplay(com.atlassian.crowd.embedded.api.User user, Issue issue, JiraHelper jiraHelper) {
//        return true;
//    }

    public CommentariusProjectsCondition(CommentariusConfigService commentariusConfigService) {
        this.commentariusConfigService = commentariusConfigService;
    }

    public boolean shouldDisplay(User user, Issue issue, JiraHelper jiraHelper) {
        //if (!isTest(jiraHelper)) return false;
        if (issue == null) {
            log.error("issue is null. Maybe running in web-resource context?");
            return false;
        }
        String pk = issue.getProjectObject().getKey();
        for (Project p : commentariusConfigService.getAllowedProjects()) {
            if (p.getKey().equalsIgnoreCase(pk)) return true;
        }
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private boolean isTest(JiraHelper jiraHelper) {
        return jiraHelper.getRequest().getServerName().equals("192.168.9.30")||jiraHelper.getRequest().getServerName().equals("127.0.0.1");
    }

    @Override
    public boolean shouldDisplay(User user, JiraHelper jiraHelper) {
        final Map<String, Object> params = jiraHelper.getContextParams();

        final Issue issue = (Issue) params.get("issue");

        if (issue != null)
        {
            return shouldDisplay(user, issue, jiraHelper);
        }

        final Project project = (Project) params.get("project");
        if (project != null)
        {
            for (Project p : commentariusConfigService.getAllowedProjects()) {
                if (p.getKey().equalsIgnoreCase(project.getKey())) return true;
            }
        }

        return false;
    }
}
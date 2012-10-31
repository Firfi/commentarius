/*
@author Atlassian
 */

package ru.megaplan.jira.plugin.commentarius.customfield;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.customfields.SortableCustomField;
import com.atlassian.jira.issue.customfields.converters.StringConverter;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.impl.StringCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.persistence.PersistenceFieldType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.UserPropertyManager;
import com.atlassian.jira.user.UserUtils;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.util.TextUtils;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;

public class UserPropertyCFType extends StringCFType implements SortableCustomField
{

    private static final Logger log = Logger.getLogger(UserPropertyCFType.class);

    private final StringConverter stringConverter;
    private final UserPropertyManager userPropertyManager;
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final CommentManager commentManager;

    public UserPropertyCFType(CustomFieldValuePersister customFieldValuePersister, StringConverter stringConverter,
            GenericConfigManager genericConfigManager, UserPropertyManager userPropertyManager,
            JiraAuthenticationContext jiraAuthenticationContext,
            CommentManager commentManager, CustomFieldManager customFieldManager)
    {
        super(customFieldValuePersister, genericConfigManager);
        this.stringConverter = stringConverter;
        this.userPropertyManager = userPropertyManager;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.commentManager = commentManager;
    }

    @Override
    public String getStringFromSingularObject(Object value)
    {
        assertObjectImplementsType(String.class, value);
        return stringConverter.getString((String) value);
    }

    @Override
    public Object getSingularObjectFromString(String string) throws FieldValidationException {
        return stringConverter.getObject(string);
    }

    @Override
    public int compare(Object customFieldObjectValue1, Object customFieldObjectValue2, FieldConfig fieldConfig)
    {
        return ((String) customFieldObjectValue1).compareTo((String) customFieldObjectValue2);
    }

    @Override
    protected PersistenceFieldType getDatabaseType()
    {
        return PersistenceFieldType.TYPE_LIMITED_TEXT;
    }

    @Override
    public Object getValueFromIssue(CustomField customField, Issue issue)
    {
        try
        {
            User user = null;
            String key = (String) customField.getDefaultValue(issue);
            if (!TextUtils.stringSet(key))
            {
                return "";
            }

            int splitIdx = key.lastIndexOf(':');
            if (splitIdx > 0)
            {

                String fieldName = key.substring(0, splitIdx);
                String propertyName = key.substring(splitIdx + 1);

                if ("reporter".equalsIgnoreCase(fieldName))
                {
                    user = issue.getReporter();
                }
                else if ("assignee".equalsIgnoreCase(fieldName))
                {
                    user = issue.getAssignee();
                }
                else if ("MPS_remoteUser".equalsIgnoreCase(fieldName) && !commentManager.getComments(issue).isEmpty()) {
                    List<Comment> comments =  commentManager.getComments(issue);
                    user = comments.get(comments.size() - 1).getAuthorUser();
                }
                else
                {
                    // find custom field named "fieldName"
                    // look up user with name = value of custom field found
                    CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
                    if (customFieldManager != null)
                    {
                        List issueFields = customFieldManager.getCustomFieldObjects(issue);
                        for (Iterator iterator = issueFields.iterator(); iterator.hasNext();)
                        {
                            CustomField cf = (CustomField) iterator.next();
                            if (fieldName.equalsIgnoreCase(cf.getName()))
                            {
                                Object cfValue = cf.getValue(issue);
                                if (cfValue instanceof String)
                                {
                                    String username = (String) cfValue;
                                    user = UserUtils.getUser(username);
                                }
                                else if (cfValue instanceof User)
                                {
                                    user = (User) cfValue;
                                }
                            }

                        }
                    }
                }
                if (user != null)
                {
                    PropertySet ps = userPropertyManager.getPropertySet(user);
                    if (ps != null)
                    {
                        return ps.getString("jira.meta." + propertyName);
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error("can't extract user property in property custom field",e);
        }

        return "";
    }
}

package ru.megaplan.jira.plugin.commentarius.rest;

import com.atlassian.core.AtlassianCoreException;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.user.preferences.UserPreferencesManager;
import com.atlassian.jira.user.util.UserManager;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.Mock;
import com.atlassian.core.user.preferences.Preferences;
import org.mockito.MockitoAnnotations;


import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static ru.megaplan.jira.plugin.commentarius.rest.UserPropertyResource.RESPONSE_BADREQUEST;
import static ru.megaplan.jira.plugin.commentarius.rest.UserPropertyResource.RESPONSE_FORBIDDEN;


/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 5/19/12
 * Time: 9:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserPropertyResourceTest extends TestCase {


    private UserPropertyResource userPropertyResource;

    @Mock private JiraAuthenticationContext jiraAuthenticationContext;
    @Mock private UserPreferencesManager userPreferencesManager;
    @Mock private UserManager userManager;
    @Mock private PermissionManager permissionManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        userPropertyResource = initPropertyResource();
    }

    private UserPropertyResource initPropertyResource() {
        UserPropertyResource result = new UserPropertyResource(
                userManager,
                jiraAuthenticationContext,
                userPreferencesManager,
                permissionManager);
        return result;
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testGetUserPropertyInvalidUsername() {
        Preferences validPreferences = getValidPreferences();
        when(userPreferencesManager.getPreferences(any(User.class))).thenReturn(validPreferences);
        when(userManager.getUser(anyString())).thenReturn(getInvalidUser());
        assertEquals(userPropertyResource.getUserProperty("12I_WILL_EAT_YOUR_BRAINS34", "testProperty"),
                RESPONSE_BADREQUEST);
    }

    @Test
    public void testGetUserPropertyNullUser() {
         when(userManager.getUser(anyString())).thenReturn(getInvalidUser());
         assertEquals(userPropertyResource.getUserProperty(null, "testProperty"),
                 RESPONSE_BADREQUEST);
    }

    @Test
    public void testUpdateUserPropertyInvalidUsername() {
        PropertyUpdateBean propertyUpdateBean = mock(PropertyUpdateBean.class);
        when(userManager.getUser(anyString())).thenReturn(getInvalidUser());
        try {
        assertEquals(userPropertyResource.updateUserProperty(null, propertyUpdateBean),
                RESPONSE_BADREQUEST);
        } catch (AtlassianCoreException e) {
            assertEquals("We found", "Core Exception!");
        }
    }

    @Test
    public void testUpdateUserPropertyDoNotHavePermissions() throws AtlassianCoreException {
        User retuser = mock(User.class);
        when(userManager.getUserObject(anyString())).thenReturn(retuser);
        PropertyUpdateBean propertyUpdateBean = mock(PropertyUpdateBean.class);
        when(propertyUpdateBean.newValue()).thenReturn("testProperty");
        when(propertyUpdateBean.oldValue()).thenReturn("oldProperty");
        when(propertyUpdateBean.key()).thenReturn("validKey");
        when(permissionManager.hasPermission(anyInt(), any(User.class))).thenReturn(false);
        assertEquals(userPropertyResource.updateUserProperty("UserWithoutPermissions", propertyUpdateBean), RESPONSE_FORBIDDEN);
    }

    private Preferences getValidPreferences() {
        Preferences validPreferences = mock(Preferences.class);
        when(
                validPreferences.getString(anyString())
        ).thenReturn("validPreference");
        return validPreferences;
    }

    private User getInvalidUser() {
        return null;
    }


}

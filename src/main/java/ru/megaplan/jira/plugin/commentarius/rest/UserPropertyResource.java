package ru.megaplan.jira.plugin.commentarius.rest;

import com.atlassian.core.AtlassianCoreException;
import com.atlassian.core.user.preferences.Preferences;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.user.preferences.UserPreferencesManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created with IntelliJ IDEA.
 * User: i.loskutov
 * Date: 11.05.12
 * Time: 19:44
 * To change this template use File | Settings | File Templates.
 */
@Path("userproperty")
public class UserPropertyResource {

    private final Logger log = Logger.getLogger(this.getClass());
    protected final UserManager userManager;
    protected final JiraAuthenticationContext jiraAuthenticationContext;
    protected final UserPreferencesManager userPreferencesManager;
    protected final PermissionManager permissionManager;

    static final Response RESPONSE_BADREQUEST = Response.status(Response.Status.BAD_REQUEST).build();
    static final Response RESPONSE_FORBIDDEN = Response.status(Response.Status.FORBIDDEN).build();
    static final Response RESPONSE_OK = Response.status(Response.Status.OK).build();

    public UserPropertyResource(UserManager userManager, JiraAuthenticationContext jiraAuthenticationContext
    , UserPreferencesManager userPreferencesManager,PermissionManager permissionManager) {
        this.userManager = userManager;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.userPreferencesManager = userPreferencesManager;
        this.permissionManager = permissionManager;
    }

    @GET
    @Path("get/{username}/{propertykey}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getUserProperty(@PathParam("username") final String username,
                                    @PathParam("propertykey") final String propertyKey) {
        User u = userManager.getUserObject(username);
        if (u == null) return RESPONSE_BADREQUEST;
        Preferences userPreferences;
        Response permissionError =  checkPermissions(u);
        if (!permissionError.equals(RESPONSE_OK)) return permissionError;
        String metaPropertyKey =   UserUtil.META_PROPERTY_PREFIX + propertyKey;
        userPreferences = userPreferencesManager.getPreferences(u);
        String prefence = userPreferences.getString(metaPropertyKey);
        if (prefence == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        return Response.ok(new UserProperty(
                u.getName(),
                propertyKey,
                prefence, // oldValue
                prefence // newValue
             )
        ).build();

    }

    /*
    userWithProperties is not always logged in user
     */
    protected Response checkPermissions(User userWithProperties) {
        if (userWithProperties != null) {
            if (!permissionManager.hasPermission(
                    Permissions.ADMINISTER, jiraAuthenticationContext.getLoggedInUser())
               ) {
                return RESPONSE_FORBIDDEN;
            }

        } else {
            log.debug("Specified user is not exist");
            return RESPONSE_BADREQUEST;
        }
        return RESPONSE_OK;
    }

    @POST
    @Path("update/{username}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({MediaType.APPLICATION_JSON})
    public Response updateUserProperty(@PathParam("username") final String username, PropertyUpdateBean propertyUpdateBean) throws AtlassianCoreException {
        User u = userManager.getUserObject(username);
        if (u == null) return RESPONSE_BADREQUEST;
        String propertyKey = propertyUpdateBean.key();
        String propertyNewValue = propertyUpdateBean.newValue();
        if (propertyKey == null || propertyKey.isEmpty()) return RESPONSE_BADREQUEST;
        String metaPropertyKey =   UserUtil.META_PROPERTY_PREFIX + propertyKey;
        Preferences userPreferences;
        Response permissionError = checkPermissions(u);
        if (!permissionError.equals(RESPONSE_OK)) return permissionError;
        userPreferences = userPreferencesManager.getPreferences(u);
        String preference = userPreferences.getString(metaPropertyKey);
        boolean isRemove = (propertyNewValue == null||propertyNewValue.isEmpty());
        if (!isRemove) {
            if (preference == null) {
                userPreferences.setString(metaPropertyKey, propertyNewValue);//setAsActualType(propertyKey, propertyUpdateBean.newValue());
            } else {
                if (log.isDebugEnabled() && !preference.equals(propertyUpdateBean.oldValue()))
                {
                    log.debug("Request old value and actual old value not equals for user : " + u.getName() + " " +
                            "and property : " + preference);
                    //return Response.status(Response.Status.BAD_REQUEST).build();
                    //remove this bullshit
                }
                userPreferences.setString(metaPropertyKey, propertyNewValue);
            }
        } else {
            if (preference != null) { //we have something for remove

                userPreferences.remove(metaPropertyKey);
            }
        }

        return Response.ok(new UserProperty(
                u.getName(),
                propertyKey,
                preference, // oldValue
                userPreferences.getString(metaPropertyKey))   // newValue
        ).build();
    }


}

package ru.megaplan.jira.plugin.commentarius.rest;

import com.atlassian.core.AtlassianCoreException;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.user.preferences.UserPreferencesManager;
import com.atlassian.jira.user.util.UserManager;
import org.apache.log4j.Logger;
import ru.megaplan.jira.plugin.commentarius.ao.MPSGradeService;
import ru.megaplan.jira.plugin.commentarius.ao.bean.mock.IGradeMock;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 5/20/12
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
@Path("mpsuserproperty")
public class MPSUserPropertyResource extends UserPropertyResource {

    private final Logger log = Logger.getLogger(MPSUserPropertyResource.class);

    public static final String gradePropertyKey = "MPS_grade";

    public static final Response INACCEPTABLE = Response.status(Response.Status.NOT_ACCEPTABLE).build();

    private static final String adminUsername = "admin";

    private final MPSGradeService mpsGradeService;

    public MPSUserPropertyResource(UserManager userManager,
                                   JiraAuthenticationContext jiraAuthenticationContext,
                                   UserPreferencesManager userPreferencesManager,
                                   PermissionManager permissionManager,
                                   MPSGradeService mpsGradeService ) {
        super(userManager, jiraAuthenticationContext, userPreferencesManager, permissionManager);
        this.mpsGradeService = mpsGradeService;
    }

    @GET
    @Path("get/{username}/{propertykey}")
    @Produces({MediaType.APPLICATION_JSON})
    @Override
    public Response getUserProperty(@PathParam("username") final String username,
    @PathParam("propertykey") final String propertyKey) {
        if (!isPropertyKeyValid(propertyKey)) return RESPONSE_BADREQUEST;
        return super.getUserProperty(username, propertyKey);
    }

    @POST
    @Path("update/{username}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({MediaType.APPLICATION_JSON})
    @Override
    public Response updateUserProperty(@PathParam("username") final String username,
                                       PropertyUpdateBean propertyUpdateBean)
            throws AtlassianCoreException {
        if (!isPropertyKeyValid(propertyUpdateBean.key())) return RESPONSE_BADREQUEST;
        if (!propertyUpdateBean.newValue().isEmpty() && // we accept here empty strings and do not check them in isPresent()
                !mpsGradeService.isPresent(createGradeMock(propertyUpdateBean))) return INACCEPTABLE;
        return super.updateUserProperty(username, propertyUpdateBean);

    }

    private IGradeMock createGradeMock(PropertyUpdateBean propertyUpdateBean) {
        IGradeMock gradeMock = mpsGradeService.getNewGradeMock();
        gradeMock.setID(propertyUpdateBean.ID());
        return gradeMock;
    }

    private boolean isPropertyKeyValid(String pk) {
        return gradePropertyKey.equals(pk);
    }

    /*
    userWithProperties is not always logged in user
     */
    @Override
    protected Response checkPermissions(User userWithProperties) {
        if (userWithProperties != null) {
            if (!userWithProperties.equals(jiraAuthenticationContext.getLoggedInUser())) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

        } else {
            log.debug("Specified user is not exist");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return RESPONSE_OK;
    }
}

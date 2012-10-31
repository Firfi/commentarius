package ru.megaplan.jira.plugin.commentarius.ao.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.security.JiraAuthenticationContext;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.apache.log4j.Logger;
import ru.megaplan.jira.plugin.commentarius.ao.ShabloniusConfigService;
import ru.megaplan.jira.plugin.commentarius.ao.bean.*;
import ru.megaplan.jira.plugin.commentarius.ao.bean.mock.IMPSTemplateMessageMock;
import ru.megaplan.jira.plugin.commentarius.rest.UserTemplateUpdateBean;
import ru.megaplan.jira.plugins.permission.manager.ao.MegaPermissionGroupManager;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.IPermissionGroupMock;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.IPermissionMock;

import java.sql.SQLException;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: i.loskutov
 * Date: 25.05.12
 * Time: 17:08
 * To change this template use File | Settings | File Templates.
 */
public class ShabloniusConfigServiceImpl implements ShabloniusConfigService {

    private final static Logger log = Logger.getLogger(ShabloniusConfigServiceImpl.class);
    private final ActiveObjects ao;
    private final MegaPermissionGroupManager megaPermissionGroupManager;
    private final JiraAuthenticationContext jiraAuthenticationContext;

    private final static String TEMPLATE_PERMISSION_GROUP_NAME = "ru.megaplan.jira.plugin.commentarius.TEMPLATE";

    public ShabloniusConfigServiceImpl(ActiveObjects ao, MegaPermissionGroupManager megaPermissionGroupManager, JiraAuthenticationContext jiraAuthenticationContext)
    {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        log.debug("initializing ShabloniusConfigServiceImpl...");
        this.ao = ao;
        this.megaPermissionGroupManager = megaPermissionGroupManager;
    }
    @Override
    public List<IMPSTemplateMessageMock> getTemplateMessages(String type) {
        final List<IMPSTemplateMessageMock> messages = new ArrayList<IMPSTemplateMessageMock>();
        for (MPSTemplateMessage msg : ao.find(MPSTemplateMessage.class)) {
            if (!msg.getType().equalsIgnoreCase(type)) continue;
            IMPSTemplateMessageMock messageMock = new MPSTemplateMessageMock(msg);
            addPermissionProperty(messageMock, msg);
            messages.add(messageMock);
        }
        return messages;
    }

    private void addPermissionProperty(IMPSTemplateMessageMock messageMock, MPSTemplateMessage msg) {
        int id = msg.getPermissionBean();
        if (id == 0) {
            messageMock.setPermissionMock(null);
            return;
        }
        IPermissionMock permissionMock = megaPermissionGroupManager.getNewPermissionMock();
        permissionMock.setID(id);
        permissionMock = megaPermissionGroupManager.getPermission(permissionMock);
        if (permissionMock == null) {
            msg.setPermissionBean(0);
            msg.save();
        }
        messageMock.setPermissionMock(permissionMock);
    }

    @Override
    public List<IMPSTemplateMessageMock> getAllTemplateMessages() {
        final List<IMPSTemplateMessageMock> messages = new ArrayList<IMPSTemplateMessageMock>();
        for (MPSTemplateMessage msg : ao.find(MPSTemplateMessage.class)) {
            MPSTemplateMessageMock mpsTemplateMessageMock = new MPSTemplateMessageMock(msg);
            addPermissionProperty(mpsTemplateMessageMock, msg);
            if (mpsTemplateMessageMock.getPermissionMock() == null) {
                msg.setPermissionBean(0);
                msg.save();
            }
            messages.add(mpsTemplateMessageMock);
        }
        return messages;
    }

    @Override
    public void addTemplateMessage(IMPSTemplateMessageMock m) {
        checkNotNull(m);
        checkNotNull(m.getSmall());
        checkNotNull(m.getType());
        IPermissionGroupMock pg = megaPermissionGroupManager.getPermissionGroup(TEMPLATE_PERMISSION_GROUP_NAME);
        checkNotNull(pg);
        DBParam small = new DBParam("SMALL", m.getSmall());
        DBParam full = new DBParam("FULL", m.getFull());
        DBParam type = new DBParam("TYPE", m.getType());
        DBParam creator = new DBParam("CREATOR", jiraAuthenticationContext.getLoggedInUser().getName());
       // EntityManager em = EntityManagerFa
        //PermissionBean pb = getPermissionBean(pg, m.getPermissionMock());
        IPermissionMock pm = m.getPermissionMock();
        checkNotNull(pm.getProjectRoleName());
        pm.setPermissionGroupMock(pg);
        IPermissionMock pb = megaPermissionGroupManager.getUniquePermission(pm);
        DBParam permission = new DBParam("PERMISSION_BEAN",pb.getID());
        try {
            MPSTemplateMessage mpsTemplateMessage =
                    ao.create(MPSTemplateMessage.class, small, full, type, permission, creator);
        } catch (Exception e) {
            if (e instanceof SQLException) {
                boolean isPresent = false;
                try {
                    isPresent = isPresent(m);
                } catch (Exception e2) {
                }
                if (!isPresent) throw new RuntimeException(e);
                else log.error("can't create duplicate for entity "
                        + MPSGrade.class.getName()
                        + "; we have some constraints do you know?");
            }
        }
    }





    @Override
    public void deleteTemplateMessage(IMPSTemplateMessageMock m) {
        checkNotNull(m);
        MPSTemplateMessage mpsTemplateMessage = null;
        Set<Integer> deletedPermissions = new HashSet<Integer>();
        if (m.getID() != 0) {
            mpsTemplateMessage = ao.get(MPSTemplateMessage.class,m.getID());
        } else {
            mpsTemplateMessage = selectMessage(m);
        }
        if (mpsTemplateMessage != null) {
            ao.delete(mpsTemplateMessage);
            deletedPermissions.add(mpsTemplateMessage.getPermissionBean());
        }
        else log.error("trying delete not existent template message");
        cleanUnusedPermissionBeans(deletedPermissions);
    }

    private void cleanUnusedPermissionBeans(Set<Integer> permissionBeanIds) {
        for (Integer id : permissionBeanIds) {
            //MPSGrade[] grades = ao.find(MPSGrade.class,
            //        Query.select().where("PERMISSION_BEAN = ?", id).limit(1));
            MPSTemplateMessage[] tmessages = ao.find(MPSTemplateMessage.class, Query.select().where("PERMISSION_BEAN = ?", id).limit(1));
            if (tmessages.length == 0) {
                megaPermissionGroupManager.deletePermission(id);
            }
        }
    }

    @Override
    public List<MPSUserTMessage> getUserTemplateMessages(String username) {
        MPSUserTMessage[] messages = ao.find(MPSUserTMessage.class, Query.select().where("USER_NAME = ?", username).order("NM"));
        return Arrays.asList(messages);
    }

    @Override
    public List<MPSUserTMessage> getAllUserTemplateMessages() {
        MPSUserTMessage[] messages = ao.find(MPSUserTMessage.class, Query.select().order("USER_NAME").order("NM"));
        return Arrays.asList(messages);
    }

    @Override
    public void addUserTemplateMessage(UserTemplateUpdateBean m) {
        MPSUserTMessage[] messages =
            ao.find(MPSUserTMessage.class, Query.select().where("USER_NAME = ? AND NM = ?", m.username(), m.number()));
        if (messages.length > 1) log.error("found more that one messages that assumed to be unique by their number and username");
        for (MPSUserTMessage message : messages) {
            ao.delete(message);
        }
        DBParam un = new DBParam("USER_NAME", m.username());
        DBParam hd = new DBParam("HEADER", m.header());
        DBParam lbl = new DBParam("LABEL", m.label());
        DBParam bd = new DBParam("BODY", m.body());
        DBParam ft = new DBParam("FOOTER", m.footer());
        DBParam nm = new DBParam("NM", m.number());
        try {
            ao.create(MPSUserTMessage.class, un, nm, lbl, hd, bd, ft);
        } catch (Exception e) {
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }

    /*
     * ShabloniusConfigService talks here witn permission group manager but not various actions
     */
    @Override
    public IMPSTemplateMessageMock getNewMessageMock(String type, String small, String full) {
        IPermissionMock permissionMock = megaPermissionGroupManager.getNewPermissionMock();
        IMPSTemplateMessageMock result = new MPSTemplateMessageMock(type, small,full, jiraAuthenticationContext.getLoggedInUser().getName());
        result.setPermissionMock(permissionMock);
        return result;
    }

    @Override
    public IMPSTemplateMessageMock getNewMessageMock() {
        return getNewMessageMock(null, null, null);
    }

    private boolean isPresent(IMPSTemplateMessageMock m) {
        MPSTemplateMessage grade = selectMessage(m);
        return grade != null;
    }


    private MPSTemplateMessage selectMessage(IMPSTemplateMessageMock m) {
        MPSTemplateMessage[] results;
        if (m.getID() != 0) return ao.get(MPSTemplateMessage.class,m.getID());
        else results = ao.find(MPSTemplateMessage.class, Query.select().where("SMALL = ? AND FULL = ? AND TYPE = ?",
                m.getSmall(), m.getFull(), m.getType()));
        if (results.length > 1) throw new RuntimeException("we have more than one results by unique field");
        if (results.length == 0) return null;
        return results[0];
    }

    private static class MPSTemplateMessageMock implements IMPSTemplateMessageMock {


        private String type;
        private String small;
        private String full;


        private IPermissionMock permissionMock;

        private String creator;

        private int ID;

        private MPSTemplateMessageMock(String type, String small, String full, String creator) {
            this.type = type;
            this.small = small;
            this.full = full;
            this.creator = creator;
        }

        private MPSTemplateMessageMock(){}

        private MPSTemplateMessageMock(MPSTemplateMessage mg) {
            type = mg.getType();
            small = mg.getSmall();
            full = mg.getFull();
            creator = mg.getCreator();
            ID = mg.getID();
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String getSmall() {
            return small;
        }

        @Override
        public void setSmall(String small) {
            this.small = small;
        }

        @Override
        public String getFull() {
            return full;
        }

        @Override
        public void setFull(String full) {
            this.full = full;
        }

        @Override
        public IPermissionMock getPermissionMock() {
            return permissionMock;
        }

        @Override
        public void setPermissionMock(IPermissionMock permissionMock) {
            this.permissionMock = permissionMock;
        }

        @Override
        public int getID() {
            return ID;
        }

        @Override
        public void setID(int ID) {
            this.ID = ID;
        }

        public String getCreator() {
            return creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }


    }


}

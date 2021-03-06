package ru.megaplan.jira.plugin.commentarius.ao.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.security.JiraAuthenticationContext;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.apache.log4j.Logger;
import ru.megaplan.jira.plugin.commentarius.action.admin.AddTemplateAction;
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
    private final JiraAuthenticationContext jiraAuthenticationContext;

    public ShabloniusConfigServiceImpl(ActiveObjects ao, JiraAuthenticationContext jiraAuthenticationContext)
    {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        log.debug("initializing ShabloniusConfigServiceImpl...");
        this.ao = ao;
    }
    @Override
    public List<IMPSTemplateMessageMock> getTemplateMessages(String type) {
        final List<IMPSTemplateMessageMock> messages = new ArrayList<IMPSTemplateMessageMock>();
        for (MPSTemplateMessage msg : ao.find(MPSTemplateMessage.class)) {
            if (!msg.getType().equalsIgnoreCase(type)) continue;
            IMPSTemplateMessageMock messageMock = new MPSTemplateMessageMock(msg);
            messages.add(messageMock);
        }
        return messages;
    }

    @Override
    public List<IMPSTemplateMessageMock> getAllTemplateMessages() {
        final List<IMPSTemplateMessageMock> messages = new ArrayList<IMPSTemplateMessageMock>();
        for (MPSTemplateMessage msg : ao.find(MPSTemplateMessage.class)) {
            MPSTemplateMessageMock mpsTemplateMessageMock = new MPSTemplateMessageMock(msg);
            messages.add(mpsTemplateMessageMock);
        }
        return messages;
    }

    @Override
    public void addTemplateMessage(IMPSTemplateMessageMock m) {
        checkNotNull(m);
        checkNotNull(m.getSmall());
        checkNotNull(m.getType());
        DBParam small = new DBParam("SMALL", m.getSmall());
        DBParam full = new DBParam("FULL", m.getFull());
        DBParam type = new DBParam("TYPE", m.getType());
        DBParam creator = new DBParam("CREATOR", jiraAuthenticationContext.getLoggedInUser().getName());
        DBParam role = new DBParam("ROLES", m.getRoles());
       // EntityManager em = EntityManagerFa
        //PermissionBean pb = getPermissionBean(pg, m.getPermissionMock());
        try {
            ao.create(MPSTemplateMessage.class, small, full, type, creator, role);
        } catch (Exception e) {
            log.error("e",e);
            if (e instanceof SQLException) {
                boolean isPresent = false;
                try {
                    isPresent = isPresent(m);
                } catch (Exception e2) {
                    log.error("e", e2);
                }
                if (!isPresent) throw new RuntimeException(e);
                else log.error("can't create duplicate for entity "
                        + MPSGrade.class.getName()
                        + "; we have some constraints do you know?");
            }
        }
    }

    @Override
    public void updateTemplateMessage(IMPSTemplateMessageMock m) {
        MPSTemplateMessage message = ao.get(MPSTemplateMessage.class, m.getID());
        message.setCreator(m.getCreator());
        message.setFull(m.getFull());
        message.setSmall(m.getSmall());
        message.setType(m.getType());
        message.setRoles(m.getRoles());
        message.save();
    }


    @Override
    public void deleteTemplateMessage(IMPSTemplateMessageMock m) {
        checkNotNull(m);
        MPSTemplateMessage mpsTemplateMessage = null;
        if (m.getID() != 0) {
            mpsTemplateMessage = ao.get(MPSTemplateMessage.class,m.getID());
        } else {
            mpsTemplateMessage = selectMessage(m);
        }
        if (mpsTemplateMessage != null) {
            ao.delete(mpsTemplateMessage);
        }
        else log.error("trying delete not existent template message");
    }

    @Override
    public IMPSTemplateMessageMock getTemplateMessage(int id) {
        MPSTemplateMessage message = ao.get(MPSTemplateMessage.class, id);
        return new MPSTemplateMessageMock(message);
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

    @Override
    public IMPSTemplateMessageMock getNewMessageMock(String type, String small, String full, String roles) {
        return new MPSTemplateMessageMock(type, small,full, jiraAuthenticationContext.getLoggedInUser().getName(), roles);
    }

    @Override
    public IMPSTemplateMessageMock getNewMessageMock() {
        return new MPSTemplateMessageMock();
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

        private String creator;

        private String roles;

        private int ID;

        private MPSTemplateMessageMock(String type, String small, String full, String creator, String roles) {
            this.type = type;
            this.small = small;
            this.full = full;
            this.creator = creator;
            this.roles = roles;
        }

        private MPSTemplateMessageMock(){}

        private MPSTemplateMessageMock(MPSTemplateMessage mg) {
            type = mg.getType();
            small = mg.getSmall();
            full = mg.getFull();
            creator = mg.getCreator();
            ID = mg.getID();
            roles = mg.getRoles();
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

        public String getRoles() {
            return roles;
        }

        public void setRoles(String role) {
            this.roles = role;
        }
    }


}

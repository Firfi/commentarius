package it.ru.megaplan.jira.plugin.commentarius;

import com.atlassian.jira.functest.framework.FuncTestCase;
import org.junit.Test;

public class CommentariusTest extends FuncTestCase
{

   @Override
    protected void setUpTest() {
        super.setUpTest();
        administration.restoreData("entities.xml"); //that is example of importing xml configuration
    }

    @Test
    public void testSomething() {
        log.log("!!!!!!!!!!!!!FUNC TEST STUB HERE!!!!!!!!!!!!");
    }

}

package com.redhat.summit2015.ceplab;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.drools.core.ClockType;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;

import com.redhat.summit2015.ceplab.model.Account;
import com.redhat.summit2015.ceplab.model.Transaction;

/**
 * Unit test for simple App.
 */
public class Lab2Test extends TestCase {

    KieSession kSession;
    
    @Before
    public void setUp(){
        // load up the knowledge base
        KieServices ks = KieServices.Factory.get();
        
        KieContainer kContainer = ks.getKieClasspathContainer();
//        Results results = kContainer.verify();
//        assertFalse("there shouldn't be any errors", results.hasMessages(Level.ERROR));
//        
//        KieSessionConfiguration kSessionConfig = KieServices.Factory.get().newKieSessionConfiguration();
//        kSessionConfig.setOption( ClockTypeOption.get( "pseudo" ) );
//        
        kSession = kContainer.newKieSession("ksession1");

        ClockTypeOption clockType = kSession.getSessionConfiguration().getOption( ClockTypeOption.class );
        assertSame( clockType.getClockType(), ClockType.PSEUDO_CLOCK.toString());

        assertNotNull("kSession should be instantiated and set to member variable", kSession);
    }
    
    @Test
    public void testSomething(){
        assertNotNull("kSession should be instantiated", kSession);
        
        Account fromAccount = new Account();
        Account toAccount = new Account();
        
        Transaction transaction1 = new Transaction(fromAccount, toAccount, BigDecimal.valueOf(200));
        Transaction transaction2 = new Transaction(fromAccount, toAccount, BigDecimal.valueOf(200));

        kSession.insert(transaction1);
        kSession.fireAllRules();
        
        fail("not implemented");
    }

}

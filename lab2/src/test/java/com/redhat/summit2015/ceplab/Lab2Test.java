package com.redhat.summit2015.ceplab;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.drools.core.ClockType;
import org.drools.core.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.EntryPoint;

import com.redhat.summit2015.ceplab.model.Account;
import com.redhat.summit2015.ceplab.model.AccountStatus;
import com.redhat.summit2015.ceplab.model.Transaction;
import com.redhat.summit2015.ceplab.model.TransactionStatus;

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
        kSession = kContainer.newKieSession();

        ClockTypeOption clockType = kSession.getSessionConfiguration().getOption( ClockTypeOption.class );
        assertSame( clockType.getClockType(), ClockType.PSEUDO_CLOCK.toString());
        
        assertNotNull("kSession should be instantiated and set to member variable", kSession);
    }
    
    @After
    public void tearDown(){
    	kSession.dispose();
    	kSession.destroy();
    }
    
    @Test
    public void testSomething(){
        assertNotNull("kSession should be instantiated", kSession);
        
        SessionPseudoClock clock = kSession.getSessionClock();
        assertNotNull("SessionClock should not be null", clock);
        
        EntryPoint entryPoint = kSession.getEntryPoint("Transfers");
        assertNotNull("EntryPoint should not be null", entryPoint);


        Account fromAccount = new Account();
        Account toAccount = new Account();
        
        Transaction transaction1 = new Transaction(fromAccount, toAccount, BigDecimal.valueOf(200));
        Transaction transaction2 = new Transaction(fromAccount, toAccount, BigDecimal.valueOf(200));

        entryPoint.insert(transaction1);
        
        clock.advanceTime(5, TimeUnit.SECONDS);
        
        entryPoint.insert(transaction2);

        kSession.fireAllRules();
        
        assertSame("the second transaction should be marked as a duplicate", TransactionStatus.DUPLICATE, transaction2.getStatus());
        System.out.println(transaction2);
    }
    
    @Test
    public void test15SecondRule(){
        assertNotNull("kSession should be instantiated", kSession);
        
        SessionPseudoClock clock = kSession.getSessionClock();
        assertNotNull("SessionClock should not be null", clock);

        EntryPoint entryPoint = kSession.getEntryPoint("Transfers");

        Account fromAccount = new Account(AccountStatus.ACTIVE, BigDecimal.valueOf(10000));
        Account toAccount = new Account(AccountStatus.ACTIVE, BigDecimal.valueOf(50000));
        
        Transaction t1 = new Transaction(fromAccount, toAccount, BigDecimal.valueOf(200));
        Transaction t2 = new Transaction(fromAccount, toAccount, BigDecimal.valueOf(300));

        System.out.println(t1);
        System.out.println(t2);
        
        entryPoint.insert(new Transaction(fromAccount, toAccount, BigDecimal.valueOf(200)));
        
        clock.advanceTime(30, TimeUnit.SECONDS);
        
        entryPoint.insert(t2);
        kSession.fireAllRules();
        
        System.out.println(t1);
        System.out.println(t2);

        assertNotSame("the second transaction should not be marked as a duplicate", TransactionStatus.DUPLICATE, t2.getStatus());
    }

}

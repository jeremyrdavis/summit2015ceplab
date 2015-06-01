package com.redhat.summit2015.ceplab;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.drools.core.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;

import com.redhat.summit2015.ceplab.model.Account;
import com.redhat.summit2015.ceplab.model.AccountStatus;
import com.redhat.summit2015.ceplab.model.Transaction;
import com.redhat.summit2015.ceplab.model.TransactionStatus;

/**
 * Unit test for simple App.
 */
public class Lab5Test extends TestCase {

    KieSession kSession;
    EntryPoint entryPoint;
    SessionPseudoClock clock;
    
    @Before
    public void setUp(){
        // load up the knowledge base
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();

        kSession = kContainer.newKieSession("ksession1");
        assertNotNull("kSession should be instantiated and set to member variable", kSession);
        
        entryPoint = kSession.getEntryPoint("CreditCard");
        assertNotNull("EntryPoint shouldn't be null", entryPoint);
        
        clock = kSession.getSessionClock();
        assertNotNull("SessionClock shouldn't be null", clock);
        
    }

    @After
    public void tearDown(){
        kSession.dispose();
        kSession.destroy();
    }

    
    @Test
    public void testSlidingWindowsOverTime(){
        assertNotNull("kSession should be instantiated", kSession);
        Account fromAccount = new Account(AccountStatus.ACTIVE, BigDecimal.valueOf(5000));
        
        Transaction t1 = new Transaction(fromAccount, new BigDecimal("10"));
        Transaction t2 = new Transaction(fromAccount, new BigDecimal("10"));
        Transaction t3 = new Transaction(fromAccount, new BigDecimal("10"));
        Transaction t4 = new Transaction(fromAccount, new BigDecimal("10"));
        Transaction t5 = new Transaction(fromAccount, new BigDecimal("10"));
        Transaction t6 = new Transaction(fromAccount, new BigDecimal("10"));
        Transaction t7 = new Transaction(fromAccount, new BigDecimal("10"));
        Transaction t8 = new Transaction(fromAccount, new BigDecimal("10"));
        
        
        Transaction suspiciousTransaction = new Transaction(fromAccount, new BigDecimal("100"));
        
        // enter a month's worth of transactions
        entryPoint.insert(t1);
        clock.advanceTime(1, TimeUnit.DAYS);
        entryPoint.insert(t2);
        clock.advanceTime(3, TimeUnit.DAYS);
        entryPoint.insert(t3);
        clock.advanceTime(3, TimeUnit.DAYS);
        entryPoint.insert(t4);
        clock.advanceTime(1, TimeUnit.DAYS);
        entryPoint.insert(t5);
        clock.advanceTime(5, TimeUnit.DAYS);
        entryPoint.insert(t6);
        clock.advanceTime(5, TimeUnit.DAYS);
        entryPoint.insert(t7);
        clock.advanceTime(5, TimeUnit.DAYS);
        entryPoint.insert(t8);
        clock.advanceTime(5, TimeUnit.DAYS);

        
        entryPoint.insert(suspiciousTransaction);
        
        kSession.fireAllRules();
        
        assertNotSame("the first transaction should not be denied", t1.getStatus(), TransactionStatus.DENIED);
        assertNotSame("the second transaction should not be denied", t2.getStatus(), TransactionStatus.DENIED);
        assertNotSame("the third transaction should not be denied", t3.getStatus(), TransactionStatus.DENIED);
        assertNotSame("the fourth transaction should not be denied", t4.getStatus(), TransactionStatus.DENIED);

        // t5 is denied because it is twice the medium of last 4 transactions (t1, t2, t3, t4)
        assertSame("the fifth withdrawl should be denied", suspiciousTransaction.getStatus(), TransactionStatus.DENIED);
    }

}

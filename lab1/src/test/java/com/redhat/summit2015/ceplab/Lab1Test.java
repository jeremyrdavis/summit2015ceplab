package com.redhat.summit2015.ceplab;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.junit.Before;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.redhat.summit2015.ceplab.model.Account;
import com.redhat.summit2015.ceplab.model.AccountStatus;


/**
 * Unit test for simple App.
 */
public class Lab1Test extends TestCase {

    KieSession kieSession;
    
    @Before
    public void setUp(){
        // load up the knowledge base
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        kieSession = kContainer.newKieSession();
        assertNotNull("kSession should be instantiated and set to member variable", kieSession);
    }

    @org.junit.Test
    public void testZeroBalanceRule(){
        assertNotNull("kSession should still be instantiated", kieSession);
        
        // create an account
        Account emptyAccount = new Account(AccountStatus.ACTIVE, BigDecimal.ZERO);
        Account fullAccount = new Account(AccountStatus.ACTIVE, BigDecimal.valueOf(200));
        
        kieSession.insert(emptyAccount);
        kieSession.insert(fullAccount);
        kieSession.fireAllRules();
        assertSame("emptyAccount should be blocked", emptyAccount.getStatus(), AccountStatus.TERMINATED);
        assertSame("fullAccount should be active", fullAccount.getStatus(), AccountStatus.ACTIVE);
    }

}

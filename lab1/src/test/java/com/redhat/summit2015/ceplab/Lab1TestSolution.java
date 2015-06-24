package com.redhat.summit2015.ceplab;

import java.math.BigDecimal;

import org.junit.Before;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.redhat.summit2015.ceplab.model.Account;
import com.redhat.summit2015.ceplab.model.AccountStatus;

import junit.framework.TestCase;

public class Lab1TestSolution extends TestCase{
	
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
    public void testBalanceBetween1and100(){
        assertNotNull("kSession should still be instantiated", kieSession);
        
        // create an account
        Account accountToBeBlocked = new Account(AccountStatus.ACTIVE, BigDecimal.valueOf(100));
        Account fullAccount = new Account(AccountStatus.ACTIVE, BigDecimal.valueOf(200));
        
        kieSession.insert(accountToBeBlocked);
        kieSession.insert(fullAccount);
        kieSession.fireAllRules();
        assertSame("accountToBeBlocked should be blocked", accountToBeBlocked.getStatus(), AccountStatus.BLOCKED);
        assertSame("fullAccount should be active", fullAccount.getStatus(), AccountStatus.ACTIVE);
    }



}

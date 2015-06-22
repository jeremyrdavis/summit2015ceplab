package com.redhat.summit2015.ceplab;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.rule.EntryPoint;

import com.redhat.summit2015.ceplab.model.Account;
import com.redhat.summit2015.ceplab.model.AccountStatus;
import com.redhat.summit2015.ceplab.model.Transaction;
import com.redhat.summit2015.ceplab.model.TransactionStatus;

/**
 * Unit test for simple App.
 */
public class Lab4Test extends BaseCEPTestCase{

	@Before
	public void setUp() {
		
		setDrls("rules/lab4rules.drl");
		setEntryPointName("CreditCard");
		super.setUp();
	}
    
    @Test
    public void testSuspiciousTransactionIsDenied(){
        assertNotNull("kSession should be instantiated", kSession);
        kSession.fireAllRules();

        Account fromAccount = new Account(AccountStatus.ACTIVE, BigDecimal.valueOf(5000));
        
        Transaction t1 = new Transaction(fromAccount, new BigDecimal("20"));
        Transaction t2 = new Transaction(fromAccount, new BigDecimal("20"));
        Transaction t3 = new Transaction(fromAccount, new BigDecimal("20"));
        Transaction t4 = new Transaction(fromAccount, new BigDecimal("20"));
        Transaction t5 = new Transaction(fromAccount, new BigDecimal("100"));
        entryPoint.insert(t1);
        clock.advanceTime(1, TimeUnit.SECONDS);
        entryPoint.insert(t2);
        clock.advanceTime(1, TimeUnit.SECONDS);
        entryPoint.insert(t3);
        clock.advanceTime(1, TimeUnit.SECONDS);
        entryPoint.insert(t4);
        clock.advanceTime(1, TimeUnit.SECONDS);
        entryPoint.insert(t5);
        kSession.fireAllRules();
        
        assertNotSame("the first transaction should not be denied", t1.getStatus(), TransactionStatus.DENIED);
        assertNotSame("the second transaction should not be denied", t2.getStatus(), TransactionStatus.DENIED);
        assertNotSame("the third transaction should not be denied", t3.getStatus(), TransactionStatus.DENIED);
        assertNotSame("the fourth transaction should not be denied", t4.getStatus(), TransactionStatus.DENIED);

        // t5 is denied because it is twice the medium of last 4 transactions (t1, t2, t3, t4)
        assertSame("the fifth withdrawl should be denied", t5.getStatus(), TransactionStatus.DENIED);
    }
    
}

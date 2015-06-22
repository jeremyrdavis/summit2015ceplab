package com.redhat.summit2015.ceplab;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.summit2015.ceplab.model.Account;
import com.redhat.summit2015.ceplab.model.AccountStatus;
import com.redhat.summit2015.ceplab.model.Transaction;
import com.redhat.summit2015.ceplab.model.TransactionStatus;

/**
 * Solution for the Lab2 Activity
 * 
 * @author jeremy.davis@redhat.com
 *
 */
public class Lab2TestSolution extends BaseCEPTestCase{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Lab2Test.class);

	@Override
	public void setUp() {
		setDrls("rules/lab2solution.drl");
		setEntryPointName("Transfers");
		super.setUp();
	}
	
	@Test
	public void test1DayRule(){
		Account fromAccount = new Account(AccountStatus.ACTIVE,
				BigDecimal.valueOf(10000));
		Account toAccount = new Account(AccountStatus.ACTIVE,
				BigDecimal.valueOf(50000));

		Transaction t1 = new Transaction(fromAccount, toAccount,
				BigDecimal.valueOf(200));
		Transaction t2 = new Transaction(fromAccount, toAccount,
				BigDecimal.valueOf(300));

		entryPoint.insert(new Transaction(fromAccount, toAccount, BigDecimal
				.valueOf(200)));
		clock.advanceTime(1, TimeUnit.DAYS);
		entryPoint.insert(t2);
		kSession.fireAllRules();
		assertSame(
				"the second transaction should be marked as suspicious",
				TransactionStatus.SUSPICIOUS, t2.getStatus());
	}


}

package com.redhat.summit2015.ceplab;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.kie.api.runtime.rule.EntryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.summit2015.ceplab.model.Account;
import com.redhat.summit2015.ceplab.model.AccountStatus;
import com.redhat.summit2015.ceplab.model.Transaction;
import com.redhat.summit2015.ceplab.model.TransactionStatus;

/**
 * Base TestCase that contains relevant member variables and handles the
 * boilerplate for building KieSessions
 * 
 * @author jeremy.davis@redhat.com
 */
public class Lab2Test extends BaseCEPTestCase {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Lab2Test.class);

	@Override
	public void setUp() {
		setDrls("rules/lab2rules.drl");
		setEntryPointName("Transfers");
		super.setUp();
	}

	@Test
	public void test15SecondRuleCatchesTransactionsWithinWindow() {
		Account fromAccount = new Account();
		Account toAccount = new Account();

		Transaction transaction1 = new Transaction(fromAccount, toAccount,
				BigDecimal.valueOf(200));
		Transaction transaction2 = new Transaction(fromAccount, toAccount,
				BigDecimal.valueOf(200));

		entryPoint.insert(transaction1);

		clock.advanceTime(5, TimeUnit.SECONDS);

		entryPoint.insert(transaction2);

		kSession.fireAllRules();

		assertSame("the second transaction should be marked as a duplicate",
				TransactionStatus.DUPLICATE, transaction2.getStatus());
		System.out.println(transaction2);
	}

	@Test
	public void test15SecondRuleDoesNotCatchTransactionsOutsideWindow() {
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
		clock.advanceTime(30, TimeUnit.SECONDS);
		entryPoint.insert(t2);
		kSession.fireAllRules();

		assertNotSame(
				"the second transaction should not be marked as a duplicate",
				TransactionStatus.DUPLICATE, t2.getStatus());
	}
	
}

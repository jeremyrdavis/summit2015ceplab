package com.redhat.summit2015.ceplab;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.summit2015.ceplab.model.Account;
import com.redhat.summit2015.ceplab.model.AccountStatus;
import com.redhat.summit2015.ceplab.model.Transaction;

/**
 * Unit test for simple App.
 * @see org.kie.api.KieServices, org.kie.api.runtime.rule.EntryPoint
 */
public class Lab3Test extends BaseCEPTestCase {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Lab3Test.class);

	@Before
	public void setUp() {
		setDrls("rules/lab3rules.drl");
		setEntryPointName("Withdrawls");
		super.setUp();
	}

	@Test
	public void testFraudulentWithdrawls() throws InstantiationException, IllegalAccessException {
		Account account = new Account(AccountStatus.ACTIVE, BigDecimal.valueOf(10000));
		Transaction withdrawl1 = new Transaction(account, BigDecimal.valueOf(400));
		Transaction withdrawl2 = new Transaction(account, BigDecimal.valueOf(400));
		Transaction withdrawl3 = new Transaction(account, BigDecimal.valueOf(150));

		FactType accountInfoFactType = kSession.getKieBase().getFactType("com.redhat.summit2015.ceplab", "AccountInfo");
		Object accountInfo = accountInfoFactType.newInstance();
		accountInfoFactType.set(accountInfo, "averageBalance",
				BigDecimal.valueOf(1000));
		accountInfoFactType.set(accountInfo, "id", account.getId());
		FactHandle accountInfoHandle = kSession.insert(accountInfoFactType);
		kSession.update(accountInfoHandle, accountInfo);		

		kSession.insert(account);
		entryPoint.insert(withdrawl1);
		entryPoint.insert(withdrawl2);
		entryPoint.insert(withdrawl3);
		

		kSession.fireAllRules();

		assertSame("Account should be blocked", account.getStatus(), AccountStatus.BLOCKED);
		
	}
	
}

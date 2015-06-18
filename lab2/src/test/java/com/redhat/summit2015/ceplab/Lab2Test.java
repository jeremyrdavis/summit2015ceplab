package com.redhat.summit2015.ceplab;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.drools.core.ClockType;
import org.drools.core.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.model.KieSessionModel.KieSessionType;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.summit2015.ceplab.model.Account;
import com.redhat.summit2015.ceplab.model.AccountStatus;
import com.redhat.summit2015.ceplab.model.Transaction;
import com.redhat.summit2015.ceplab.model.TransactionStatus;

/**
 * Unit test for simple App.
 */
public class Lab2Test extends TestCase {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Lab2Test.class);

	KieSession kSession;

	@Before
	public void setUp() {

		KieServices kieServices = KieServices.Factory.get();
		KieModuleModel kieModuleModel = kieServices.newKieModuleModel();

		KieBaseModel kieBaseModel = kieModuleModel
				.newKieBaseModel("lab2KieBase").setDefault(true)
				.setEventProcessingMode(EventProcessingOption.STREAM);
		KieSessionModel kieSessionModel = kieBaseModel
				.newKieSessionModel("lab2KieSession")
				.setClockType(ClockTypeOption.get("pseudo"))
				.setType(KieSessionType.STATEFUL).setDefault(true);
		

		KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
		kieFileSystem.write(ResourceFactory
				.newClassPathResource("rules/transaction1.drl"));
		kieFileSystem.writeKModuleXML(kieModuleModel.toXML());

		KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
		kieBuilder.buildAll();

		List<Message> errorMessages = kieBuilder.getResults().getMessages(
				Level.ERROR);
		for (Message m : errorMessages) {
			System.out.println(m);
		}
		assertEquals("There shouldn't be any errors", kieBuilder.getResults()
				.getMessages(Level.ERROR).size(), 0);

		ReleaseId releaseId = kieBuilder.getKieModule().getReleaseId();
		
		KieContainer kcontainer = kieServices.newKieContainer(releaseId);

		KieBase kieBase = kcontainer.getKieBase("lab2KieBase");

		// Configure and create the KieSession
		kSession = kcontainer.newKieSession("lab2KieSession");

		// KieBaseConfiguration kieBaseConfiguration =
		// kieServices.newKieBaseConfiguration();
		// KieBase kieBase = kcontainer.newKieBase(kieBaseConfiguration);
		//
		// KieSessionConfiguration kieSessionConfiguration =
		// kieServices.newKieSessionConfiguration();
		// kieSessionConfiguration.set

		// KieContainer kContainer = ks.getKieClasspathContainer();
		// kSession = kContainer.newKieSession();

		ClockTypeOption clockType = kSession.getSessionConfiguration()
				.getOption(ClockTypeOption.class);
		assertSame(clockType.getClockType(), ClockType.PSEUDO_CLOCK.toString());

		assertNotNull(
				"kSession should be instantiated and set to member variable",
				kSession);
	}

	@After
	public void tearDown() {
		kSession.dispose();
		kSession.destroy();
	}

	@Test
	public void testSomething() {
		assertNotNull("kSession should be instantiated", kSession);

		SessionPseudoClock clock = kSession.getSessionClock();
		assertNotNull("SessionClock should not be null", clock);

		EntryPoint entryPoint = kSession.getEntryPoint("Transfers");
		assertNotNull("EntryPoint should not be null", entryPoint);

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
	public void test15SecondRule() {
		assertNotNull("kSession should be instantiated", kSession);

		SessionPseudoClock clock = kSession.getSessionClock();
		assertNotNull("SessionClock should not be null", clock);

		EntryPoint entryPoint = kSession.getEntryPoint("Transfers");

		Account fromAccount = new Account(AccountStatus.ACTIVE,
				BigDecimal.valueOf(10000));
		Account toAccount = new Account(AccountStatus.ACTIVE,
				BigDecimal.valueOf(50000));

		Transaction t1 = new Transaction(fromAccount, toAccount,
				BigDecimal.valueOf(200));
		Transaction t2 = new Transaction(fromAccount, toAccount,
				BigDecimal.valueOf(300));

		System.out.println(t1);
		System.out.println(t2);

		entryPoint.insert(new Transaction(fromAccount, toAccount, BigDecimal
				.valueOf(200)));

		clock.advanceTime(30, TimeUnit.SECONDS);

		entryPoint.insert(t2);
		kSession.fireAllRules();

		System.out.println(t1);
		System.out.println(t2);

		assertNotSame(
				"the second transaction should not be marked as a duplicate",
				TransactionStatus.DUPLICATE, t2.getStatus());
	}

}

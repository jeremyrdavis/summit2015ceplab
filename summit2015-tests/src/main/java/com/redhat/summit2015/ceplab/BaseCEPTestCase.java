package com.redhat.summit2015.ceplab;

import java.util.List;

import junit.framework.TestCase;

import org.drools.core.ClockType;
import org.drools.core.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Before;
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
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.io.ResourceFactory;

/**
 * Base class that wraps the boilerplate needed for testing the workshop code.
 * 
 * @author jeremy.davis@redhat.com
 *
 */
public abstract class BaseCEPTestCase extends TestCase {

	public BaseCEPTestCase() {
		super();
	}

	public BaseCEPTestCase(String drlFiles) {
		super();
		this.drls = drlFiles;
	}

	protected String kieSessionName = "kieSession";

	protected String kieBaseName = "kieBase";

	protected String drls;

	protected KieSession kSession;

	protected SessionPseudoClock clock;

	/**
	 * Runs before each test methods (methods annotated with @Test) and sets up
	 * the test environment.
	 */
	@Before
	public void setUp() {

		// make sure we have rules files
		assertNotNull("Inherited member variable 'drls' cannot be null.  This holds the name of the rules file to execute", drls);
		assertNotSame("Inherited member variable 'drls' cannot be empty.  This holds the name of the rules file to execute", drls.length(), 0);

		// 
		KieServices kieServices = KieServices.Factory.get();
		KieModuleModel kieModuleModel = kieServices.newKieModuleModel();

		KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(kieBaseName)
				.setDefault(true)
				.setEventProcessingMode(EventProcessingOption.STREAM);
		KieSessionModel kieSessionModel = kieBaseModel
				.newKieSessionModel(kieSessionName)
				.setClockType(ClockTypeOption.get("pseudo"))
				.setType(KieSessionType.STATEFUL).setDefault(true);

		KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
		kieFileSystem.write(ResourceFactory.newClassPathResource(drls));
		kieFileSystem.writeKModuleXML(kieModuleModel.toXML());

		KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
		kieBuilder.buildAll();

		List<Message> errorMessages = kieBuilder.getResults().getMessages(
				Level.ERROR);
		for (Message m : errorMessages) {
			System.out.println(m);
		}

		assertEquals("There shouldn't be any errors when building the rules",
				kieBuilder.getResults().getMessages(Level.ERROR).size(), 0);

		ReleaseId releaseId = kieBuilder.getKieModule().getReleaseId();

		KieContainer kcontainer = kieServices.newKieContainer(releaseId);

		// Configure and create the KieSession
		kSession = kcontainer.newKieSession(kieSessionName);

		ClockTypeOption clockType = kSession.getSessionConfiguration()
				.getOption(ClockTypeOption.class);
		assertSame(clockType.getClockType(), ClockType.PSEUDO_CLOCK.toString());

		assertNotNull(
				"KieSession should be instantiated and set to member variable",
				kSession);

		// we need to get the clock to manipulate in the test
		clock = kSession.getSessionClock();
		assertNotNull("SessionClock should not be null", clock);
	}

	@After
	public void tearDown() {
		kSession.dispose();
		kSession.destroy();
	}

	// --------------------------------------------------------------------------
	// generated getters and setters
	// --------------------------------------------------------------------------

	public String getKieSessionName() {
		return kieSessionName;
	}

	public void setKieSessionName(String kieSessionName) {
		this.kieSessionName = kieSessionName;
	}

	public String getKieBaseName() {
		return kieBaseName;
	}

	public void setKieBaseName(String kieBaseName) {
		this.kieBaseName = kieBaseName;
	}

	public String getDrls() {
		return drls;
	}

	public void setDrls(String drls) {
		this.drls = drls;
	}

	public KieSession getkSession() {
		return kSession;
	}

	public void setkSession(KieSession kSession) {
		this.kSession = kSession;

	}
}

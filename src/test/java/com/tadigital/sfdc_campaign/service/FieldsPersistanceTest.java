package com.tadigital.sfdc_campaign.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.tadigital.sfdc_campaign.model.ACSConfig;
import com.tadigital.sfdc_campaign.model.SFDCConfig;
import com.tadigital.sfdc_campaign.repo.FieldsRepo;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FieldsPersistanceTest {
	
	@MockBean
	private FieldsRepo fieldRepo;
	
	@Autowired
	FieldsPersistance fieldsPersistance;
	
    static HttpGet mockHttpGet;
    
    static HttpClient mockHttpClient;
    
    static HttpResponse mockHttpResponse;
	
	static ACSConfig mockacsConfig;
	
	static SFDCConfig mocksalesConfig;
	
	static ACSConfig mockacsEncryptedConfig;
	
	
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		mockacsEncryptedConfig = new ACSConfig();
		mockacsEncryptedConfig.setClientId("NTVjY2NmMDkyYTQxNDM1NTk3NTlmYjdkMjIxMWE0MTY=");
		mockacsEncryptedConfig.setClientSecret("NWM0NTM3OTMtNTI4YS00Mzc5LThjZTUtMDFhYjQxNzQwOTBi");
		mockacsEncryptedConfig.setOrganizationId("ODU2RjVCREU1OEMxNThBNTBBNDk1RDUwQEFkb2JlT3Jn");
		mockacsEncryptedConfig.setTechAccountId("QzU1QzM3Mjc1QkVBOTdFQjBBNDk1Q0QwQHRlY2hhY2N0LmFkb2JlLmNvbQ==");
		mockacsEncryptedConfig.setConfigname("testacs");
		mockacsEncryptedConfig.setUserid(1);
		mockacsEncryptedConfig.setCheckstatus("Active");
		mockacsEncryptedConfig.setConfigId(1l);
		
		
		
		mockacsConfig = new ACSConfig();
		mockacsConfig.setClientId("55cccf092a4143559759fb7d2211a416");
		mockacsConfig.setClientSecret("5c453793-528a-4379-8ce5-01ab4174090b");
		mockacsConfig.setOrganizationId("856F5BDE58C158A50A495D50@AdobeOrg");
		mockacsConfig.setTechAccountId("C55C37275BEA97EB0A495CD0@techacct.adobe.com");
		mockacsConfig.setConfigname("testacs");
		mockacsConfig.setUserid(1);
		mockacsConfig.setCheckstatus("Active");
		
		
		mocksalesConfig = new SFDCConfig();
		mocksalesConfig.setClientId("3MVG9YDQS5WtC11pmgCt8QmG_6bzIPSNcYox0XO1R9WhOshqNvhfNsSVjrrlFuSKfcl8wQ0_w8xbHu3DswTOA");
		mocksalesConfig.setClientSecret("1363638882656468784");
		mocksalesConfig.setUserName("nivedhag@tadigital.com");
		mocksalesConfig.setSfdcPassword("Techaspect@02");
		mocksalesConfig.setSecretToken("4IAwnn5pYx69ZXlwXS4Lg7wsn");
		mocksalesConfig.setSyncType("Full Sync");
		mocksalesConfig.setCheckstatus("Active");
		mocksalesConfig.setUserid(1);
		mocksalesConfig.setConfigname("testsales");	
		
		mockHttpResponse = Mockito.mock(HttpResponse.class);
		mockHttpClient = Mockito.mock(HttpClient.class);
		mockHttpGet = Mockito.mock(HttpGet.class);
	}
	

	@Test
	public void testSaveCampaignFields() throws Exception {
			
		fieldsPersistance.getAndSaveCampaignFields(mockacsEncryptedConfig);
	
	}

}

/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tadigital.sfdc_campaign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.tadigital.sfdc_campaign.utils.delfile;


/**
 * @author nivedha.g
 *
 */

@ComponentScan(basePackages = { "com.tadigital.sfdc_campaign" })
@SpringBootConfiguration
@EnableAutoConfiguration
@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = { "com.tadigital.sfdc_campaign.model" })
public class SFDCCampaignApp {

	public static void main(String[] args) throws Exception {
		delfile.delete("logs/app.log");
		SpringApplication.run(SFDCCampaignApp.class, args);
	}

}

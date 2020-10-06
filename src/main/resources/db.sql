CREATE TABLE sfdcacs.`acs_config` (
  `idacs_config` int(11) NOT NULL AUTO_INCREMENT,
  `clientid` varchar(150) DEFAULT NULL,
  `client_secret` varchar(150) DEFAULT NULL,
  `organization_id` varchar(100) DEFAULT NULL,
  `tech_account_id` varchar(150) DEFAULT NULL,
  `userid` int(11) DEFAULT NULL,
  `checkstatus` varchar(45) DEFAULT NULL,
  `config_name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idacs_config`)
);


INSERT INTO sfdcacs.`acs_config` VALUES (1,'NTVjY2NmMDkyYTQxNDM1NTk3NTlmYjdkMjIxMWE0MTY=','NWM0NTM3OTMtNTI4YS00Mzc5LThjZTUtMDFhYjQxNzQwOTBi','ODU2RjVCREU1OEMxNThBNTBBNDk1RDUwQEFkb2JlT3Jn','QzU1QzM3Mjc1QkVBOTdFQjBBNDk1Q0QwQHRlY2hhY2N0LmFkb2JlLmNvbQ==',1,'Active','first');

CREATE TABLE sfdcacs.`sfdc_config` (
  `idsfdc_config` int(11) NOT NULL AUTO_INCREMENT,
  `clientid` varchar(150) DEFAULT NULL,
  `client_secret` varchar(150) DEFAULT NULL,
  `username` varchar(100) DEFAULT NULL,
  `sfdcpassword` varchar(150) DEFAULT NULL,
  `secret_token` varchar(150) DEFAULT NULL,
  `dataset` varchar(150) DEFAULT NULL,
  `userid` int(11) DEFAULT NULL,
  `checkstatus` varchar(45) DEFAULT NULL,
  `synctype` varchar(45) DEFAULT NULL,
  `config_name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idsfdc_config`)
);

INSERT INTO sfdcacs.`sfdc_config` VALUES (1,'M01WRzlZRFFTNVd0QzExcG1nQ3Q4UW1HXzZieklQU05jWW94MFhPMVI5V2hPc2hxTnZoZk5zU1ZqcnJsRnVTS2ZjbDh3UTBfdzh4Ykh1M0Rzd1RPQQ==','MTM2MzYzODg4MjY1NjQ2ODc4NA==','bml2ZWRoYWdAdGFkaWdpdGFsLmNvbQ==','VGVjaGFzcGVjdEAwMg==','NElBd25uNXBZeDY5Wlhsd1hTNExnN3dzbg==','Lead',1,'Active','Full Sync','first');

CREATE TABLE sfdcacs.`field_repo` (
  `field_id` int(11) NOT NULL AUTO_INCREMENT,
  `repo_type` varchar(45) DEFAULT NULL,
  `field_name` varchar(45) DEFAULT NULL,
  `field_type` varchar(45) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `belongs_to` varchar(45) DEFAULT NULL,
  `salesconfigid` int(11) DEFAULT NULL,
  `acsconfigid` int(11) DEFAULT NULL,
  PRIMARY KEY (`field_id`)
);

CREATE TABLE sfdcacs.`logins` (
  `userid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `last_logged_on` datetime DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `firstname` varchar(45) DEFAULT NULL,
  `lastname` varchar(45) DEFAULT NULL,
  `subscribed` varchar(45) DEFAULT NULL,
  `otp` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`userid`)
);

INSERT INTO `logins`
(`userid`,
`username`,
`password`,
`last_logged_on`,
`created_on`,
`firstname`,
`lastname`,
`subscribed`,
`otp`)
VALUES
(1,"demo","ZGVtbw==","2018-11-20 12:07:00","2018-11-20 12:07:00","User","User","Yes","5555");

CREATE TABLE sfdcacs.`map_config` (
  `map_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `mapped_pair` varchar(45) DEFAULT NULL,
  `map_config_id` int(11) DEFAULT NULL,
  `pair_belongs_to` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`map_id`)
);

INSERT INTO `sfdcacs`.`map_config`
(`map_id`,
`user_id`,
`mapped_pair`,
`map_config_id`,
`pair_belongs_to`)
VALUES
(1,1,"Email+email",1,"Lead");

CREATE TABLE `map_master` (
  `map_config_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `config_name` varchar(45) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `salesconfigid` int(11) DEFAULT NULL,
  `acsconfigid` int(11) DEFAULT NULL,
  `salesconfig` varchar(45) DEFAULT NULL,
  `acsconfig` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`map_config_id`)
);

INSERT INTO `sfdcacs`.`map_master`
(`map_config_id`,
`user_id`,
`config_name`,
`status`,
`salesconfigid`,
`acsconfigid`,
`salesconfig`,
`acsconfig`)
VALUES
(1,
1,"mapConfig1","Active",1,1,"salesConfig","acsConfig");


CREATE TABLE sfdcacs.`sch_config` (
  `schid` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) DEFAULT NULL,
  `cronexp` varchar(45) DEFAULT NULL,
  `synctype` varchar(45) DEFAULT NULL,
  `taskname` varchar(45) DEFAULT NULL,
  `dataset` varchar(45) DEFAULT NULL,
  `scheduletype` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`schid`)
);

CREATE TABLE sfdcacs.`scheduler_runs` (
  `runid` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) DEFAULT NULL,
  `lastrun` datetime DEFAULT NULL,
  `pid` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`runid`)
);

CREATE TABLE `scheduler_run_log` (
  `log_id` int(11) NOT NULL AUTO_INCREMENT,
  `run_id` int(11) NOT NULL,
  `log_data` longtext,
  `endstatus` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`log_id`,`run_id`)
);
  
CREATE TABLE sfdcacs.`dashboard_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) DEFAULT NULL,
  `datatype` varchar(45) DEFAULT NULL,
  `datacount` int(11) DEFAULT NULL,
  `date` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

INSERT INTO `sfdcacs`.`dashboard_data`
(`id`,
`userid`,
`datatype`,
`datacount`,
`date`)
VALUES
(1,
1,
"Lead",
34,
"01-01-2019");

CREATE TABLE sfdcacs.`analytics_config` (
  `analytics_config_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `analytics_config_name` varchar(45) DEFAULT NULL,
  `analytics_config_type` varchar(45) DEFAULT NULL,
  `rs_id` varchar(100) DEFAULT NULL,
  `dimensions_id` varchar(100) DEFAULT NULL,
  `json_file` blob,
  `status` varchar(45) NOT NULL,
  `file_name` varchar(45) DEFAULT NULL,
  `campaign_config_id` int(11) DEFAULT NULL,
  `campaign_config_name` varchar(45) DEFAULT NULL,
  `salesforce_config_id` int(11) DEFAULT NULL,
  `salesforce_config_name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`analytics_config_id`)
);


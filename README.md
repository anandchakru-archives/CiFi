## CiFi
Project to wire up a client app with jenkins and github without exposing jenkins to github webhooks.
Still very much in progress. Only shutdown setup so far.

Refer /cifi-sample for a sample client application.

Initial:
1) drop database if exists cifi2; create database cifi2;
2) http://localhost:1984/cifi/first
    2.1) Create User(s)
      2.1.1) Add Roles to User(s)
    2.2) Create Apps
      2.2.1) Add Repo details
      2.2.2) Add Pipe details
      2.2.3) Add Node details
    2.3) Test Github Push Webhook
    2.4) Test Jenkins Build Success Webhook
    2.5) Test Jenkins Build Fail Webhook
    2.6) See Release builds available for deploy
    2.7) Setup gmail/twilio/slack for notification


Scene 1:
CodeCommit -> Github -> webhook -> https://ops.example.com/webhook/github/{appId} -> nginx -> cifi-core -> Trigger Jenkins

Scene 2:
Jenkins build complete -> https://ops.example.com/webhook/jenkins/{appId} -> nginx -> cifi-core -> deploy'able
Jenkins build fail -> https://ops.example.com/webhook/jenkins/fail/{appId} -> nginx -> cifi-core -> deploy'able

Scene 3:
https://ops.example.com/login/google
https://ops.example.com/login/github
https://ops.example.com/login/facebook
https://ops.example.com/login/twitter
https://ops.example.com/versions/{appid}
https://ops.example.com/deploy/{nodeid}/{appid}/{version} -> nginx -> cifi-web[OAUTH/ROLE_ADMIN] -> HTTP-200
https://ops.example.com/deploy/status/{nodeid}/{appid}/{version}/{status} -> nginx -> cifi-web[OAUTH/ROLE_ADMIN] -> HTTP-200
https://ops.example.com/restart/node/{appid}/{nodeid}
https://ops.example.com/restart/build/{appid}
https://ops.example.com/info/{appid}


app
  pipe {app-> {agent(default:jenkins),url,user,apiToken,buildToken,signToken}}
  repo {app-> {agent(default:github),url,apiUrl,apiToken,signToken}}

=====

drop database if exists cifi2;
create database cifi2;
use cifi2;

  CREATE TABLE `metaSettings` (
  	`metaSettingsId` bigint(20) NOT NULL AUTO_INCREMENT,
  	`cifiShutdownToken` varchar(255) DEFAULT 'nokey',
  	PRIMARY KEY (`metaSettingsId`)
  ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

  CREATE TABLE `usr` (
    `usrId` bigint(20) NOT NULL AUTO_INCREMENT,
    `agreedPrivacy` bit(1) DEFAULT b'0',
    `agreedTos` bit(1) DEFAULT b'0',
    `enabled` bit(1) DEFAULT b'0',
    `expiredAccount` bit(1) DEFAULT b'0',
    `expiredCreds` bit(1) DEFAULT b'0',
    `fname` varchar(75) DEFAULT NULL,
    `lname` varchar(75) DEFAULT NULL,
    `locked` bit(1) DEFAULT b'0',
    `marketting_allowed` bit(1) DEFAULT b'0',
    `last_login` bigint(20) DEFAULT NULL,
    `oauthId` varchar(50) DEFAULT NULL,
    `oauthJson` text,
    `oauthProvider` enum ('GOOGLE') DEFAULT NULL,
    `password` varchar(255) DEFAULT NULL,
    `refreshToken` varchar(250) DEFAULT NULL,
    `tz` varchar(250) DEFAULT 'America/Los_Angeles',
    `unsubscribed` bit(1) DEFAULT b'0',
    `username` varchar(255) DEFAULT NULL,
    `avatar` varchar(250) DEFAULT NULL,
    PRIMARY KEY (`usrId`)
  ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

  CREATE TABLE `usrRole` (
    `usrRoleId` bigint(20) NOT NULL AUTO_INCREMENT,
    `roleId` bigint(20) NOT NULL,
    `usrId` bigint(20) NOT NULL,
    PRIMARY KEY (`usrRoleId`),
    KEY `FKusrRoleUsrId` (`usrId`),
    CONSTRAINT `FKusrIdUsrRoleUsrId` FOREIGN KEY (`usrId`) REFERENCES `usr` (`usrId`),
    KEY `FKusrRoleDefId` (`roleId`),
    CONSTRAINT `FKusrIdUsrRoleDefUsrId` FOREIGN KEY (`roleId`) REFERENCES `usrRoleDef` (`usrRoleDefId`)
  ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

  CREATE TABLE `usrRoleDef` (
    `usrRoleDefId` bigint(20) NOT NULL AUTO_INCREMENT,
    `role` varchar(50) NOT NULL,
    `description` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`usrRoleDefId`)
  ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

  CREATE TABLE `app` (
    `appId` bigint(20) NOT NULL AUTO_INCREMENT,
    `appName` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`appId`)
  ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

  CREATE TABLE `appPipe` (
    `appPipeId` bigint(20) NOT NULL AUTO_INCREMENT,
    `appId` bigint(20) NOT NULL,
    `agent` ENUM('JENKINS') DEFAULT 'JENKINS',
    `url` varchar(255) DEFAULT NULL,
    `name` varchar(255) DEFAULT NULL,
    `user` varchar(255) DEFAULT NULL,
    `apiToken` varchar(255) DEFAULT NULL,
    `buildTriggerToken` varchar(255) DEFAULT NULL,
    `signVerifyToken` varchar(255) DEFAULT NULL,
    `regexInclude` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`appPipeId`),
    KEY `FKappPipeAppId` (`appId`),
    CONSTRAINT `FKappIdAppPipeAppId` FOREIGN KEY (`appId`) REFERENCES `app` (`appId`)
  ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

  CREATE TABLE `appRepo` (
    `appRepoId` bigint(20) NOT NULL AUTO_INCREMENT,
    `appId` bigint(20) NOT NULL,
    `agent` ENUM('GITHUB') DEFAULT 'GITHUB',
    `url` varchar(255) DEFAULT NULL,
    `apiUrl` varchar(255) DEFAULT NULL,
    `apiToken` varchar(255) DEFAULT NULL,
    `signVerifyToken` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`appRepoId`),
    KEY `FKappRepoAppId` (`appId`),
    CONSTRAINT `FKappIdAppRepoAppId` FOREIGN KEY (`appId`) REFERENCES `app` (`appId`)
  ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

  CREATE TABLE `appNode` (
    `appNodeId` bigint(20) NOT NULL AUTO_INCREMENT,
    `appId` bigint(20) NOT NULL,
    `name` varchar(255) DEFAULT NULL,
    `ip` varchar(50) DEFAULT NULL,
    PRIMARY KEY (`appNodeId`),
    KEY `FKappNodeAppId` (`appId`),
    CONSTRAINT `FKappIdAppNodeAppId` FOREIGN KEY (`appId`) REFERENCES `app` (`appId`)
  ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

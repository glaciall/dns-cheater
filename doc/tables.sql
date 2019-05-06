-- MySQL dump 10.13  Distrib 5.7.18, for Win64 (x86_64)
--
-- Host: localhost    Database: dns-cheater
-- ------------------------------------------------------
-- Server version	5.7.18-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ruleId` bigint(20) DEFAULT NULL,
  `type` varchar(10) DEFAULT NULL COMMENT '地址类型，IPv4或IPv6',
  `address` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `address_ruleid_idx` (`ruleId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COMMENT='规则所设定的IP集';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (4,3,'IPv4','192.168.10.2'),(5,3,'IPv4','192.168.10.3'),(6,3,'IPv4','192.168.10.4'),(7,3,'IPv4','192.168.10.5'),(8,4,'IPv4','127.0.0.1'),(9,4,'IPv4','127.0.0.2'),(10,4,'IPv4','127.0.0.3'),(11,4,'IPv4','127.0.0.4'),(12,3,'IPv4','192.168.10.6'),(13,3,'IPv4','192.168.10.7'),(14,3,'IPv4','192.168.10.8'),(15,3,'IPv4','192.168.10.9'),(16,3,'IPv4','192.168.10.10'),(20,8,'IPv4','127.0.0.1');
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rule`
--

DROP TABLE IF EXISTS `rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ipFrom` bigint(20) DEFAULT NULL COMMENT 'IP范围开始地址',
  `ipTo` bigint(20) DEFAULT NULL COMMENT 'IP范围结束地址',
  `timeFrom` int(11) DEFAULT NULL COMMENT '时间段开始时间，格式：HHmmss',
  `timeTo` int(11) DEFAULT NULL COMMENT '时间段结束时间',
  `matchMode` varchar(20) DEFAULT NULL COMMENT '域名匹配模式',
  `name` varchar(100) DEFAULT NULL COMMENT '匹配的域名',
  `priority` int(11) DEFAULT NULL COMMENT '匹配优先级',
  `enabled` bit(1) DEFAULT NULL COMMENT '是否启用',
  `dispatchMode` varchar(20) DEFAULT NULL COMMENT '分发模式，如iphash、round-robin、random',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='解析规则';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rule`
--

LOCK TABLES `rule` WRITE;
/*!40000 ALTER TABLE `rule` DISABLE KEYS */;
INSERT INTO `rule` VALUES (3,3232238081,3232238335,0,180000,'suffix','.wukon.com.cn',0,1,'iphash'),(4,3232238081,3232238335,0,235959,'contains','.weibo.',0,1,'round-robin'),(8,3232238081,3232238335,0,235900,'suffix','.hentai.org.cn',0,1,'round-robin');
/*!40000 ALTER TABLE `rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(20) NOT NULL COMMENT '用户类型，sa或admin',
  `name` varchar(100) NOT NULL COMMENT '用户名',
  `password` varchar(32) NOT NULL COMMENT '混合salt加密后的密码',
  `salt` varchar(16) DEFAULT NULL COMMENT '为密码加密的盐',
  `accesstoken` varchar(32) DEFAULT NULL COMMENT '当前登陆令牌信息',
  `nonce` varchar(16) DEFAULT NULL,
  `enabled` bit(1) DEFAULT b'1' COMMENT '用户是否已经启用',
  `lastLoginTime` datetime DEFAULT NULL COMMENT '最近登陆时间',
  `lastLoginIP` varchar(50) DEFAULT NULL COMMENT '最近登陆IP',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_name_idx` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'sa','admin','ccf1ec65be67beb2852027f918003108','YCX9q803N4pur9if','8a4c209c7bc710f2c00188b32b7a2002','uDQichgqpwZSE0kB',1,'2019-05-06 23:23:22','0:0:0:0:0:0:0:1');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'dns-cheater'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-05-06 23:31:21

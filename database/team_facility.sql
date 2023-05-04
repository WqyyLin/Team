-- MySQL dump 10.13  Distrib 8.0.32, for Win64 (x86_64)
--
-- Host: localhost    Database: team
-- ------------------------------------------------------
-- Server version	8.0.31

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `facility`
--

DROP TABLE IF EXISTS `facility`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `facility` (
  `fid` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设施名字',
  `capacity` int NOT NULL COMMENT '单个场地可容纳人数',
  `description` varchar(3600) DEFAULT NULL COMMENT '设施内单个场地容量',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `isValid` int NOT NULL DEFAULT '1',
  `stopTime` datetime DEFAULT '9999-12-31 00:00:00',
  `startTime` time NOT NULL,
  `endTime` time NOT NULL,
  PRIMARY KEY (`fid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `facility`
--

LOCK TABLES `facility` WRITE;
/*!40000 ALTER TABLE `facility` DISABLE KEYS */;
INSERT INTO `facility` VALUES (1,'Swimming pool',32,'description','title',1,'9999-12-31 00:00:00','09:00:00','21:00:00'),(2,'Fitness room',25,'description','title',1,'9999-12-31 00:00:00','09:00:00','21:00:00'),(3,'Squash courts',4,'description','title',1,'9999-12-31 00:00:00','09:00:00','21:00:00'),(4,'Squash courts',4,'description','title',1,'9999-12-31 00:00:00','09:00:00','21:00:00'),(5,'Squash courts',4,'description','title',1,'9999-12-31 00:00:00','09:00:00','21:00:00'),(6,'Squash courts',4,'description','title',1,'9999-12-31 00:00:00','09:00:00','21:00:00'),(7,'Sports hall',20,'description','title',1,'9999-12-31 00:00:00','09:00:00','21:00:00'),(31,'table tennis',2,'You can learn many thing from us at table tennis, at the same time you can relax yourself and make you become a healthier person!','Normal Stand table tennis',1,'9999-12-03 00:00:00','09:00:00','21:00:00'),(32,'table tennis',2,'You can learn many thing from us at table tennis, at the same time you can relax yourself and make you become a healthier person!','Normal Stand table tennis',1,'9999-12-03 00:00:00','09:00:00','21:00:00');
/*!40000 ALTER TABLE `facility` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-05-04 16:58:15

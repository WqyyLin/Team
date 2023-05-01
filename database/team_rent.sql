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
-- Table structure for table `rent`
--

DROP TABLE IF EXISTS `rent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rent` (
  `rid` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `email` varchar(255) NOT NULL COMMENT '用户',
  `time` datetime NOT NULL COMMENT '使用时间',
  `money` int NOT NULL COMMENT '花销',
  `rentTime` datetime NOT NULL COMMENT '预约成功时间',
  `limitTime` datetime NOT NULL,
  `pid` int NOT NULL,
  `islesson` int NOT NULL,
  `facility` varchar(225) NOT NULL,
  `num` int NOT NULL,
  `orderNumber` varchar(225) NOT NULL,
  PRIMARY KEY (`rid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rent`
--

LOCK TABLES `rent` WRITE;
/*!40000 ALTER TABLE `rent` DISABLE KEYS */;
INSERT INTO `rent` VALUES (77,'2505217826@qq.com','2023-02-23 11:37:23',10,'2023-02-23 11:37:23','2023-02-23 11:37:23',0,0,'',0,''),(78,'fusisun@gmail.com','2023-03-16 11:37:23',10,'2023-02-24 21:11:44','2023-03-17 11:37:23',0,0,'',0,''),(79,'fusisun@gmail.com','2023-02-23 11:37:23',10,'2023-02-24 21:11:44','2023-03-13 00:50:29',0,0,'',0,''),(80,'2505217826@qq.com','2023-02-23 11:37:23',10,'2023-02-24 21:11:44','2023-03-13 00:50:29',0,0,'',0,''),(81,'fusisun@gmail.com','2023-02-23 11:37:23',10,'2023-02-24 21:11:44','2023-03-13 00:50:29',0,0,'',0,''),(82,'2505217826@qq.com','2023-02-23 11:37:23',10,'2023-03-11 21:53:51','2023-03-13 00:50:29',0,0,'',0,''),(83,'2505217826@qq.com','2023-02-23 11:37:23',10,'2023-03-11 21:53:51','2023-03-13 00:50:29',0,0,'',0,''),(84,'2505217826@qq.com','2023-02-23 11:37:23',10,'2023-03-11 21:53:51','2023-03-13 00:50:29',0,0,'',0,''),(85,'fusisun@gmail.com','2023-02-23 11:37:23',10,'2023-03-11 21:53:51','2023-03-13 00:50:29',0,0,'',0,''),(86,'2505217826@qq.com','2023-03-11 23:37:23',10,'2023-03-11 21:54:16','2023-03-13 00:50:29',0,0,'',0,''),(87,'fusisun@gmail.com','2023-03-11 23:37:23',10,'2023-03-11 21:54:16','2023-03-13 00:50:29',0,0,'',0,''),(88,'fusisun@gmail.com','2023-03-11 23:37:23',10,'2023-03-11 21:54:16','2023-03-13 00:50:29',0,0,'',0,''),(89,'2505217826@qq.com','2023-03-11 23:37:23',10,'2023-03-11 21:54:16','2023-03-13 00:50:29',0,0,'',0,''),(90,'fusisun@gmail.com','2023-03-11 22:37:23',10,'2023-03-13 00:05:04','2023-03-13 00:50:29',0,0,'',0,''),(93,'fusisun@gmail.com','2023-03-11 23:37:23',10,'2023-03-13 00:05:04','2023-03-17 00:50:29',0,0,'',0,''),(95,'2505217826@qq.com','2023-03-11 23:37:23',10,'2023-03-13 00:50:29','2023-03-13 00:50:29',0,0,'',0,'');
/*!40000 ALTER TABLE `rent` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-05-01 21:10:38
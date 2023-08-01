-- aq.metrics definition

CREATE TABLE `metrics` (
  `id` int NOT NULL AUTO_INCREMENT,
  `timestamp` timestamp NOT NULL,
  `detector_id` int NOT NULL,
  `placement` varchar(255) DEFAULT NULL,
  `p_0_3_um` float DEFAULT NULL,
  `p_0_3_um_b` float DEFAULT NULL,
  `p_2_5_um` float DEFAULT NULL,
  `p_2_5_um_b` float DEFAULT NULL,
  `gas_680` float DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_metrics_detector` (`detector_id`),
  KEY `fk_metrics_placement` (`placement`),
  CONSTRAINT `fk_metrics_detector` FOREIGN KEY (`detector_id`) REFERENCES `detector` (`id`),
  CONSTRAINT `fk_metrics_placement` FOREIGN KEY (`placement`) REFERENCES `detector` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2950 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
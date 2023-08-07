-- aq.ecobee_token definition

CREATE TABLE `ecobee_token` (
  `id` int NOT NULL AUTO_INCREMENT,
  `access_token` text,
  `access_token_expires_at` timestamp NULL DEFAULT NULL,
  `refresh_token` varchar(255) NOT NULL,
  `refresh_token_expires_at` timestamp NULL DEFAULT NULL,
  `detector_id` int NOT NULL,
  `placement` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
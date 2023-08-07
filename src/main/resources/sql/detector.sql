-- aq.detector definition

CREATE TABLE `detector` (
  `id` int NOT NULL,
  `ip_addr` varchar(255) DEFAULT NULL,
  `type` varchar(15) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

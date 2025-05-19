CREATE DATABASE IF NOT EXISTS `zaytsev_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `zaytsev_db`;

CREATE TABLE IF NOT EXISTS `partners` (
  `partner_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `registration_date` DATE NULL DEFAULT NULL,
  `partner_type` VARCHAR(50) NOT NULL,
  `director` VARCHAR(255) NULL DEFAULT NULL,
  `email` VARCHAR(100) NULL DEFAULT NULL,
  `phone` VARCHAR(20) NULL DEFAULT NULL,
  `legal_address` TEXT NULL DEFAULT NULL,
  `inn` VARCHAR(20) NULL DEFAULT NULL,
  `rating` INT NULL DEFAULT NULL,
  PRIMARY KEY (`partner_id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `rate` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  `total_sales` INT NULL DEFAULT 0,
  `discount_rate` FLOAT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `sales_history` (
  `sale_id` INT NOT NULL AUTO_INCREMENT,
  `product_name` VARCHAR(255) NOT NULL,
  `partner_name` VARCHAR(255) NULL DEFAULT NULL,
  `quantity` INT NOT NULL,
  `sale_date` DATE NOT NULL,
  PRIMARY KEY (`sale_id`),
  KEY `fk_sales_partner` (`partner_name`),
  CONSTRAINT `fk_sales_partner` FOREIGN KEY (`partner_name`) REFERENCES `partners` (`name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

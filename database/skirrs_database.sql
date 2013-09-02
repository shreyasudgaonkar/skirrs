-- phpMyAdmin SQL Dump
-- version 3.5.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Sep 02, 2013 at 06:54 PM
-- Server version: 5.5.24-log
-- PHP Version: 5.4.3

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `skirrs`
--

-- --------------------------------------------------------

--
-- Table structure for table `user_addresses`
--
-- Creation: Aug 24, 2013 at 12:57 AM
--

DROP TABLE IF EXISTS `user_addresses`;
CREATE TABLE IF NOT EXISTS `user_addresses` (
  `user_id` int(10) NOT NULL,
  `street_address` varchar(256) NOT NULL,
  `city` varchar(32) NOT NULL,
  `state` varchar(32) NOT NULL,
  `zip` varchar(16) NOT NULL,
  `country` varchar(16) NOT NULL,
  PRIMARY KEY (`user_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB;

-- --------------------------------------------------------

--
-- Table structure for table `user_profiles`
--
-- Creation: Aug 24, 2013 at 12:04 AM
--

DROP TABLE IF EXISTS `user_profiles`;
CREATE TABLE IF NOT EXISTS `user_profiles` (
  `user_id` int(10) NOT NULL COMMENT 'refers to user_id in `users`',
  `dob` date NOT NULL COMMENT 'date of birth',
  `sex` char(1) NOT NULL COMMENT 'M or F',
  `profile_type_id` int(6) NOT NULL COMMENT 'refers to account_type_id in account_types',
  `picture_id` varchar(512) DEFAULT NULL COMMENT 'refers to picture_id in pictures',
  `rating` int(5) NOT NULL DEFAULT '0' COMMENT 'rating from 1 to 5',
  `rides_offered` int(10) DEFAULT NULL COMMENT 'total rides offered so far',
  `avg_reponse_time` varchar(64) DEFAULT NULL COMMENT 'response time to requests from passengers',
  `registration_date` date NOT NULL COMMENT 'user registered on',
  PRIMARY KEY (`user_id`),
  KEY `user_id` (`user_id`),
  KEY `profile_type_id` (`profile_type_id`)
) ENGINE=InnoDB;

-- --------------------------------------------------------

--
-- Table structure for table `rides_offered`
--
-- Creation: Sep 02, 2013 at 06:30 PM
--

DROP TABLE IF EXISTS `rides_offered`;
CREATE TABLE IF NOT EXISTS `rides_offered` (
  `ride_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'auto-incremented and primary',
  `user_id` int(10) NOT NULL COMMENT 'user who posted the ride',
  `source` varchar(128) NOT NULL COMMENT 'ride from',
  `destination` varchar(128) NOT NULL COMMENT 'ride to',
  `departure_date_time` datetime NOT NULL COMMENT 'date and time of departure',
  `seats_offered` int(10) NOT NULL COMMENT 'no.of available seats',
  `price` int(10) NOT NULL COMMENT 'price quoted by driver',
  `pick_up_offered` tinyint(1) NOT NULL COMMENT '0 --> false, 1 --> True',
  `share_contact_info` tinyint(1) NOT NULL COMMENT '0 --> false, 1 --> True',
  `ltt` float(10,6) NOT NULL COMMENT 'lattitude',
  `lng` float(10,6) NOT NULL COMMENT 'longitude',
  `comments` text NOT NULL COMMENT 'comments about ride/driver',
  PRIMARY KEY (`ride_id`),
  KEY `user_id` (`user_id`),
  KEY `ride_id` (`ride_id`),
  KEY `ride_id_2` (`ride_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--
-- Creation: Aug 23, 2013 at 11:26 PM
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `email_address` varchar(254) NOT NULL,
  `user_id` int(10) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(26) NOT NULL,
  `last_name` varchar(26) DEFAULT NULL,
  `password` varchar(256) NOT NULL,
  `phone_number` varchar(15) NOT NULL,
  PRIMARY KEY (`email_address`),
  UNIQUE KEY `user_id` (`user_id`,`phone_number`),
  KEY `email_address` (`email_address`)
) ENGINE=InnoDB COMMENT='Base user table' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------
--

-- Table structure for table `profile_types`
--
-- Creation: Aug 24, 2013 at 12:07 AM
--

DROP TABLE IF EXISTS `profile_types`;
CREATE TABLE IF NOT EXISTS `profile_types` (
  `profile_type_id` int(10) NOT NULL COMMENT 'profile-id',
  `profile_name` varchar(32) NOT NULL COMMENT 'passenger/driver/dealer, etc.',
  PRIMARY KEY (`profile_type_id`),
  UNIQUE KEY `profile_name` (`profile_name`),
  KEY `profile_type_id` (`profile_type_id`)
) ENGINE=InnoDB;

--
-- Dumping data for table `profile_types`
--

INSERT INTO `profile_types` (`profile_type_id`, `profile_name`) VALUES
(4, 'dealer'),
(3, 'driver'),
(2, 'passenger'),
(1, 'skirrs_admin');

-- --------------------------------------------------------
--
-- Constraints for dumped tables
--

--
-- Constraints for table `rides_offered`
--
ALTER TABLE `rides_offered`
  ADD CONSTRAINT `rides_offered_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `user_addresses`
--
ALTER TABLE `user_addresses`
  ADD CONSTRAINT `user_addresses_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `user_profiles`
--
ALTER TABLE `user_profiles`
  ADD CONSTRAINT `user_profiles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `user_profiles_ibfk_2` FOREIGN KEY (`profile_type_id`) REFERENCES `profile_types` (`profile_type_id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

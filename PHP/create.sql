CREATE TABLE IF NOT EXISTS `group` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `key` varchar(100) NOT NULL,
  PRIMARY KEY `id` (`id`)
);


CREATE TABLE IF NOT EXISTS `user` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `name` varchar2(64) NOT NULL,
  `surname`varchar2(64) NOT NULL,
  `photo`, -- which type of data?
  `group_id` int(5),
  PRIMARY KEY(`id`),
  FOREIGN KEY (`group_id`)
    REFERENCES `group(id)`
);

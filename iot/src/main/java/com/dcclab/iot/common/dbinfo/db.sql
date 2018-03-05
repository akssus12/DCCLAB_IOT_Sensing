CREATE TABLE `tb_cpuinfo` (
  `idx` int(11) NOT NULL AUTO_INCREMENT,
  `cpuNm` varchar(45) NOT NULL,
  `cpuTemp` float NOT NULL,
  `cpuLoad` float NOT NULL,
  `crtDttm` varchar(45) NOT NULL,
  PRIMARY KEY (`idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
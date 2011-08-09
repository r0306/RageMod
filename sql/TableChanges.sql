-- *** 8-2-11
-- Add LogonMessageQueue to Players

ALTER TABLE Players ADD LogonMessageQueue VARCHAR(1024);

-- *** 8-3-11
-- Add YCoord to PlayerTowns

ALTER TABLE PlayerTowns ADD YCoord INT;

-- *** 8-4-11

ALTER TABLE Players ADD TreasuryBlocks INT NOT NULL DEFAULT '0';

-- *** 8-8-11
-- New Permits table for temporary building passes

CREATE TABLE IF NOT EXISTS `Permits` (
  `ID_Permit` int(11) NOT NULL AUTO_INCREMENT,
  `ID_Player_Holder` int(11) NOT NULL,
  `ID_Player_Granter` int(11) NOT NULL,
  `Type` varchar(64) NOT NULL,
  `Expiration` datetime NOT NULL,
  PRIMARY KEY (`ID_Permit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;
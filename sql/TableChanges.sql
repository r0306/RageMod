-- *** 8-2-11
-- Add LogonMessageQueue to Players

ALTER TABLE Players ADD LogonMessageQueue VARCHAR(1024);

-- *** 8-3-11
-- Add YCoord to PlayerTowns

ALTER TABLE PlayerTowns ADD YCoord INT;

-- *** 8-4-11

ALTER TABLE Players ADD TreasuryBlocks INT NOT NULL DEFAULT '0';
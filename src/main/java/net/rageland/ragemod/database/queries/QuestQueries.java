package net.rageland.ragemod.database.queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.inventory.ItemStack;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.database.RageDB;
import net.rageland.ragemod.quest.Flags;
import net.rageland.ragemod.quest.Quest;
import net.rageland.ragemod.quest.QuestData;
import net.rageland.ragemod.quest.QuestRequirements;
import net.rageland.ragemod.quest.RewardData;
import net.rageland.ragemod.quest.RewardQuest;

@SuppressWarnings("unused")
public class QuestQueries {
	
	private RageDB rageDB;
	private RageMod plugin;
	
	public QuestQueries(RageDB rageDB, RageMod plugin)
	{
		this.rageDB = rageDB;
		this.plugin = plugin;
	}
	
	// Load all active quest objects
	public HashMap<Integer, Quest> loadAllQuests() 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	    HashMap<Integer, Quest> quests = new HashMap<Integer, Quest>();
	    Quest quest;
	    QuestData questData;
	    QuestRequirements questReq;
	    RewardData rewardData;
	    Flags flags;
	    
		try
    	{
			conn = rageDB.getConnection();
        	preparedStatement = conn.prepareStatement("" +
        			"SELECT q.ID_Quest, q.Name, q.Type, IFNULL(q.ID_QuestSource, 0) as ID_QuestSource, q.ID_NPCInstance_Source, " +
        			"		IFNULL(q.ID_NPCInstance_Target, 0) as ID_NPCInstance_Target, " +
        			"		IFNULL(q.ID_NPCInstance_Aux1, 0) as ID_NPCInstance_Aux1, " +
        			"		IFNULL(q.ID_NPCInstance_Aux2, 0) as ID_NPCInstance_Aux2, " +
        			"		IFNULL(q.ID_NPCInstance_Aux3, 0) as ID_NPCInstance_Aux3, " +
        			"		IFNULL(q.ID_Player_Target, 0) as ID_Player_Target, " +
        			"		IFNULL(q.ID_Quest_Prereq, 0) as ID_Quest_Prereq, " +
        			"		IFNULL(q.Required_Affinity, -10) as Required_Affinity, " +
        			"		IFNULL(q.Required_Reputation, 0) as Required_Reputation, " +
        			"		IFNULL(q.Required_Rank, 0) as Required_Rank, " +
        			"		q.Reward_Money, " +
        			"		IFNULL(q.Reward_ItemID, 0) as Reward_ItemID, " +
        			"		IFNULL(q.Reward_ItemAmount, 0) as Reward_ItemAmount, " +
        			"		q.Reward_Exp, q.Reward_Affinity, q.Reward_Reputation, " +
        			"		q.Phrase_Start, q.Phrase_Finish, q.Phrase_Unfinished, " +
        			"		q.IsNonExclusive, q.IsReserved, q.IsTimed, q.TimeLimit_Seconds " +
        			"FROM Quests q " +
        			"INNER JOIN NPCInstances ni ON ni.ID_NPCInstance = q.ID_NPCInstance_Source " +
        			"WHERE (ni.DespawnTime > NOW() OR ni.DespawnTime IS NULL) ");
        			
        		
        	rs = preparedStatement.executeQuery();
        	while ( rs.next() ) 
        	{
        		questData = new QuestData(rs.getString("Name"), rs.getInt("ID_Quest"), rs.getString("Phrase_Start"), 
        				rs.getString("Phrase_End"), 
        				new QuestRequirements(rs.getInt("ID_Quest_Prereq"), rs.getInt("Required_Rank"), 
        									  rs.getInt("Required_Affinity"), rs.getInt("Required_Reputation")), 
        				0);
        		rewardData = new RewardData(new ItemStack(34), rs.getInt("Reward_ItemAmount"), (double)rs.getInt("Reward_Money"));
        		flags = new Flags(rs.getBoolean("IsNonExclusive"), rs.getBoolean("IsReserved"));
        		
        		switch( rs.getInt("Type") )
        		{
        			case Quest.TYPE_REWARD:
        				quest = new RewardQuest(questData, rewardData, flags, rs.getInt("ID_NPCInstance_Source"));
        				break;
        			default:
        				quest = null;
        				System.out.println("Error: Quest ID " + rs.getInt("ID_Quest") + " is of invalid type.");
        		}
        		
        		quests.put(rs.getInt("ID_Quest"), quest);
        	}
        	
    	} 
		catch (Exception e) {
    		System.out.println("Error in QuestQueries.loadAllQuests(): " + e.getMessage());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
		
		return quests;
	}

	// Log a task as complete in the database
	public void setComplete(String taskName) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
		try
    	{
			conn = rageDB.getConnection();
    		preparedStatement = conn.prepareStatement(
    				"INSERT INTO Tasks (Name, Timestamp) VALUES ('" + taskName + "',NOW())");
    		preparedStatement.executeUpdate();	
    	} 
    	catch (SQLException e) {
    		System.out.println("Error in RageDB.setComplete(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}	
	}

}

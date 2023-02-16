package Decision.Teleporter;

import Enums.*;
import Models.*;

import java.util.*;
import Decision.General.*;

public class teleporter {
    
    public double teleOffPrio;
    private int acceptableDiff = 10; 
    private int minSizeTel = 50;
    //Info: Defend prio gabung sama defense

    public void getTeleOffPrio(GameState State, GameObject bot){
        //size checking and num of tele
        List<GameObject> playerList = General.getObjectListSize(ObjectTypes.PLAYER, State, bot);
        if(playerList.size()!=0){
            if(bot.getSize()>playerList.get(0).getSize()+acceptableDiff*3 &&bot.getSize()>minSizeTel &&bot.getTeleporterCount()>0){
                teleOffPrio = 0;
            }else{
                if(checkTeleport(State,bot) == 1){
                    teleOffPrio=0;
                }else if(checkTeleport(State,bot)==-1){
                    teleOffPrio = 200;
                }
            }
        }else{teleOffPrio = 200;}
    }



    public Integer checkTeleport(GameState gameState, GameObject bot){
        /*Return:
         * 1 if teleporter near smaller player
         * 0 if teleporter in safe position
         * -1 if teleport near bigger player or not available
         */
        //get Teleport List
        List<GameObject> teleportList = General.getObjectListSize(ObjectTypes.TELEPORTER,  gameState, bot);
        int i = 0;
        while(i<teleportList.size()){
            if(bot.getTeleporterAngle() == teleportList.get(i).getCurrHeading()){
            }else{
                i++;
            }
        }
        if(i!=teleportList.size()){
            List<GameObject> playerList = General.getObjectListDistance(ObjectTypes.PLAYER, gameState, teleportList.get(i));
            if(bot.getSize() > General.distanceFromPlayerToObject(playerList.get(0), bot) && bot.getSize() > playerList.get(0).getSize()){
                //attack case
                return 1;
            }else{
                //escape case
                i =0;
                while(i<playerList.size()){
                    
                    if(bot.getSize() < playerList.get(i).getSize() && General.distanceFromPlayerToObject(playerList.get(i), bot)-bot.getSize() - playerList.get(i).getSize()>=acceptableDiff){return -1;}
                    i++;
                }
                return 0;
            }
        }else{
            return 2;//return 2 if no teleporter
        }
    }
    public PlayerAction doTeleport(GameState gameState, GameObject bot){
        List<GameObject> playerList = General.getObjectListSize(ObjectTypes.PLAYER, gameState, bot);
        PlayerAction commands;
        switch(checkTeleport(gameState, bot)){
            case 2:
                commands.setHeading(General.objectHeading(playerList.get(0), bot));
                commands.setAction(PlayerActions.FIRETELEPORT);
                break;
            case 1:
                commands.setAction(PlayerActions.TELEPORT);
                break;
            case 0:
                commands.setAction(PlayerActions.TELEPORT);
                break;
        }
        return commands;
    }
} 

package Decision.Farm;

import Enums.*;
import Models.*;

import java.lang.Math;
import java.util.*;
import java.util.stream.*;

import Decision.General.*;


public class Farm {

        public double prio;
        

        public void getFarmPrio(GameObject player, GameState gameState){
                //tick: action/second
                //time: num of action
                //100 means most prioritize
                //The concept is if it takes a millenium to get food, we go cowabungga
                //bigger priority means more farming
                //A bit of suggestion, subtract farm priority with other priority
                double time = (getNearestFood(player, gameState)-player.size)/player.getSpeed();
                var temp = General.getObjectListDistance(ObjectTypes.PLAYER, gameState, player);
                /*Get Nearest Player Priority */ 
                this.prio = 200;
                
        }


        public double getNearestFood(GameObject player, GameState gameState){
                var foodList = gameState.getGameObjects()
                .stream().filter(item -> (item.getGameObjectType() == ObjectTypes.FOOD || item.getGameObjectType()==ObjectTypes.SUPERFOOD))
                .sorted(Comparator
                        .comparing(item -> General.distanceFromPlayerToObject(item, player)))
                .collect(Collectors.toList());
                if(foodList.size() == 0){
                        return 9999;
                }else{
                        return General.distanceFromPlayerToObject(foodList.get(0), player);
                }
                
        }

        public PlayerAction normalFarm(PlayerAction command1, GameObject player, GameState gameState){
                PlayerAction command = new PlayerAction();
                        var foodList = gameState.getGameObjects()
                        .stream().filter(item -> (item.getGameObjectType() == ObjectTypes.FOOD || item.getGameObjectType()==ObjectTypes.SUPERFOOD || item.getGameObjectType()==ObjectTypes.SUPERNOVAPICKUP))
                        .sorted(Comparator
                                .comparing(item -> General.distanceFromPlayerToObject(item, player)))
                        .collect(Collectors.toList());
                        command.heading = General.objectHeading(foodList.get(0), player);
                        command.setAction(PlayerActions.FORWARD);
               return command;
        }
}


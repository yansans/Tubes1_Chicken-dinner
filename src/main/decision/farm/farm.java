

import Enums.*;
import Models.*;
import general.*;
import java.lang.Math;
import java.util.*;
import java.util.stream.*;


public class farm {

        public double prio;
        

        public void getFarmPrio(World a){
                //tick: action/second
                //time: num of action
                //100 means most prioritize
                //The concept is if it takes a millenium to get food, we go cowabungga
                //bigger priority means more farming
                //A bit of suggestion, subtract farm priority with other priority
                int time = (getNearestFood()-player.size)/player.getSpeed();
                Integer tick = a.getCurrentTick();
                this.prio = 100-(time/tick)*20;

        }
        public int getNearestFood(){
                var foodList = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD || item.getGameObjectType()==ObjectTypes.SUPER_FOOD)
                .sorted(Comparator
                        .comparing(item -> getDistanceBetween(player.Position, item)))
                .collect(Collectors.toList());
                return getDistanceBetween(player, foodList.get(0));
        }

        public PlayerAction normalFarm(){
                PlayerAction command;
                        var foodList = gameState.getGameObjects()
                        .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD || item.getGameObjectType()==ObjectTypes.SUPER_FOOD)
                        .sorted(Comparator
                                .comparing(item -> getDistanceBetween(player.Position, item)))
                        .collect(Collectors.toList());
                        command.heading = getHeadingBetween(foodList.get(0));
                        command.setAction(1);
                return command;
        }

        public PlayerAction farmInCone(Position x, int cone){
            /*An idea that farms while moving toward something*/
            /*x: Posistion to go to */
            /*cone: The limited degree for farmming */
            /*Say we are at C,food at B, we go to A*/
            /*Formula  acos((a^2+b^2-c^2)/2ab)*/
            /*a = getDistanceBetween(player.position,item) */
            /*c = getDistanceBetween(item,x) */
            int b = getDistanceBetween(player.Posistion,x);
            double temp = Math.toRadians(x);
            
            PlayerAction command;
            var foodList = gameState.getGameObjects()
            .stream().filter((item -> item.getGameObjectType() == ObjectTypes.FOOD || item.getGameObjectType()==ObjectTypes.SUPER_FOOD && cos(temp)<(pow(getDistanceBetween(player.Posistion, item),2)+pow(b, 2)-pow(getDistanceBetween(item,x),2))/(2*b*getDistanceBetween(player.Posistion, item))  ))
            .sorted(Comparator
                    .comparing(item -> getDistanceBetween(player.Position, item)))
            .collect(Collectors.toList());
            command.heading = getHeadingBetween(foodList.get(0));
            command.setAction(1);
            return command;
        }
}


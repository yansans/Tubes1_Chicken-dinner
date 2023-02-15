package Decision.Decisionmaker;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

import Decision.Defense.*;
import Decision.Farm.*;
import Decision.General.*;
import Decision.Retreat.*;
import Decision.Offence.*;

public class Decisionmaker {
    public int decision_kind;
    public int decision_kind_variation;
    public double temp_prio;

    public Defense defense_prio;
    public Retreat retreat_prio;
    public Farm farm_prio;
    public Offence offence_prio;

    private GameObject player;
    private GameState gameState;

    public Decisionmaker(GameObject player, GameState gameState) {
        this.player = player;
        this.gameState = gameState;
        this.defense_prio = new Defense();
        this.retreat_prio = new Retreat();
        this.farm_prio = new Farm();
        this.offence_prio = new Offence(player, gameState);
    }
    // lanjutkan

    // masukkan perintah" yang sesuai

    // generalisasi
    public void findPriority() {
        defense_prio.getDefensePrio(player, gameState);
        decision_kind = 1;
        temp_prio = defense_prio.prio;

        retreat_prio.getRetreatPrio(player, gameState);
        if (temp_prio > retreat_prio.prio) {
            decision_kind = 2;
            decision_kind_variation = this.retreat_prio.kind;
            temp_prio = retreat_prio.prio;
        }

        farm_prio.getFarmPrio(player, gameState);
        if (temp_prio > farm_prio.prio) {
            decision_kind = 3;
            temp_prio = farm_prio.prio;
        }

        offence_prio.getPrioGeneral();
        if (temp_prio > offence_prio.prio){
            decision_kind = 4;
        }
    }

    public PlayerAction whatBotShouldDo() {
        findPriority();
        PlayerAction command = new PlayerAction();
        System.out.println("decision kind: " + decision_kind);

        if (this.decision_kind == 1) { // anggap 1 adalah defense
            command.action = PlayerActions.USESHIELD;
            command.heading = 0;
            return command;
        } else if (this.decision_kind == 2) { // anggap 2 adalah retreat
            if (decision_kind_variation == 1) { // lari dari gas
                // putar balik
                command.action = PlayerActions.FORWARD;
                command.heading = (player.currentHeading + 180) % 360;
            } else if (decision_kind_variation == 2) { // lari dari player lain
                var playerlist = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.PLAYER)
                .sorted(Comparator.comparing(item -> General.distanceFromPlayerToProjectileTrajectory(item, player)))
                .collect(Collectors.toList());

                command.action = PlayerActions.FORWARD;
                command.heading = (General.objectHeading(playerlist.get(0), player) + 180) % 360;           

            } else{ // lari dari supernova
                // menjauh dari tempat supernova sekarang
                var supernovalist = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPERNOVABOMB)
                .sorted(Comparator.comparing(item -> General.distanceFromPlayerToProjectileTrajectory(item, player)))
                .collect(Collectors.toList());

                command.action = PlayerActions.FORWARD;
                command.heading = (General.objectHeading(supernovalist.get(0), player) + 180) % 360;
            } 
            return command;
        }else if(this.decision_kind==3){//Farm
            Farm.normalFarm(command, player, gameState);
            /* ADV
            if(retreat_prio.prio<farm_prio.prio-10){
                var playerlist = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.PLAYER)
                .sorted(Comparator.comparing(item -> General.distanceFromPlayerToProjectileTrajectory(item, player)))
                .collect(Collectors.toList());
                
                Farm.farmInCone(command, );
            }
            else{
                Farm.normalFarm(command, player, gameState);
            }*/
        } else if (decision_kind == 4){
            return offence_prio.doOffence();
        }
        System.out.println("end of function ");
        return command;
        }
    }


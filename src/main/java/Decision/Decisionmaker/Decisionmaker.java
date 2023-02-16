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
        this.defense_prio = new Defense(player, gameState);
        this.retreat_prio = new Retreat(player, gameState);
        this.farm_prio = new Farm();
        this.offence_prio = new Offence(player, gameState);
    }
    // lanjutkan

    // masukkan perintah" yang sesuai

    // generalisasi
    public void findPriority() {
        defense_prio.getDefensePrio();
        decision_kind = 1;
        temp_prio = defense_prio.prio;

        retreat_prio.getRetreatPrio();
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
            return defense_prio.actionDefense();
        } else if (this.decision_kind == 2) { // anggap 2 adalah retreat
           return retreat_prio.actionRetreat();
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


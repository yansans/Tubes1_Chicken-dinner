package Decision.Decisionmaker;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

import Decision.Defense.*;
import Decision.Farm.*;
import Decision.General.*;
import Decision.Retreat.*;

public class Decisionmaker {
    public int decision_kind;
    public int decision_kind_variation;
    public double temp_prio;

    public Defense defense_prio;
    public Retreat retreat_prio;
    public Farm farm_prio;
    // lanjutkan

    // masukkan perintah" yang sesuai

    // generalisasi
    public void findPriority(GameObject player, GameState gameState) {
        this.defense_prio.getDefensePrio(player, gameState);
        this.decision_kind = 1;
        this.temp_prio = defense_prio.prio;

        this.retreat_prio.getRetreatPrio(player, gameState);
        if (this.temp_prio > this.retreat_prio.prio) {
            this.decision_kind = 2;
            this.decision_kind_variation = this.retreat_prio.kind;
            this.temp_prio = retreat_prio.prio;
        }

        this.farm_prio.getFarmPrio(player, gameState);
        if (this.temp_prio > this.farm_prio.prio) {
            this.decision_kind = 3;
            this.temp_prio = farm_prio.prio;
        }

    }

    public PlayerAction whatBotShouldDo(GameObject player, GameState gameState) {
        findPriority(player, gameState);
        PlayerAction command = new PlayerAction();

        if (this.decision_kind == 1) { // anggap 1 adalah defense
            command.action = PlayerActions.USESHIELD;
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
        }  
        return command;
        }
    }


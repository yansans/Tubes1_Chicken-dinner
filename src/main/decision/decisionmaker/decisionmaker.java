import Enums.*;
import Models.*;
import general.*;
import defense.*;
import retreat.*;
// import farming dll nanti

import java.util.*;
import java.util.stream.*;

public class decisionmaker {
    public int decision_kind;
    public int decision_kind_variation;

    public defense defense_prio;
    public retreat retreat_prio;
    // lanjutkan

    // masukkan perintah" yang sesuai

    // generalisasi
    public void findPriority(GameObject player) {
        defense_prio.getDefensePrio(player);
        retreat_prio.getRetreatPrio(player);
        // lanjutkan

        // cari yang paling kecil dan ganti decision_kind
    }

    public PlayerAction whatBotShouldDo(GameObject player) {
        findPriority(player);
        PlayerActions command;

        if (this.decision_kind == 1) { // anggap 1 adalah defense
            command.action = PlayerActions.USE_SHIELD;
            return command;
        } else if (this.decision_kind == 2) { // anggap 2 adalah retreat
            if (decision_kind_variation == 1) { // lari dari gas
                // putar balik
                command.action = PlayerActions.FORWARD;
                command.heading = (player.currentHeading + 180) % 360;
            } else if (decision_kind_variation == 2) { // lari dari player lain

            } else{ // lari dari supernova
                // menjauh dari tempat supernova sekarang
                var supernovalist = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPER_NOVA_BOMB)
                .sorted(Comparator.comparing(item -> distanceFromPlayerToProjectileTrajectory(item, player)))
                .collect(Collectors.toList());

                command.action = PlayerActions.FORWARD;
                command.heading = (objectHeading(supernovalist.get(0), player) + 180) % 360;
            } 
        } 

        }
    }


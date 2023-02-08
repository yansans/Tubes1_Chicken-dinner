import Enums.*;
import Models.*;
import general.*;

import java.util.*;
import java.util.stream.*;

public class retreat {
    public double prio;
    public int kind;

    public void getRetreatPrio(GameObject player) {
        // priority untuk kabur dari player lebih penting daripada menghindari gas cloud
        // untuk gas cloud
        double gas_prio;
        var gasList = gameState.getGameObjects()
            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD)
            .sorted(Comparator.comparing(item -> distanceFromPlayerToObject(item, player)))       
            .collect(Collectors.toList());
        
        gas_prio = distanceFromPlayerToProjectileTrajectory(gasList.get(0), player) * 3; // skalanya dapat berubah

        // untuk kabur dari player
        double run_prio;
        var playerlist = gameState.getGameObjects()
            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.PLAYER)
            .sorted(Comparator.comparing(item -> distanceFromPlayerToObject(item, player)))
            .collect(Collectors.toList());
        
        run_prio = distanceFromPlayerToProjectileTrajectory(playerlist.get(0), player);

        // untuk kabur dari supernova
        double supernova_prio;
        var supernovalist = gameState.getGameObjects()
            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPER_NOVA_BOMB)
            .sorted(Comparator.comparing(item -> distanceFromPlayerToProjectileTrajectory(item, player)))
            .collect(Collectors.toList());

        if (supernovalist.size() == 0) {
            supernova_prio = 100;
        } else {
            supernova_prio = distanceFromPlayerToProjectileTrajectory(supernovalist.get(0), player);
        }

        if (run_prio <= gas_prio & run_prio <= supernova_prio) {
            this.prio = run_prio;
            this.kind = 2;
        } else if (gas_prio <= run_prio & gas_prio <= supernova_prio) {
            this.prio = gas_prio;
            this.kind = 1;
        } else {
            this.prio = supernova_prio;
            this.kind = 3;
        }
    }
}

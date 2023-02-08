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

        if (this.decision_kind == 1) { // anggap 1 adalah defense
            return PlayerActions.USE_SHIELD;
        } else if (this.decision_kind == 2) { // anggap 2 adalah retreat
            
        }
    }
}

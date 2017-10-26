package zeus.quantm.greenfood.network.models.distance;

/**
 * Created by EDGY on 6/23/2017.
 */

public class Element {
    private Distance distance;
    private String status;

    public Element(Distance distance, String status) {
        this.distance = distance;
        this.status = status;
    }

    public Distance getDistance() {
        return distance;
    }

    public String getStatus() {
        return status;
    }
}

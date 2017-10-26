package zeus.quantm.greenfood.network.models.distance;

import java.util.List;

/**
 * Created by EDGY on 6/23/2017.
 */

public class Row {
    private List<Element> elements;

    public Row(List<Element> elements) {
        this.elements = elements;
    }

    public List<Element> getElements() {
        return elements;
    }
}

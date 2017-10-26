package zeus.quantm.greenfood.network.models.order;

import com.google.gson.annotations.SerializedName;

import zeus.quantm.greenfood.models.Notification;

/**
 * Created by EDGY on 7/5/2017.
 */

public class NotificationAPI {
    @SerializedName("body")
    private Order order;

    public NotificationAPI(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
}


package zeus.quantm.greenfood.network.models.order;

/**
 * Created by EDGY on 6/30/2017.
 */

public class SendOrderResponse {
    private int success;
    private int failure;

    public SendOrderResponse(int success, int failure) {
        this.success = success;
        this.failure = failure;
    }

    public int getSuccess() {
        return success;
    }

    public int getFailure() {
        return failure;
    }
}

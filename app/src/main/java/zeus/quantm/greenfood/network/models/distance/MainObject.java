package zeus.quantm.greenfood.network.models.distance;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by EDGY on 6/23/2017.
 */

public class MainObject {
    private List<Row> rows;
    @SerializedName("status")
    private String statusGoogleAPI;

    public MainObject(List<Row> rows, String statusGoogleAPI) {
        this.rows = rows;
        this.statusGoogleAPI = statusGoogleAPI;
    }

    public String getStatusGoogleAPI() {
        return statusGoogleAPI;
    }

    public List<Row> getRows() {
        return rows;
    }
}

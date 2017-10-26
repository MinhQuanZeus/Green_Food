package zeus.quantm.greenfood.network.models.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by EDGY on 6/30/2017.
 */

public class Order implements Serializable{
    @SerializedName("buyer_id")
    private String buyerID;
    @SerializedName("buyer_name")
    private String buyerName;
    @SerializedName("check_id")
    private String sellerID;
    @SerializedName("food_name")
    private String foodName;
    @SerializedName("image_food")
    private String foodImgLink;
    private long quantity;
    private String type;
    private String time;
    private String status;

    public Order() {
    }

    public Order(String buyerID, String buyerName, String sellerID, String foodName, String foodImgLink, long quantity, String type, String time, String status) {
        this.buyerID = buyerID;
        this.buyerName = buyerName;
        this.sellerID = sellerID;
        this.foodName = foodName;
        this.foodImgLink = foodImgLink;
        this.quantity = quantity;
        this.type = type;
        this.time = time;
        this.status = status;
    }

    public Order(String buyerID, String sellerID, String foodName, String foodImgLink, long quantity, String type, String time, String status) {
        this.buyerID = buyerID;
        this.sellerID = sellerID;
        this.foodName = foodName;
        this.foodImgLink = foodImgLink;
        this.quantity = quantity;
        this.type = type;
        this.time = time;
        this.status = status;
    }

    public Order(String buyerID,String buyerName ,String sellerID, String foodName, String foodImgLink, long quantity, String type, String time) {
        this.buyerID = buyerID;
        this.buyerName = buyerName;
        this.sellerID = sellerID;
        this.foodName = foodName;
        this.foodImgLink = foodImgLink;
        this.quantity = quantity;
        this.type = type;
        this.time = time;
    }

    public String getBuyerID() {
        return buyerID;
    }

    public String getSellerID() {
        return sellerID;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodImgLink() {
        return foodImgLink;
    }

    public long getQuantity() {
        return quantity;
    }

    public String getType() {
        return type;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public String getBuyerName() {
        return buyerName;
    }

    @Override
    public String toString() {
        return "Order{" +
                "buyerID='" + buyerID + '\'' +
                ", buyerName='" + buyerName + '\'' +
                ", sellerID='" + sellerID + '\'' +
                ", foodName='" + foodName + '\'' +
                ", foodImgLink='" + foodImgLink + '\'' +
                ", quantity=" + quantity +
                ", type='" + type + '\'' +
                ", time='" + time + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

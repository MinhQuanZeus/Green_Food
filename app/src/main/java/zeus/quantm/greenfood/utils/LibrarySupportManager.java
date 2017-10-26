package zeus.quantm.greenfood.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


/**
 * Created by EDGY on 6/25/2017.
 */

public class LibrarySupportManager {
    private static LibrarySupportManager librarySupportManager;

    public static LibrarySupportManager getInstance(){
        if(librarySupportManager == null) {
            librarySupportManager = new LibrarySupportManager();
        }
        return librarySupportManager;
    }

    public static String randomUserTokenID(){
        List<String> listUserID = new ArrayList<>();
        listUserID.add("user-KnN_Fol8TkBKZNbClqm");
        listUserID.add("user-KnN_Id6kkSnhm9a4yWA");
        listUserID.add("user-KnNajIM03r_-tOVQMMs");
        return listUserID.get(new Random().nextInt(2));
    }

    public static String currentDateTime(){
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                .format(new Date())
                .replace(" ", " lúc ");
    }



    public static String formatCurrency(long currency){
        return new DecimalFormat("###,###")
                .format(currency)
                .replaceAll(",", ".");
    }

    public String distanceFromLocationFormat(String inputDistance){
        return inputDistance.replace(" mi", " km");
    }

    public static Date convertStringToDate(String dateString)
    {
        Date date = null;
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try{
            date = df.parse(dateString.replace("lúc ",""));
        }
        catch ( Exception ex ){
            ex.printStackTrace();
        }
        return date;
    }

    public static Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

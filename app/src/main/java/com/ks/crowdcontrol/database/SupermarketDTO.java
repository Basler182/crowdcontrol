package com.ks.crowdcontrol.database;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.ks.crowdcontrol.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.Data;

@Data
public class SupermarketDTO {
    private static int staticListID= 1;
    //ID of Database
    private String id;
    //ID in List View
    private String listId;
    //Address and Name
    private String name;
    private String street;
    private String houseNumber;
    private String city;
    private String zipCode;
    //Amount of people allowed to enter
    private int current_customers;
    //People currently inside the supermarket
    private int max_customers;
    //Coordinates of the supermarket
    private double latitude, longitude;
    //Supermarket Type
    private Type type;
    //Map for the Chart
    private Map<Integer, Integer> chartMap;

    public SupermarketDTO(String random){
        this.id = "1";
        this.listId = "1";
        this.name = "Aldi Süd";
        this.street = "Theodor Storm Straße";
        this.houseNumber ="14";
        this.city ="Regensburg";
        this.zipCode = "93051";

        this.current_customers = 10;
        this.max_customers = 0;

        this.latitude = 49.0;
        this.longitude = 12.0;

        this.type = Type.GROCERIES;
    }

    public SupermarketDTO(DocumentSnapshot documentSnapshot){
        id = documentSnapshot.getId();
        listId = String.valueOf(staticListID);
        staticListID++;
        //Name of supermarket
        name = documentSnapshot.getString("name");
        //Chart Map
        chartMap = (Map<Integer, Integer>) documentSnapshot.getData().get("chartmap");
        //Address
        HashMap addressMap = (HashMap) Objects.requireNonNull(documentSnapshot.getData()).get("address");
        assert addressMap != null;
        street = (String) addressMap.get("street");
        houseNumber = (String) addressMap.get("houseNumber");
        city = (String) addressMap.get("city");
        zipCode = (String) addressMap.get("zipCode");
        //Location
        HashMap locationMap = (HashMap) documentSnapshot.getData().get("gps");
        assert locationMap != null;
        if(locationMap.get("gps") != null && locationMap.containsKey("gps")){
            GeoPoint geoPoint = (GeoPoint) locationMap.get("gps");
            assert geoPoint != null;
            latitude = geoPoint.getLatitude();
            longitude = geoPoint.getLongitude();
        }
        type = Type.GROCERIES;
    }

    public String getShortAddress() {
        return city + " " + street + " " + houseNumber;
    }



    /**
     * The type the order.
     */
    public enum Type {
        GROCERIES("groceries", R.drawable.ic_type_groceries, R.string.order_title_groceries),
        OTHER("other", R.drawable.ic_type_groceries, R.string.order_title_groceries);

        @NonNull
        private final String name;
        @DrawableRes
        private final int icon;
        @StringRes
        private final int title;

        Type(@NonNull String name, @DrawableRes int icon, @StringRes int title) {
            this.name = name;
            this.icon = icon;
            this.title = title;
        }

        /**
         * Gets the appropriate type for the given internal name.
         *
         * @param name The internal name of the type.
         * @return The parsed type.
         */
        @NonNull
        public static Type byName(String name) {
            for (Type type : values()) {
                if (type.name.equals(name)) {
                    return type;
                }
            }
            return OTHER;
        }

        /**
         * Gets the internal name of the type. This is the name used in firebase.
         *
         * @return The internal name of the type.
         */
        @NonNull
        public String getName() {
            return name;
        }

        /**
         * Gets the icon that describes the order type.
         *
         * @return The resource id of the icon.
         */
        @DrawableRes
        public int getIcon() {
            return icon;
        }

        /**
         * Gets the string resource of the tile of the type.
         *
         * @return The resource id of the type title.
         */
        @StringRes
        public int getTitle() {
            return title;
        }
    }

}

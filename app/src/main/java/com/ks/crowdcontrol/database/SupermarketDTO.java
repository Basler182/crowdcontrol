package com.ks.crowdcontrol.database;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.ks.crowdcontrol.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import lombok.Data;


/**
 * Data Transfer Object of the Supermarkets.
 */
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
    //Supermarket Type means if its a supermarket or medical care shop
    private Type type;
    //ArrayList for the Chart it includes  the AI calculated data
    private ArrayList chartData;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public int getCurrent_customers() {
        return current_customers;
    }

    public void setCurrent_customers(int current_customers) {
        this.current_customers = current_customers;
    }

    public int getMax_customers() {
        return max_customers;
    }

    public void setMax_customers(int max_customers) {
        this.max_customers = max_customers;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public ArrayList getChartData() {
        return chartData;
    }

    public void setChartData(ArrayList chartData) {
        this.chartData = chartData;
    }

    public SupermarketDTO(DocumentSnapshot documentSnapshot){
        id = documentSnapshot.getId();
        listId = String.valueOf(staticListID);
        staticListID++;
        //Name of supermarket
        name = documentSnapshot.getString("name");
        //Chart Map
        chartData = (ArrayList) Objects.requireNonNull(documentSnapshot.getData()).get("chartData");
        //Address
        HashMap addressMap = (HashMap) Objects.requireNonNull(documentSnapshot.getData()).get("address");
        assert addressMap != null;
        street = (String) addressMap.get("street");
        houseNumber = (String) Objects.requireNonNull(addressMap.get("houseNumber")).toString();
        city = (String) addressMap.get("city");
        zipCode = (String) Objects.requireNonNull(addressMap.get("zipCode")).toString();
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

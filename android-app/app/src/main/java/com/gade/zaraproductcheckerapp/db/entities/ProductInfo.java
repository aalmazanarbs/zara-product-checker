package com.gade.zaraproductcheckerapp.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gade.zaraproductcheckerapp.db.converters.DateConverter;

import java.util.Date;

// The entity needs to be parcelable/serializable but Room do it
@Entity(indices = {@Index(value = {"api_id", "desired_color", "desired_size"}, unique = true)})
public class ProductInfo {

    @PrimaryKey(autoGenerate = true)
    public Long uid;
    @TypeConverters(DateConverter.class)
    private Date added;

    // DB fields
    @ColumnInfo(name = "api_id")
    private String APIId;
    private String name;
    private String url;
    @ColumnInfo(name = "image_base64")
    private String imageBase64;
    @ColumnInfo(name = "desired_size")
    private String desiredSize;
    @ColumnInfo(name = "desired_color")
    private String desiredColor;
    private String availability;
    private String price;

    // Class Attributes
    @Ignore
    private Boolean sizeStatusChanges = false;
    @Ignore
    private Boolean priceChanges = false;
    @Ignore
    private Boolean notFound = false;

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Date getAdded() {
        return added;
    }

    public void setAdded(Date added) {
        this.added = added;
    }

    public String getAPIId() {
        return APIId;
    }

    public void setAPIId(String APIId) {
        this.APIId = APIId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getDesiredSize() {
        return desiredSize;
    }

    public void setDesiredSize(String desiredSize) {
        this.desiredSize = desiredSize;
    }

    public String getDesiredColor() {
        return desiredColor;
    }

    public void setDesiredColor(String desiredColor) {
        this.desiredColor = desiredColor;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Boolean hasSizeStatusChanged() {
        return sizeStatusChanges;
    }

    public void setSizeStatusChanges(Boolean sizeStatusChanges) {
        this.sizeStatusChanges = sizeStatusChanges;
    }

    public Boolean hasPriceChanged() {
        return priceChanges;
    }

    public void setPriceChanges(Boolean priceChanges) {
        this.priceChanges = priceChanges;
    }

    public Boolean isNotFound() {
        return notFound;
    }

    public void setNotFound(Boolean notFound) {
        this.notFound = notFound;
    }
}

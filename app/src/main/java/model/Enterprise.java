package model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.Locale;

import helper.FirebaseConfig;

public class Enterprise implements Serializable {

    private String enterpriseId;
    private String urlImage;
    private String enterpriseName;
    private String enterpriseCategory;
    private String deliveryTime;
    private String deliveryRate;

    public Enterprise() {
    }

    public void save(){

        DatabaseReference reference = FirebaseConfig.getReference();
        DatabaseReference enterpriseRef = reference
                .child("Enterprises")
                .child(enterpriseId);
        enterpriseRef.setValue(this);
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName.toLowerCase();
    }

    public String getEnterpriseCategory() {
        return enterpriseCategory;
    }

    public void setEnterpriseCategory(String enterpriseCategory) {
        this.enterpriseCategory = enterpriseCategory;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getDeliveryRate() {
        return deliveryRate;
    }

    public void setDeliveryRate(String deliveryRate) {
        this.deliveryRate = deliveryRate;
    }
}

package model;

import com.google.firebase.database.DatabaseReference;

import helper.FirebaseConfig;

public class Product {

    private String userId;
    private String productId;
    private String urlImage;
    private String name;
    private String category;
    private String description;
    private String price;

    public Product() {
        DatabaseReference reference = FirebaseConfig.getReference();
        DatabaseReference productRef = reference
                .child("Products");
        setProductId(productRef.push().getKey());
    }

    public void save(){

        DatabaseReference reference = FirebaseConfig.getReference();
        DatabaseReference productRef = reference
                .child("Products")
                .child(userId)
                .child(getProductId());
        productRef.setValue(this);

    }

    public void remove(){

        DatabaseReference reference = FirebaseConfig.getReference();
        DatabaseReference productRef = reference
                .child("Products")
                .child(userId)
                .child(getProductId());
        productRef.removeValue();
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}

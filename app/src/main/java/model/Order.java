package model;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.List;

import helper.FirebaseConfig;

public class Order {

    private String address;
    private String enterpriseId;
    private String userid;
    private String orderId;
    private String paymentMethod;
    private String leadName;
    private String status = "Pendente";
    private String observation;
    private String total;
    private List<OrderItems> orderItems;

    public Order() {
    }

    public Order(String userId, String enterpriseId) {

        setUserid(userId);
        setEnterpriseId(enterpriseId);

        DatabaseReference reference = FirebaseConfig.getReference();
        DatabaseReference orderRef = reference
                .child("Orders_User")
                .child(enterpriseId)
                .child(userId);
        setOrderId(orderRef.push().getKey());
    }

    public void save(){

        DatabaseReference reference = FirebaseConfig.getReference();
        DatabaseReference orderRef = reference
                .child("Orders_User")
                .child(getEnterpriseId())
                .child(getUserid());
        orderRef.setValue(this);
    }

    public void remove(){

        DatabaseReference reference = FirebaseConfig.getReference();
        DatabaseReference orderRef = reference
                .child("Orders_User")
                .child(getEnterpriseId())
                .child(getUserid());
        orderRef.removeValue();
    }

    public void confirm(){

        DatabaseReference reference = FirebaseConfig.getReference();
        DatabaseReference orderRef = reference
                .child("Orders")
                .child(getEnterpriseId())
                .child(getOrderId());
        orderRef.setValue(this);
    }

    public void updateStatus(){

        HashMap<String, Object> status = new HashMap<>();
        status.put("status", getStatus());

        DatabaseReference reference = FirebaseConfig.getReference();
        DatabaseReference orderRef = reference
                .child("Orders")
                .child(getEnterpriseId())
                .child(getOrderId());
        orderRef.updateChildren(status);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getLeadName() {
        return leadName;
    }

    public void setLeadName(String leadName) {
        this.leadName = leadName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItems> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItems> orderItems) {
        this.orderItems = orderItems;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}

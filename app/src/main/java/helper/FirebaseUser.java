package helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class FirebaseUser {

    public static String getUserId(){

        FirebaseAuth auth = FirebaseConfig.getAuth();
        return auth.getUid();
    }

    public static com.google.firebase.auth.FirebaseUser getCurrentUser(){

        FirebaseAuth user = FirebaseConfig.getAuth();

        return user.getCurrentUser();
    }

    public static boolean updateUserType(String userType){

        try{

            com.google.firebase.auth.FirebaseUser user = getCurrentUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(userType)
                    .build();
            user.updateProfile(profile);

            return true;
        }catch (Exception e){

            e.printStackTrace();
            return false;
        }
    }
}

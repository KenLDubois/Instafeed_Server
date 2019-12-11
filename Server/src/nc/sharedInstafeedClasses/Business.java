package nc.sharedInstafeedClasses;

import java.io.Serializable;

public class Business implements Serializable {

    public String BusinessName;
    public String Address;

    public Business(String businessName, String address){
        BusinessName = businessName;
        Address = address;
    }

}
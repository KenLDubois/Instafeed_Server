package nc.sharedInstafeedClasses;

import java.io.Serializable;
import java.util.Date;

public class Rating implements Serializable {
    public String UserName;
    public String Title;
    public String Description;
    public Float StarRating;
    public Business RatedBusiness;
    public Date RatingDate;
    public Double Latitude;
    public Double Longitude;
    
    public Rating(Business business, float starRating, Double lat, Double longi, String user){

        RatedBusiness = business;
        StarRating = starRating;
        RatingDate = new Date();
        Latitude = lat;
        Longitude = longi;
        UserName = user;
    }

    public Rating(Business business, float starRating, String title, String description, Double lat, Double longi, String user){
        RatedBusiness = business;
        StarRating = starRating;
        RatingDate = new Date();
        Title = title;
        Description = description;
        Latitude = lat;
        Longitude = longi;
        UserName = user;
    }

}
package Model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class FishPrice {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty fishName;
    private final SimpleStringProperty fishDetails;
    private final SimpleDoubleProperty todayPrice;
    private final SimpleStringProperty place;
    private final SimpleStringProperty status;
    private final SimpleObjectProperty<LocalDate> date;
    private final  SimpleStringProperty images;
    public FishPrice(int id, String fishName, String fishDetails, double todayPrice,
                     String place, String status, LocalDate date, String images) {
        this.id = new SimpleIntegerProperty(id);
        this.fishName = new SimpleStringProperty(fishName);
        this.fishDetails = new SimpleStringProperty(fishDetails);
        this.todayPrice = new SimpleDoubleProperty(todayPrice);
        this.place = new SimpleStringProperty(place);
        this.status = new SimpleStringProperty(status);
        this.date = new SimpleObjectProperty<>(date);
        this.images = new SimpleStringProperty(images);
    }

    public int getId() { return id.get(); }
    public String getFishName() { return fishName.get(); }
    public String getFishDetails() { return fishDetails.get(); }
    public double getTodayPrice() { return todayPrice.get(); }
    public String getPlace() { return place.get(); }
    public String getStatus() { return status.get(); }
    public LocalDate getDate() { return date.get(); }
    public  String getImage(){return  images.get();}
}


package sample.Identification;

public class BirthPlace {

    private String city;
    private String region;
    private String area;
    private String country;

    public BirthPlace(){
        this.city = "";
        this.region = "";
        this.area = "";
        this.country = "";
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "BirthPlace{" +
                "city='" + city + '\'' +
                ", region='" + region + '\'' +
                ", area='" + area + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}

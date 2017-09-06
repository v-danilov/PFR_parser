package sample.POJO;

public class Person {

    private String id_request;
    private String lastname;
    private String firstname;
    private String surname;
    private String birthDate;
    private String sex;

    public Person() {
        id_request = "";
        lastname = "";
        firstname = "";
        surname = "";
        birthDate = "";
        sex = "";
    }

    public Person(Person p) {
        id_request = p.getId_request();
        lastname = p.getLastname();
        firstname = p.getFirstname();
        surname = p.getSurname();
        birthDate = p.getBirthDate();
        sex = p.getSex();
    }

    public String getId_request() {
        return id_request;
    }

    public void setId_request(String id_request) {
        this.id_request = id_request;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id_request='" + id_request + '\'' +
                ", lastname='" + lastname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", surname='" + surname + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }

    public boolean isEmpty(){
        if(id_request.isEmpty()){
            return true;
        }
        return false;
    }
}

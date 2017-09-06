package sample.Identification;

import sample.POJO.Person;

public class IdentifiedPerson extends Person {

    private BirthPlace birthPlace;
    private IdentificationResult identificationResult;

    public IdentifiedPerson() {
        super();
        birthPlace = new BirthPlace();
        identificationResult = new IdentificationResult();
    }

    public IdentifiedPerson(Person p) {
        super(p);
        birthPlace = new BirthPlace();
        identificationResult = new IdentificationResult();
    }

    public IdentificationResult getIdentificationResult() {
        return identificationResult;
    }

    public void setIdentificationResult(IdentificationResult validationResult) {
        this.identificationResult = validationResult;

    }

    public BirthPlace getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(BirthPlace birthPlace) {
        this.birthPlace = birthPlace;
    }

    @Override
    public String toString() {

        return super.toString() + "IdentifiedPerson{" +
                ", identificationResult=" + identificationResult +
                '}';
    }

    public void parseProperty(String name, String value) {
        switch (name) {

            case "ИдентификаторЗапроса":
                this.setId_request(value);
                break;
            case "Фамилия":
                this.setLastname(value);
                break;
            case "Имя":
                this.setFirstname(value);
                break;
            case "Отчество":
                this.setSurname(value);
                break;
            case "ДатаРождения":
                this.setBirthDate(value);
                break;
            case "Пол":
                this.setSex(value);
                break;
            case "ГородРождения":
                this.birthPlace.setCity(value);
                break;
            case "РайонРождения":
                this.birthPlace.setCity(value);
                break;
            case "ОбластьРождения":
                this.birthPlace.setCity(value);
                break;
            case "СтранаРождения":
                this.birthPlace.setCity(value);
                break;
            //default
            default:
                break;
        }
    }
}


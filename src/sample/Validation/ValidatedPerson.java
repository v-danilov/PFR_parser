package sample.Validation;

import sample.POJO.Person;

public class ValidatedPerson extends Person {

    private String snils;
    private ValidationResult validationResult;

    public ValidatedPerson(){
        super();
        snils = "";
        validationResult = new ValidationResult();
    }

    public ValidatedPerson(Person p){
        super(p);
        snils = "";
        validationResult = new ValidationResult();
    }

    public String getSnils() {
        return snils;
    }

    public void setSnils(String snils) {
        this.snils = snils;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }



    public void parseProperty(String name, String value) {
        switch (name) {

            case "ИдентификаторЗапроса":
                this.setId_request(value);
                break;
            case "СНИЛС":
                this.setSnils(value);
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
            //default
            default:
                break;
        }
    }


}
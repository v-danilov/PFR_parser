package sample.Identification;

import java.util.Random;

public class IdentificationResult {
    private String status;
    private String snils;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSnils() {
        return snils;
    }

    public void setSnils(String snils) {
        this.snils = snils;
    }

    public void generateSnils(){
        int first_block = generateInt(3);
        int second_block = generateInt(3);
        int third_block = generateInt(3);
        int last_block = generateInt(2);
        String snils_number = first_block + "-"
                                + second_block + "-"
                                + third_block + " "
                                + last_block;
        this.setSnils(snils_number);
    }

    private int generateInt(int digit_number){
        Random rnd=new Random();
        if( digit_number == 2) {
            return rnd.nextInt(89) + 10;
        }
        else {
            return rnd.nextInt(899) + 100;
        }
    }

    @Override
    public String toString() {
        return "IdentificationResult{" +
                "status='" + status + '\'' +
                ", snils='" + snils + '\'' +
                '}';
    }
}

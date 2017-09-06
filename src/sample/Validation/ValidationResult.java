package sample.Validation;

public class ValidationResult {
    private String status;
    private String description;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "status='" + status + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

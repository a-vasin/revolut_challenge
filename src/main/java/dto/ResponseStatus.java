package dto;

/**
 * @author a-vasin
 */
public enum ResponseStatus {
    SUCCESS("Success"),
    ERROR("Error");

    ResponseStatus(String status) {
        this.status = status;
    }

    public final String status;
}

package in.dev.gmsk.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class InvalidFilePath {

    private String error;
    private String message;
    private String httpStatus;
}

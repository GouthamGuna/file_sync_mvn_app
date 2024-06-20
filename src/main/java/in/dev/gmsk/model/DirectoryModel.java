package in.dev.gmsk.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class DirectoryModel {

    private String name;
    @JsonIgnore
    private volatile String path;
    private String url;
}

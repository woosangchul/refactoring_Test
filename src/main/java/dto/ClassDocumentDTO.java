package dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ClassDocumentDTO {
    private String title;
    private String date;
}

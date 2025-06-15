package farm.farmshop.dto;

import java.time.LocalDateTime;

import farm.farmshop.entity.Alert;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlertDto {
    private Long id;
    private String type;
    private String message;
    private LocalDateTime createdAt;


    public static AlertDto fromEntity(Alert alert) {
        return new AlertDto(
            alert.getId(),
            alert.getType(),
            alert.getMessage(),
            alert.getCreatedAt()
        );
    }
}

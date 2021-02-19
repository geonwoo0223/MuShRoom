package ssafy.a105.mushroom.vo;

import java.io.Serializable;
import javax.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

@Embeddable
@Data
public class MultiId implements Serializable {

  private String masterId;
  private String slaveId;

  @Builder
  public MultiId(String masterId, String slaveId) {
    this.masterId = masterId;
    this.slaveId = slaveId;
  }

  public MultiId() {
  }
}

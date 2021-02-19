package ssafy.a105.mushroom.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataClass<T> {

  private String type;
  private Integer index;
  private T obj;
}

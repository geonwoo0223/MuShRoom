package ssafy.a105.mushroom.vo;

import lombok.Data;

@Data
public class MessageDTO {
  String id;
  String message = "";

  public MessageDTO(String id) {
    this.id = id;
  }

  public MessageDTO(String id, String message) {
    this.id = id;
    this.message = message;
  }
}

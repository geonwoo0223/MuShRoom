package ssafy.a105.mushroom.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EmbeddedId;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import ssafy.a105.mushroom.util.CustomObjectIdSerializer;

@Data
@Document(collection = "music")
public class DataDTO {

  @JsonSerialize(using = CustomObjectIdSerializer.class)
  @MongoId
  @EmbeddedId
  private MultiId id;
  List<MusicPage> musicPageList = new ArrayList<>();
  List<Record> recordList = new ArrayList<>();

  public DataDTO(MultiId id) {
    this.id = id;
  }
}

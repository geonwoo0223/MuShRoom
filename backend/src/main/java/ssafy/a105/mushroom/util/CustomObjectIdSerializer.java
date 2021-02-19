package ssafy.a105.mushroom.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import ssafy.a105.mushroom.vo.MultiId;

public class CustomObjectIdSerializer extends JsonSerializer<MultiId> {

  @Override
  public void serialize(MultiId id, JsonGenerator gen,
      SerializerProvider serializerProvider) throws IOException {

    gen.writeStartObject();
    gen.writeFieldName("masterId");
    gen.writeString(id.getMasterId());
    gen.writeFieldName("slaveId");
    gen.writeString(id.getSlaveId());
    gen.writeEndObject();
  }
}

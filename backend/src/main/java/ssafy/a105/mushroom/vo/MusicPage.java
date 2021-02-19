package ssafy.a105.mushroom.vo;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class MusicPage {
  private String pageName = "New page";
  private List<Music> musicList = new ArrayList<>();
}

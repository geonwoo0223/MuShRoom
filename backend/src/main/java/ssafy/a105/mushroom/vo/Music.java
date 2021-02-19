package ssafy.a105.mushroom.vo;

import java.util.Date;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

@Data
public class Music {

  private String url;
  private String fileName;
  private Double volume = 0.0;
  private Double distortion = 0.0;
  private Double gain = 0.0;
  private Double reverb = 0.0;
  private boolean isLoop = false;
  private Double loopStart = 0.0;
  private Double loopEnd = 0.0;
  private Double delay = 0.0;
  private Double offset = 0.0;

  @CreatedDate
  private Date timestamp;

  @Builder
  public Music(String url, String fileName, Double volume, Double distortion, Double gain,
      Double reverb, boolean isLoop, Double loopStart, Double loopEnd, Double delay, Double offset) {
    this.url = url;
    this.fileName = fileName;
    this.volume = volume;
    this.distortion = distortion;
    this.gain = gain;
    this.reverb = reverb;
    this.isLoop= isLoop;
    this.loopStart = loopStart;
    this.loopEnd = loopEnd;
    this.delay = delay;
    this.offset = offset;
  }
}

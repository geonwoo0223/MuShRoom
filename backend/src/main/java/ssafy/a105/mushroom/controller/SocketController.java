package ssafy.a105.mushroom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import ssafy.a105.mushroom.service.MainService;
import ssafy.a105.mushroom.vo.MessageDTO;
import ssafy.a105.mushroom.vo.Music;
import ssafy.a105.mushroom.vo.DataClass;
import ssafy.a105.mushroom.vo.MusicPage;
import ssafy.a105.mushroom.vo.Record;

@Controller
public class SocketController {

  private final MainService mainService;

  @Autowired
  public SocketController(MainService mainService) {
    this.mainService = mainService;
  }

  @MessageMapping("/socket/music-page/{id}/receive")
  @SendTo("/socket/music-page/{id}/send")
  public DataClass<MusicPage> musicSocketHandler(@DestinationVariable String id, DataClass<MusicPage> obj) {
    // 객체가 주어지지 않을 경우 처리
    if(obj.getObj() == null) obj.setObj(new MusicPage());

    String type = obj.getType();
    if (type.equals("add")) {
      mainService.insertMusicPage(id, obj.getIndex(), obj.getObj());
    } else if (type.equals("delete")) {
      mainService.deleteMusicPage(id, obj.getIndex());
    } else if (type.equals("update")) {
      mainService.updateMusicPage(id, obj.getIndex(), obj.getObj());
    }

    return obj;
  }

  @MessageMapping("/socket/music/{id}/{pid}/receive")
  @SendTo("/socket/music/{id}/{pid}/send")
  public DataClass<Music> musicSocketHandler(@DestinationVariable String id, @DestinationVariable Integer pid, DataClass<Music> obj) {
    String type = obj.getType();
    if (type.equals("add")) {
      mainService.insertMusic(id, pid, obj.getIndex(), obj.getObj());
    } else if (type.equals("delete")) {
      mainService.deleteMusic(id, pid, obj.getIndex());
    } else if (type.equals("update")) {
      mainService.updateMusic(id, pid, obj.getIndex(), obj.getObj());
    }

    return obj;
  }

  @MessageMapping("/socket/record/{id}/receive")
  @SendTo("/socket/record/{id}/send")
  public DataClass<Record> recordSocketHandler(@DestinationVariable String id,
      DataClass<Record> obj) {
    String type = obj.getType();
    if (type.equals("add")) {
      mainService.insertRecord(id, obj.getIndex(), obj.getObj());
    } else if (type.equals("delete")) {
      mainService.deleteRecord(id, obj.getIndex());
    } else if (type.equals("update")) {
      mainService.updateRecord(id, obj.getIndex(), obj.getObj());
    }

    return obj;
  }

  @MessageMapping("/socket/chat/{id}/receive")
  @SendTo("/socket/chat/{id}/send")
  public MessageDTO chatSocketHandler(@DestinationVariable String id, MessageDTO msg) {
    System.out.println(msg);
    return msg;
  }
}

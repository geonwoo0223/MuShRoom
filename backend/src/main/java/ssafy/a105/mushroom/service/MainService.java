package ssafy.a105.mushroom.service;

import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssafy.a105.mushroom.repository.MainRepository;
import ssafy.a105.mushroom.vo.DataDTO;
import ssafy.a105.mushroom.vo.MultiId;
import ssafy.a105.mushroom.vo.Music;
import ssafy.a105.mushroom.vo.MusicPage;
import ssafy.a105.mushroom.vo.Record;

@Service
public class MainService {

  @Autowired
  private final MainRepository mainRepository;

  public MainService(MainRepository mainRepository) {
    this.mainRepository = mainRepository;
  }

  public DataDTO deleteData(String id) {
    DataDTO data = mainRepository.findByIdMasterIdOrIdSlaveId(id, id).orElseThrow(() ->
        new IllegalArgumentException("Document does not exist"));
    mainRepository.delete(data);

    return data;
  }

  public DataDTO makeData() {
    String masterId = "";
    while (true) {
      masterId = makeRandomString();
      if (!mainRepository.existsByIdMasterIdOrIdSlaveId(masterId, masterId)) {
        break;
      }
    }

    String slaveId = "";
    while (true) {
      slaveId = makeRandomString();
      if (!mainRepository.existsByIdMasterIdOrIdSlaveId(slaveId, slaveId)) {
        break;
      }
    }

    // 기본 페이지 등록
    DataDTO data = new DataDTO(new MultiId(masterId, slaveId));
    List<MusicPage> musicPageList = data.getMusicPageList();
    musicPageList.add(new MusicPage());
    data.setMusicPageList(musicPageList);
    mainRepository.save(data);

    return data;
  }

  public List<DataDTO> getAllData() {
    return mainRepository.findAll();
  }

  public DataDTO getOneData(String id) {
    return mainRepository.findByIdMasterIdOrIdSlaveId(id, id)
        .orElseThrow(() -> new IllegalArgumentException("Document does not exist"));
  }

  public List<MusicPage> insertMusicPage(String id, Integer index, MusicPage musicPage) {
    DataDTO data = getOneData(id);
    List<MusicPage> musicPageList = data.getMusicPageList();
    musicPageList.add(index, musicPage);
    data.setMusicPageList(musicPageList);
    mainRepository.save(data);

    return musicPageList;
  }

  public List<MusicPage> deleteMusicPage(String id, Integer index) {
    DataDTO data = getOneData(id);
    List<MusicPage> musicPageList = data.getMusicPageList();
    musicPageList.remove(index.intValue());
    data.setMusicPageList(musicPageList);
    mainRepository.save(data);

    return musicPageList;
  }

  public List<MusicPage> updateMusicPage(String id, Integer index, MusicPage musicPage) {
    DataDTO data = getOneData(id);
    List<MusicPage> musicPageList = data.getMusicPageList();
    musicPage.setMusicList(musicPageList.get(index).getMusicList());
    musicPageList.set(index, musicPage);
    data.setMusicPageList(musicPageList);
    mainRepository.save(data);

    return musicPageList;
  }


  public List<Music> insertMusic(String id, Integer pageNum, Integer index, Music music) {
    DataDTO data = getOneData(id);
    List<MusicPage> musicPageList = data.getMusicPageList();
    MusicPage musicPage = musicPageList.get(pageNum);
    List<Music> musicList = musicPage.getMusicList();
    musicList.add(index, music);
    musicPage.setMusicList(musicList);
    musicPageList.set(pageNum, musicPage);

    data.setMusicPageList(musicPageList);
    mainRepository.save(data);

    return musicList;
  }

  public List<Music> deleteMusic(String id, Integer pageNum, Integer index) {
    DataDTO data = getOneData(id);
    List<MusicPage> musicPageList = data.getMusicPageList();
    MusicPage musicPage = musicPageList.get(pageNum);
    List<Music> musicList = musicPage.getMusicList();
    musicList.remove(index.intValue());
    musicPage.setMusicList(musicList);
    musicPageList.set(pageNum, musicPage);

    data.setMusicPageList(musicPageList);
    mainRepository.save(data);

    return musicList;
  }

  public List<Music> updateMusic(String id, Integer pageNum, Integer index, Music music) {
    DataDTO data = getOneData(id);
    List<MusicPage> musicPageList = data.getMusicPageList();
    MusicPage musicPage = musicPageList.get(pageNum);
    List<Music> musicList = musicPage.getMusicList();
    musicList.set(index, music);
    musicPage.setMusicList(musicList);
    musicPageList.set(pageNum, musicPage);

    data.setMusicPageList(musicPageList);
    mainRepository.save(data);

    return musicList;
  }

  public List<Record> insertRecord(String id, Integer index, Record record) {
    DataDTO data = getOneData(id);
    List<Record> recordList = data.getRecordList();
    recordList.add(index, record);
    data.setRecordList(recordList);
    mainRepository.save(data);

    return recordList;
  }

  public List<Record> deleteRecord(String id, Integer index) {
    DataDTO data = getOneData(id);
    List<Record> recordList = data.getRecordList();
    recordList.remove(index.intValue());
    data.setRecordList(recordList);
    mainRepository.save(data);

    return recordList;
  }

  public List<Record> updateRecord(String id, Integer index, Record record) {
    DataDTO data = getOneData(id);
    List<Record> recordList = data.getRecordList();
    recordList.set(index, record);
    data.setRecordList(recordList);
    mainRepository.save(data);

    return recordList;
  }


  public String makeRandomString() {
    int leftLimit = 48; // 0
    int rightLimit = 122; // z
    int targetStringLength = 15;

    return new Random().ints(leftLimit, rightLimit + 1)
        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
        .limit(targetStringLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }
}

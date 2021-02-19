package ssafy.a105.mushroom.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ssafy.a105.mushroom.service.MainService;
import ssafy.a105.mushroom.vo.DataDTO;

// @CrossOrigin(origins = "*")
@RestController
public class SimpleController {

  @Autowired
  private final MainService mainService;

  public SimpleController(MainService mainService) {
    this.mainService = mainService;
  }

  @GetMapping("/data")
  public ResponseEntity<List<DataDTO>> getAllData() {
    return new ResponseEntity<>(mainService.getAllData(), HttpStatus.OK);
  }

  // 새로운 방 생성
  @PostMapping("/data")
  public ResponseEntity<DataDTO> insertData() {
    return new ResponseEntity<>(mainService.makeData(), HttpStatus.OK);
  }

  @GetMapping("/data/{id}")
  public ResponseEntity<DataDTO> getOneData(@PathVariable String id) {
    return new ResponseEntity<>(mainService.getOneData(id), HttpStatus.OK);
  }

  @DeleteMapping("/data/{id}")
  public ResponseEntity<DataDTO> deleteOneData(@PathVariable String id) {
    return new ResponseEntity<>(mainService.deleteData(id), HttpStatus.OK);
  }
}

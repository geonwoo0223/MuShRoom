package ssafy.a105.mushroom.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import ssafy.a105.mushroom.vo.DataDTO;

public interface MainRepository extends MongoRepository<DataDTO, String> {

  Optional<DataDTO> findByIdMasterIdOrIdSlaveId(String masterId, String slaveId);

  boolean existsByIdMasterIdOrIdSlaveId(String masterId, String slaveId);
}

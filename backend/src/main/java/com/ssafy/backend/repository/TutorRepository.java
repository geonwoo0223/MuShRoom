package com.ssafy.backend.repository;

import com.ssafy.backend.model.TutorEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TutorRepository extends MongoRepository<TutorEntity, String> {

}

package ssafy.a105.mushroom.configure;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

// 감사기능 사용을 위한 Mongo Config
@Configuration
@EnableMongoAuditing
public class MongoConfig {

}

package com.troy.common.mongodb.repository;

import com.troy.common.mongodb.domain.MgBaseEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Auther: zhuqing
 * @Date: 2023/8/17 13:13:21
 * @Description: MgBaseRepository
 * @Version: 1.0.0
 */
public interface MgBaseRepository<T extends MgBaseEntity> extends MongoRepository<T , Long> {
}

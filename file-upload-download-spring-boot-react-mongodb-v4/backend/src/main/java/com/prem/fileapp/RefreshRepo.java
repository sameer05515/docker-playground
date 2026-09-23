package com.prem.fileapp;
import org.springframework.data.mongodb.repository.MongoRepository;import java.util.*;
public interface RefreshRepo extends MongoRepository<RefreshToken,String>{Optional<RefreshToken> findByTokenHash(String h);List<RefreshToken> findByFamilyId(String f);}

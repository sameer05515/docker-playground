package com.prem.fileapp;
import org.springframework.data.annotation.Id;import org.springframework.data.mongodb.core.index.Indexed;import org.springframework.data.mongodb.core.mapping.Document;import java.time.Instant;
@Document("refresh_tokens") public class RefreshToken{@Id public String id;public String userId;@Indexed(unique=true) public String tokenHash;@Indexed public String familyId;@Indexed(expireAfter="0s") public Instant expiresAt;public boolean revoked;public Instant createdAt;}

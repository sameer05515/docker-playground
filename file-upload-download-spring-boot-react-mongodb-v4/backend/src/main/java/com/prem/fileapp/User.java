package com.prem.fileapp;
import org.springframework.data.annotation.Id;import org.springframework.data.mongodb.core.mapping.Document;
@Document("users") public class User{@Id public String id;public String username;public String passwordHash;public String role="USER";public boolean enabled=true;}

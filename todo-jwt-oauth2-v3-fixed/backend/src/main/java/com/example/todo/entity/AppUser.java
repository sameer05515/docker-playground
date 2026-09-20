package com.example.todo.entity;

import jakarta.persistence.*;

@Entity
@Table(name="users")
public class AppUser {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(unique=true, nullable=false) private String username;
    @Column(nullable=false) private String password;
    @Column(unique=true) private String email;
    @Column(nullable=false) private String role="USER";
    public Long getId(){return id;} public String getUsername(){return username;} public void setUsername(String v){username=v;}
    public String getPassword(){return password;} public void setPassword(String v){password=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getRole(){return role;} public void setRole(String v){role=v;}
}
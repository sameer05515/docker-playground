package com.prem.fileapp;
import org.springframework.boot.*;import org.springframework.boot.autoconfigure.*;import org.springframework.context.annotation.Bean;import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;import org.springframework.security.crypto.password.PasswordEncoder;
@SpringBootApplication public class App{
 public static void main(String[] a){SpringApplication.run(App.class,a);}
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
}
